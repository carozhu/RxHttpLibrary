package com.carozhu.rxhttp.rx;

/**
 * Created by caro
 * 参考：http://www.jianshu.com/p/b65797e44ec0
 */

public class RxCodeAction {
    // 消息 Code
    public int code;
    // 消息对象
    public Object data;

    public RxCodeAction(int code, Object data) {
        this.code = code;
        this.data = data;
    }

}
