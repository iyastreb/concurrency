package concurrency;

import java.util.ArrayList;
import java.util.List;

public class MultiExecutor {
	List<Runnable> tasks;
    /* 
     * @param tasks to executed concurrently
     */
    public MultiExecutor(List<Runnable> tasks) {
        this.tasks = tasks;
    }

    /**
     * Starts and executes all the tasks concurrently
     */
    public void executeAll() {
        List<Thread> threads = new ArrayList<>();
        for (var task : tasks) {
        	Thread thread = new Thread(task);
        	thread.start();
        	threads.add(thread);
        }
        for (var thread : threads) {
        	try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }

	public static void main(String[] args) {
		List<Runnable> tasks = new ArrayList<>();
		for (int i = 0; i < 10; ++i) {
			final int num = i;
			tasks.add(() -> System.out.println("Task" + num));
		}
		MultiExecutor ex = new MultiExecutor(tasks);
		ex.executeAll();
	}

}
