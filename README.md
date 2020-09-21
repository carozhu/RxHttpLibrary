## 简单使用
```
  implementation ('com.carozhu:RxHttpLibrary:2.0.0.8')
  //implementation 'com.carozhu:RxHttpLibrary:2.0.0.8@aar'
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

## Retrofit请求数据返回String
*[https://www.jianshu.com/p/64bbae45e479](https://www.jianshu.com/p/64bbae45e479)
我们一般为了方便解析数据,会在配置Retrofit时设置数据转为实体Bean
new Retrofit.Builder()
.addConverterFactory(GsonConverterFactory.create())// Gson转换器
...

但是如果我们在测试阶段,或不知道返回内容时,需要得到返回的String类型数据,该怎么做呢?
下面提供两种方法:
1.更换转换器为Scalars
//依赖
compile com.squareup.retrofit2:converter-scalars:2.0.0'  
// 配置
new Retrofit.Builder()  
.addConverterFactory(ScalarsConverterFactory.create())  
...
// 将返回类型修改为String
public interface ApiInterfacr {
@POST(Api.GetCarBrand)
Observable<String> getBrand();
}

ApiInterfacr apiInterfacr = retrofit.create(ApiInterfacr.class);
Observable<String> observable = apiInterfacr.getBrand();
......
.subscribe(new Observer<String>() {
......
@Override
public void onNext(String result) 

mResponseText.setText(result());
}
......
});

2.不用换转换器,修改Call<ResponseBody>类型必须是ResponseBody
public interface ApiInterfacr {
@POST(Api.GetCarBrand)
Observable<ResponseBody> getBrand();
}

ApiInterfacr apiInterfacr = retrofit.create(ApiInterfacr.class);
Observable<ResponseBody> observable = apiInterfacr.getBrand();
......
.subscribe(new Observer<ResponseBody>() {
......
@Override
public void onNext(ResponseBody result) {

try {
mResponseText.setText(result.string());
} catch (IOException e) {
e.printStackTrace();
}
}
......
});

作者：Endeav0r
链接：https://www.jianshu.com/p/64bbae45e479
来源：简书
简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
