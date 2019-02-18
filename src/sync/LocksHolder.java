package sync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LocksHolder<T> {
    private final Map<T, ReentrantLock> locksCache = new ConcurrentHashMap<>();

    private final Map<T, Integer> waitingQueue = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    public ReentrantLock subscribe(T id) {

        synchronized (this.lock) {
            System.out.println(id + ": subcribe");
            Integer waitingCount = this.waitingQueue.get(id);
            if (waitingCount == null) {
                this.waitingQueue.put(id, 0);
            } else {
                this.waitingQueue.put(id, waitingCount + 1);
            }

            ReentrantLock lock = this.locksCache.get(id);
            if (lock == null) {
                lock = new ReentrantLock();
                this.locksCache.putIfAbsent(id, lock);
            }

            return lock;
        }

    }

    public void unSubscribe(T id) {

        synchronized (this.lock) {
            System.out.println(id + ": unsubcribe");
            Integer waitingCount = this.waitingQueue.get(id);

            if (waitingCount == 0) { //no more waiting thread
                System.out.println(id + ": released");
                this.locksCache.remove(id);
                this.waitingQueue.remove(id);
            } else {
                this.waitingQueue.put(id, waitingCount - 1);
            }
        }
    }

}
