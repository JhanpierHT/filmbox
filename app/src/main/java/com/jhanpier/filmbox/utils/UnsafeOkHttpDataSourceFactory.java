package com.jhanpier.filmbox.utils;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.okhttp.OkHttpDataSource;

import okhttp3.OkHttpClient;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;

public class UnsafeOkHttpDataSourceFactory {

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

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            return new OkHttpDataSource.Factory(okHttpClient);

        } catch (Exception e) {
            throw new RuntimeException("Error creando UnsafeOkHttpDataSourceFactory", e);
        }
    }
}
