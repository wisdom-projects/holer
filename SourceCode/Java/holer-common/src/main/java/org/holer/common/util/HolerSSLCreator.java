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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.holer.common.constant.HolerConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @Class Name : HolerSSLCreator 
* @Description: Holer SSL context creator 
* @Author     : Yudong (Dom) Wang 
* @Email      : wisdomtool@qq.com
* @Date       : Apr 13, 2018 5:09:44 PM 
* @Version    : Wisdom Holer V1.0 
*/
public class HolerSSLCreator
{
    private static Logger log = LoggerFactory.getLogger(HolerSSLCreator.class);

    private HolerConfig config = HolerConfig.getConfig();

    private SSLContext sslCtx = null;

    private static HolerSSLCreator creator = null;

    public static HolerSSLCreator getCreator()
    {
        // Initialize creator
        if (null == creator)
        {
            creator = new HolerSSLCreator();
        }
        return creator;
    }
    
    /** 
    * @Title      : HolerSSLCreator 
    * @Description: Default constructor 
    * @Param      : 
    */
    public HolerSSLCreator()
    {
        this.sslCtx = this.init();
    }

    /**
    * 
    * @Title      : getSSLContext 
    * @Description: Get SSL context 
    * @Param      : @return 
    * @Return     : SSLContext
    * @Throws     :
     */
    public SSLContext getSSLContext()
    {
        return this.sslCtx;
    }

    /**
    * 
    * @Title      : loadJks 
    * @Description: Get JKS file input stream 
    * @Param      : @param jksPath
    * @Param      : @return
    * @Param      : @throws FileNotFoundException 
    * @Return     : InputStream
    * @Throws     :
     */
    private InputStream loadJks(String jksPath) throws FileNotFoundException
    {
        ClassLoader loader = HolerSSLCreator.class.getClassLoader();
        URL ju = loader.getResource(jksPath);
        if (null != ju)
        {
            log.info("Starting with jks file: {}.", jksPath);
            return loader.getResourceAsStream(jksPath);
        }

        log.warn("No keystore has been found in the bundled resources. Scanning filesystem...");
        File jf = new File(jksPath);
        if (jf.exists())
        {
            log.info("Loading external keystore. jks file: {}.", jksPath);
            return new FileInputStream(jf);
        }

        log.warn("The keystore file does not exist. jks file: {}.", jksPath);
        return null;
    }
    
    /**
    * 
    * @Title      : init 
    * @Description: Initializing SSL context 
    * @Param      : @return 
    * @Return     : SSLContext
    * @Throws     :
     */
    public SSLContext init()
    {
        log.info("Checking SSL configuration...");
        final String jks = config.strValue(HolerConst.HOLER_SSL_JKS, HolerConst.HOLER_SSL_JKS_DEFAULT);
        log.info("Initializing SSL context. Keystore path: {}.", jks);

        // If the jks is existed, then key store and key manager password is required
        final String sslPaswd = config.strValue(HolerConst.HOLER_SSL_PASSWD, HolerConst.HOLER_SSL_PASSWD_DEFAULT);

        try
        {
            log.info("Loading keystore. Keystore path: {}.", jks);
            final KeyStore ks = KeyStore.getInstance(HolerConst.JKS);
            ks.load(this.loadJks(jks), sslPaswd.toCharArray());

            log.info("Initializing key manager...");
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, sslPaswd.toCharArray());

            // A trust manager needs to be added to the server context.
            // Use key store as trust store, as server needs to trust
            // Certificates signed by the server certificates
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            // Initialize SSl context
            log.info("Initializing SSL context...");
            SSLContext sslCtx = SSLContext.getInstance(HolerConst.TLS);
            sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            log.info("The SSL context has been initialized successfully.");

            return sslCtx;
        }
        catch(Exception e)
        {
            log.error("Unable to initialize SSL context. Cause: {}, error message: {}.", e.getCause(), e.getMessage());
            return null;
        }
    }
}
