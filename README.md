# 分支信息

用户名：admin/123456，客户端：web/123456

# 功能

```
- 密码模式
- 自定义手机验证码模式
- 授权码模式
- 简化模式
- 刷token模式
- 退出测试接口
- 简单授权页面
- 不需要accessToken测试接口
- 需要accessToken测试接口
- 需要特定权限测试接口
- scope测试接口
- client权限测试接口
- 统一Api封装
- 统一异常处理
- 数据库建表及表数据查询
```

## 开发环境

- **JDK 1.8 +**
- **Maven 3.5 +**
- **IntelliJ IDEA ULTIMATE 2018.2 +** (*注意：建议使用 IDEA 开发，同时保证安装 `lombok` 插件，如果是eclipse也要确保安装了`lombok` 插件*)
- **Redis 3.0 +**
- **Mysql 5.7 +**

## 运行方式

1. `git clone https://https://github.com/copoile/springcloud-oauth2.git`
2. 使用 IDEA 打开 clone 下来的项目
3. 项目启动顺序: eureka-server > auth-server > resource-server
> 注意：auth-server依赖redis和mysql服务，记得先启动服务哦~

# License

[MIT](./LICENSE)

Copyright (c) 2019-present Yaohw
