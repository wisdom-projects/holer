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

package org.holer.common.container;

/** 
* @Class Name : HolerContainer 
* @Description: Holer common container 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@outlook.com
* @Date       : Mar 20, 2018 8:16:41 PM 
* @Version    : Wisdom Holer V1.0 
*/
public interface HolerContainer
{
    /**
    * 
    * @Title      : init 
    * @Description: Container initialization
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    void init();
    
    /**
    * 
    * @Title      : start 
    * @Description: Start container
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    void start();

    /**
    * 
    * @Title      : stop 
    * @Description: Stop container
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    void stop();
}
