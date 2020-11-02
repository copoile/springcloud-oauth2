# 认证信息

用户名：admin/123456，客户端：web/123456

数据库初始化sql在项目根目录下/auth_db.sql

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

# 数据库表

* 权限表

  | id          | 主键id   |
  | :---------- | -------- |
  | authority   | 权限     |
  | description | 权限描述 |

* 客户端表

  | id                             | 主键id                                   |
  | ------------------------------ | ---------------------------------------- |
  | client_id                      | 客户端id                                 |
  | client_secret                  | 客户端密钥                               |
  | scopes                         | scopes，多个以英文逗号分隔               |
  | resource_ids                   | 资源ids，以英文逗号分隔                  |
  | grant_types                    | grant_types，以英文逗号分隔              |
  | redirect_uris                  | 重定向uris，以英文逗号分隔               |
  | auto_approve_scopes            | 授权码模式自动审批scopes，以英文逗号分隔 |
  | access_token_validity_seconds  | accessToken有效秒数                      |
  | refresh_token_validity_seconds | refreshToken有效秒数                     |
  | authority_ids                  | 权限ids，以英文逗号分隔                  |

* 角色表

  | id          | 主键id   |
  | ----------- | -------- |
  | name        | 角色名称 |
  | description | 描述     |

* 用户表

  | id        | 主键id               |
  | --------- | -------------------- |
  | username  | 用户名               |
  | password  | 密码                 |
  | enable    | 是否启用，1:是，0:否 |
  | phone     | 手机号               |
  | nick_name | 昵称                 |
  | avatar    | 头像                 |

* 用户角色关联表

  | id          | 主键id |
  | ----------- | ------ |
  | sys_user_id | 用户id |
  | sys_role_id | 角色id |

* 角色权限关联表

  | 主键id | id               |
  | ------ | ---------------- |
  | 角色id | sys_role_id      |
  | 权限id | sys_authority_id |

  

# 测试样例

* 获取token成功

  ```json
  {
      "code": 0,
      "message": "成功",
      "data": {
          "token": "f16bfe40-c5d2-4a47-a4c4-8439db05adb0",
          "tokenType": "bearer",
          "refreshToken": "380a2751-d419-484d-a964-0b11b64ba209",
          "scope": [
              "server"
          ],
          "expire": 7199
      }
  }
  ```

* 客户端信息不正确

  ```json
  {
      "code": 1011,
      "message": "无效客户端"
  }
  ```

* 用户名或密码错误

  ```json
  {
      "code": 1001,
      "message": "用户名或密码错误"
  }
  ```

* tokn过期或无效

  ```json
  {
      "code": 1009,
      "message": "凭证无效或已过期"
  }
  ```

* 不允许访问

  ```json
  {
      "code": 1007,
      "message": "不允许访问"
  }
  ```

* 授权码模式获取code：localhost:8001/oauth/authorize?response_type=code&scope=server&client_id=web&rediect_url=http://www.baidu.com&state=0583

* 简化模式获取token：localhost:8001/oauth/authorize?response_type=token&scope=server&client_id=web&rediect_url=http://www.baidu.com&state=0583

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
