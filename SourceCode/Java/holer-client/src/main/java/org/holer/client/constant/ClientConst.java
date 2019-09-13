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

package org.holer.client.constant;

/** 
* @Class Name : ClientConst 
* @Description: Holer client constant definition 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Apr 13, 2018 1:12:09 PM 
* @Version    : Holer V1.2 
*/
public class ClientConst
{
    public static final int MAX_POOL_SIZE = 100;

    public static final String HOLER_ACCESS_KEY = "HOLER_ACCESS_KEY";

    public static final String MSG_NO_AVAILABLE_PORT = "There are no available ports for the holer access key.\n";

    public static final String MSG_IS_INUSE_KEY = "Holer access key is in use by other holer client.\n" + 
                                                  "If you want to have exclusive holer service\n" + 
                                                  "please visit 'www.wdom.net' for more details.\n";

    public static final String MSG_DISABLED_ACCESS_KEY = "Holer access key has been disabled.\n";

    public static final String MSG_INVALID_ACCESS_KEY = "Holer access key is not valid.\n";

    public static final String MSG_DISABLED_TRIAL_CLIENT = "Your holer client is overuse.\n" + 
                                                           "The trial holer access key can only be used for 20 minutes in 24 hours.\n" + 
                                                           "If you want to have exclusive holer service\n" + 
                                                           "please visit 'www.wdom.net' for more details.\n";

}
