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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @Class Name : HolerContainerHelper 
* @Description: Holer container helper 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@outlook.com
* @Date       : Mar 21, 2018 9:43:55 AM 
* @Version    : Wisdom Holer V1.0 
*/
public class HolerContainerHelper
{
    private static Logger log = LoggerFactory.getLogger(HolerContainerHelper.class);

    private static volatile boolean running = true;

    private static List<HolerContainer> cachedContainers;

    /**
    * 
    * @Title      : startContainers 
    * @Description: Start all the containers
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    private static void startContainers()
    {
        for (HolerContainer container : cachedContainers)
        {
            log.info("Starting container [{}].", container.getClass().getName());
            container.start();
            log.info("Container [{}] started.", container.getClass().getName());
        }
    }

    /**
    * 
    * @Title      : stopContainers 
    * @Description: Stop all the containers
    * @Param      :  
    * @Return     : void
    * @Throws     :
     */
    private static void stopContainers()
    {
        for (HolerContainer container : cachedContainers)
        {
            log.info("Stopping container [{}].", container.getClass().getName());
            try
            {
                container.stop();
                log.info("Container [{}] stopped.", container.getClass().getName());
            }
            catch(Exception e)
            {
                log.warn("Container stopped with exception.", e);
            }
        }
    }

    /**
    * 
    * @Title      : stop 
    * @Description: Stop running 
    * @Param      : @param status 
    * @Return     : void
    * @Throws     :
     */
    public static void stop(int status)
    {
        stopContainers();
        log.info("Stopped holer client with status {}.", status);
        System.exit(status);
    }

    /**
    * 
    * @Title      : start 
    * @Description: Start all the containers 
    * @Param      : @param containers 
    * @Return     : void
    * @Throws     :
     */
    public static void start(List<HolerContainer> containers)
    {
        cachedContainers = containers;

        // Start all the containers
        startContainers();

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                synchronized (HolerContainerHelper.class)
                {
                    // Stop all the containers
                    stopContainers();
                    running = false;
                    HolerContainerHelper.class.notify();
                }
            }
        });

        synchronized (HolerContainerHelper.class)
        {
            while (running)
            {
                try
                {
                    HolerContainerHelper.class.wait();
                }
                catch(Throwable e)
                {
                    // Ignore this exception
                }
            }
        }
    }
}
