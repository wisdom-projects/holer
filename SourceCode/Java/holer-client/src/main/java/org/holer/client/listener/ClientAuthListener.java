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

package org.holer.client.listener;

import org.holer.client.ClientContainer;
import org.holer.client.constant.ClientConst;
import org.holer.client.util.ClientMgr;
import org.holer.common.constant.HolerConst;
import org.holer.common.model.HolerMsg;
import org.holer.common.util.HolerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/** 
* @Class Name : ClientAuthListener 
* @Description: Holer client listener
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 01, 2019 10:23:37 PM 
* @Version    : Holer V1.2 
*/
public class ClientAuthListener implements ChannelFutureListener
{
    private static Logger log = LoggerFactory.getLogger(ClientAuthListener.class);

    @Override
    public void operationComplete(ChannelFuture future) throws Exception
    {
        final HolerConfig config = HolerConfig.getConfig();
        final String host = config.strValue(HolerConst.HOLER_SERVER_HOST);
        final int port = config.intValue(HolerConst.HOLER_SERVER_PORT, HolerConst.HOLER_SERVER_PORT_DEFAULT);
        ClientContainer container = ClientContainer.getContainer();

        if (!future.isSuccess())
        {
            System.out.println("Unable to connect holer server <" + host + ":" + port + ">");
            log.warn("Failed to connect holer server {}", future.cause().getMessage());
            container.restart();
            return;
        }

        Channel clientChannel = future.channel();
        ClientMgr.setClientChannel(clientChannel);

        HolerMsg hmsg = new HolerMsg();
        hmsg.setType(HolerMsg.TYPE_AUTH);
        hmsg.setUri(config.strValue(ClientConst.HOLER_ACCESS_KEY));
        clientChannel.writeAndFlush(hmsg);

        container.setSleepTime(HolerConst.ONE_SECOND);
        log.info("Connect holer server success {}", clientChannel);
    }
}
