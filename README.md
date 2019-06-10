# saga
saga分布式事务
## 实现功能
* 基于saga协议的分布式事务实现
* 支持异步cancel， 同时支持并发cancel，尽可能减少对系统的性能影响
* 支持spring cloud和dubbo
* 支持非rpc的方法作为事务参与方
## TODO：
* dashboard / admin 事务的可视化以及管理
## 上手指南
### 基本概念
* SagaTransactional注解：定义事务边界，当前支持事务嵌套，但是只有最外层事务才会提交或者回滚
* SagaParticipative注解：定义事务参与方，该标记设置cancelMethod，如果未设置，则事务回滚时什么也不做
### 运行demo
* 执行 ./tim-saga-core/src/main/dbscripts/saga.sql 创建事务表结构
* 运行mvn install
* 修改 ./tim-saga-demo/tim-saga-demo-springcloud/tim-saga-demo-springcloud-orderservice/项目下的application.yml中的数据库配置
* 运行 ./tim-saga-demo/tim-saga-demo-springcloud/ 目录下三个程序 尝试创建订单
## 测试
## 部署
## 使用到的框架
