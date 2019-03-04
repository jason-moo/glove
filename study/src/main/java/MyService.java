import java.util.concurrent.*;

/**
 * Created by jason_moo on 2018/1/17.
 */
public class MyService {

    static class MyThread extends Thread {
        CyclicBarrier cyclicBarrier;

        public MyThread(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            try {
                System.out.println("逐步到达起跑点！");
                Thread.sleep((int) (Math.random() * 10000) );
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (BrokenBarrierException e){
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ConcurrentSkipListMap concurrentSkipListMap;
        ConcurrentLinkedQueue concurrentLinkedQueue;
        CopyOnWriteArrayList copyOnWriteArrayList;
        ArrayBlockingQueue arrayBlockingQueue;
        PriorityBlockingQueue priorityBlockingQueue;

        LinkedBlockingDeque linkedBlockingDeque = new LinkedBlockingDeque();
        linkedBlockingDeque.poll();


    }

}
