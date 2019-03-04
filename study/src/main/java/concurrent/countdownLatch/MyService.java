package concurrent.countdownLatch;

import java.util.concurrent.CountDownLatch;

/**
 * @Author xuezn
 * @Date 2019Äê02ÔÂ22ÈÕ 14:21:03
 */
public class MyService {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void testMethod(){
        try {
            System.out.println("A");
            countDownLatch.await();
            System.out.println("B");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void downMethod(){
        System.out.println("X");
        countDownLatch.countDown();
    }

    static class MyThread extends Thread{

        private MyService myService;

        public MyThread(MyService myService) {
            this.myService = myService;
        }

        @Override
        public void run() {
            myService.testMethod();
        }
    }


    public static void main(String[] args) {
        MyService myService = new MyService();
        MyThread thread = new MyThread(myService);
        thread.start();
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        myService.downMethod();
    }
}
