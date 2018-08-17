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

import io.netty.channel.ChannelHandlerContext;

/** 
* @Class Name : HolerStatusListener 
* @Description: Holer channel status listener 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@outlook.com
* @Date       : Mar 20, 2018 8:25:00 PM 
* @Version    : Wisdom Holer V1.0 
*/
public interface HolerStatusListener
{
    /**
    * 
    * @Title      : channelInactive 
    * @Description: Check if channel is inactive 
    * @Param      : @param ctx 
    * @Return     : void
    * @Throws     :
     */
    void channelInactive(ChannelHandlerContext ctx);
}
