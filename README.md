# saga
saga分布式事务
## 分布式事务的核心要点
* 事务的持久化，事务只有持久化了才能在宕机的时候，根据持久化事务的状态来执行回滚操作，实现最终一致性
* 参与方的cancel操作的幂等性，因为事务cancel的时候可能会多次尝试，所以接口需要实现幂等操作
## 本项目实现功能
* 基于saga协议的分布式事务实现
* 支持异步cancel， 同时支持并发cancel，尽可能减少对系统的性能影响
* 支持spring cloud和dubbo（待实现，会基于filter的方式实现）
## TODO：
* dashboard / admin 事务的可视化以及管理
## 上手指南
### 基本概念
* SagaTransactional注解：定义事务边界，当前支持事务嵌套，但是只有最外层事务才会提交或者回滚
* SagaParticipative注解：定义事务参与方，该标记设置cancelMethod，如果未设置，则事务回滚时什么也不做
* ApplicationId: 如果多个应用程序使用同一个saga持久化数据，在事务恢复的时候需要指定applicationId来获取待恢复的事务，防止
### 运行demo
* 运行 mvn install
* 执行 ./tim-saga-core/src/main/dbscripts/saga.sql 创建事务表结构
* 执行 ./time-saga-demo/dbscripts/saga-demo.sql 创建demo的数据库、表结构和初始化数据
* 如果数据库地址不是localhost:3036，就修改account inventory order项目下的数据库配置，如果为localhost:3036，则无需修改
* 修改 ./tim-saga-demo/tim-saga-demo-springcloud/tim-saga-demo-springcloud-orderservice/项目下的application.yml中的数据库配置
* 启动 ./tim-saga-demo/tim-saga-demo-springcloud/ 目录下三个程序 account inventory order
* 访问 http://localhost:10087/swagger-ui.html 尝试各种支付订单的方法
## 测试
## 部署
## 鸣谢
* 本项目参考了[tcc-transaction](https://github.com/changmingxie/tcc-transaction) 和[hmily](https://github.com/yu199195/hmily)的实现
## 使用到的框架
