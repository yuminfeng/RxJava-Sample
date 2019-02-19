package rx1.creation;

import rx.Emitter;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx1.RxUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Creating Observables
 */
public class CreationDemo {

    public static final String TAG = RxUtil.class.getSimpleName();

    public void create() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
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

        /**
         *
         Observable.create((Observable.OnSubscribe<String>) subscriber -> {
         if (!subscriber.isUnsubscribed()) {
         subscriber.onNext("Hello");
         subscriber.onNext("Rx Java");
         subscriber.onNext("World!!");
         subscriber.onCompleted();
         }
         }).subscribe(new Observer<String>() {
        @Override public void onCompleted() {
        System.out.println(TAG + " , onCompleted");
        }

        @Override public void onError(Throwable e) {
        System.out.println(TAG + " , onError:" + e.getMessage());
        }

        @Override public void onNext(String s) {
        System.out.println(TAG + " , onNext:" + s);
        }
        });
         */
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

    /**
     * 返回 将一个或多个对象转换成发射这个或这些对象的 Observable
     */
    public void just() {
        Observable.just("Hello world!", " - yumf").subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(TAG + " , " + s);
            }
        });

        /**
         *  lambda
         *  Observable.just(greeting).subscribe(s -> System.out.println(TAG + " , " + s));
         */
    }

    /**
     * 返回一个 能将Iterable ,Array 或 Future 发射的 Observable
     */
    public void from() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        Observable.from(list).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println(TAG + " , " + integer);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println(TAG + " , " + throwable.getMessage());
            }
        }, new Action0() {
            @Override
            public void call() {
                System.out.println(TAG + " , complete");
            }
        });

        /**
         *
         *   Observable.from(list).subscribe(integer -> System.out.println(TAG + " , " + integer),
                    throwable -> System.out.println(TAG + " , " + throwable.getMessage()),
                    () -> System.out.println(TAG + " , complete"));
         */
    }

    /**
     *
     * 返回一个按照指定时间间隔发出序列号的 Observable 对象。
     *  默认运行在一个新线程上面
     */
    public void interval() {

        Observable.interval(1, TimeUnit.SECONDS, Schedulers.trampoline()).subscribe(time -> {
            if (time % 2 == 0) {
                System.out.println("Tick");
            } else {
                System.out.println("Tock");
            }
        });
    }

    /**
     * 返回一个 在指定延迟后发出的数据的 Observable
     * 默认运行在一个新线程上面
     */
    public void timer() {

        /**
         *
            Observable.timer(2, TimeUnit.SECONDS, Schedulers.trampoline())
                .subscribe(v -> System.out.println("Egg is ready!"));
         */

        Observable.timer(2, TimeUnit.SECONDS, Schedulers.trampoline())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        System.out.println("Egg is ready!" + aLong);
                    }
                });
    }
}
