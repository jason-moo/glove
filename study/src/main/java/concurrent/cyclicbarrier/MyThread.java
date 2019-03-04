package concurrent.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @Author xuezn
 * @Date 2019��02��22�� 14:40:53
 */
public class MyThread extends Thread {

    private CyclicBarrier cyclicBarrier;

    public MyThread(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((int)(Math.random() * 1000));
            System.out.println(Thread.currentThread().getName() + "����");
            cyclicBarrier.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }catch (BrokenBarrierException e){
            e.printStackTrace();
        }
    }

    static class Main{
        public static void main(String[] args) {

            CyclicBarrier cyclicBarrier = new CyclicBarrier(5, new Runnable() {
                @Override
                public void run() {
                    System.out.println("ȫ�������ˣ�");
                }
            });
            MyThread[] myThreads = new MyThread[10];
            for (int i = 0; i < 10; i++) {
                myThreads[i] = new MyThread(cyclicBarrier);
            }
            for (int i = 0; i < 10; i++) {
                myThreads[i].start();
            }
        }
    }
}
