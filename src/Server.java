import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        ServerSocket serverSocket;
        int id = 0;
        try {
            serverSocket = new ServerSocket(8080);
            while (true) {
                ExecutorService chat = Executors.newCachedThreadPool();
                final Socket clientSocket = serverSocket.accept();
                id++;
                System.out.println("New user joined the server");

                chat.submit(new Thread(() -> {
                    try {
                        newClient(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void newClient(Socket socket) throws IOException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter outPut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        String msgReceived;
        String msgToSend;
        while ((msgReceived = inputReader.readLine()) != null) {

            System.out.println("Message received: ".concat(msgReceived));

            msgToSend = consoleInput.readLine();

            outPut.write(msgToSend);
            outPut.newLine();
            outPut.flush();
        }
    }
}