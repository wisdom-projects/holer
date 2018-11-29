# What is holer
Holer exposes local servers behind NATs and firewalls to the public internet over secure tunnels. <br/>
Support forwarding message based on TCP protocol.<br/><br/>
Holer是一个将局域网内的应用映射到公网上访问的端口映射工具，支持转发基于TCP协议的报文。
![Demo](https://github.com/Wisdom-Projects/holer/blob/master/Image/demo.png)
# How it works

#### 1. Install `Java 1.7` or higher version;
使用前请先安装`Java 1.7`或者更高版本；

#### 2. Install web server, take `tomcat` as an example;
安装Web服务端，以tomcat为例；<br/><br/>
Install and start tomcat<br/>
安装并启动tomcat<br/><br/>
Input the following URL in browser to check if tomcat service is accessible :<br/>
在浏览器里输入如下URL来检查tomcat服务是否可以正常访问：<br/>
`http://127.0.0.1:8080`

#### 3. Download and unzip software [*`holer-client.zip`*](https://github.com/Wisdom-Projects/holer/blob/master/Binary/Java), modify configuration file:
下载并解压软件包[`holer-client.zip`](https://github.com/Wisdom-Projects/holer/blob/master/Binary/Java)，修改配置文件：<br/>
`holer-client/conf/holer.conf`<br/><br/>
Only need to set `HOLER_ACCESS_KEY` as follows:<br/>
只需设置`HOLER_ACCESS_KEY`如下：

`HOLER_ACCESS_KEY=HOLER_CLIENT-2F8D8B78B3C2A0AE`<br/>

#### 4. Start holer
启动Holer服务<br/>
`cd holer-client/bin`<br/><br/>
**Windows**:<br/>
Run command `startup.bat` or double click `startup.bat`<br/><br/>
**Linux**:<br/>
Run command `sh startup.sh`

Then visit web application through the following URL:<br/>
然后就可以通过如下URL来访问Web应用：<br/><br/>
 `http://holer.org:65530` or `http://holer65530.wdom.net`

#### 5. Internet and local address mapping
公网和内网的地址映射关系

Holer Access Key             |Domain Name|Internet Address|Local Address
-----------------------------|-----------|----------------|---------------
HOLER_CLIENT-2F8D8B78B3C2A0AE|holer65530.wdom.net|holer.org:65530|127.0.0.1:8080
HOLER_CLIENT-3C07CDFD1BF99BF2|holer65531.wdom.net|holer.org:65531|127.0.0.1:8088
HOLER_CLIENT-2A623FCB6E2A7D1D|holer65532.wdom.net|holer.org:65532|127.0.0.1:80
HOLER_CLIENT-AF3E6391525F70E4|N/A|holer.org:65533|127.0.0.1:3389
HOLER_CLIENT-822404317F9D8ADD|N/A|holer.org:65534|127.0.0.1:22
HOLER_CLIENT-27DD1389DF1D4DBC|N/A|holer.org:65535|127.0.0.1:3306

If your tomcat port is 80 or 8088, please select the matched key to configure.<br/>
如果您本地的tomcat端口是80或者8088，请选择匹配的key进行设置。<br/>

Please refer to [**the blogs**](http://blog.wdom.net/tag/Holer) for more demos and help.<br/>
请参考[**博客文章**](http://blog.wdom.net/tag/Holer)获得更多的使用示例和帮助。<br/>

#### 6. Other Holer Softwares
其他的holer软件<br/><br/>
These holer softwares ([source code](https://github.com/Wisdom-Projects/holer/tree/master/SourceCode/Go)，[package](https://github.com/Wisdom-Projects/holer/tree/master/Binary/Go)) are implemented by GO, and support many different OS and hardware architectures. <br/>
这些holer软件（[源代码](https://github.com/Wisdom-Projects/holer/tree/master/SourceCode/Go)，[软件包](https://github.com/Wisdom-Projects/holer/tree/master/Binary/Go)）是由GO语言实现，支持多种操作系统和硬件架构。<br/><br/>
Take `Windows & Linux x86-64bit` as an example, execute the following commands:<br/>
这里以`Windows & Linux x86-64bit` 为例，执行如下命令：<br/><br/>
**Windows**:<br/>
`holer-windows-amd64.exe -k HOLER_CLIENT-2F8D8B78B3C2A0AE`<br/><br/>
**Linux**:<br/>
`nohup ./holer-linux-amd64 -k HOLER_CLIENT-2F8D8B78B3C2A0AE`<br/>

# Support
All of the above holer access keys and ports have been shared to public. Users can also apply for exclusive holer services.
If you want to have **exclusive holer services**, please contact by QQ for application. For more details about holer services, please visit [**Wisdom**](http://www.wdom.net).<br/><br/>
以上的全部key和端口都已公开共享。用户也可以申请专属的holer服务，如果您需要**专属的holer服务**，请QQ联系开通。Holer服务详情，请访问[**Wisdom**](http://www.wdom.net)。<br/><br/>
_**QQ**    : 2353941272_<br/>

# Donate
如果 **holer** 工具对您帮助很大，并且您很愿意支持工具的后续开发和维护，您可以扫下方二维码随意打赏，就当是请我喝杯茶或是咖啡，将不胜感激。 **♥ 谢谢 ♥**

If **holer** helps you a lot, and you would like to support this tool's further development and the continuous maintenance of this tool. You can sweep the following QR code free to donate me, which asked me to have a cup of tea or coffee. Your donation is highly appreciated. **♥ Thank you ♥** <br/>

**♥ Donate ♥ by Alipay, WeChat Pay**.

![Donate by pay](https://github.com/Wisdom-Projects/rest-client/blob/master/images/donate_pay.png)
