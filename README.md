staffcenter-service
==============

### 环境配置

* JDK 1.8+  [下载](http://www.oracle.com/technetwork/cn/java/javase/downloads/jdk8-downloads-2133151-zhs.html)       

* Apache Maven 3.5+  [下载](https://maven.apache.org/download.cgi)       
 
 1. 打开maven配置文件```./apache-maven-3.5.x/conf/settings.xml```
 
 2. 找到```<mirrors></mirrors>```标签节点,添加一个的mirror子节点
```
      <mirror>
          <id>nexus-aliyun</id>
          <mirrorOf>*</mirrorOf>
          <name>Nexus aliyun</name>
          <url>http://maven.aliyun.com/nexus/content/groups/public</url>
      </mirror>
 ```


### 目录结构

``` 
staffcenter-service
├── common -- 项目工具包
├── entity -- 数据实体
├── service -- 员工中心服务提供者
├── api -- 员工中心服务消费者
├── task -- 员工中心任务


### 项目编译

```
 依次编译 common, entity, service, api|task 到maven中央仓库
```




### 运行项目

 ##### (1)启动staffcenter服务提供者
 1. 在IDE里找service子工程com.juyoufuli.service.ServiceApplication.java文件并打开，右键点击 Debug As -> Java Application 启动服务
 
 2. 打开浏览器访问：[http://192.168.10.204:8501/](http://192.168.10.204:8501/),如下所示：
 ![Image text](https://image.lexiangla.com/company_0b4b301eff1811e7ad4d5254005b9a60/assets/2018/07/f1af0702-88ba-11e8-9dab-525400b865c4.png-resize1920?sign=/lVbTDTUdvR6tiJq0kV6K3awAtphPTEwMDI5MTYyJms9QUtJRE4yMmgydDZqV0pscEtMYWdsVVRSaWx6czFycjZvYWZ2JmU9MTUzMjMyNDUxNCZ0PTE1MzE3MTk3MTQmcj02MDc3ODg3MyZmPS9jb21wYW55XzBiNGIzMDFlZmYxODExZTdhZDRkNTI1NDAwNWI5YTYwL2Fzc2V0cy8yMDE4LzA3L2YxYWYwNzAyLTg4YmEtMTFlOC05ZGFiLTUyNTQwMGI4NjVjNC5wbmcmYj1sZXhpYW5n)
  
  ##### (2)启动api服务消费者
  1. 在IDE里找service子工程com.juyoufuli.api.ApiApplication.java文件并打开，右键点击 Debug As -> Java Application 启动服务
  
  2. 打开浏览器访问：[http://192.168.10.204:8501/](http://192.168.10.204:8501/),如下所示：
 ![Image text](https://image.lexiangla.com/company_0b4b301eff1811e7ad4d5254005b9a60/assets/2018/07/e9704b64-88ba-11e8-a075-525400bab841.png-resize1920?sign=D6pGszlLRUnWTuyTmpsZMczQzgFhPTEwMDI5MTYyJms9QUtJRE4yMmgydDZqV0pscEtMYWdsVVRSaWx6czFycjZvYWZ2JmU9MTUzMjMyNDUwMCZ0PTE1MzE3MTk3MDAmcj0xMjcyNzYyODA2JmY9L2NvbXBhbnlfMGI0YjMwMWVmZjE4MTFlN2FkNGQ1MjU0MDA1YjlhNjAvYXNzZXRzLzIwMTgvMDcvZTk3MDRiNjQtODhiYS0xMWU4LWEwNzUtNTI1NDAwYmFiODQxLnBuZyZiPWxleGlhbmc=)
  
### 规范约定

```

- service类，需要在叫名`service`的包下，并以`Service`结尾，如`TestServiceImpl`

- controller类，需要在以`controller`结尾的包下，类名以Controller结尾，如`TestController.java`，并继承`BaseController`

- task类，需要在叫名`task`的包下，并以`Task`结尾，如`TestTask.java`

- configuration类，需要在叫名`autoconfigure`的包下，并以`Configuration`结尾，如`TestConfiguration.java`

- mapper.xml，需要在名叫`mapper`的包下，并以`Mapper.xml`结尾，如`TestMapper.xml`

- mapper接口，需要在名叫`mapper`的包下，并以`Mapper`结尾，如`TestMapper.java`

- model实体类，需要在名叫`model`的包下，命名规则为数据表转驼峰规则，如`Test.java`

- 类名：首字母大写驼峰规则；方法名：首字母小写驼峰规则；常量：全大写；变量：首字母小写驼峰规则，尽量非缩写

- 配置文件放到`src/main/resources`目录下

- 静态资源文件放到`src/main/webapp/static`目录下

- jsp文件，需要在`/WEB-INF/views`目录下

- 更多规范，参考[[阿里巴巴Java开发手册] https://github.com/alibaba/p3c/blob/master/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8C%EF%BC%88%E7%BA%AA%E5%BF%B5%E7%89%88%EF%BC%89.pdf

```

