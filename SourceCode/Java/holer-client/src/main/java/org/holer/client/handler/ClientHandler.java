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

import org.apache.commons.lang.StringUtils;
import org.holer.client.ClientContainer;
import org.holer.client.constant.ClientConst;
import org.holer.client.listener.IntraServerListener;
import org.holer.client.util.ClientMgr;
import org.holer.common.constant.HolerConst;
import org.holer.common.model.HolerMsg;
import org.holer.common.util.HolerConfig;
import org.holer.common.util.HolerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;

/** 
* @Class Name : ClientHandler 
* @Description: Holer client channel handler 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 20, 2018 10:23:37 PM 
* @Version    : Holer V1.2 
*/
public class ClientHandler extends SimpleChannelInboundHandler<HolerMsg>
{
    private static Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private HolerConfig config = HolerConfig.getConfig();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        log.error("Caught holer client exception {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    /**
    * 
    * @Title      : handleConnectMsg 
    * @Description: Handle connected message 
    * @Param      : @param clientChannel
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleConnectMsg(final Channel clientChannel, final HolerMsg msg)
    {
        String srvData = new String(msg.getData());
        String host = StringUtils.substringBefore(srvData, ":");
        String port = StringUtils.substringAfter(srvData, ":");

        if (StringUtils.isBlank(host))
        {
            log.error("Invalid server host {}", host);
            return;
        }

        if (StringUtils.isBlank(port) || !StringUtils.isNumeric(port))
        {
            log.error("Invalid server port {}", port);
            return;
        }

        int portNum = Integer.parseInt(port);
        Bootstrap intraServer = ClientContainer.getContainer().getIntraServer();
        IntraServerListener listener = new IntraServerListener(clientChannel, msg);
        intraServer.connect(host, portNum).addListener(listener);
    }

    /**
    * 
    * @Title      : handleDisconnectMsg 
    * @Description: Handle disconnected message 
    * @Param      : @param clientChannel
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleDisconnectMsg(final Channel clientChannel, final HolerMsg msg)
    {
        Channel intraChannel = clientChannel.attr(HolerConst.HOLER_CHANNEL).get();
        if (null == intraChannel)
        {
            return;
        }

        clientChannel.attr(HolerConst.HOLER_CHANNEL).set(null);
        ClientMgr.pushClient(clientChannel);
        intraChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
    * 
    * @Title      : handleTransferMsg 
    * @Description: Handle transferred message  
    * @Param      : @param clientChannel
    * @Param      : @param allocator
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleTransferMsg(final Channel clientChannel, final ByteBufAllocator allocator, final HolerMsg msg)
    {
        Channel intraChannel = clientChannel.attr(HolerConst.HOLER_CHANNEL).get();
        if (null == intraChannel)
        {
            return;
        }

        ByteBuf buf = allocator.buffer(msg.getData().length);
        buf.writeBytes(msg.getData());
        intraChannel.writeAndFlush(buf);
    }

    /**
    * 
    * @Title      : handleServerMsg 
    * @Description: Handle server message 
    * @Param      : @param clientChannel
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleServerMsg(final Channel clientChannel, final HolerMsg msg)
    {
        String msgInfo = HolerConst.EMPTY;
        switch (msg.getType())
        {
            case HolerMsg.TYPE_NO_AVAILABLE_PORT:
                msgInfo = ClientConst.MSG_NO_AVAILABLE_PORT;
                break;
            case HolerMsg.TYPE_IS_INUSE_KEY:
                msgInfo = ClientConst.MSG_IS_INUSE_KEY;
                break;
            case HolerMsg.TYPE_DISABLED_ACCESS_KEY:
                msgInfo = ClientConst.MSG_DISABLED_ACCESS_KEY;
                break;
            case HolerMsg.TYPE_DISABLED_TRIAL_CLIENT:
                msgInfo = ClientConst.MSG_DISABLED_TRIAL_CLIENT;
                break;
            case HolerMsg.TYPE_INVALID_KEY:
                msgInfo = ClientConst.MSG_INVALID_ACCESS_KEY;
                break;
            default:
                msgInfo = HolerConst.EMPTY;
                break;
        }

        if (StringUtils.isEmpty(msgInfo))
        {
            return;
        }

        System.out.println(ClientConst.HOLER_ACCESS_KEY + "=" + config.strValue(ClientConst.HOLER_ACCESS_KEY));
        System.out.println(msgInfo);

        HolerUtil.close(clientChannel);
        ClientContainer.getContainer().stop(msg.getType());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HolerMsg msg) throws Exception
    {
        Channel clientChannel = ctx.channel();
        switch (msg.getType())
        {
            case HolerMsg.TYPE_CONNECT:
                handleConnectMsg(clientChannel, msg);
                break;
            case HolerMsg.TYPE_DISCONNECT:
                handleDisconnectMsg(clientChannel, msg);
                break;
            case HolerMsg.TYPE_TRANSFER:
                handleTransferMsg(clientChannel, ctx.alloc(), msg);
                break;
            default:
                handleServerMsg(clientChannel, msg);
                break;
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception
    {
        Channel clientChannel = ctx.channel();
        Channel intraChannel = clientChannel.attr(HolerConst.HOLER_CHANNEL).get();
        if (null != intraChannel)
        {
            intraChannel.config().setOption(ChannelOption.AUTO_READ, clientChannel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        Channel clientChannel = ctx.channel();
        if (ClientMgr.getClientChannel() == clientChannel)
        {
            ClientMgr.setClientChannel(null);
            ClientMgr.clearIntraServer();
            ClientContainer.getContainer().restart();
        }
        else
        {
            Channel intraChannel = clientChannel.attr(HolerConst.HOLER_CHANNEL).get();
            HolerUtil.close(intraChannel);
        }

        ClientMgr.removeClient(clientChannel);
        super.channelInactive(ctx);
    }

}
