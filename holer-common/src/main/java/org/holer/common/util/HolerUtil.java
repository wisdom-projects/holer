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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/** 
* @Class Name : HolerUtil 
* @Description: Holer utilities 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@outlook.com
* @Date       : Mar 22, 2018 4:05:32 PM 
* @Version    : Wisdom Holer V1.0 
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
}
