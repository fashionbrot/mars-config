# mars-config

#### 介绍
spring mvc 、springboot 动态配置系统。http 轮训方式 更新 动态配置

#### 软件架构
软件架构说明

##### 后端使用技术 ：springboot mybatis jwt
##### 前端使用：thymeleaf 模板引擎
##### 数据库 ：mysql


#### 安装教程

1. mars-console 后端管理页面，直接使用外置tomcat 启动即可
2. mars-spring-config 发布jar 后依赖当前jar 即可使用
3. 访问地址：ip + port  账户：mars 密码：mars  权限：超级管理员

#### 使用说明

1. 在启动类上使用 @EnableMarsConfig 开启功能使用
    ##### 1、mars.config.app-id 服务名 和 后端appName 一致
    ##### 2、mars.config.env-code  环境Code 和 后端envCode 一致
    ##### 3、mars.config.http.server-address 服务Ip，多个服务地址已逗号分隔
    
2. 其他配置默认

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


#### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)