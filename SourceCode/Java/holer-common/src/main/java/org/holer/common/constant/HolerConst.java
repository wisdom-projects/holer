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

package org.holer.common.constant;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/** 
* @Class Name : HolerConst 
* @Description: Holer constant definition 
* @Author     : Dom Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 19, 2018 11:15:15 AM 
* @Version    : Holer V1.2 
*/
public class HolerConst
{    
    public static final byte HEADER_SIZE = 4;

    public static final int TYPE_SIZE = 1;

    public static final int SERIAL_NUM_SIZE = 8;

    public static final int URI_LEN_SIZE = 1;

    /* Max packet size is 2M */
    public static final int MAX_FRAME_LEN = 2 * 1024 * 1024;

    public static final int FIELD_OFFSET = 0;

    public static final int FIELD_LEN = 4;

    public static final int INIT_BYTES_TO_STRIP = 0;

    public static final int ADJUSTMENT = 0;

    public static final int READ_IDLE_TIME = 60;

    public static final int WRITE_IDLE_TIME = 40;

    public static final int HOLER_SERVER_PORT_DEFAULT = 6060;

    public static final long HALF_SECOND = 500L;

    public static final long ONE_SECOND = 1000L;

    public static final long THREE_SECONDS = 3 * ONE_SECOND;

    public static final long THIRTY_SECONDS = 30 * ONE_SECOND;

    public static final long ONE_MINUTE = 60 * ONE_SECOND;

    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    public static final long TWELVE_HOURS = 12 * ONE_HOUR;

    public static final long ONE_DAY = 24 * ONE_HOUR;

    public static final long THREE_DAYS = 3 * ONE_DAY;

    public static final long TWO_MINUTES = 2 * ONE_MINUTE;

    public static final long THREE_MINUTES = 3 * ONE_MINUTE;

    public static final long FIVE_MINUTES = 5 * ONE_MINUTE;

    public static final long SEVEN_MINUTES = 7 * ONE_MINUTE;

    public static final long TEN_MINUTES = 10 * ONE_MINUTE;

    public static final long TWENTY_MINUTES = 20 * ONE_MINUTE;

    public static final String TLS = "TLS";

    public static final String JKS = "JKS";

    public static final String EMPTY = "";

    public static final String HOLER_CONF = "holer.conf";

    public static final String HOLER_SERVER_HOST = "HOLER_SERVER_HOST";

    public static final String HOLER_SERVER_PORT = "HOLER_SERVER_PORT";

    public static final String HOLER_SSL_ENABLE = "HOLER_SSL_ENABLE";

    public static final String HOLER_SSL_JKS = "HOLER_SSL_JKS";

    public static final String HOLER_SSL_PASSWD = "HOLER_SSL_PASSWORD";

    public static final String HOLER_SSL_PASSWD_DEFAULT = "Wisdom-Holer";

    public static final String HOLER_CONF_DIR = "conf/";

    public static final String HOLER_SSL_JKS_DEFAULT = HOLER_CONF_DIR + "holer.jks";

    public static final AttributeKey<Channel> HOLER_CHANNEL = AttributeKey.newInstance("HOLER_CHANNEL");

    public static final AttributeKey<String> HOLER_URI = AttributeKey.newInstance("HOLER_URI");

    public static final AttributeKey<String> HOLER_KEY = AttributeKey.newInstance("HOLER_KEY");
}
