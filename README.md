# ![Holer](http://blog.wdom.net/upload/2019/11/v3sonj7kuogp1orspp1ek7t4jt.png)

# 1. Holer简介

Holer exposes local servers behind NATs and firewalls to the public internet over secure tunnels. <br/>
Support forwarding message based on TCP protocol.<br/><br/>
Holer是一个将局域网中的应用映射到公网访问的端口映射软件，支持转发基于TCP协议的报文。<br/>
![示意图](https://github.com/wisdom-projects/holer/blob/8d7794f500cfc2cc33702f92983d1674dab4917e/Image/demo.png)

# 2. Holer使用

Holer支持以下两种使用方式，根据实际需求，任选其中一种方式即可：

**方式一：** <br/>
使用**公开的holer映射**或者**开通holer服务**，通过**holer客户端软件**经**holer服务器**实现公网访问，详见**2.1节**。<br/>

**方式二：** <br/>
使用**holer服务端软件**搭建holer服务，通过**holer客户端软件**经**自己服务器**实现公网访问，详见**2.2节**。<br/>

Holer客户端软件有Java版本（详见**2.1.1节**）和Go版本（详见**2.1.2节**），根据偏好，任选其中一种版本使用即可。

如果下载holer软件遇到问题，更多的下载地址详见**3.2节**。

## 2.1. 使用公开的holer映射或者开通holer服务（方式一）

使用**公开的holer映射**或者**开通holer服务**，通过holer客户端软件经**holer服务器**实现公网访问。<br/>

**公开的holer映射**详情如下：

访问密钥                      |访问域名    |公网地址   |本地地址|使用场景
-----------------------------|-----------|-----------|-----------|--------
HOLER_CLIENT-2F8D8B78B3C2A0AE|holer65530.wdom.net|holer.org:65530|127.0.0.1:8080|WEB
HOLER_CLIENT-3C07CDFD1BF99BF2|holer65531.wdom.net|holer.org:65531|127.0.0.1:8088|WEB
HOLER_CLIENT-2A623FCB6E2A7D1D|holer65532.wdom.net|holer.org:65532|127.0.0.1:80|WEB
HOLER_CLIENT-AF3E6391525F70E4|N/A|holer.org:65533|127.0.0.1:3389|远程桌面
HOLER_CLIENT-822404317F9D8ADD|N/A|holer.org:65534|127.0.0.1:22|SSH
HOLER_CLIENT-27DD1389DF1D4DBC|N/A|holer.org:65535|127.0.0.1:3306|数据库

这里以映射本地Tomcat默认端口8080为例，选择表中的第一条映射进行配置；如果Web服务端的端口是80或者8088，请选择相匹配的端口映射，其他TCP端口映射步骤类似，更多的使用示例请参考[**官方文档**](http://blog.wdom.net)。

### 2.1.1. 使用Java版本的holer客户端实现步骤

Java版本的holer客户端软件（[源代码](https://github.com/wisdom-projects/holer/tree/master/SourceCode/Java)，[软件包](https://github.com/Wisdom-Projects/holer/tree/master/Binary/Java)）是由Java语言实现，支持跨平台。<br/>

#### 2.1.1.1. 安装 Java
安装Java 1.7或者更高版本，执行命令 `java -version` 检查Java是否可用。

#### 2.1.1.2. 安装Web服务端

以Tomcat为例，安装并启动Tomcat，默认安装的端口是8080；<br/>
在浏览器里输入如下URL来检查Tomcat服务是否可以正常访问：<br/>
`http://127.0.0.1:8080`

#### 2.1.1.3. 下载并配置holer客户端 
下载并解压软件包[`holer-client.zip`](https://github.com/Wisdom-Projects/holer/blob/master/Binary/Java)
修改配置文件：<br/>
`holer-client/conf/holer.conf`<br/>

设置`HOLER_ACCESS_KEY`如下：

`HOLER_ACCESS_KEY=HOLER_CLIENT-2F8D8B78B3C2A0AE`

#### 2.1.1.4. 启动holer

进入目录：<br/>
`cd holer-client/bin`<br/>

**Windows系统**:<br/>
执行命令 `startup.bat` 或者双击 `startup.bat`<br/>

**Linux系统**:<br/>
执行命令 `bash startup.sh`<br/>

然后就可以通过如下URL来访问Web应用：<br/>
`http://holer65530.wdom.net` 或者 `http://holer.org:65530` 

#### 2.1.1.5. 设置开机启动

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

根据提示输入**holer access key**和**holer server host** <br/>
输入示例：
```
------------------------------------------
Enter holer access key: HOLER_CLIENT-2F8D8B78B3C2A0AE
------------------------------------------
Enter holer server host: holer.org
------------------------------------------
```

### 2.1.2. 使用Go版本的holer客户端实现步骤
Go版本的holer客户端软件（[源代码](https://github.com/Wisdom-Projects/holer/tree/master/SourceCode/Go)，[软件包](https://github.com/Wisdom-Projects/holer/tree/master/Binary/Go)）是由GO语言实现，支持多种操作系统和硬件架构。<br/>

#### 2.1.2.1. 安装Web服务端

以Tomcat为例，安装并启动Tomcat，默认安装的端口是8080；<br/>
在浏览器里输入如下URL来检查Tomcat服务是否可以正常访问：<br/>
`http://127.0.0.1:8080`

#### 2.1.2.2. 下载holer客户端

根据实际的系统平台，选择匹配的软件包下载并解压[`holer-xxx.tar.gz`](https://github.com/Wisdom-Projects/holer/blob/master/Binary/Go)

#### 2.1.2.3. 启动holer

这里以`Windows & Linux x86-64bit` 为例，启动holer执行如下命令：<br/><br/>
**Windows系统**:<br/>
`holer-windows-amd64.exe -k HOLER_CLIENT-2F8D8B78B3C2A0AE -s holer.org`<br/>
也可以执行命令 `startup.bat` 或者双击 `startup.bat`

**Linux系统**:<br/>
`nohup ./holer-linux-amd64 -k HOLER_CLIENT-2F8D8B78B3C2A0AE -s holer.org &`<br/>
也可以执行命令 `bash startup.sh`<br/>
首次启动根据提示输入**holer access key**和**holer server host** <br/>
输入示例：
```
------------------------------------------
Enter holer access key: HOLER_CLIENT-2F8D8B78B3C2A0AE
------------------------------------------
Enter holer server host: holer.org
------------------------------------------
```

#### 2.1.2.4. 设置开机启动
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
根据提示输入**holer access key**和**holer server host** <br/>
输入示例：
```
------------------------------------------
Enter holer access key: HOLER_CLIENT-2F8D8B78B3C2A0AE
------------------------------------------
Enter holer server host: holer.org
------------------------------------------
```

## 2.2. 使用holer服务端软件搭建holer服务（方式二）

使用**holer服务端软件**搭建holer服务，通过holer客户端软件经**自己服务器**实现公网访问。<br/>
用户也可以下载 [**holer-server.zip**](https://github.com/wisdom-projects/holer/releases) 搭建自己的holer服务。<br/>
如果下载holer软件遇到问题，更多的下载地址详见**3.2节**。<br/>

![Holer Server](http://blog.wdom.net/upload/2019/04/pnlmngj08sh4eqv8fdb97oto0p.png)

### 2.2.1. 搭建holer服务端准备工作
(1) 准备一台可以经**公网IP访问**的Linux系统或者Windows系统主机；

(2) 安装**Java 1.8及以上版本**，执行命令 `java -version` 检查Java是否可用；

(3) 安装并启动Nginx, 建议安装其稳定版本；

(4) 安装MariaDB并**设置root用户密码**；

(5) 设置安全规则，允许访问holer服务端端口**6060、600**以及**端口映射规则**所涉及的端口；

(6) 建议申请域名并且完成域名备案，并设置域名**泛解析（*.域名）**和**直接解析主域名（@.域名）**，如果没有域名可以直接使用**IP和端口**访问。

### 2.2.2. 配置并启动holer服务端
解压软件包，打开配置文件 `holer-server/resources/application.yaml`

#### 2.2.2.1. 修改数据库用户名和密码

```
spring:
  datasource:
    username: root
    password: 123456
```

#### 2.2.2.2. 修改域名和Nginx主目录

```
holer
  domain:
    name: your-domain.com
  nginx:
    #home: /usr/local/nginx
    home: C:/nginx-1.14.2
```

将示例中的域名`your-domain.com`修改成自己**备案过**的域名，如果没有域名，请忽略该配置项。

Linux系统默认安装Nginx路径 `/usr/local/nginx` <br/>
Windows系统中可以先将Nginx复制到某个目录下，然后在配置文件中指定其主目录。<br/><br/>
**注意事项：** <br/>
请确保Nginx主目录下存在配置文件：`conf/nginx.conf` <br/>
Nginx目录结构示例：
```
Nginx主目录
├── conf
│   ├── nginx.conf
.   .
.   .
```

如果需要用到HTTPS功能，Window系统版本的Nginx默认支持HTTPS功能，Linux系统需要下载Nginx源码，配置和编译以及安装执行如下命令：
```
./configure --with-http_ssl_module
make;make install
```
#### 2.2.2.3. 启动holer服务端

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

#### 2.2.2.4. 设置开机启动

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

### 2.2.3. 创建端口映射

#### 2.2.3.1. 登录holer管理系统

如果配置文件`application.yaml`中设置了域名，并且指定了Nginx主目录，则在浏览器输入URL `http://holer.your-domain.com`
如果没有设置域名访问，则通过IP和端口登录系统 `http://IP地址:600`
![Holer Login](http://blog.wdom.net/upload/2019/04/oru7f1ojueilep57qkfkimrobf.png)

登录系统需要输入默认的管理员账号，默认用户名： `admin` 密码： `admin123`

用户也可以在文件`holer-server/resources/conf/holer-data.sql`中修改默认的用户名和密码，然后重启holer服务端使其生效。

#### 2.2.3.2. 创建客户端和端口映射

在用户列表页面中创建一个holer客户端<br/>
`http://holer.your-domain.com/view/holer-client.html`
![Holer Client](https://github.com/wisdom-projects/holer/blob/8d7794f500cfc2cc33702f92983d1674dab4917e/Image/holer-client.png)

在端口映射页面中为该holer客户端创建端口映射<br/>
`http://holer.your-domain.com/view/holer-port.html`
![Holer Port](https://github.com/wisdom-projects/holer/blob/8d7794f500cfc2cc33702f92983d1674dab4917e/Image/holer-port.png)

在数据统计页面中查看报表信息<br/>
`http://holer.your-domain.com/view/holer-report.html`
![Holer Report](https://github.com/wisdom-projects/holer/blob/8d7794f500cfc2cc33702f92983d1674dab4917e/Image/holer-report.png)

#### 2.2.3.3. 配置holer客户端使其与holer服务端实现端口映射功能

在用户列表页面中选中一条客户端记录，在页面右上角点击详情按钮，弹出的详情框下点击复制按钮；
![Holer Copy](http://blog.wdom.net/upload/2019/04/q7ffnsuu6ghf4p66chtb3001r3.png)

然后将详情信息粘贴到记事本里，请严格按照详情信息里的使用说明进行操作，这样可以顺利完成holer客户端配置，从而实现基于自己holer服务端的端口映射功能。

# 3. 支持与帮助

## 3.1. Holer使用示例
获得更多的holer使用示例，请参考[**官方文档**](http://blog.wdom.net)。

## 3.2. Holer下载
### 3.2.1. Holer客户端软件
[**软件地址一**](https://github.com/wisdom-projects/holer/tree/master/Binary)<br/>
[**软件地址二**](https://pan.baidu.com/s/1APDAaaaQxTa71IR2hDjIaA#list/path=%2Fsharelink2808252679-1014620033513253%2Fholer%2Fholer-client&parentPath=%2Fsharelink2808252679-1014620033513253)<br/>

### 3.2.2 Holer服务端软件
[**软件地址一**](https://github.com/wisdom-projects/holer/releases)<br/>
[**软件地址二**](https://pan.baidu.com/s/1APDAaaaQxTa71IR2hDjIaA#list/path=%2Fsharelink2808252679-1014620033513253%2Fholer%2Fholer-server&parentPath=%2Fsharelink2808252679-1014620033513253)<br/>

## 3.3. 申请holer服务
用户可以使用上述公开的holer映射，也可以申请holer服务；<br/>
如果需要**holer服务**，请联系 **QQ 2353941272 或微信 wangyudongdom** 开通。<br/>
Holer服务详情，请访问[**Wisdom**](http://www.wdom.net) <br/>
![Wisdom](http://blog.wdom.net/upload/2019/09/6pb9liclg4jbercad2d4hhnj3j.gif)

