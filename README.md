# yoogurt-taxi

![项目架构图](https://github.com/liu-weihao/yoogurt-taxi/blob/master/architecture.png?raw=true)


yoogurt-taxi包含都是业务代码，即为上图中的Business Service部分，而在这之上的部分，为项目的通用支撑服务，详见[foundation-service](https://github.com/liu-weihao/foundation-service "foundation-service")。

自底向上进行讲解：

1、taxi-model，严格意义上讲，这不是一个spring boot项目，而是一个普通的maven module，所以不能单独启动，更不能单独部署。这里面存放的是与Mybatis3相关的model，mapper，以及[mybatis-generator](https://github.com/mybatis/generator "mybatis-generator")插件。作为公共依赖存在，taxi-model将被绝大数module依赖，除非打算不与MySQL交互；

2、taxi-common，与taxi-model一样，这也是一个普通的maven module。这里面放置了一些共用的类，比如controller，dao，enums，vo等。项目的所有module都依赖于此，但是与taxi-model没有直接关联；

下面需要结合功能脑图进行讲解，如下图所示：

![功能脑图](https://github.com/liu-weihao/yoogurt-taxi/blob/master/yoogurt-taxi(App%20Client).png?raw=true)

3、taxi-system，系统设置模块，相对独立；

4、taxi-auth，鉴权中心，待完善；

5、taxi-account，我的钱包模块；

6、taxi-user，用户管理模块；

7、taxi-order，租单模块；

8、taxi-finance，支付，退款，转账等财务相关操作；

9、taxi-notification，通知提醒服务。