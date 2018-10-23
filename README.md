### 参考鸣谢
* [RxHttpUtils](https://github.com/lygttpod/RxHttpUtils)
* [RxEasyHttp](https://github.com/zhou-you/RxEasyHttp)

### 简单使用
```

  RetrofitOkhttpClient.init(this);
  RetrofitOkhttpClient.getInstance()
                      .setBaseUrl("insert your base Http url")
                      .addLogInterceptor(true);

  YourApiServer yourApiServer = ApiHelper.getAPIService(YourApiServer.class);
  yourApiServer.doYourNetworkRequestMethod();
```
