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

import org.holer.client.util.ClientMgr;
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
 * @Email      : wisdomtool@qq.com
 * @Date       : Mar 20, 2018 10:25:11 PM
 * @Version    : Holer V1.2
 */
public class IntraServerHandler extends SimpleChannelInboundHandler<ByteBuf>
{
    private static Logger log = LoggerFactory.getLogger(IntraServerHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        log.error("Caught intra-server exception {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception
    {
        Channel intraChannel = ctx.channel();
        Channel holerChannel = intraChannel.attr(HolerConst.HOLER_CHANNEL).get();

        if (null == holerChannel)
        {
            intraChannel.close();
            return;
        }

        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);

        HolerMsg hmsg = new HolerMsg();
        hmsg.setType(HolerMsg.TYPE_TRANSFER);
        hmsg.setUri(ClientMgr.getIntraServerUri(intraChannel));
        hmsg.setData(data);
        holerChannel.writeAndFlush(hmsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        Channel intraChannel = ctx.channel();
        String uri = ClientMgr.getIntraServerUri(intraChannel);
        ClientMgr.removeIntraServer(uri);

        Channel holerChannel = intraChannel.attr(HolerConst.HOLER_CHANNEL).get();
        if (null != holerChannel)
        {
            HolerMsg hmsg = new HolerMsg();
            hmsg.setType(HolerMsg.TYPE_DISCONNECT);
            hmsg.setUri(uri);
            holerChannel.writeAndFlush(hmsg);
        }

        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception
    {
        Channel intraChannel = ctx.channel();
        Channel holerChannel = intraChannel.attr(HolerConst.HOLER_CHANNEL).get();
        if (null != holerChannel)
        {
            holerChannel.config().setOption(ChannelOption.AUTO_READ, intraChannel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }
}
