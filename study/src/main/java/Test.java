import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jason_moo on 2018/6/12.
 */
public class Test {


    private Lock lock1 = new ReentrantLock();

    private Lock lock2 =  new ReentrantLock();


    private void test(){

        lock1.lock();
        try {

            lock2.lock();
                // 做什么事情
            lock2.unlock();

        }catch (Exception e){

        }finally {
            lock1.unlock();
        }
    }

}
