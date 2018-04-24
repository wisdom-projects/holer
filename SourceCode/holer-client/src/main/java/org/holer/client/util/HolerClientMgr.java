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

package org.holer.client.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;
import org.holer.client.constant.HolerClientConst;
import org.holer.client.listener.HolerBorrowListener;
import org.holer.common.constant.HolerConst;
import org.holer.common.util.HolerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

/** 
* @Class Name : HolerClientMgr 
* @Description: Holer client channel manager 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@outlook.com
* @Date       : Mar 21, 2018 11:02:57 AM 
* @Version    : Wisdom Holer V1.0 
*/
public class HolerClientMgr
{
    private static Logger log = LoggerFactory.getLogger(HolerClientMgr.class);

    private static Map<String, Channel> intraServers = new ConcurrentHashMap<String, Channel>();

    private static ConcurrentLinkedQueue<Channel> holerClients = new ConcurrentLinkedQueue<Channel>();

    private static volatile Channel holerServer = null;

    private static HolerConfig config = HolerConfig.getConfig();

    /**
    * 
    * @Title      : borrowHolerClient 
    * @Description: Borrow a holer client
    * @Param      : @param strap
    * @Return     : void
    * @Throws     :
     */
    public static void borrowHolerClient(Bootstrap strap, final HolerBorrowListener listener)
    {
        Channel channel = holerClients.poll();
        if (null != channel)
        {
            listener.success(channel);
            return;
        }

        String host = config.strValue(HolerConst.HOLER_SERVER_HOST);
        int port = config.intValue(HolerConst.HOLER_SERVER_PORT);
        strap.connect(host, port).addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if (future.isSuccess())
                {
                    listener.success(future.channel());
                }
                else
                {
                    log.warn("Connect holer server failed. {}", future.cause().getMessage());
                    listener.fail(future.cause());
                }
            }
        });
    }

    /**
    * 
    * @Title      : returnHolerClient 
    * @Description: Return a holer client 
    * @Param      : @param channel 
    * @Return     : void
    * @Throws     :
     */
    public static void returnHolerClient(Channel channel)
    {
        if (holerClients.size() > HolerClientConst.MAX_POOL_SIZE)
        {
            channel.close();
        }
        else
        {
            channel.config().setOption(ChannelOption.AUTO_READ, true);
            channel.attr(HolerConst.HOLER_CHANNEL).set(null);
            holerClients.offer(channel);
            log.debug("Return holer chanel to the pool, channel is {}, pool size is {} ", channel, holerClients.size());
        }
    }

    /**
    * 
    * @Title      : clearIntraServer 
    * @Description: Clear intranet server channel 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public static void clearIntraServer()
    {
        log.info("Channel closed, clear intranet server channels.");

        Iterator<Entry<String, Channel>> it = intraServers.entrySet().iterator();
        while (it.hasNext())
        {
            Channel channel = it.next().getValue();
            if (channel.isActive())
            {
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
        intraServers.clear();
    }

    public static void removeClient(Channel channel)
    {
        holerClients.remove(channel);
    }

    public static void setIntraServerUri(Channel channel, String uri)
    {
        channel.attr(HolerConst.HOLER_URI).set(uri);
    }

    public static String getIntraServerUri(Channel channel)
    {
        return channel.attr(HolerConst.HOLER_URI).get();
    }

    public static Channel getIntraServer(String uri)
    {
        return intraServers.get(uri);
    }

    public static void addIntraServer(String uri, Channel channel)
    {
        intraServers.put(uri, channel);
    }

    public static Channel removeIntraServer(String uri)
    {
        if (StringUtils.isEmpty(uri))
        {
            return null;
        }
        return intraServers.remove(uri);
    }

    public static Channel getHolerServer()
    {
        return holerServer;
    }

    public static void setHolerServer(Channel holerServer)
    {
        HolerClientMgr.holerServer = holerServer;
    }
}
