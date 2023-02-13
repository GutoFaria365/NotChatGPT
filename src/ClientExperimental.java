import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientExperimental {
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader inputReader;
        BufferedWriter output;
        BufferedReader consoleInput;
        boolean firstTime;
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
                    socket.close();
                    break;
                }

                output.write(msgToSend);
                output.newLine();
                output.flush();

                ExecutorService viewMessage = Executors.newCachedThreadPool();

                Socket finalSocket = socket;
                viewMessage.submit(new Thread(() -> {
                    String msgReceived = null;
                    try {
                        BufferedReader consoleRead = new BufferedReader(new InputStreamReader(finalSocket.getInputStream()));
                        while ((msgReceived = consoleRead.readLine()) != null) {
                            System.out.println(msgReceived);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}