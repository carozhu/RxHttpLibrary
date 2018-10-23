package com.carozhu.rxhttp.retrofitOkhttp;

import android.app.Application;


import com.carozhu.rxhttp.cookie.CookieManger;
import com.carozhu.rxhttp.https.HttpsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;


import org.apache.http.params.HttpParams;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.disposables.Disposable;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * 参考来自RxEasyHttp-master
 * 目的是主要加入自己的优化和需求
 * 且Github上的RxEasyHttp库多少存在一些问题。
 * 故根据自己情况封装。
 * <p>
 * TODO 01 ： 缓存换做RxCache
 */
public class RetrofitOkhttpClient {
    private static Application application;
    public static final int DEFAULT_MILLISECONDS = 60000;             //默认的超时时间
    private static final int DEFAULT_RETRY_COUNT = 3;                 //默认重试次数
    private static final int DEFAULT_RETRY_INCREASEDELAY = 0;         //默认重试叠加时间
    private static final int DEFAULT_RETRY_DELAY = 500;               //默认重试延时

    private String mBaseUrl;                                          //全局BaseUrl
    private int mRetryCount = DEFAULT_RETRY_COUNT;                    //重试次数默认3次
    private int mRetryDelay = DEFAULT_RETRY_DELAY;                    //延迟xxms重试
    private int mRetryIncreaseDelay = DEFAULT_RETRY_INCREASEDELAY;    //叠加延迟
    private HttpHeaders mCommonHeaders;                               //全局公共请求头

    private OkHttpClient.Builder okHttpClientBuilder;                 //okhttp请求的客户端
    private Retrofit.Builder retrofitBuilder;                         //Retrofit请求Builder

    private CookieManger cookieManager;                               //Cookie管理

    final private Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    public RetrofitOkhttpClient() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(new DefaultHostnameVerifier());
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.retryOnConnectionFailure(true);


        retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder
                //.addConverterFactory(GsonConverterFactory.create())//添加gson转换器 default
                .addConverterFactory(ScalarsConverterFactory.create())//解决retrofit2图文上传文件参数多双引号问题
                .addConverterFactory(GsonConverterFactory.create(gson))//添加gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());//添加rxjava转换器

    }

    public static void init(Application mApplication) {
        application = mApplication;
    }

    public static class RetrofitOkhttpClientHolder {
        private static final RetrofitOkhttpClient INSTANCE = new RetrofitOkhttpClient();
    }

    public static final RetrofitOkhttpClient getInstance() {
        checkNotNull(application, "please init RetrofitOkhttpClient in your Application");
        return RetrofitOkhttpClientHolder.INSTANCE;
    }

    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return okHttpClientBuilder;
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return retrofitBuilder;
    }


    public Retrofit getRetrofit() {

        return retrofitBuilder.client(okHttpClientBuilder.build()).build();
    }

    public RetrofitOkhttpClient configGsonConverterFactory() {
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
        return this;
    }

    public RetrofitOkhttpClient configJacksonConverterFactory() {
        retrofitBuilder.addConverterFactory(JacksonConverterFactory.create());
        return this;
    }


    /**
     * 设置全局base
     *
     * @param baseUrl
     * @return
     */
    public RetrofitOkhttpClient setBaseUrl(String baseUrl) {
        checkNotNull(baseUrl, "baseUrl cannot null");
        retrofitBuilder.baseUrl(baseUrl);
        return this;
    }


    public <T> T createApi(Class<T> clazz) {

        return getRetrofit().create(clazz);
    }

    /**
     * add DNS
     *
     * @param dns
     * @return
     */
    public RetrofitOkhttpClient addDns(Dns dns) {
        okHttpClientBuilder.dns(dns);
        return this;
    }

    /**
     * config connect timeout
     */
    public RetrofitOkhttpClient setConnectTimeout(long timeout, TimeUnit unit) {
        okHttpClientBuilder.connectTimeout(timeout, unit);
        return this;
    }

    /**
     * TODO
     * add Cache  with RxCache
     */
    public RetrofitOkhttpClient addCacheInterceptor() {

        return this;
    }

    /**
     * 添加日志拦截器
     * 是否开启请求日志
     *
     * @return
     */
    public RetrofitOkhttpClient addLogInterceptor(boolean loggable, String VERSION_NAME) {
        if (loggable) {
            okHttpClientBuilder.addInterceptor(new LoggingInterceptor.Builder()
                    .loggable(loggable)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("Request")
                    .response("Response")
                    //.addHeader("version", VERSION_NAME)
                    //.addQueryParam("query", "0")
                    /*
                    .logger(new Logger() {
                        @Override
                        public void log(int level, String tag, String msg) {
                            Log.w(tag, msg);
                        }
                    })
                    .executor(Executors.newSingleThreadExecutor())
                    */
                    .build());
        }
        return this;
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    /**
     * https的全局访问规则
     */
    public RetrofitOkhttpClient setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /**
     * https的全局自签名证书
     */
    public RetrofitOkhttpClient setCertificates(InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, certificates);
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    /**
     * https双向认证证书
     */
    public RetrofitOkhttpClient setCertificates(InputStream bksFile, String password, InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates);
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    /**
     * 全局cookie存取规则
     */
    public RetrofitOkhttpClient setCookieStore(CookieManger mCookieManager) {
        cookieManager = mCookieManager;
        okHttpClientBuilder.cookieJar(cookieManager);
        return this;
    }

    /**
     * 获取全局的cookie实例
     */
    public static CookieManger getCookieJar() {
        return getInstance().cookieManager;
    }

    /**
     * 全局读取超时时间
     */
    public RetrofitOkhttpClient setReadTimeOut(long readTimeOut) {
        okHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局写入超时时间
     */
    public RetrofitOkhttpClient setWriteTimeOut(long writeTimeout) {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局连接超时时间
     */
    public RetrofitOkhttpClient setConnectTimeout(long connectTimeout) {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 超时重试次数
     */
    public RetrofitOkhttpClient setRetryCount(int retryCount) {
        if (retryCount < 0) {
            throw new IllegalArgumentException("retryCount must > 0");
        }
        mRetryCount = retryCount;
        return this;
    }

    /**
     * 超时重试次数
     */
    public static int getRetryCount() {
        return getInstance().mRetryCount;
    }

    /**
     * 超时重试延迟时间
     */
    public RetrofitOkhttpClient setRetryDelay(int retryDelay) {
        if (retryDelay < 0) {
            throw new IllegalArgumentException("retryDelay must > 0");
        }
        mRetryDelay = retryDelay;
        return this;
    }

    /**
     * 超时重试延迟时间
     */
    public static int getRetryDelay() {
        return getInstance().mRetryDelay;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public RetrofitOkhttpClient setRetryIncreaseDelay(int retryIncreaseDelay) {
        if (retryIncreaseDelay < 0) {
            throw new IllegalArgumentException("retryIncreaseDelay must > 0");
        }
        mRetryIncreaseDelay = retryIncreaseDelay;
        return this;
    }

    /**
     * 超时重试延迟叠加时间
     */
    public static int getRetryIncreaseDelay() {
        return getInstance().mRetryIncreaseDelay;
    }

    /**
     * 取消订阅
     */
    public static void cancelSubscription(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}
