import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
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


        Observable.just("Hello, world!")
                .subscribe(s -> System.out.println(s));
    }
}
