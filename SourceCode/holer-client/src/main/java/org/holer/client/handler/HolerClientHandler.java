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
import org.holer.client.constant.HolerClientConst;
import org.holer.client.listener.HolerBorrowListener;
import org.holer.client.listener.HolerStatusListener;
import org.holer.client.util.HolerClientMgr;
import org.holer.common.constant.HolerConst;
import org.holer.common.container.HolerContainerHelper;
import org.holer.common.model.HolerMsg;
import org.holer.common.util.HolerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;

/** 
* @Class Name : HolerClientHandler 
* @Description: Holer client channel handler 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@outlook.com
* @Date       : Mar 20, 2018 10:23:37 PM 
* @Version    : Wisdom Holer V1.0 
*/
public class HolerClientHandler extends SimpleChannelInboundHandler<HolerMsg>
{
    private static Logger log = LoggerFactory.getLogger(HolerClientHandler.class);

    private Bootstrap holerClient = null;

    private Bootstrap intraServer = null;

    private HolerConfig config = HolerConfig.getConfig();

    private HolerStatusListener listener = null;

    /**
    * 
    * @Title      : HolerClientHandler 
    * @Description: Default constructor 
    * @Param      : @param holerClient
    * @Param      : @param intraServer
    * @Param      : @param listener
     */
    public HolerClientHandler(Bootstrap holerClient, Bootstrap intraServer, HolerStatusListener listener)
    {
        this.holerClient = holerClient;
        this.intraServer = intraServer;
        this.listener = listener;
    }

    /**
    * 
    * @Title      : handleConnectMsg 
    * @Description: Handle connected message 
    * @Param      : @param ctx
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleConnectMsg(final ChannelHandlerContext ctx, final HolerMsg msg)
    {
        String serverData = new String(msg.getData());
        String[] serverInfo = serverData.split(":");
        if (serverInfo.length < 2)
        {
            log.warn("Invalid server information, {}", serverData);
            return;
        }

        String host = serverInfo[0];
        int port = Integer.parseInt(serverInfo[1]);
        this.intraServer.connect(host, port).addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                // Connect intranet server success
                if (future.isSuccess())
                {
                    final Channel intraServer = future.channel();
                    log.debug("Connect intranet server success, {}", intraServer);
                    intraServer.config().setOption(ChannelOption.AUTO_READ, false);

                    // Borrow connection
                    HolerClientMgr.borrowHolerClient(holerClient, new HolerBorrowListener()
                    {
                        @Override
                        public void success(Channel channel)
                        {
                            // Connection binding
                            channel.attr(HolerConst.HOLER_CHANNEL).set(intraServer);
                            intraServer.attr(HolerConst.HOLER_CHANNEL).set(channel);

                            // Remote binding
                            HolerMsg hmsg = new HolerMsg();
                            hmsg.setType(HolerMsg.TYPE_CONNECT);
                            hmsg.setUri(msg.getUri() + "@" + config.strValue(HolerClientConst.HOLER_ACCESS_KEY));
                            channel.writeAndFlush(hmsg);

                            intraServer.config().setOption(ChannelOption.AUTO_READ, true);
                            HolerClientMgr.addIntraServer(msg.getUri(), intraServer);
                            HolerClientMgr.setIntraServerUri(intraServer, msg.getUri());
                        }

                        @Override
                        public void fail(Throwable cause)
                        {
                            HolerMsg hmsg = new HolerMsg();
                            hmsg.setType(HolerMsg.TYPE_DISCONNECT);
                            hmsg.setUri(msg.getUri());
                            ctx.channel().writeAndFlush(hmsg);
                            log.error("It is failed.", cause);
                        }
                    });
                }
                else
                {
                    HolerMsg hmsg = new HolerMsg();
                    hmsg.setType(HolerMsg.TYPE_DISCONNECT);
                    hmsg.setUri(msg.getUri());
                    ctx.channel().writeAndFlush(hmsg);
                }
            }
        });
    }

    /**
    * 
    * @Title      : handleDisconnectMsg 
    * @Description: Handle disconnected message 
    * @Param      : @param ctx
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleDisconnectMsg(final ChannelHandlerContext ctx, final HolerMsg msg)
    {
        Channel intraServer = ctx.channel().attr(HolerConst.HOLER_CHANNEL).get();
        if (null == intraServer)
        {
            return;
        }

        log.debug("Handle disconnected message, {}", intraServer);
        ctx.channel().attr(HolerConst.HOLER_CHANNEL).set(null);
        HolerClientMgr.returnHolerClient(ctx.channel());
        intraServer.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
    * 
    * @Title      : handleTransferMsg 
    * @Description: Handle transferred message 
    * @Param      : @param ctx
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleTransferMsg(final ChannelHandlerContext ctx, final HolerMsg msg)
    {
        Channel intraServer = ctx.channel().attr(HolerConst.HOLER_CHANNEL).get();
        if (null == intraServer)
        {
            return;
        }

        ByteBuf buf = ctx.alloc().buffer(msg.getData().length);
        buf.writeBytes(msg.getData());
        log.debug("Write data to intranet server, {}", intraServer);
        intraServer.writeAndFlush(buf);
    }

    /**
    * 
    * @Title      : handleServerMsg 
    * @Description: Handle server message 
    * @Param      : @param ctx
    * @Param      : @param msg 
    * @Return     : void
    * @Throws     :
     */
    private void handleServerMsg(final ChannelHandlerContext ctx, final HolerMsg msg)
    {
        String msgInfo = HolerConst.EMPTY;
        switch (msg.getType())
        {
            case HolerMsg.TYPE_NO_AVAILABLE_PORT:
                msgInfo = HolerClientConst.MSG_NO_AVAILABLE_PORT;
                break;
            case HolerMsg.TYPE_IS_INUSE_KEY:
                msgInfo = HolerClientConst.MSG_IS_INUSE_KEY;
                break;
            case HolerMsg.TYPE_DISABLED_ACCESS_KEY:
                msgInfo = HolerClientConst.MSG_DISABLED_ACCESS_KEY;
                break;
            case HolerMsg.TYPE_DISABLED_TRIAL_CLIENT:
                msgInfo = HolerClientConst.MSG_DISABLED_TRIAL_CLIENT;
                break;
            case HolerMsg.TYPE_INVALID_KEY:
                msgInfo = HolerClientConst.MSG_INVALID_ACCESS_KEY;
                break;
            default:
                msgInfo = HolerConst.EMPTY;
                break;
        }

        if (StringUtils.isEmpty(msgInfo))
        {
            return;
        }

        ctx.channel().close();
        System.out.println(HolerClientConst.HOLER_ACCESS_KEY + "=" + config.strValue(HolerClientConst.HOLER_ACCESS_KEY));
        System.out.println(msgInfo);
        HolerContainerHelper.stop(msg.getType());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HolerMsg msg) throws Exception
    {
        log.debug("Received message from holer server, type is {}.", msg.getType());
        switch (msg.getType())
        {
            case HolerMsg.TYPE_CONNECT:
                handleConnectMsg(ctx, msg);
                break;
            case HolerMsg.TYPE_DISCONNECT:
                handleDisconnectMsg(ctx, msg);
                break;
            case HolerMsg.TYPE_TRANSFER:
                handleTransferMsg(ctx, msg);
                break;
            default:
                handleServerMsg(ctx, msg);
                break;
        }
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception
    {
        Channel intraServer = ctx.channel().attr(HolerConst.HOLER_CHANNEL).get();
        if (null != intraServer)
        {
            intraServer.config().setOption(ChannelOption.AUTO_READ, ctx.channel().isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        // Control connection
        if (HolerClientMgr.getHolerServer() == ctx.channel())
        {
            HolerClientMgr.setHolerServer(null);
            HolerClientMgr.clearIntraServer();
            listener.channelInactive(ctx);
        }
        else
        {
            // Data transmission connection
            Channel intraServer = ctx.channel().attr(HolerConst.HOLER_CHANNEL).get();
            if (null != intraServer && intraServer.isActive())
            {
                intraServer.close();
            }
        }

        HolerClientMgr.removeClient(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        log.error("Exception caught.", cause);
        super.exceptionCaught(ctx, cause);
    }
}
