import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader inputReader;
        BufferedWriter output;
        BufferedReader consoleInput;
        try {
            socket = new Socket("localhost", 8080);

            inputReader = new BufferedReader(new InputStreamReader(System.in));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            consoleInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (!socket.isClosed()) {
                String msgToSend = inputReader.readLine();

                if (msgToSend.compareTo("EXIT") == 0) {
                    break;
                }

                output.write(msgToSend);
                output.newLine();
                output.flush();

                String msgReceived = consoleInput.readLine();
                System.out.println("Message received: ".concat(msgReceived));
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