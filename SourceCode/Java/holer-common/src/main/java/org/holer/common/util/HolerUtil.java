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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/** 
* @Class Name : HolerUtil 
* @Description: Holer utilities 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 22, 2018 4:05:32 PM 
* @Version    : Holer V1.2 
*/
public class HolerUtil
{
    private static Logger log = LoggerFactory.getLogger(HolerUtil.class);

    /**
    * 
    * @Title      : toJsonText 
    * @Description: Change object to JSON text 
    * @Param      : @param instance
    * @Param      : @return 
    * @Return     : String
    * @Throws     :
     */
    public static <T> String toJson(T instance)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(instance);
        }
        catch(Exception e)
        {
            log.debug("Write object [" + instance + "] as json string failed.", e);
        }
        return StringUtils.EMPTY;
    }

    /**
    * 
    * @Title      : toObject 
    * @Description: JSON to object 
    * @Param      : @param json
    * @Param      : @param clas
    * @Param      : @return 
    * @Return     : T
    * @Throws     :
     */
    public static <T> T toObject(String json, Class<T> clas)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return (T) mapper.readValue(json, clas);
        }
        catch(Exception e)
        {
            log.error("Failed to change json text [" + json + "] to object.", e);
        }

        return null;
    }

    /**
    * 
    * @Title      : toObject 
    * @Description: JSON to object 
    * @Param      : @param json
    * @Param      : @param typeToken
    * @Param      : @return 
    * @Return     : T
    * @Throws     :
     */
    public static <T> T toObject(String json, TypeToken<T> typeToken)
    {
        try
        {
            Gson gson = new Gson();
            return gson.fromJson(json, typeToken.getType());
        }
        catch(Exception e)
        {
            log.debug("Change JSON to object failed.", e);
        }
        return null;
    }

    /**
    * 
    * @Title      : toJsonFile 
    * @Description: Change JSON to file 
    * @Param      : @param jf
    * @Param      : @param instance 
    * @Return     : void
    * @Throws     :
     */
    public static <T> void toJsonFile(File jf, T instance)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            mapper.writerWithDefaultPrettyPrinter().writeValue(jf, instance);
        }
        catch(Exception e)
        {
            log.error("Change object [" + instance + "] to json file [" + jf.getName() + "] failed.", e);
        }
    }
    
    /**
    * 
    * @Title      : close 
    * @Description: Close input stream 
    * @Param      : @param is 
    * @Return     : void
    * @Throws     :
     */
    public static void close(InputStream is)
    {
        if (null == is)
        {
            return;
        }

        try
        {
            is.close();
        }
        catch(IOException e)
        {
            // Ignore this exception
        }
    }
    
    /**
    * 
    * @Title      : close 
    * @Description: Close channel
    * @Param      : @param channel 
    * @Return     : void
    * @Throws     :
     */
    public static void close(Channel channel)
    {
        if (null == channel)
        {
            return;
        }

        try
        {
            channel.close();
        }
        catch(Exception e)
        {
            // Ignore this exception
        }
    }

    /**
    * 
    * @Title      : close 
    * @Description: Close channel handler context
    * @Param      : @param ctx 
    * @Return     : void
    * @Throws     :
     */
    public static void close(ChannelHandlerContext ctx)
    {
        if (null == ctx)
        {
            return;
        }

        try
        {
            close(ctx.channel());
            ctx.close();
        }
        catch(Exception e)
        {
            // Ignore this exception
        }
    }

    /**
    * 
    * @Title      : sleep 
    * @Description: Thread sleep 
    * @Param      : @param millis 
    * @Return     : void
    * @Throws     :
     */
    public static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch(InterruptedException e)
        {
            // Ignore this exception
        }
    }

    /**
    * 
    * @Title      : division 
    * @Description: A division method
    * @Param      : @param a
    * @Param      : @param b
    * @Param      : @return 
    * @Return     : String
    * @Throws     :
     */
    public static String division(long a, long b)
    {
        double num = (double) a / b;

        DecimalFormat df = new DecimalFormat("0.0");
        String result = df.format(num);

        return result;
    }

    /**
    * 
    * @Title      : exec 
    * @Description: Execute command 
    * @Param      : @param cmd 
    * @Return     : void
    * @Throws     :
     */
    public static void exec(String cmd)
    {
        try
        {
            Runtime.getRuntime().exec(cmd);
        }
        catch(Exception e)
        {
            log.error("Failed to execute command: " + cmd, e);
        }
    }

    /**
    * 
    * @Title      : isActive 
    * @Description: Check if it is active 
    * @Param      : @param ch
    * @Param      : @return 
    * @Return     : boolean
    * @Throws     :
     */
    public static boolean isActive(Channel ch)
    {
        if (null == ch)
        {
            return false;
        }
        if (ch.isActive() || ch.isOpen())
        {
            return true;
        }
        return false;
    }
}
