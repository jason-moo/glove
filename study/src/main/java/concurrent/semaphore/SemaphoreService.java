package concurrent.semaphore;

import java.util.concurrent.Semaphore;

/**
 * @Author xuezn
 * @Date 2019年02月21日 15:51:25
 */
public class SemaphoreService {

    private Semaphore semaphore = new Semaphore(1);

    private Semaphore semaphorB = new Semaphore(10);

    public void testMethod() {
        try {
            System.out.println(Thread.currentThread().getName() + "进入测试方法！" );
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " begin timer " + System.currentTimeMillis());
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + " end timer " + System.currentTimeMillis());
            semaphore.release();
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testDrainPermits(){
        try {
            semaphorB.acquireUninterruptibly();
            System.out.println(semaphorB.availablePermits());
            System.out.println(semaphorB.drainPermits() + "   "+ semaphorB.availablePermits());
        }finally {
            semaphorB.release();
            System.out.println(semaphorB.availablePermits());
        }
    }

    static class ThreadA extends Thread {

        private SemaphoreService sepaphoreService;

        public ThreadA(SemaphoreService sepaphoreService) {
            super();
            this.sepaphoreService = sepaphoreService;
        }

        @Override
        public void run() {
            sepaphoreService.testMethod();
        }
    }


    static class ThreadB extends Thread {

        private SemaphoreService sepaphoreService;

        public ThreadB(SemaphoreService sepaphoreService) {
            super();
            this.sepaphoreService = sepaphoreService;
        }

        @Override
        public void run() {
            sepaphoreService.testMethod();
        }
    }

    static class ThreadC extends Thread {

        private SemaphoreService sepaphoreService;

        public ThreadC(SemaphoreService sepaphoreService) {
            super();
            this.sepaphoreService = sepaphoreService;
        }

        @Override
        public void run() {
            sepaphoreService.testMethod();
        }
    }

//    public static void main(String[] args) throws Exception{
//        SemaphoreService semaphoreService = new SemaphoreService();
//        ThreadA threadA = new ThreadA(semaphoreService);
//        ThreadB threadB = new ThreadB(semaphoreService);
//        ThreadC threadc = new ThreadC(semaphoreService);
//        threadA.start();
//        threadB.start();
//        threadc.start();
//        Thread.sleep(10000);
//        System.out.println(semaphoreService.semaphore.availablePermits());
//    }

    public static void main(String[] args) {
        SemaphoreService semaphoreService = new SemaphoreService();
        semaphoreService.testDrainPermits();
    }

}
