package concurrency;

public class First {

	public static void main(String[] args) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
			}
		});
		
		Thread thread = new Thread(() -> System.out.println("Hello"));
		thread.start();
		thread.join();
		
		thread = new Thread(() -> { throw new RuntimeException("Failure");});
		thread.start();
		thread.join();
		System.out.println("Done");
	}

}
