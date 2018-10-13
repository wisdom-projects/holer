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

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.holer.client.constant.HolerClientConst;
import org.holer.client.handler.HolerClientHandler;
import org.holer.client.handler.IntraServerHandler;
import org.holer.client.listener.HolerStatusListener;
import org.holer.client.util.HolerClientMgr;
import org.holer.common.constant.HolerConst;
import org.holer.common.container.HolerContainer;
import org.holer.common.container.HolerContainerHelper;
import org.holer.common.model.HolerMsg;
import org.holer.common.util.HolerConfig;
import org.holer.common.util.HolerIdleChecker;
import org.holer.common.util.HolerMsgDecoder;
import org.holer.common.util.HolerMsgEncoder;
import org.holer.common.util.HolerSSLCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;

/** 
* @Class Name : HolerClientContainer 
* @Description: Holer client container 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 20, 2018 8:11:57 PM 
* @Version    : Wisdom Holer V1.0 
*/
public class HolerClientContainer implements HolerContainer, HolerStatusListener
{
    private static Logger log = LoggerFactory.getLogger(HolerClientContainer.class);

    private NioEventLoopGroup workerGroup = null;

    private Bootstrap holerClient = null;

    private Bootstrap intraServer = null;

    private HolerConfig config = HolerConfig.getConfig();

    private SSLContext sslContext = null;

    private long sleepTime = HolerConst.ONE_SECOND;

    /** 
    * @Title      : HolerClientContainer 
    * @Description: Default constructor 
    * @Param      : 
    */
    public HolerClientContainer()
    {
        this.init();
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
        holerClient.connect(host, port).addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    HolerClientMgr.setHolerServer(future.channel());
                    HolerMsg hmsg = new HolerMsg();
                    hmsg.setType(HolerMsg.TYPE_AUTH);
                    hmsg.setUri(config.strValue(HolerClientConst.HOLER_ACCESS_KEY));
                    future.channel().writeAndFlush(hmsg);
                    sleepTime = HolerConst.ONE_SECOND;
                    log.info("Connect holer server success, {}", future.channel());
                }
                else
                {
                    System.out.println("Unable to connect holer server <" + host + ":" + port + ">");
                    log.warn("Connect holer server failed. {}", future.cause().getMessage());
                    waitForReconnect();
                    connectHolerServer();
                }
            }
        });
    }

    /**
    * 
    * @Title      : waitForReconnect 
    * @Description: Wait for reconnection 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    private void waitForReconnect()
    {
        try
        {
            if (sleepTime > HolerConst.ONE_MINUTE)
            {
                sleepTime = HolerConst.ONE_SECOND;
            }

            synchronized (this)
            {
                sleepTime = sleepTime * 2;
                wait(sleepTime);
            }
        }
        catch(InterruptedException e)
        {
            // Ignore this exception
        }
    }

    @Override
    public void init()
    {
        workerGroup = new NioEventLoopGroup();
        intraServer = new Bootstrap();
        intraServer.group(workerGroup);
        intraServer.channel(NioSocketChannel.class);
        intraServer.handler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception
            {
                ch.pipeline().addLast(new IntraServerHandler());
            }
        });

        holerClient = new Bootstrap();
        holerClient.group(workerGroup);
        holerClient.channel(NioSocketChannel.class);
        holerClient.handler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception
            {
                if (config.boolValue(HolerConst.HOLER_SSL_ENABLE, false))
                {
                    if (null == sslContext)
                    {
                        sslContext = HolerSSLCreator.getCreator().getSSLContext();
                    }

                    SSLEngine engine = sslContext.createSSLEngine();
                    engine.setUseClientMode(true);
                    ch.pipeline().addLast(new SslHandler(engine));
                }
                ch.pipeline().addLast(new HolerMsgDecoder(HolerConst.MAX_FRAME_LEN, HolerConst.FIELD_OFFSET, HolerConst.FIELD_LEN, HolerConst.ADJUSTMENT, HolerConst.INIT_BYTES_TO_STRIP));
                ch.pipeline().addLast(new HolerMsgEncoder());
                ch.pipeline().addLast(new HolerIdleChecker(HolerConst.READ_IDLE_TIME, HolerConst.WRITE_IDLE_TIME - 10, 0));
                ch.pipeline().addLast(new HolerClientHandler(holerClient, intraServer, HolerClientContainer.this));
            }
        });
    }

    @Override
    public void start()
    {
        this.connectHolerServer();
    }

    @Override
    public void stop()
    {
        workerGroup.shutdownGracefully();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        this.waitForReconnect();
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
        List<HolerContainer> containers = new ArrayList<HolerContainer>();
        containers.add(new HolerClientContainer());
        HolerContainerHelper.start(containers);
    }
}