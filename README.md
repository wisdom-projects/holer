# What is holer
Holer exposes local servers behind NATs and firewalls to the public internet over secure tunnels. <br/>
Support forwarding message based on TCP protocol.<br/><br/>
Holer是一个将局域网服务器代理到公网的内网穿透工具，支持转发基于TCP协议的报文。
![Demo](https://github.com/Wisdom-Projects/holer/blob/master/Image/demo.png)
# How it works
#### 1. Download software package [*`holer-client.zip`*](https://github.com/Wisdom-Projects/holer/blob/master/Binary);
下载软件包[`holer-client.zip`](https://github.com/Wisdom-Projects/holer/blob/master/Binary)；

#### 2. Install `Java 1.7` or higher version;
使用前请先安装`Java 1.7`或者更高版本；

#### 3. Unzip `holer-client.zip`, modify configuration file
解压`holer-client.zip`，修改配置文件<br/>
`holer-client/conf/holer.conf`<br/><br/>
Only need to set `HOLER_ACCESS_KEY`:<br/>
只需设置`HOLER_ACCESS_KEY`：

`HOLER_ACCESS_KEY=HOLER_CLIENT-2F8D8B78B3C2A0AE`<br/>
     
#### 4. Start holer
启动Holer服务<br/>
`cd holer-client/bin`<br/><br/>
**Windows**:<br/>
Run command `startup.bat` or double click `startup.bat`<br/><br/>
**Linux**:<br/>
Run command `sh startup.sh`
     
#### 5. Internet and local address mapping
公网和内网的地址映射关系

Holer Access Key             |Internet Address   | Local Address
-----------------------------|-------------------|---------------
HOLER_CLIENT-2F8D8B78B3C2A0AE|106.14.70.153:65530|127.0.0.1:8080
HOLER_CLIENT-3C07CDFD1BF99BF2|106.14.70.153:65531|127.0.0.1:8088
HOLER_CLIENT-2A623FCB6E2A7D1D|106.14.70.153:65532|127.0.0.1:80
HOLER_CLIENT-AF3E6391525F70E4|106.14.70.153:65533|127.0.0.1:3389
HOLER_CLIENT-822404317F9D8ADD|106.14.70.153:65534|127.0.0.1:22

#### 6. Demo
使用示例<br/><br/>
If your tomcat program local URL: <br/>
如果您本地的tomcat服务地址：<br/>
`http://127.0.0.1:8088`<br/>

Exposes to the public internet URL: <br/>
代理到公网上的服务地址为：<br/>
`http://106.14.70.153:65531`<br/>

Only need to modify configuration file to set `HOLER_ACCESS_KEY`: <br/>
只需要在配置文件里修改`HOLER_ACCESS_KEY`即可：<br/>
`holer-client/conf/holer.conf`<br/>
`HOLER_ACCESS_KEY=HOLER_CLIENT-3C07CDFD1BF99BF2`<br/>

Restart holer, then you can visit your web application through URL `http://106.14.70.153:65531`<br/>
重启Holer，然后就可以通过URL `http://106.14.70.153:65531`来访问您的Web应用。<br/>

# Support
All of the above holer access keys and ports have been shared to public. In order to enable users to get more shared holer services, every user can use each holer access key to get holer service. <br/>
If you want to get **exclusive** access key and port, please contact the author by mail or QQ.<br/><br/>
以上的全部Holer Access Key和端口都已公开共享，为了让用户获得到更多的共享的Holer服务，每个用户可以使用每一个Holer Access Key获取到Holer服务。<br/>
如果您需要**独享的**Holer Access Key和端口，请邮件或者QQ联系作者。<br/><br/>
_**Author**: Yudong (Dom) Wang_ <br/>
_**E-mail**: wisdomtool@outlook.com_<br/>
_**QQ**    : 2353941272_<br/>

# Donate
如果 **Holer** 工具对您帮助很大，并且您很愿意支持工具的后续开发和维护，您可以扫下方二维码随意打赏，就当是请我喝杯茶或是咖啡，将不胜感激。 **♥ 谢谢 ♥**

If the **Holer** helps you a lot, and you would like to support this tool's further development and the continuous maintenance of this tool. You can sweep the following QR code free to donate me, which asked me to have a cup of tea or coffee. Your donation is highly appreciated. **♥ Thank you ♥** <br/>

[**♥ Donate ♥ by PayPal**](https://www.paypal.me/WisdomTool) , Alipay, WeChat Pay.

![Donate by pay](https://github.com/Wisdom-Projects/rest-client/blob/master/images/donate_pay.png)
