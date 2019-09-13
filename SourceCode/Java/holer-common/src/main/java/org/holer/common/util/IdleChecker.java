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

package org.holer.common.util;

import org.holer.common.model.HolerMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/** 
* @Class Name : HolerIdleChecker 
* @Description: check idle channel 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 22, 2018 12:22:55 PM 
* @Version    : Holer V1.2 
*/
public class IdleChecker extends IdleStateHandler
{
    private static Logger log = LoggerFactory.getLogger(IdleChecker.class);

    public IdleChecker(int readerIdleTime, int writerIdleTime, int allIdleTime)
    {
        super(readerIdleTime, writerIdleTime, allIdleTime);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception
    {
        if (IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT == evt)
        {
            log.debug("Channel write timeout {}.", ctx.channel());
            HolerMsg hmsg = new HolerMsg();
            hmsg.setType(HolerMsg.TYPE_HEARTBEAT);
            ctx.channel().writeAndFlush(hmsg);
        }
        else if (IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT == evt)
        {
            log.warn("Channel read timeout {}.", ctx.channel());
            ctx.channel().close();
        }
        super.channelIdle(ctx, evt);
    }
}
