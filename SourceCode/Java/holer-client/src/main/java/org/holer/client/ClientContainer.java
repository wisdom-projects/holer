/*
 * Copyright 2018-present, Yudong (Dom) Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.holer.client;

import org.holer.client.init.ClientInitializer;
import org.holer.client.init.IntraServerInitializer;
import org.holer.client.listener.ClientAuthListener;
import org.holer.client.util.ClientMgr;
import org.holer.common.constant.HolerConst;
import org.holer.common.util.HolerConfig;
import org.holer.common.util.HolerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/** 
* @Class Name : ClientContainer 
* @Description: Holer client container 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 20, 2018 8:11:57 PM 
* @Version    : Holer V1.2 
*/
public class ClientContainer
{
    private static Logger log = LoggerFactory.getLogger(ClientContainer.class);

    private static ClientContainer container = null;

    private NioEventLoopGroup workerGroup = null;

    private Bootstrap holerClient = null;

    private Bootstrap intraServer = null;

    private HolerConfig config = HolerConfig.getConfig();

    private long sleepTime = HolerConst.ONE_SECOND;

    /** 
    * @Title      : HolerClientContainer 
    * @Description: Default constructor 
    * @Param      : 
    */
    public ClientContainer()
    {
        this.init();
    }

    /**
    * 
    * @Title      : getContainer 
    * @Description: Get container instance
    * @Param      : @return 
    * @Return     : ClientContainer
    * @Throws     :
     */
    public static ClientContainer getContainer()
    {
        if (null == container)
        {
            container = new ClientContainer();
        }
        return container;
    }

    /**
    * 
    * @Title      : init 
    * @Description: Initialize variables 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public void init()
    {
        this.sleepTime = HolerConst.ONE_SECOND;
        this.workerGroup = new NioEventLoopGroup();

        this.intraServer = new Bootstrap();
        this.intraServer.group(this.workerGroup);
        this.intraServer.channel(NioSocketChannel.class);
        this.intraServer.handler(new IntraServerInitializer());

        this.holerClient = new Bootstrap();
        this.holerClient.group(this.workerGroup);
        this.holerClient.channel(NioSocketChannel.class);
        this.holerClient.handler(new ClientInitializer());
    }

    public void setSleepTime(long sleepTime)
    {
        this.sleepTime = sleepTime;
    }

    public Bootstrap getHolerClient()
    {
        return holerClient;
    }

    public Bootstrap getIntraServer()
    {
        return intraServer;
    }

    /**
    * 
    * @Title      : connectHolerServer 
    * @Description: Connect to holer server 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    private void connectHolerServer()
    {
        final String host = config.strValue(HolerConst.HOLER_SERVER_HOST);
        final int port = config.intValue(HolerConst.HOLER_SERVER_PORT, HolerConst.HOLER_SERVER_PORT_DEFAULT);
        this.holerClient.connect(host, port).addListener(new ClientAuthListener());
    }

    /**
    * 
    * @Title      : waitMoment 
    * @Description: Wait for reconnection 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    private void waitMoment()
    {
        if (this.sleepTime > HolerConst.ONE_MINUTE)
        {
            this.sleepTime = HolerConst.ONE_SECOND;
        }

        this.sleepTime = this.sleepTime * 2;
        HolerUtil.sleep(this.sleepTime);
    }

    /**
    * 
    * @Title      : start 
    * @Description: Start holer client 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public void start()
    {
        this.connectHolerServer();
    }

    /**
    * 
    * @Title      : stop 
    * @Description: Stop holer client 
    * @Param      : @param status 
    * @Return     : void
    * @Throws     :
     */
    public void stop(byte status)
    {
        this.workerGroup.shutdownGracefully();
        ClientMgr.clearHolerClient();

        log.info("Stopped holer client with status {}", status);
        System.exit(status);
    }

    /**
    * 
    * @Title      : restart 
    * @Description: Restart holer client 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public void restart()
    {
        this.waitMoment();
        this.connectHolerServer();
    }

    /**
    * 
    * @Title      : main 
    * @Description: Holer client main process 
    * @Param      : @param args 
    * @Return     : void
    * @Throws     :
     */
    public static void main(String[] args)
    {
        ClientContainer.getContainer().start();
    }
}