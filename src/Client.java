import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static void main(String[] args) {

        final Socket socket;
        try {
            socket = new Socket("localhost", 8080);
            System.out.println("Connection established");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            ExecutorService letsGetToWork = Executors.newCachedThreadPool();

            letsGetToWork.submit(new Thread(() -> {
                while (!socket.isClosed()) {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("initialize");
                    String msgToSend = null;
                    System.out.println(msgToSend);
                    try {
                        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        System.out.println("output = " + output);
                        msgToSend = inputReader.readLine();
                        System.out.println(msgToSend);
                        System.out.println("msgToSend = " + msgToSend);

                        if (msgToSend.compareTo("EXIT") == 0) {
                            break;
                        }
                        output.write(msgToSend);
                        output.newLine();
                        output.flush();
                        System.out.println("msg sent");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }));

            letsGetToWork.submit(new Thread(() -> {
                while (!socket.isClosed()) {
                    try {
                        Thread.sleep(100);
                        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String msgReceived = consoleInput.readLine();
                        System.out.println("Message received: ".concat(msgReceived));
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }));

        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}