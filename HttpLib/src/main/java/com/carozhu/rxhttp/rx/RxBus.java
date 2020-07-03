package com.carozhu.rxhttp.rx;


import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import io.reactivex.Observable;

/**
 * Rxjava2 rxbus
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 * 参考：https://gist.github.com/jaredsburrows/e9706bd8c7d587ea0c1114a0d7651d13
 * Rxbus 升级为 RxJava 2 版本 -- > http://www.jianshu.com/p/b22e6b10c6cf
 * <p>
 * <p>
 * 说明：
 * Rxjava2默认的 RxBus 在订阅者处理事件出现异常后，订阅者无法再收到事件，这是 RxJava 当初本身的设计原则，
 * 但是在事件总线中这反而是个问题，不过 JakeWharton 大神写了即使出现异常也不会终止订阅关系的 RxRelay，
 * 所以基于 RxRelay 就能写出有异常处理能力的 Rxbus。--->http://johnnyshieh.github.io/android/2017/03/10/rxbus-rxjava2/
 * JakeWharton/RxRelay:https://github.com/JakeWharton/RxRelay
 * <p>
 * 支持3种Bus:
 * Publish Bus( RxBus，参见 PublishSubject )
 * Behavior Bus(参见 BehaviorSubject)
 * Replay Bus(参见ReplaySubject)
 * 等3中Rxbus类型可参考博客：http://blog.csdn.net/u011271348/article/details/69946650
 * </p>
 * 说明一下，RxBus支持注解方式订阅和粘滞事件。BehaviorBus和ReplayBus都是不支持的（仅支持手动订阅和发普通事件。
 * 为什么不支持注解呢，因为这两种并不常(我)用(懒)。至于不支持粘滞事件，那是因为这两个天生不合适(这是真的).
 * 比如对于ReplayBus，他发的每个事件就是粘滞事件（因为每个订阅者不管什么时候订阅都能获取到订阅前的所有事件））。
 * 想必理解三种Bus特点的同学估计已经知道原因了。不明白的请看看PublishSubject、BehaviorSubject、ReplaySubject的不同点
 * </p> from -http://www.jianshu.com/p/7f4a709d2be5
 * <p>
 * 参考来自：http://www.jianshu.com/p/81fe1e3715bd
 * 带code的封装参考：http://www.jianshu.com/p/b65797e44ec0
 *
 * 基于Kotlin实现的三种 RxBus:参考http://www.jianshu.com/p/86fe5ecd6ea5
 */

/**
 * 有异常处理的 Rxbus -- JakeWharton 大神写了即使出现异常也不会终止订阅关系的 RxRelay，
 * 所以基于 RxRelay 就能写出有异常处理能力的 Rxbus
 */

public final class RxBus {
    private static volatile RxBus instance;
    /**
     * 主题
     */
    private final Relay<Object> mBus;

    /**
     * PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
     */
    private RxBus() {
        this.mBus = PublishRelay.create().toSerialized();
    }

    /**
     * 单列
     *
     * @return
     */
    public static RxBus getDefault() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = Holder.BUS;
                }
            }
        }
        return instance;
    }

    /**
     * 提供了一个新的事件
     * 发布
     *
     * @param obj
     */
    public void post(Object obj) {
        mBus.accept(obj);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }
}


//usage simple for toObservable  参考：http://blog.csdn.net/u011271348/article/details/69946650
//    private void operateBus() {
//        RxBus.getDefault().toObservable()
//                .map(new Function<Object, ActicleBean.OthersBean>() {
//                    @Override
//                    public ActicleBean.OthersBean apply(@NonNull Object o) throws Exception {
//                        return (ActicleBean.OthersBean) o;
//                    }
//
//                })
//                .subscribe(new Consumer<ActicleBean.OthersBean>() {
//                    @Override
//                    public void accept(@NonNull ActicleBean.OthersBean othersBean) throws Exception {
//                        if (othersBean != null) {
//                            ToastUtils.toast(ActicleListActivity.this, "othersBean" + othersBean.getDescription());
//                        }
//                    }
//
//                });
//    }
