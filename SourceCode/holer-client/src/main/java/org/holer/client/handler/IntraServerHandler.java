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

package org.holer.client.handler;

import org.holer.client.util.HolerClientMgr;
import org.holer.common.constant.HolerConst;
import org.holer.common.model.HolerMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;

/** 
* @Class Name : IntraServerHandler 
* @Description: Intranet server channel handler 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@outlook.com
* @Date       : Mar 20, 2018 10:25:11 PM 
* @Version    : Wisdom Holer V1.0 
*/
public class IntraServerHandler extends SimpleChannelInboundHandler<ByteBuf>
{
    private static Logger log = LoggerFactory.getLogger(IntraServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception
    {
        Channel intraServer = ctx.channel();
        Channel channel = intraServer.attr(HolerConst.HOLER_CHANNEL).get();
        if (null == channel)
        {
            // Disconnect client channel
            ctx.channel().close();
            return;
        }

        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);

        HolerMsg hmsg = new HolerMsg();
        hmsg.setType(HolerMsg.TYPE_TRANSFER);
        hmsg.setUri(HolerClientMgr.getIntraServerUri(intraServer));
        hmsg.setData(data);
        channel.writeAndFlush(hmsg);
        log.debug("Write data to holer server, {}, {}", intraServer, channel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        Channel intraServer = ctx.channel();
        String uri = HolerClientMgr.getIntraServerUri(intraServer);
        HolerClientMgr.removeIntraServer(uri);
        Channel channel = intraServer.attr(HolerConst.HOLER_CHANNEL).get();
        if (null != channel)
        {
            log.debug("Channel inactive, {}", intraServer);
            HolerMsg hmsg = new HolerMsg();
            hmsg.setType(HolerMsg.TYPE_DISCONNECT);
            hmsg.setUri(uri);
            channel.writeAndFlush(hmsg);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception
    {
        Channel intraServer = ctx.channel();
        Channel channel = intraServer.attr(HolerConst.HOLER_CHANNEL).get();
        if (null != channel)
        {
            channel.config().setOption(ChannelOption.AUTO_READ, intraServer.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        log.error("Exception caught.", cause);
        super.exceptionCaught(ctx, cause);
    }
}
