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

import org.holer.common.constant.HolerConst;
import org.holer.common.model.HolerMsg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/** 
* @Class Name : MsgDecoder 
* @Description: Holer message decoder 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 22, 2018 10:34:48 AM 
* @Version    : Holer V1.2 
*/
public class MsgDecoder extends LengthFieldBasedFrameDecoder
{
    public MsgDecoder(int maxFrameLen, 
                           int lenFieldOffset, 
                           int lenFieldLen,
                           int lenAdjustment, 
                           int initBytesToStrip)
    {
        super(maxFrameLen, 
              lenFieldOffset, 
              lenFieldLen, 
              lenAdjustment,
              initBytesToStrip);
    }

    public MsgDecoder(int maxFrameLen, 
                           int lenFieldOffset, 
                           int lenFieldLen, 
                           int lenAdjustment, 
                           int initBytesToStrip, 
                           boolean failFast)
    {
        super(maxFrameLen, 
              lenFieldOffset, 
              lenFieldLen, 
              lenAdjustment, 
              initBytesToStrip, 
              failFast);
    }

    @Override
    protected HolerMsg decode(ChannelHandlerContext ctx, ByteBuf bin) throws Exception
    {
        ByteBuf in = (ByteBuf) super.decode(ctx, bin);
        if (null == in)
        {
            return null;
        }

        if (in.readableBytes() < HolerConst.HEADER_SIZE)
        {
            return null;
        }

        int frameLen = in.readInt();
        if (in.readableBytes() < frameLen)
        {
            return null;
        }

        HolerMsg hmsg = new HolerMsg();
        hmsg.setType(in.readByte());
        hmsg.setSerialNumber(in.readLong());

        byte uriLen = in.readByte();
        byte[] uriBytes = new byte[uriLen];
        in.readBytes(uriBytes);
        hmsg.setUri(new String(uriBytes));

        int dataLen = frameLen - HolerConst.TYPE_SIZE - HolerConst.SERIAL_NUM_SIZE - HolerConst.URI_LEN_SIZE - uriLen;
        byte[] data = new byte[dataLen];
        in.readBytes(data);
        hmsg.setData(data);
        in.release();

        return hmsg;
    }
}
