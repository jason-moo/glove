package concurrent.semaphore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author xuezn
 * @Date 2019年02月21日 16:51:34
 */
public class ListPool {

    private List<String> list = new ArrayList<>();
    private int poolMaxSize = 3;
    private int semaphorePermits = 5;
    private Semaphore semaphore = new Semaphore(semaphorePermits);
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public ListPool() {
        for (int i = 0; i < poolMaxSize; i++) {
            list.add("薛振南" + i+1);
        }
    }

    public String get(){
        String getString = null;
        try {
            semaphore.acquire();
            lock.lock();
            while (list.size() == 0){
                condition.await();
            }
            getString = list.remove(0);
            lock.unlock();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return getString;
    }

    public void put(String msg){
        lock.lock();
        list.add(msg);
        condition.signalAll();
        lock.unlock();
        semaphore.release();
    }

    static class MyThread extends Thread{
        private ListPool listPool;

        public MyThread(ListPool listPool) {
            this.listPool = listPool;
        }

        @Override
        public void run() {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                String getString = listPool.get();
                System.out.println(Thread.currentThread().getName() + "取得值" + getString);
                listPool.put(getString);
            }
        }
    }

    public static void main(String[] args) {
        ListPool listPool = new ListPool();
        MyThread[] myThreads = new MyThread[12];

        for (int i = 0; i < 12; i++) {
            myThreads[i] = new MyThread(listPool);
        }

        Arrays.asList(myThreads).stream().forEach(e -> {
            e.start();
        });
    }
}
