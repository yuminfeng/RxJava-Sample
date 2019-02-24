import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx1.RxUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuminfeng
 */
public class Test {

    public static void main(String[] arg) {
        System.out.println("------- start ------");
        RxUtil rxUtil = new RxUtil();
//        rxUtil.create();
//        rxUtil.just();
//        rxUtil.from();
//        rxUtil.interval();
//        rxUtil.timer();

//        query()
//                .flatMap(new Func1<List<String>, Observable<String>>() {
//                    @Override
//                    public Observable<String> call(List<String> strings) {
//                        return Observable.from(strings);
//                    }
//                })
//                .filter(new Func1<String, Boolean>() {
//                    @Override
//                    public Boolean call(String s) {
//                        return s != null;
//                    }
//                })
//                .flatMap(new Func1<String, Observable<?>>() {
//                    @Override
//                    public Observable<?> call(String s) {
//                        return Observable.just(s + "__end ");
//                    }
//                })
//                .take(3)
//                .subscribe(s -> System.out.println(s));

        Observable.just("Hello, world!")
                .subscribeOn(Schedulers.newThread())  //指定Observable自身在哪个调度器上执行
//                .map(s -> potentialException(s))
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        System.out.println(Thread.currentThread().getName());
                        return s;
                    }
                })
                .observeOn(Schedulers.computation()) //指定一个观察者在哪个调度器上观察这个Observable
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        System.out.println(Thread.currentThread().getName());
                        System.out.println(s);
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Ouch!");
                    }
                });
    }

    private static String potentialException(String s) {
        int flg = 10/0;
        return s + flg;
    }

    public static Observable<List<String>> query() {
        List<String> urls = new ArrayList<>();
        urls.add("hello");
        urls.add("world!");
        urls.add(null);
        urls.add("hello");
        urls.add("world!");
        return Observable.just(urls);
    }
}
