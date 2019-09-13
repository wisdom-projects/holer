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

package org.holer.client.init;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.holer.client.handler.ClientHandler;
import org.holer.common.constant.HolerConst;
import org.holer.common.util.HolerConfig;
import org.holer.common.util.IdleChecker;
import org.holer.common.util.MsgDecoder;
import org.holer.common.util.MsgEncoder;
import org.holer.common.util.SSLCreator;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;

/** 
* @Class Name : ClientInitializer 
* @Description: Holer client initializer
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 01, 2019 10:23:37 PM 
* @Version    : Holer V1.2 
*/
public class ClientInitializer extends ChannelInitializer<SocketChannel>
{
    private SSLContext sslContext = null;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception
    {
        if (HolerConfig.getConfig().boolValue(HolerConst.HOLER_SSL_ENABLE, false))
        {
            if (null == this.sslContext)
            {
                this.sslContext = SSLCreator.getCreator().getSSLContext();
            }

            SSLEngine engine = this.sslContext.createSSLEngine();
            engine.setUseClientMode(true);
            ch.pipeline().addLast(new SslHandler(engine));
        }
        ch.pipeline().addLast(new MsgDecoder(HolerConst.MAX_FRAME_LEN, HolerConst.FIELD_OFFSET, HolerConst.FIELD_LEN, HolerConst.ADJUSTMENT, HolerConst.INIT_BYTES_TO_STRIP));
        ch.pipeline().addLast(new MsgEncoder());
        ch.pipeline().addLast(new IdleChecker(HolerConst.READ_IDLE_TIME, HolerConst.WRITE_IDLE_TIME - 10, 0));
        ch.pipeline().addLast(new ClientHandler());
    }

}
