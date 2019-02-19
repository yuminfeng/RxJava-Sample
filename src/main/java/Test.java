import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
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

        query()
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> strings) {
                        return Observable.from(strings);
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s != null;
                    }
                })
                .flatMap(new Func1<String, Observable<?>>() {
                    @Override
                    public Observable<?> call(String s) {
                        return Observable.just(s + "__end ");
                    }
                })
                .take(3)
                .subscribe(s -> System.out.println(s));
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
