package rx1;

import rx.Emitter;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by yuminfeng
 */
public class RxUtil {

    public static final String TAG = RxUtil.class.getSimpleName();

    public void create(){
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()){
                    subscriber.onNext("Hello");
                    subscriber.onNext("Rx Java");
                    subscriber.onNext("World!!");
                    subscriber.onCompleted();
                }
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println(TAG + " , onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(TAG + " , onError:" + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                System.out.println(TAG + " , onNext:" + s);
            }
        });
    }

    public void create2() {
        Observable<String> observable = Observable.create(new Action1<Emitter<String>>() {
            @Override
            public void call(Emitter<String> emitter) {
                emitter.onNext("hello");
                emitter.onNext("RxJava 1.x");
                emitter.onNext("world");
                emitter.onCompleted();
            }
        }, Emitter.BackpressureMode.NONE);

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println(TAG + " , onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                System.out.println(TAG + " , onError:" + e.getMessage());
            }
            @Override
            public void onNext(String s) {
                System.out.println(TAG + " , onNext:" + s);
            }
        };
        observable.subscribe(subscriber);
    }

    public void just(){
        Observable.just("hello,world!").subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(TAG + " , " + s);
            }
        });
    }
}
