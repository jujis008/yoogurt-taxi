# 支付系统详细设计文档


## 一、支付平台设计

目前，支付的流程分成了三大部分：发起支付，发起退款，接收回调。 
（在线转账功能延后考虑） 

考虑到吞吐量的影响，将原先同步的编程方式改为异步的编程方式，不出意外的话，将会使用到Java8的ExecutorService和CompletableFuture。

此外，还用到了公司其他的现成的东西：RabbitMQ，Redis，MongoDB

具体是如何做到的，请接着往下读。

1、发起支付

架构图如下所示：

![支付架构图](https://github.com/liu-weihao/yoogurt-taxi/blob/master/taxi-finance/docs/draw.io/export/payment.png?raw=true)

跟着标注的序号，可以跟踪到一个支付请求是如何发起的（Sequence Diagram就免了），流程描述如下：

1) Submit a pay task. 当客户端需要发起支付的时候，起始是向支付任务队列里面加入了一个新的支付任务，这个过程是异步实现的。先根据客户端提交的参数，构造好一个新的支付任务；

2) Offer a task，开启一个异步任务，做的事情就是向MQ中添加一个新的支付任务，等待被消费；

3) Pay task description，一旦异步任务被成功创建，将会把第一步构造好的支付任务信息直接return给客户端；

4) Poll a task，与此同时，支付任务的消费者将新的支付任务poll下来进行执行；

5) Send a pay request，这一步需要根据实际情况而定。并不是所有的支付请求都要先经过第三方支付平台，比如支付宝；而对于微信，则还需要凭支付参数申请一个prepay_id，再经由客户端发起支付；

6) Response，没什么好说的，第三方渠道返回的支付必要参数；

7) Cache result，至此，一个支付任务可以算是完成了，可以将任务的执行结果（无论成功与否）缓存在Redis中，随时等待客户端的回访；

8) Query result，客户端在提交支付任务后，间隔一定时间后（建议3s），发起一个结果查询的请求；

9) Query，直接进Redis查找结果；

10) Synchronize，这是一个异步的操作，将支付任务的执行结果“顺便”同步到MongoDB中，并删除Redis中缓存的任务执行结果。持久化到MongoDB主要是为后续的容错，重试，数据分析，风控平台等提供落地的数据源；

11) Return，由Redis返回给应用服务器；

12) Return payment，应用服务器再将最终的支付对象返回给客户端。

让我们更深入一点，看看Class Diagram：

![支付-类图](https://github.com/liu-weihao/yoogurt-taxi/blob/master/taxi-finance/docs/uml/export/payment.png?raw=true)

大部分还是能看懂的，我解释几个关键的property：
1) CommonParams.appId，这是为了区分不同的产品所设置的。现实中，很有可能一个产品会申请与之对应的支付渠道，然后再nirvana-pay中创建应用，设置好对应的支付参数，系统将会分配一个appId，凭此值就可以直接定位到各个支付参数。本想再区分一下测试环境和正式环境，想想还是算了吧！会把自己玩死的；

2) PayParams.amount，这里代表的是支付金额的意思，但是这套支付系统的金额单位统一设置成：人民币【分】；

3) Payment.payId，支付对象的id，退款的时候会用到；

4) Payment.metadata，理论上，元数据这个字段没啥限制，要是非要说有限制，那么就是字段长度了——5000个字符。这个字段的想象空间还是很大的：用于填写丰富的交易相关信息，用于在增长智能系统产品中进行深入商业分析。包括交易行为多维分析、人群分析、产品转化路径、个性化推荐、智能补贴、定向推送等。看产品经理要怎么玩了；

5) Payment.credential，这个字段非常非常重要，其中装载的就是客户端最终发起支付请求的凭证；

6) Retry机制，用户可以设置重试与否，一旦设置了TaskInfo.needRetry=true（不出意外，默认就是允许重试），就启用了nirvana-pay的Retry机制。还可以设置重试的次数（TaskInfo.retryTimes），默认三次，分别间隔1s，2s，3s，间隔时间以公差为1的等差数列组成。当然不会让用户无限重试，系统内置有一个最大重试次数，最大重试次数内置为5次。

<font color=red>
**为什么是5次？
你感受一下，1s，2s，3s，4s，5s，整个请求链条就被拉长到了15s，这对客户端简直就是灾难了！！** 
</font>

发起支付的最后一部分——数据库设计：

### 1、MongoDB

先解释一下为什么要用MongoDB：

个人觉得，如果这个通用服务要得到较好的推广（甚至是开源），用MySQL等关系型数据库是不二之选，因为一个完整实用的系统，必然是少不了数据库的，如果一旦用了一些非传统的东西，必然会提高一部分人的对接成本。有的人一看不符合团队的技术栈，直接就不考虑了。

为什么我还是要用MongoDB呢？

① 团队的技术栈里面有这么个东西，不用白不用；

② MongoDB普及程度实在是不要太高，还不用上点NoSQL的东西，感觉自己分分钟被OUT掉了；

③ 要存储的数据结构需要支持动态扩展的特性，我就看中MongoDB的灵活性，如下是要存储的数据结构：

document_name = “Payment”

	{
	    "payId": "pay_Oyvrf9vP880STm1e9G5CSCm1",
	    "method": "gogen.nirvana.pay",
	    "version": "v1.0",
	    "timestamp": 1473044885,
	    "created": 1473042835,
	    "paid": false,
	    "appId": "app_KiPGa98abDmLe9ev",
	    "channel": "wx",
	    "orderNo": "20161899798416",
	    "clientIp": "192.168.18.189",
	    "amount": 10000,
	    "subject": "用户充值订单(￥100.0)",
	    "body": "用户充值订单(￥100.0)",
	    "paidTime": null,
	    "transactionNo": "",
	    "statusCode": "",
	    "message": "",
	    "metadata": {
	        "user_id": "170204469176",
	        "phone_number": "13811234567"
	    },
	    "credential": {
	        "appId": "wx4932b5159d18311e",
	        "partnerId": "1269774001",
	        "prepayId": "wx201609051033574da13955420883291539",
	        "nonceStr": "1e99d8ffdde926ed9cbdf4d2e614abad",
	        "timeStamp": "1473042837",
	        "packageValue": "Sign=WXPay",
	        "sign": "1CECCE6B13C956DEBA88800B3DEC4DBE"
	    },
	    "extra": {},
	    "description": ""
	}

其中，metadata，credential，extra这类字段，并没有一个特别固定的规范，用MySQL要冗余一下字段才行，或者针对每个渠道去分表，想想都觉得烦！

### 2、MySQL
因为nirvana-pay被设计成为支持多应用，多渠道，所以此处用到MySQL存放一些应用配置。
暂时只支持支付宝接入，其他渠道日后可随时扩展。
E-R图免了，直接上数据库表结构：

1) finance_bank_card：用户绑卡信息

| 字段名 | 名称 | 数据类型 | NOT NULL | 注释 |
| :--- | --- | --- | --- | --- |
| id | 自增ID | int | TRUE |   |
| user_id | 用户的id | varchar(45) | TRUE |   |
| card_no | 银行卡号 | varchar(20) | TRUE |   |
| bank_name | 开户行 | varchar(20) | TRUE |   |
| id_card_no | 身份证号码 | varchar(20) | TRUE |   |
| mobile | 手机号码 | varchar(15) | TRUE |   |
| creator | 创建人id | varchar(45) | TRUE |   |
| gmt_create | 创建时间 | varchar(30) | TRUE | |

2) finance_account：账户信息表

| 字段名 | 名称 | 数据类型 | NOT NULL | 注释 |
| --- | --- | --- | --- | --- |
| id | 自增ID | int | TRUE |   |
| user_id | 用户ID | varchar(64) | TRUE |   |
| account_no | 账户编号 | varchar(20) | TRUE | 账单编号，已添加唯一索引。与外部系统对接，使用此字段标识。 |
| account_name | 账户名称 | varchar(32) | TRUE | 账户名称 |
| balance | 账户余额 | decimal(12,2) | TRUE | 账户当前余额 |
| parent_id | 上级账户id | int | TRUE | 上级账户id，顶级账户的parent_id=0 |
| level | 账户级别 | int | TRUE | 账户级别从1开始 |
| is_deleted | 是否删除 | smallint | TRUE |   |
| gmt_create | 创建时间 | datetime | TRUE |   |
| creator | 创建人ID | varchar(64) | TRUE |   |
| gmt_modify | 上次修改时间 | datetime | TRUE |   |
| modifier | 修改人ID | varchar(64) | TRUE |   |

3) finance_bill：用户账单

| 字段名 | 名称 | 数据类型 | NOT NULL | 注释 |
| --- | --- | --- | --- | --- |
| id | 自增ID | int | TRUE |   |
| billNo | 账单编号 | varchar(20) | TRUE | 账单编号，已添加唯一索引。与外部系统对接，使用此字段标识。 |
| user_id | 用户ID | varchar(64) | TRUE |   |
| account_id | 账户id | int | TRUE |   |
| context_id | 交易对象id | varchar(64) | TRUE |   |
| tag | 交易标签 | varchar(32) | TRUE |   |
| type | 账单类型 | varchar(32) | TRUE |   |
| amount | 交易金额 | decimal(12,2) | TRUE |   |
| from | 资金来源 | varchar(128) | TRUE | 资金来源 |
| to | 资金去向 | varchar(128) | TRUE | 资金去向，通常是收款人的卡号 |
| receiver | 收款人姓名 | varchar(32) | TRUE | 收款人姓名 |
| contact | 收款人联系方式 | varchar(64) | FALSE | 收款人联系方式 |
| description | 交易描述信息 | varchar(128) | FALSE |   |
| status | 账单状态 | varchar(32) | TRUE | 交易状态: PENDING-待处理，TRANSFERING-转账中，SUCCESS-处理成功，REFUSE-拒绝，FAIL-处理失败 |
| remark | 账单备注 | varchar(128) | FALSE |   |
| is_deleted | 是否删除 | smallint | TRUE |   |
| gmt_create | 创建时间 | datetime | TRUE |   |
| creator | 创建人ID | varchar(64) | TRUE |   |
| gmt_modify | 上次修改时间 | datetime | TRUE |   |
| modifier | 修改人ID | varchar(64) | TRUE |   |

4) finance_record：账单操作记录表

| 字段名 | 名称 | 数据类型 | NOT NULL | 注释 |
| --- | --- | --- | --- | --- |
| id | 自增ID | int | TRUE |   |
| bill_id | 账单id | int | TRUE |   |
| status | 账单状态 | varchar(32) | TRUE | 交易状态: PENDING-待处理，TRANSFERING-转账中，SUCCESS-处理成功，REFUSE-拒绝，FAIL-处理失败 |
| remark | 操作备注 | varchar(128) | FALSE |   |
| is_deleted | 是否删除 | smallint | TRUE |   |
| creator | 创建人ID | varchar(64) | TRUE |   |
| gmt_create | 创建时间 | datetime | TRUE |   |
| modifier | 修改人ID | varchar(64) | TRUE |   |
| gmt_modify | 上次修改时间 | datetime | TRUE |   |


