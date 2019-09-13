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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;
import org.holer.client.constant.ClientConst;
import org.holer.common.constant.HolerConst;
import org.holer.common.util.HolerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

/** 
* @Class Name : ClientMgr 
* @Description: Holer client channel manager 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 21, 2018 11:02:57 AM 
* @Version    : Holer V1.2 
*/
public class ClientMgr
{
    private static Logger log = LoggerFactory.getLogger(ClientMgr.class);

    private static Map<String, Channel> intraServers = new ConcurrentHashMap<String, Channel>();

    private static ConcurrentLinkedQueue<Channel> holerClients = new ConcurrentLinkedQueue<Channel>();

    private static volatile Channel clientChannel = null;

    public static Channel getClientChannel()
    {
        return clientChannel;
    }

    public static void setClientChannel(Channel channel)
    {
        ClientMgr.clientChannel = channel;
    }

    public static void setIntraServerUri(Channel channel, String uri)
    {
        channel.attr(HolerConst.HOLER_URI).set(uri);
    }

    public static String getIntraServerUri(Channel channel)
    {
        return channel.attr(HolerConst.HOLER_URI).get();
    }

    public static void removeClient(Channel channel)
    {
        holerClients.remove(channel);
    }

    /**
    *     
    * @Title      : pollClient 
    * @Description: pull one holer client 
    * @Param      : @return 
    * @Return     : Channel
    * @Throws     :
     */
    public static Channel pollClient()
    {
        return holerClients.poll();
    }

    /**
    * 
    * @Title      : pushClient 
    * @Description: Return a holer client 
    * @Param      : @param channel 
    * @Return     : void
    * @Throws     :
     */
    public static void pushClient(Channel channel)
    {
        if (holerClients.size() > ClientConst.MAX_POOL_SIZE)
        {
            channel.close();
            return;
        }

        channel.config().setOption(ChannelOption.AUTO_READ, true);
        channel.attr(HolerConst.HOLER_CHANNEL).set(null);
        holerClients.offer(channel);
    }

    /**
    * 
    * @Title      : clearIntraServer 
    * @Description: Clear intra-server channel 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public static void clearIntraServer()
    {
        log.info("Channel closed, clear intranet server channels.");
        for (Entry<String, Channel> entry : intraServers.entrySet())
        {
            Channel channel = entry.getValue();
            if (channel.isActive())
            {
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
        intraServers.clear();
    }

    /**
    * 
    * @Title      : clearHolerClient 
    * @Description: Close and clear all the channels 
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    public static void clearHolerClient()
    {
        // Clear and close intra-servers
        for (Entry<String, Channel> entry : intraServers.entrySet())
        {
            Channel channel = entry.getValue();
            HolerUtil.close(channel);
        }
        intraServers.clear();

        // Close and clear client channel
        HolerUtil.close(clientChannel);
        for (Channel clientChannel : holerClients)
        {
            HolerUtil.close(clientChannel);
        }
    }

    /**
    * 
    * @Title      : addIntraServer 
    * @Description: Add intra-server  
    * @Param      : @param uri
    * @Param      : @param channel 
    * @Return     : void
    * @Throws     :
     */
    public static void addIntraServer(String uri, Channel channel)
    {
        intraServers.put(uri, channel);
    }

    /**
    * 
    * @Title      : removeIntraServer 
    * @Description: Remove intra-server 
    * @Param      : @param uri
    * @Param      : @return 
    * @Return     : Channel
    * @Throws     :
     */
    public static Channel removeIntraServer(String uri)
    {
        if (StringUtils.isEmpty(uri))
        {
            return null;
        }
        return intraServers.remove(uri);
    }

}
