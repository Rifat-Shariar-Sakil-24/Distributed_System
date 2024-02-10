// Client.java

package MultipleClientsOneServer;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            final Socket socket = new Socket("localhost", 5000);
            System.out.println("Connected to server...");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter your name:");
            String name = reader.readLine();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(name);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String message;
                        while ((message = in.readLine()) != null) {
                            System.out.println(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            System.out.println("You can start Texting Now!");
            Scanner scanner = new Scanner(System.in);
            String userInput;
            while (true) {
                //System.out.print("input> ");
                userInput = scanner.nextLine();
                out.println(userInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
