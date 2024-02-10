package MultipleClientsOneServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Map<String, PrintWriter> clients = new HashMap<>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10); // Use a fixed-size thread pool with 10 threads

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server started...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientName = clientReader.readLine();

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.put(clientName, out);

                threadPool.execute(new ClientHandler(clientSocket, clientName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String sender, String message) {
        for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
            if (!entry.getKey().equals(sender)) {
                entry.getValue().println(sender + ": " + message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket, String name) {
            this.clientSocket = socket;
            this.clientName = name;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received message from " + clientName + ": " + message);
                    broadcastMessage(clientName, message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
