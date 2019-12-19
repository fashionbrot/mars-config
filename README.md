# mars-config

#### 介绍
spring mvc 、springboot 动态配置系统。http 轮询方式 更新 动态配置

##### 登录页
![Image text](https://github.com/fashionbrot/mars-config/blob/master/document/login.png)

##### 首页
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/111.png)

##### 用户管理页
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/userInfo.png)
##### 用户编辑页
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/userInfoEdit.png)

##### 应用列表页
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/appInfo.png)

##### 环境列表页
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/envInfo.png)

##### 权限列表页
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/roleInfo.png)

##### 权限编辑窗口
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/roleInfoSystemConfig.png)

##### 配置列表页
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/systemConfig.png)

##### 配置编辑窗口
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/systemConfigEdit.png)

##### 配置历史记录
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/systemConfigHistory.png)

##### 配置历史回滚页
![Image](https://github.com/fashionbrot/mars-config/blob/master/document/historyDiff.png)

#### 软件架构
软件架构说明

##### 后端使用技术 ：springboot mybatis jwt
##### 前端使用：thymeleaf 模板引擎
##### 数据库 ：mysql
##### jdk   ：1.8


#### 安装教程

1. 先创建数据库mars_db ,导入 sql目录下的 init.sql 文件
2. mars-console 后端管理页面，直接使用外置tomcat 启动即可 端口默认：8080
3. mars-spring-config 发布jar 后依赖当前jar 即可使用
4. 访问地址：ip + port  账户：mars 密码：mars  权限：超级管理员

#### 使用说明

|配置参数|配置说明|是否必填|
|---|---|---|
|mars.config.app-id|应用名称|必填|
|mars.config.env-code|环境code|必填|
|mars.config.http.server-address|server地址多个逗号分隔|必填|
|mars.config.enable-local-cache|是否开启本地缓存默认false|否|
|mars.config.local-cache-path|本地缓存路径(默认user.home)|否|
|mars.config.enable-error-log|是否开启http轮询访问日志|否|

```xml
<dependency>
    <groupId>com.github.fashionbrot</groupId>
    <artifactId>mars-spring-config</artifactId>
    <version>0.1.1</version>
</dependency>

```

1. 在启动类上使用 @EnableMarsConfig 开启功能使用

    
2. 在后端管理操作
    ##### 1、应用环境管理 菜单 创建 应用、环境
    ##### 2、配置管理  菜单 创建配置 点击发布，依赖 mars-spring-config 就会收到服务端修改内容

3. springboot 具体使用步骤 

#### 1、在启动类上添加 @EnableMarsConfig 开启mars 动态配置
```java
import EnableMarsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableMarsConfig
public class Main  extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```


#### 2、通过 @MarsValue 获取动态配置的值 如同spring @Value 功能 autoRefreshed 表示是否自动更新当前值

```java

    @MarsValue(value = "${abc}",autoRefreshed = true)
    private String abc;

``` 

#### 3、通过@MarsConfigurationProperties 注解把对应配置映射到 TestConfig 类中，如springboot @ConfigurationProperties 功能相似
####    @MarsProperty 读取配置key  @MarsIgnoreField忽略abc字段配置

```java

import MarsConfigurationProperties;
import MarsIgnoreField;
import MarsProperty;
import lombok.Data;


@Data
@MarsConfigurationProperties(fileName = "aaa",autoRefreshed = true)
public class TestConfig {

    @MarsProperty("abc")
    public String name ;

    @MarsIgnoreField
    private String abc;
}


```

#### 4、通过 @MarsConfigListener 监听文件变化，可根据需要使用
```java

    @MarsConfigListener(fileName = "aaa",type = ConfigTypeEnum.TEXT)
    public void marsConfigListenerTest(String context){
        System.out.print(context);
    }

    @MarsConfigListener(fileName = "aaa",type = ConfigTypeEnum.PROPERTIES)
    public void marsConfigListenerProperties(Properties properties){
        System.out.print(properties.toString());
    }

```

#### 5、在配置文件中添加以下配置
```properties

mars.config.app-id=app
mars.config.env-code=betaXX
mars.config.http.server-address=192.168.0.108:8080

```

#### 6、可通过 mars-test项目中的 springboot-test 参考使用demo 
#### 7、如有问题请通过 mars-issue 提出 告诉我们。我们非常认真地对待错误和缺陷，在产品面前没有不重要的问题。不过在创建错误报告之前，请检查是否存在报告相同问题的issues。


### 如有问题请联系官方qq群：52842583
