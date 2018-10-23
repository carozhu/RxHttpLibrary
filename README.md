## 简单使用
```
  implementation 'com.carozhu:RxHttpLibrary:1.2.6@aar'
  RetrofitOkhttpClient.init(this);
  RetrofitOkhttpClient.getInstance()
                      .setBaseUrl("insert your base Http url")
                      .addLogInterceptor(true);

  YourApiServer yourApiServer = ApiHelper.getAPIService(YourApiServer.class);
  yourApiServer.doYourNetworkRequestMethod();
```
## 参考鸣谢
* [RxHttpUtils](https://github.com/lygttpod/RxHttpUtils)
* [RxEasyHttp](https://github.com/zhou-you/RxEasyHttp)