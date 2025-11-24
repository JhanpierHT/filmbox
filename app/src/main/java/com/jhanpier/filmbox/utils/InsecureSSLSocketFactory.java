package com.jhanpier.filmbox.utils;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class InsecureSSLSocketFactory {

    public static final X509TrustManager trustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    };

    public static final SSLContext sslContext;

    static {
        SSLContext contextTemp = null;
        try {
            contextTemp = SSLContext.getInstance("SSL");
            contextTemp.init(null, new TrustManager[]{ trustManager }, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sslContext = contextTemp;
    }
}
