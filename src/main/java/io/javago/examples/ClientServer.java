package io.javago.examples;

import io.javago.sync.WaitGroup;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.IntStream;

import static io.javago.Go.go;

public class ClientServer {

	private static final WaitGroup wg = new WaitGroup();
	private static final int numClients = 10;
	private static final int serverPort = 8000;

	public static void main(String[] args) {
		Server s = new Server(serverPort);
		wg.add(numClients);
		go(s);
		IntStream.range(0, numClients).forEach(i -> go(new Client("localhost", serverPort, i)));
		wg.await();
	}

	public static class Server implements Runnable {

		private final int portNumber;

		public Server(int portNumber) {
			this.portNumber = portNumber;
		}

		@Override
		public void run() {
			try (
				ServerSocket serverSocket = new ServerSocket(portNumber)
			) {
				while (true) {
					Socket clientSocket = serverSocket.accept();
					go(() -> {
						try (
							ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
							wg
							) {
							String msg = (String) is.readObject();
							System.out.println("Received: " + msg);
							clientSocket.close();
						} catch (IOException | ClassNotFoundException e) {
							throw new RuntimeException(e);
						}
					});
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static class Client implements Runnable {

		private final String host;
		private final int serverPortNumber;
		private final int clientID;

		public Client(String host, int serverPortNumber, int clientID) {
			this.host = host;
			this.serverPortNumber = serverPortNumber;
			this.clientID = clientID;
		}

		@Override
		public void run() {
			try (
				Socket s = new Socket(host, serverPortNumber);
				ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream())
				){
				out.writeObject("Hello from client ID: " + clientID);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
