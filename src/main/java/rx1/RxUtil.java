package rx1;

import rx1.creation.CreationDemo;

/**
 * Created by yuminfeng
 */
public class RxUtil {

    public static final String TAG = RxUtil.class.getSimpleName();
    CreationDemo creationDemo = new CreationDemo();

    public void create() {
        creationDemo.create();
    }

    public void just() {
        creationDemo.just();
    }

    public void from(){
        creationDemo.from();
    }

    public void interval(){
        creationDemo.interval();
    }

    public void timer(){
        creationDemo.timer();
    }
}
