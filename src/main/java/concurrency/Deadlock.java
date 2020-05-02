package concurrency;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {
	public static class Data {
		private Lock a = new ReentrantLock();
		private Lock b = new ReentrantLock();
		private Random random = new Random();
		
		public void read() throws Exception {
			boolean cond = random.nextBoolean();
			Lock a = cond ? this.a : this.b;
			Lock b = cond ? this.b : this.a;
			System.out.println("Thread " + Thread.currentThread().getName());
			if (a.tryLock(2, TimeUnit.SECONDS))
			try {
				Thread.sleep(1);
				if (b.tryLock(2, TimeUnit.SECONDS))
				try {
					Thread.sleep(10);
				} finally { 
					b.unlock();
				}
			} finally {
				a.unlock();
			}
			System.out.println("Thread " + Thread.currentThread().getName());
		}
	}

	public static class Task implements Runnable {
		private Data data;
		private Random random;
		
		public Task(Data data) {
			this.data = data;
			random = new Random();
		}

		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(random.nextInt(100));
					data.read();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> {
			System.out.println("Checking...");
			ThreadMXBean man = ManagementFactory.getThreadMXBean();
			long[] deads = man.findDeadlockedThreads();
			if (deads != null && deads.length != 0)
			for (long dead : deads) {
				System.out.println("Identified deadlocked thread " + dead);
				Set<Thread> threads = Thread.getAllStackTraces().keySet();
				for (Thread thread : threads){
				    if (thread.getId() == dead){
				        thread.interrupt();
				        System.out.println("Interrupted " + dead);
				    }
				}
			}
		}, 10, 3, TimeUnit.SECONDS);
	    
		Data data = new Data();
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < 2; ++i) {
			threads.add(new Thread(new Task(data)));
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
		scheduler.shutdown();
	}

}
