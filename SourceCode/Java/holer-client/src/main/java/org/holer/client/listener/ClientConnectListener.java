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
import io.netty.channel.ChannelOption;

/** 
* @Class Name : ClientConnectListener 
* @Description: Holer client connection listener
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 01, 2019 10:23:37 PM 
* @Version    : Holer V1.2 
*/
public class ClientConnectListener implements ChannelFutureListener
{
    private static Logger log = LoggerFactory.getLogger(ClientConnectListener.class);

    private Channel clientChannel = null;

    private Channel intraChannel = null;

    private HolerMsg msg = null;

    /** 
    * @Title      : ClientConnectListener 
    * @Description: constructor 
    * @Param      : @param clientChannel
    * @Param      : @param intraServer
    * @Param      : @param msg
    */
    public ClientConnectListener(Channel clientChannel, Channel intraChannel, HolerMsg msg)
    {
        this.clientChannel = clientChannel;
        this.intraChannel = intraChannel;
        this.msg = msg;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception
    {
        if (!future.isSuccess())
        {
            HolerMsg hmsg = new HolerMsg();
            hmsg.setType(HolerMsg.TYPE_DISCONNECT);
            hmsg.setUri(this.msg.getUri());
            this.clientChannel.writeAndFlush(hmsg);
            log.error("Failed to connect holer server {}", future.cause().getMessage());
            return;
        }

        HolerConfig config = HolerConfig.getConfig();
        Channel holerChannel = future.channel();

        holerChannel.attr(HolerConst.HOLER_CHANNEL).set(this.intraChannel);
        this.intraChannel.attr(HolerConst.HOLER_CHANNEL).set(holerChannel);

        HolerMsg hmsg = new HolerMsg();
        hmsg.setType(HolerMsg.TYPE_CONNECT);
        hmsg.setUri(this.msg.getUri() + "@" + config.strValue(ClientConst.HOLER_ACCESS_KEY));
        holerChannel.writeAndFlush(hmsg);

        this.intraChannel.config().setOption(ChannelOption.AUTO_READ, true);
        ClientMgr.addIntraServer(this.msg.getUri(), this.intraChannel);
        ClientMgr.setIntraServerUri(this.intraChannel, this.msg.getUri());
    }

}
