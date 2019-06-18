# saga
Java saga分布式事务
## 分布式事务的核心要点
* 事务的持久化，事务只有持久化了才能在宕机的时候，根据持久化事务的状态来执行回滚操作，实现最终一致性
* 参与方的cancel操作的幂等性，因为事务cancel的时候可能会多次尝试，所以接口需要实现幂等操作
* 需要定时任务定时恢复因为异常没有达到数据一致性的事务，同时要考虑定时任务多实例时对事务的争抢逻辑
## 本项目实现功能
* 基于saga协议的分布式事务实现
* 支持异步cancel， 同时支持并发cancel，尽可能减少对系统的性能影响
* 支持spring cloud（dubbo待实现）
## 对比 tcc-transaction hmily
因为上述两个分布式事务都是tcc事务，本质上可能没有对比性，但是都属于同步的柔性事务，对于业务代码来说都有侵入性，所以还是对比一下吧。
#### 当然这个只是个人的一面之词，如有不正确的地方，欢迎大家批评指正。
### tcc vs saga
* tcc是try confirm cancel, 所以无论事务成功失败，都需要与每一个服务进行至少两次的交互，性能相对较差。（可以通过异步confirm cancel提升性能)
* tcc每个事务参与方需要提供三个方法
* saga 是每个事务参与方直接提交，如果出现失败，就把已经提交的回滚即可，每个事务参与方需要提供2个方法，如果事务最终成功（可能性很大），则与每一个事务参与方只需要交互一次
### tcc-transaction
#### 优点
* ##### 事务可靠性高
#### 缺点
* #### 性能不太好
    因为作者把所有的事务参与方放在了事务对象的一个字段中，导致添加、修改任何一个事务参与方都需要重新序列化该字段，该字段很大，持久化性能不高
### hmily
#### 优点
* 性能好
* 生态好，完成度高
* 作者有视频对源码讲解，作者很nice
#### 缺点
* ##### 极端场景下事务不可靠
    >作者在解释高性能的时候提到："事务日志的持久化是异步执行的， 事务日志写失败的时候，会使用内存缓存，如果此时系统宕机了，恭喜可以买彩票了"
个人认为如果业务代码bug导致OOM，或者系统断电重启，很可能会导致日志的持久化失败的同时内存缓存不可用，作为事务的实现，保证数据的一致性应该是第一标准，任何情况下都不应该出现不一致的问题，至少在人工介入的情况下可以恢复
* ##### Demo中的cancel confirm方法并没有考虑到幂等性
    >这个不太能说是缺点吧，只是提醒大家自己的业务实现上需要注意这一点
#### tim-saga
#### 优点
* 通过SagaTransactional和SagaParticipative 两个注解来定义事务更清晰
* 性能相对还可以，（如果觉得性能不够高，可以考虑写事务和更新事务时写入消息队列），但是之久化一定要保证
* 事务可靠性高
* 事务恢复的定时任务job获取transaction时使用了悲观锁，防止同一个tranaction被多个job实例执行恢复
* 事务达到重试上限，需要人工介入时支持报警，当前支持钉钉报警
* 逻辑简单，方便调试，找bug
#### 缺点
* 刚开始做，可能会有bug
* 需要在接口层面实现cancel方法，业务侵入性相对更大一些吧
* 事务嵌套支持较弱，只支持PROPAGATION_REQUIRED。 但是应该能满足绝大部分的场景了吧
* 不支持在rp链式c调用中传递事务
## TODO：
* dashboard / admin 事务的可视化以及管理
## 上手指南
### 基本概念
* SagaTransactional注解：定义事务边界，当前支持事务嵌套，但是只有最外层事务才会提交或者回滚
* SagaParticipative注解：定义事务参与方，该标记设置cancelMethod，如果未设置，则事务回滚时什么也不做
* ApplicationId: 如果多个应用程序使用同一个saga持久化数据，在事务恢复的时候需要指定applicationId来获取待恢复的事务，防止
### 运行demo
* 运行 mvn clean install -DskipTests
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
* 欢迎大家指正系统中代码或者设计上的问题，
## 使用到的框架