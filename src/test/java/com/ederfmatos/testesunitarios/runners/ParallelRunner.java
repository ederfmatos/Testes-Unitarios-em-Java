package com.ederfmatos.testesunitarios.runners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParallelRunner extends BlockJUnit4ClassRunner {

	public ParallelRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		setScheduler(new ThreadPool());
	}

	private static class ThreadPool implements RunnerScheduler {

		private ExecutorService executor;

		public ThreadPool() {
			executor = Executors.newFixedThreadPool(6);
		}

		@Override
		public void schedule(Runnable runnable) {
			executor.submit(runnable);
		}

		@Override
		public void finished() {
			executor.shutdown();

			try {
				executor.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		}

	}

}
