# What is holer
Holer exposes local servers behind NATs and firewalls to the public internet over secure tunnels. <br/>
Support forwarding message based on TCP protocol.<br/><br/>
Holer是一个将局域网中的应用映射到公网访问的端口映射软件，支持转发基于TCP协议的报文。
![Demo](https://github.com/Wisdom-Projects/holer/blob/master/Image/demo.png)
# How it works

## 1. Holer使用

### 1.1. 安装 Java
安装Java 1.7或者更高版本；
执行命令 `java -version` 检查Java是否可用。

### 1.2. 安装Web服务端

以Tomcat为例，安装并启动Tomcat<br/><br/>
在浏览器里输入如下URL来检查Tomcat服务是否可以正常访问：<br/>
`http://127.0.0.1:8080`

### 1.3. 配置Holer 
下载并解压软件包[`holer-client.zip`](https://github.com/Wisdom-Projects/holer/blob/master/Binary/Java)

修改配置文件：<br/>
`holer-client/conf/holer.conf`<br/>

设置`HOLER_ACCESS_KEY`如下：

`HOLER_ACCESS_KEY=HOLER_CLIENT-2F8D8B78B3C2A0AE`

### 1.4. 启动Holer

进入目录：<br/>
`cd holer-client/bin`<br/>

**Windows系统**:<br/>
执行命令 `startup.bat` 或者双击 `startup.bat`<br/>

**Linux系统**:<br/>
执行命令 `bash startup.sh`<br/>

然后就可以通过如下URL来访问Web应用：<br/>
`http://holer65530.wdom.net` 或者 `http://holer.org:65530` 

### 1.5. 设置开机启动

进入目录：<br/>
`cd holer-client/bin`<br/>

**Windows系统**:<br/>
双击 `setup.vbs` <br/>
**注意事项：** <br/>
请确保当前用户对如下目录具有读取、写入、执行、修改等权限：<br/>
`C:\ProgramData\Microsoft\Windows\Start Menu\Programs\StartUp `<br/>

**Linux系统**:<br/>
执行命令 `bash setup.sh`<br/>
**注意事项：** <br/>
**CentOS 7, RedHat 7, Ubuntu 18** 及更高版本，建议执行命令`bash setup-service.sh`设置开机启动；<br/>

根据提示输入**holer access key**和**holer server host**

### 1.6. 公网和内网的地址映射关系

Holer Access Key             |Domain Name|Internet Address|Local Address
-----------------------------|-----------|----------------|---------------
HOLER_CLIENT-2F8D8B78B3C2A0AE|holer65530.wdom.net|holer.org:65530|127.0.0.1:8080
HOLER_CLIENT-3C07CDFD1BF99BF2|holer65531.wdom.net|holer.org:65531|127.0.0.1:8088
HOLER_CLIENT-2A623FCB6E2A7D1D|holer65532.wdom.net|holer.org:65532|127.0.0.1:80
HOLER_CLIENT-AF3E6391525F70E4|N/A|holer.org:65533|127.0.0.1:3389
HOLER_CLIENT-822404317F9D8ADD|N/A|holer.org:65534|127.0.0.1:22
HOLER_CLIENT-27DD1389DF1D4DBC|N/A|holer.org:65535|127.0.0.1:3306

如果您本地的Tomcat端口是80或者8088，请选择匹配的key进行设置。<br/>

请参考[**博客文章**](http://blog.wdom.net/tag/Holer)获得更多的使用示例和帮助。<br/>

### 1.7. Go版本的Holer客户端

Go版本的Holer客户端软件（[源代码](https://github.com/Wisdom-Projects/holer/tree/master/SourceCode/Go)，[软件包](https://github.com/Wisdom-Projects/holer/tree/master/Binary/Go)）是由GO语言实现，支持多种操作系统和硬件架构。<br/>

#### 1.7.1. 启动Holer

这里以`Windows & Linux x86-64bit` 为例，启动Holer执行如下命令：<br/><br/>
**Windows系统**:<br/>
`holer-windows-amd64.exe -k HOLER_CLIENT-2F8D8B78B3C2A0AE`<br/>
也可以执行命令 `startup.bat` 或者双击 `startup.bat`

**Linux系统**:<br/>
`nohup ./holer-linux-amd64 -k HOLER_CLIENT-2F8D8B78B3C2A0AE &`<br/>
也可以执行命令 `bash startup.sh`<br/>
首次启动根据提示输入**holer access key**和**holer server host**

#### 1.7.2. 设置开机启动
进入目录：<br/>
`cd holer-client/bin`<br/>

**Windows系统**:<br/>
双击 `setup.vbs` <br/>
**注意事项：** <br/>
请确保当前用户对如下目录具有读取、写入、执行、修改等权限：<br/>
`C:\ProgramData\Microsoft\Windows\Start Menu\Programs\StartUp `<br/>

**Linux系统**:<br/>
执行命令 `bash setup.sh`<br/>
**注意事项：** <br/>
**CentOS 7, RedHat 7, Ubuntu 18** 及更高版本，建议执行命令`bash setup-service.sh`设置开机启动；<br/>
根据提示输入**holer access key**和**holer server host**

## 2. Holer服务端软件使用

用户也可以下载 [**holer-server.zip**](https://github.com/wisdom-projects/holer/releases) 搭建自己的Holer服务端。

### 2.1. 搭建Holer服务端准备工作
(1) 准备一台Linux系统或者Windows系统主机；

(2) 安装Java 1.8及以上版本，执行命令 `java -version` 检查Java是否可用；

(3) 安装并启动Nginx, 建议安装其稳定版本；

(4) 安装MariaDB并设置root用户密码；

(5) 设置安全规则，允许访问Holer服务端端口6060、600以及端口映射规则所涉及的端口；

(6) 建议申请域名并且完成域名备案，如果没有域名可以直接使用IP和端口访问。

### 2.2. 配置并启动Holer服务端
解压软件包，打开配置文件 `holer-server/resources/application.yaml`

#### 2.2.1. 修改数据库用户名和密码

```
spring:
  datasource:
    username: root
    password: 123456
```

#### 2.2.2. 修改域名和Nginx主目录

```
holer
  domain:
    name: your-domain.com
  nginx:
    #home: /usr/local/nginx
    home: C:/nginx-1.14.2
```
Linux系统默认安装Nginx路径 `/usr/local/nginx`
Windows系统中可以先将Nginx复制到某个目录下，然后在配置文件中指定其主目录。

如果需要用到HTTPS功能，Window系统版本的Nginx默认支持HTTPS功能，Linux系统需要下载Nginx源码，配置和编译以及安装执行如下命令：
```
./configure --with-http_ssl_module
make;make install
```
#### 2.2.3. 启动Holer服务端

Linux系统执行启动命令如下：
```
cd holer-server
chmod 755 holer
bash holer start
```
Windows系统执行启动命令如下：
```
cd holer-server
startup.bat
```
或者双击 `startup.bat`

#### 2.2.4. 设置开机启动

进入目录：<br/>
`cd holer-server/bin`<br/>

**Windows系统**:<br/>
双击 `setup.vbs` <br/>
**注意事项：** <br/>
请确保当前用户对如下目录具有读取、写入、执行、修改等权限：<br/>
`C:\ProgramData\Microsoft\Windows\Start Menu\Programs\StartUp `<br/>

**Linux系统**:<br/>
执行命令 `bash setup.sh`<br/>
**注意事项：** <br/>
**CentOS 7, RedHat 7, Ubuntu 18** 及更高版本，建议执行命令`bash setup-service.sh`设置开机启动。<br/>

### 2.3. 创建端口映射

#### 2.3.1. 登录Holer管理系统

如果配置文件`application.yaml`中设置了域名，并且指定了Nginx主目录，则在浏览器输入URL `http://holer.your-domain.com`
如果没有设置域名访问，则通过IP和端口登录系统 `http://IP地址:600`
![Holer Login](http://blog.wdom.net/upload/2019/04/oru7f1ojueilep57qkfkimrobf.png)

登录系统需要输入默认的管理员账号，默认用户名： `admin` 密码： `admin123`

用户也可以在文件`holer-server/resources/conf/holer-data.sql`中修改默认的用户名和密码，然后重启Holer服务端使其生效。

#### 2.3.2. 创建客户端和端口映射

在用户列表页面中创建一个Holer客户端<br/>
`http://holer.your-domain.com/view/holer-client.html`
![Holer Client](http://blog.wdom.net/upload/2019/04/1he44jumd2g9no95c6f8fsa5re.png)

在端口映射页面中为该Holer客户端创建端口映射<br/>
`http://holer.your-domain.com/view/holer-port.html`
![Holer Port](http://blog.wdom.net/upload/2019/04/0s78i863v4h6tr1vfdg3eo3trv.png)

在数据统计页面中查看报表信息<br/>
`http://holer.your-domain.com/view/holer-report.html`
![Holer Report](http://blog.wdom.net/upload/2019/04/5atk5j8ii2gl1rqfl3l6672sdq.png)

#### 2.3.3. 配置Holer客户端使其与Holer服务端实现端口映射功能

在用户列表页面中选中一条客户端记录，在页面右上角点击详情按钮，弹出的详情框下点击复制按钮；
![Holer Copy](http://blog.wdom.net/upload/2019/04/q7ffnsuu6ghf4p66chtb3001r3.png)

然后将详情信息粘贴到记事本里，严格按照详情信息里的使用说明进行操作，这样即可完成Holer客户端配置，从而实现基于自己Holer服务端的端口映射功能。

# Support
All of the above holer access keys and ports have been shared to public. Users can also apply for exclusive holer services.
If you want to have **exclusive holer services**, please contact by QQ for application. For more details about holer services, please visit [**Wisdom**](http://www.wdom.net).<br/><br/>
以上的全部key和端口都已公开共享。用户也可以申请holer服务，如果您需要**holer服务**，请QQ联系开通。Holer服务详情，请访问[**Wisdom**](http://www.wdom.net)。<br/><br/>
_**QQ**    : 2353941272_<br/>

