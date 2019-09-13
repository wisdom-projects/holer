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

package org.holer.common.model;

import java.util.Arrays;

/** 
* @Class Name : HolerMsg 
* @Description: Exchange message between holer client and server  
* @Author     : Dom Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Mar 19, 2018 12:27:20 PM 
* @Version    : Holer V1.2 
*/
public class HolerMsg
{
    /**
     * Authenticate message to check whether accessKey is correct
     */
    public static final byte TYPE_AUTH = 0x01;

    /**
     * There are no available ports for the access key
     */
    public static final byte TYPE_NO_AVAILABLE_PORT = 0x02;

    /**
     * Holer connection message
     */
    public static final byte TYPE_CONNECT = 0x03;

    /**
     * Holer disconnection message
     */
    public static final byte TYPE_DISCONNECT = 0x04;

    /**
     * Holer data transfer
     */
    public static final byte TYPE_TRANSFER = 0x05;

    /**
     * Access key is in use by other holer client
     */
    public static final byte TYPE_IS_INUSE_KEY = 0x06;

    /**
     * Heart beat
     */
    public static final byte TYPE_HEARTBEAT = 0x07;

    /**
     * Disabled access key
     */
    public static final byte TYPE_DISABLED_ACCESS_KEY = 0x08;

    /**
     * Disabled trial client
     */
    public static final byte TYPE_DISABLED_TRIAL_CLIENT = 0x09;

    /**
     * Invalid access key
     */
    public static final byte TYPE_INVALID_KEY = 0x10;

    /**
     * Message type
     */
    private byte type;

    /**
     * Message type serial number
     */
    private long serialNumber;

    /**
     * Message request command
     */
    private String uri;

    /**
     * Message transfer data
     */
    private byte[] data;

    public byte getType()
    {
        return type;
    }

    public void setType(byte type)
    {
        this.type = type;
    }

    public long getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("HolerMsg [type=");
        builder.append(type);
        builder.append(", serialNumber=");
        builder.append(serialNumber);
        builder.append(", uri=");
        builder.append(uri);
        builder.append(", data=");
        builder.append(Arrays.toString(data));
        builder.append("]");
        return builder.toString();
    }
}
