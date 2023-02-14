import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientExperimental {
    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\033[34m";

    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader inputReader;
        BufferedWriter output;
        BufferedReader consoleInput;
        try {
            socket = new Socket("localhost", 8080);
            consoleInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String insertName = consoleInput.readLine();
            System.out.println(insertName);

            inputReader = new BufferedReader(new InputStreamReader(System.in));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (!socket.isClosed()) {
                String msgToSend = inputReader.readLine();

                if (msgToSend.compareTo("EXIT") == 0) {
                    break;
                }

                output.write(msgToSend);
                output.newLine();
                output.flush();

                // Thread creation to always receive messages sent
                ExecutorService viewMessage = Executors.newCachedThreadPool();
                Socket finalSocket = socket;

                viewMessage.submit(new Thread(() -> {
                    String msgReceived = null;
                    try {
                        BufferedReader consoleRead = new BufferedReader(new InputStreamReader(finalSocket.getInputStream()));
                        while (!viewMessage.isShutdown() && (msgReceived = consoleRead.readLine()) != null) {
                            System.out.println(msgReceived);
                        }
                    } catch (EOFException e) {
                        viewMessage.shutdownNow();
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        viewMessage.shutdownNow();
                    }
                }));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                System.out.println(BLUE + "You have been disconnected from the server" + RESET);
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}