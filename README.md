

#### 介绍 mars系统配置  


#### 软件架构及软件架构说明
1.  后端使用技术 ：springboot mybatis jwt
2.  前端使用：thymeleaf 模板引擎
3.  数据库 ：mysql
4.   jdk   ：1.8
5. 【客户端】 使用框架spring、springboot、springcloud 系统

#### 安装教程
1.  先创建数据库mars_db ,导入 sql目录下的 init.sql 文件
2.  修改 mars-console-2.0.0.jar 包中的配置文件 \BOOT-INF\classes\application.properties 中的mysql 配置，及端口，使用命令 java -jar mars-console-2.0.0.jar 启动
3.  访问地址：ip + port  账户：mars 密码：mars  权限：超级管理员
4.  mars-console-2.0.0.jar 可以配置化集群模式
 
```properties
#【后端系统】集群配置
mars.cluster.address=ip:port,ip2:port,ip3:port
#【后端系统】同步其他服务器重试次数（默认3）
mars.cluster.sync.retry=3
```

#### 使用说明
#### 1、【客户端】 引入jar 包, 分为spring、springboot|springcloud 各自支持的jar
```xml
<!-- spring 引入 -->
<dependency>
    <groupId>com.github.fashionbrot</groupId>
    <artifactId>mars-spring-config</artifactId>
    <version>2.0.0</version>
</dependency>

<!-- springboot or springcloud 引入 -->
<dependency>
    <groupId>com.github.fashionbrot</groupId>
    <artifactId>mars-config-springboot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
```

#### 2、 【客户端】properties 配置

|配置参数|配置说明|是否必填|
|---|---|---|
|mars.config.app-id|应用名称|必填|
|mars.config.env-code|环境code|必填|
|mars.config.server-address|server地址多个逗号分隔|必填|
|mars.config.listen-long-poll-ms|客户端轮训毫秒数(默认30000)|否|
|mars.config.enable-local-cache|是否开启本地缓存默认false|否|
|mars.config.local-cache-path|本地缓存路径(默认user.home)|否|
|mars.config.enable-error-log|是否开启http轮询访问日志|否|

#### 3、【客户端】spring 需要@EnableMarsConfig注解启动（springboot|springcloud 不需要） 
```java
package com.github.fashionbrot.springboot.config;
import com.github.fashionbrot.spring.config.annotation.EnableMarsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableMarsConfig
public class SpringConfig {
}
```

#### 4、使用系统配置
###### (1)、【后端系统】应用环境管理 菜单 创建 应用、环境
###### (2)、【后端系统】配置管理 菜单 创建配置 点击发布，依赖 mars-spring-config 就会收到服务端修改内容
###### (3)、【客户端】通过 @MarsValue 获取动态配置的值 如同spring @Value 功能 autoRefreshed 表示是否自动更新当前值
```java
    @MarsValue(value = "${abc}",autoRefreshed = true)
    private String abc;
``` 
###### (4)、【客户端】通过@MarsConfigurationProperties 注解把对应配置映射到 TestConfig 类中 <br/> 如springboot @ConfigurationProperties 功能相似 @MarsProperty 读取配置key  @MarsIgnoreField忽略abc字段配置
```java
import MarsConfigurationProperties;
import MarsIgnoreField;
import MarsProperty;
import lombok.Data;
@Data
//aaa 对应【后端系统】里面的 文件名称
@MarsConfigurationProperties(fileName = "aaa",autoRefreshed = true)
public class TestConfig {
    //修改字段的绑定名称
    @MarsProperty("abc")
    public String name ;
    //忽略字段的绑定
    @MarsIgnoreField
    private String abc;
}
```
###### (5)、通过 @MarsConfigListener 监听文件变化，可根据需要使用
```java
    @MarsConfigListener(fileName = "aaa")
    public void marsConfigListenerTest(String context){
        System.out.print(context);
    }
    @MarsConfigListener(fileName = "aaa")
    public void marsConfigListenerProperties(Properties properties){
        System.out.print(properties.toString());
    }
```


### mars 数据库配置系统 介绍

####  [软件架构及软件架构说明](#软件架构及软件架构说明)如上
####  [安装教程](#安装教程)如上

#### 使用说明
#### 1、【客户端】 引入jar 包, 分为spring、springboot|springcloud 各自支持的jar
```xml
<!-- spring 引入 -->
<dependency>
    <groupId>com.github.fashionbrot</groupId>
    <artifactId>mars-spring-value</artifactId>
    <version>0.1.0</version>
</dependency>

<!-- springboot or springcloud 引入 -->
<dependency>
    <groupId>com.github.fashionbrot</groupId>
    <artifactId>mars-value-springboot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```
#### 2、 【客户端】properties 配置

|配置参数|配置说明|是否必填|
|---|---|---|
|mars.value.app-id|应用名称|必填|
|mars.value.env-code|环境code|必填|
|mars.value.server-address|server地址多个逗号分隔|必填|
|mars.value.listen-long-poll-ms|客户端轮训毫秒数（默认10000）|否|
|mars.value.enable-listen-log|轮训日志是否开启|否|

#### 3、【客户端】spring 需要@EnableMarsValue注解启动（springboot|springcloud 不需要） 
```java
package com.github.fashionbrot.springboot.config;
import com.github.fashionbrot.value.config.annotation.EnableMarsValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableMarsValue
public class SpringConfigValue {
    
}
```


#### 4、使用系统配置
###### (1)、【后端系统】应用环境管理 菜单 创建 应用、环境
###### (2)、【后端系统】模板管理 创建模板(好比创建一个java类)、创建好模板在创建模板属性（如java类的 属性）
###### (3)、【后端系统】配置数据管理  新建就可以创建一条数据
###### (4)、【客户端】可配置获取的数据持久化到客户端的Class 类型
```java
//需要继承 MarsTemplateKeyMapping 类实现  initTemplateKeyClass 方法
public class ConfigValueController extends  MarsTemplateKeyMapping{
        
        //系统启动加载方法
        public Map<String,Class> initTemplateKeyClass() {
            Map<String,Class> map=new HashMap<>();
            //test 代表模板key
            //TestModel.class 代表test 模板数据的class 类型
            map.put("test", TestModel.class);
            return map;
        }
}
```
###### (5)【客户端】客户端获取 数据2中方式
```java

    @RequestMapping("get")
    @ResponseBody
    public Object test(String templateKey){
        
        //通过 MarsConfigValueCache.getDeepTemplateObject 方式获取， templatekey 是模块key
        //getDeepTemplateObject 方法是深度copy，不会污染上层数据
        List<TestModel> list = MarsConfigValueCache.getDeepTemplateObject(templateKey);
        if (CollectionUtil.isNotEmpty(list)){
            for(TestModel t: list){
                t.setTest(t.getTest()+"你好");
            }
        }
        return list;
    }

    @RequestMapping("get3")
    @ResponseBody
    public List test3(String templateKey){

        //通过 MarsConfigValueCache.getTemplateObject 方式获取， templatekey 是模块key
        //getTemplateObject 浅copy，会污染上层数据
        List list = MarsConfigValueCache.getTemplateObject(templateKey);
        return list;
    }


```



#### 可通过 mars-test项目中的 springboot-test 参考使用demo 
#### 如有问题请通过 mars-issue 提出 告诉我们。我们非常认真地对待错误和缺陷，在产品面前没有不重要的问题。不过在创建错误报告之前，请检查是否存在报告相同问题的issues。
#### 如有问题请联系官方qq群：52842583
