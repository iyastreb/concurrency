package concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarrierMain {
    public static class Barrier {
        private final int numberOfWorkers;
        private Semaphore semaphore = new Semaphore(0); //** blank 1 **/);
        private int counter = 0; //** blank 2 **/;
        private Lock lock = new ReentrantLock();
     
        public Barrier(int numberOfWorkers) {
            this.numberOfWorkers = numberOfWorkers;
        }
     
        public void barrier() {
            lock.lock();
            boolean isLastWorker = false;
            try {
                counter++;
     
                if (counter == numberOfWorkers) {
                    isLastWorker = true;
                }
            } finally {
                lock.unlock();
            }
     
            if (isLastWorker) {
                semaphore.release(numberOfWorkers - 1);//** blank 3 **/);
            } else {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                }
            }
        }
    }
     
    public static class CoordinatedWorkRunner implements Runnable {
        private Barrier barrier;
     
        public CoordinatedWorkRunner(Barrier barrier) {
            this.barrier = barrier;
        }
     
        @Override
        public void run() {
            try {
                task();
            } catch (InterruptedException e) {
            }
        }
     
        private void task() throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + " part 1 of the work is finished");
            Thread.sleep(new Random().nextInt(100));
            barrier.barrier();
     
            System.out.println(Thread.currentThread().getName() + " part 2 of the work is finished");
        }
    }
	public static void main(String[] args) {
	    int numberOfThreads = 4;
	    
	    List<Thread> threads = new ArrayList<>();
	 
	    Barrier barrier = new Barrier(numberOfThreads);
	    for (int i = 0; i < numberOfThreads; i++) {
	        threads.add(new Thread(new CoordinatedWorkRunner(barrier)));
	    }
	 
	    for(Thread thread: threads) {
	        thread.start();
	    }
	}

}
