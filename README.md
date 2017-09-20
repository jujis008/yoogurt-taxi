# yoogurt-taxi

![项目架构图](https://github.com/liu-weihao/yoogurt-taxi/blob/master/architecture.png?raw=true)


yoogurt-taxi包含都是业务代码，即为上图中的Business Service部分，而在这之上的部分，为项目的通用支撑服务，详见[foundation-service](https://github.com/liu-weihao/foundation-service "foundation-service")。

自底向上进行讲解：

1、taxi-common，严格意义上讲，这不是一个spring boot项目，而是一个普通的maven module。这里面放置了一些共用的类，比如controller，dao，enums，vo等。项目的所有module都依赖于此；

2、taxi-model，与taxi-common一样，这也是一个普通的maven module，所以不能单独启动，更不能单独部署。这里面存放的是与Mybatis3相关的model，mapper，以及[mybatis-generator](https://github.com/mybatis/generator "mybatis-generator")插件。依赖于taxi-common；

3、taxi-gateway，统一网关，基于zuul实现，集成了Apache Shiro和JWT作为安全验证。

下面需要结合功能脑图进行讲解，如下图所示：

![功能脑图](https://github.com/liu-weihao/yoogurt-taxi/blob/master/yoogurt-taxi(App%20Client).png?raw=true)

4、taxi-system，系统设置模块，相对独立；

5、taxi-auth，鉴权中心，待完善；

6、taxi-account，我的钱包模块；

7、taxi-user，用户管理模块；

8、taxi-order，租单模块；

9、taxi-finance，支付，退款，转账等财务相关操作；

10、taxi-notification，通知提醒服务。
