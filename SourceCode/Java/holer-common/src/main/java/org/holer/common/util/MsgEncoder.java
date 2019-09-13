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
import io.netty.handler.codec.MessageToByteEncoder;

/** 
* @Class Name : MsgEncoder 
* @Description: Holer message encoder 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 22, 2018 10:35:16 AM 
* @Version    : Holer V1.2 
*/
public class MsgEncoder extends MessageToByteEncoder<HolerMsg>
{
    @Override
    protected void encode(ChannelHandlerContext ctx, HolerMsg msg, ByteBuf out) throws Exception
    {
        int bodyLen = HolerConst.TYPE_SIZE + HolerConst.SERIAL_NUM_SIZE + HolerConst.URI_LEN_SIZE;
        byte[] uriBytes = null;
        if (null != msg.getUri())
        {
            uriBytes = msg.getUri().getBytes();
            bodyLen += uriBytes.length;
        }

        if (null != msg.getData())
        {
            bodyLen += msg.getData().length;
        }

        // Write the total packet length but without length field's length.
        out.writeInt(bodyLen);
        out.writeByte(msg.getType());
        out.writeLong(msg.getSerialNumber());

        if (null != uriBytes)
        {
            out.writeByte((byte) uriBytes.length);
            out.writeBytes(uriBytes);
        }
        else
        {
            out.writeByte((byte) 0x00);
        }

        if (null != msg.getData())
        {
            out.writeBytes(msg.getData());
        }
    }
}
