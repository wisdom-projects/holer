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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.holer.common.constant.HolerConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @Class Name : HolerConfig 
* @Description: Holer configuration 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 20, 2018 9:16:15 PM 
* @Version    : Holer V1.2 
*/
public class HolerConfig
{
    private static Logger log = LoggerFactory.getLogger(HolerConfig.class);

    private static HolerConfig config = null;

    private Properties holerConf = new Properties();

    public static HolerConfig getConfig()
    {
        if (null == config)
        {
            config = new HolerConfig();
        }
        return config;
    }

    /** 
    * @Title      : HolerConfig 
    * @Description: Default constructor 
    * @Param      : 
    */
    public HolerConfig()
    {
        this.initConfig(HolerConst.HOLER_CONF_DIR + HolerConst.HOLER_CONF);
        this.initConfig(HolerConst.HOLER_CONF);
    }

    /**
    * 
    * @Title      : initConfig 
    * @Description: Initialize configuration failed 
    * @Param      : @param confFile 
    * @Return     : void
    * @Throws     :
     */
    public void initConfig(String confFile)
    {
        InputStream is = null;
        try
        {
            is = HolerConfig.class.getClassLoader().getResourceAsStream(confFile);
            if (null == is)
            {
                log.warn("Can not find configuration file: " + confFile);
                return;
            }
            this.holerConf.load(is);
        }
        catch(IOException e)
        {
            log.error("Failed to load configuration file.");
            throw new RuntimeException(e);
        } 
        finally
        {
            HolerUtil.close(is);
        }
    }

    public String strValue(String key)
    {
        String value = this.holerConf.getProperty(key);
        return StringUtils.trim(value);
    }

    public String strValue(String key, String defaultValue)
    {
        String value = this.strValue(key);
        if (StringUtils.isBlank(value))
        {
            return defaultValue;
        }
        return value;
    }

    public Integer intValue(String key)
    {
        String value = this.strValue(key);
        if (StringUtils.isEmpty(value))
        {
            return null;
        }
        if (StringUtils.isNumeric(value))
        {
            return Integer.valueOf(value);
        }
        return null;
    }

    public int intValue(String key, int defaultValue)
    {
        Integer value = this.intValue(key);
        if (null == value)
        {
            return defaultValue;
        }
        return value;
    }

    public Boolean boolValue(String key)
    {
        String value = this.strValue(key);
        if (StringUtils.isBlank(value))
        {
            return null;
        }

        if (!value.equals(Boolean.toString(false)) && 
            !value.equals(Boolean.toString(true)))
        {
            return null;
        }

        return Boolean.valueOf(value);
    }

    public boolean boolValue(String key, Boolean defaultValue)
    {
        Boolean value = this.boolValue(key);
        if (null == value)
        {
            return defaultValue;
        }
        return value;
    }
}
