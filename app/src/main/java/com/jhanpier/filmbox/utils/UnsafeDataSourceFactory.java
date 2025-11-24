package com.jhanpier.filmbox.utils;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;

public class UnsafeDataSourceFactory {

    @UnstableApi
    public static DataSource.Factory create() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            HostnameVerifier hostnameVerifier = (hostname, session) -> true;

            DefaultHttpDataSource.Factory baseFactory = new DefaultHttpDataSource.Factory()
                    .setAllowCrossProtocolRedirects(true);

            return () -> {
                try {
                    DefaultHttpDataSource ds = (DefaultHttpDataSource) baseFactory.createDataSource();

                    Field field = ds.getClass().getDeclaredField("connectionFactory");
                    field.setAccessible(true);
                    field.set(ds, new HttpsURLConnectionFactory(sslSocketFactory, hostnameVerifier));

                    return ds;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear UnsafeDataSourceFactory", e);
        }
    }

    public static class HttpsURLConnectionFactory {

        private final SSLSocketFactory sslSocketFactory;
        private final HostnameVerifier hostnameVerifier;

        public HttpsURLConnectionFactory(SSLSocketFactory factory, HostnameVerifier verifier) {
            this.sslSocketFactory = factory;
            this.hostnameVerifier = verifier;
        }

        public void configureConnection(HttpsURLConnection connection) {
            connection.setSSLSocketFactory(sslSocketFactory);
            connection.setHostnameVerifier(hostnameVerifier);
        }
    }
}
