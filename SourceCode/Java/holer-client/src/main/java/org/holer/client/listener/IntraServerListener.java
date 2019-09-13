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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;

/** 
* @Class Name : IntraServerListener 
* @Description: Intra-server listener 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 01, 2019 10:23:37 PM 
* @Version    : Holer V1.2 
*/
public class IntraServerListener implements ChannelFutureListener
{
    private Channel clientChannel = null;

    private HolerMsg msg = null;

    /** 
    * @Title      : IntraServerListener 
    * @Description: constructor 
    * @Param      : @param channel
    * @Param      : @param msg
    */
    public IntraServerListener(Channel channel, HolerMsg msg)
    {
        this.clientChannel = channel;
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
            return;
        }

        final HolerConfig config = HolerConfig.getConfig();
        final Channel intraChannel = future.channel();
        intraChannel.config().setOption(ChannelOption.AUTO_READ, false);

        Channel holerChannel = ClientMgr.pollClient();
        if (null == holerChannel)
        {
            String host = config.strValue(HolerConst.HOLER_SERVER_HOST);
            int port = config.intValue(HolerConst.HOLER_SERVER_PORT);

            ClientConnectListener listener = new ClientConnectListener(this.clientChannel, intraChannel, this.msg);
            Bootstrap holerClient = ClientContainer.getContainer().getHolerClient();
            holerClient.connect(host, port).addListener(listener);
            return;
        }

        holerChannel.attr(HolerConst.HOLER_CHANNEL).set(intraChannel);
        intraChannel.attr(HolerConst.HOLER_CHANNEL).set(holerChannel);

        HolerMsg hmsg = new HolerMsg();
        hmsg.setType(HolerMsg.TYPE_CONNECT);
        hmsg.setUri(this.msg.getUri() + "@" + config.strValue(ClientConst.HOLER_ACCESS_KEY));
        holerChannel.writeAndFlush(hmsg);

        intraChannel.config().setOption(ChannelOption.AUTO_READ, true);
        ClientMgr.addIntraServer(this.msg.getUri(), intraChannel);
        ClientMgr.setIntraServerUri(intraChannel, this.msg.getUri());
    }

}
