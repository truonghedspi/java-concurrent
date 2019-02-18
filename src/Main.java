import sync.LocksHolder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {



    public static void main(String[] args) {
        System.out.println("Hello World!");

        LocksHolder<String> locksHolder = new LocksHolder<>();

        Thread t1 = new Thread(new Worker(locksHolder, "Truong"));
        Thread t2 = new Thread(new Worker(locksHolder, "Truong"));
        Thread t3= new Thread(new Worker(locksHolder, "Dung"));

        t1.start();
        t2.start();
        t3.start();
    }
}

class Worker implements Runnable {

    private LocksHolder<String> locksHolder;

    private String id;

    public Worker(LocksHolder<String> locksHolder, String id) {
        this.locksHolder = locksHolder;
        this.id = id;
    }

    @Override
    public void run() {
        ReentrantLock lock = this.locksHolder.subscribe(this.id);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {

        }

        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getId() + " - " + this.id + ": doing some work");
                    Thread.sleep(5000);
                } finally {
                    lock.unlock();
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.locksHolder.unSubscribe(this.id);
        }
    }
}