import rx.Observable;
import rx.functions.Action1;

/**
 * Created by yuminfeng on 2019/2/15.
 */
public class Test {

    public static void main(String[] arg) {
        System.out.println("hello world!!");

        String[] names = new String[]{"hello", "world!"};
        Observable.from(names)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String name) {
                        System.out.println(name);
                    }
                });
    }
}
