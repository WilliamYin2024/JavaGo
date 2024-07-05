package io.javago.examples;

class CountJava {

	public static void main(String[] args) {
		Thread t = new Thread(new Counter(10));
		t.start();
	}

	private static class Counter implements Runnable {

		private final int n;

		public Counter(int n) {
			this.n = n;
		}

		@Override
		public void run() {
			for (int i = 1; i <= n; i++) {
				System.out.println(i);
			}
		}
	}
}
