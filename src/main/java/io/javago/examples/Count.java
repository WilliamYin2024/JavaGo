package io.javago.examples;

import io.javago.sync.WaitGroup;

import static io.javago.Go.go;

class Count {

	public static void main(String[] args) {
		final WaitGroup wg = new WaitGroup();
		wg.add(1);
		go(() -> count(10, wg));
		wg.await();
	}

	private static void count(int n, WaitGroup wg) {
		try (wg) {
			for (int i = 1; i <= n; i++) {
				System.out.println(i);
			}
		}
	}
}
