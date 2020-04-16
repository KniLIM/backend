# backend

## 注意事项

- dubbo-api模块下有个没用的主类APP,这个类不要删,不然没法子编译.

- 项目的数据模型几乎都应该定义在dubbo-api的model包下,每个服务绝对私有的除外.