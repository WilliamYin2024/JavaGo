package io.github.williamyin2024.javago;

import io.github.williamyin2024.javago.sync.WaitGroup;

import static io.github.williamyin2024.javago.Go.go;

public class Main {
	public static void main(String[] args) {
		WaitGroup wg = new WaitGroup();
		for (int i = 1; i <= 3; i++) {
			wg.add(1);
			final int finalI = i;
			go(() -> {
				System.out.println(finalI);
				wg.done();
			});
		}
		wg.await();
	}
}