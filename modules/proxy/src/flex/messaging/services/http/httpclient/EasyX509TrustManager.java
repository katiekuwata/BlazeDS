/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */

package flex.messaging.services.http.httpclient;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import flex.messaging.util.Trace;

/*
 * ====================================================================
 *
 *  Copyright 2002-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

/**
 * @exclude
 * <p>
 * EasyX509TrustManager unlike default {@link javax.net.ssl.X509TrustManager} accepts
 * self-signed certificates.
 * </p>
 * <p>
 * This trust manager SHOULD NOT be used for productive systems
 * due to security reasons, unless it is a concious decision and
 * you are perfectly aware of security implications of accepting
 * self-signed certificates
 * </p>
 *
 *  <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component.
 * The component is provided as a reference material, which may be inappropriate
 * use without additional customization.
 * </p>
 * @author <a href="mailto:adrian.sutton@ephox.com">Adrian Sutton</a>
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a> 
 */
public class EasyX509TrustManager implements X509TrustManager
{
    private X509TrustManager standardTrustManager = null;

    private boolean trustStore;

    /**
     * Constructor for EasyX509TrustManager.
     */
    public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException
    {
        super();
        TrustManagerFactory factory = null;
        try
        {
            factory = TrustManagerFactory.getInstance("SunX509");
        }
        catch (NoSuchAlgorithmException nsae)
        {
            // Fallback attempt - try for an IbmX509 factory in case we're running in WAS with no Sun providers registered.
            try
            {
                factory = TrustManagerFactory.getInstance("IbmX509");
            }
            catch (NoSuchAlgorithmException nsae2)
            {
                throw new NoSuchAlgorithmException("Neither SunX509 nor IbmX509 trust manager supported.");
            }
        }
        factory.init(keystore);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length == 0)
        {
            
            factory.init(keystore);
            trustmanagers = factory.getTrustManagers();
            
            // If we still have no trust managers, throw.
            if (trustmanagers.length == 0)
                throw new NoSuchAlgorithmException("Neither SunX509 nor IbmX509 trust manager supported.");
        }
        this.standardTrustManager = (X509TrustManager)trustmanagers[0];

        // very lax settings must be used if flex.trustStore is being used
        trustStore = (System.getProperty("flex.trustStore") != null);
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[] x509Certificates, String authType)
     */
    public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException
    {
        if (trustStore)
        {
            return;
        }
        standardTrustManager.checkServerTrusted(certificates, authType);
    }

    /**
     * @see com.sun.net.ssl.X509TrustManager#isServerTrusted(X509Certificate[])
     */
    public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException
    {
        if (trustStore)
        {
            return;
        }
        if (certificates != null)
        {
            if (Trace.ssl)
            {
                Trace.trace("Server certificate chain:");
                for (int i = 0; i < certificates.length; i++)
                {
                    Trace.trace("X509Certificate[" + i + "]=" + certificates[i]);
                }
            }
        }
        if ((certificates != null) && (certificates.length == 1))
        {
            X509Certificate certificate = certificates[0];
            try
            {
                certificate.checkValidity();
            }
            catch (CertificateException e)
            {
                if (Trace.ssl)
                {
                    Trace.trace(e.toString());
                }
                throw e;
            }
        }
        else
        {
            standardTrustManager.checkServerTrusted(certificates, authType);
        }
    }

    /**
     * @see com.sun.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers()
    {
        return this.standardTrustManager.getAcceptedIssuers();
    }
}
