import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx1.RxUtil;

/**
 * Created by yuminfeng
 */
public class Test {

    public static void main(String[] arg) {
        System.out.println("------- start ------");
        RxUtil rxUtil = new RxUtil();
//        rxUtil.create();
//        rxUtil.create2();
//        rxUtil.just();


//        Observable.just("Hello, world!")
//                .subscribe(s -> System.out.println(s + "-Dan"));

//        Observable.just("Hello,world!")
//                .map(s -> s.hashCode())
//                .subscribe(s -> System.out.println(s));

//        Observable.just("Hello, world!")
//                .map(s -> s.hashCode())
//                .map(i -> Integer.toString(i))
//                .subscribe(s -> System.out.println(s));

        Observable.just("Hello, world!")
                .map(s -> s + " -Dan")
                .map(s -> s.hashCode())
                .map(i -> Integer.toString(i))
                .subscribe(s -> System.out.println(s));
    }
}
