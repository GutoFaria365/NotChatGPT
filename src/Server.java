import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        ServerSocket serverSocket;
        List<String> users = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(8080);
            while (true) {
                ExecutorService chat = Executors.newCachedThreadPool();
                final Socket clientSocket = serverSocket.accept();

                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter outputName = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                outputName.write("Please insert your username");
                outputName.newLine();
                outputName.flush();

                String userName = consoleInput.readLine();
                users.add(userName);
                System.out.println(userName.concat(" has joined the server"));

                chat.submit(new Thread(() -> {

                    try {
                        newClient(userName, clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void newClient(String user, Socket socket) throws IOException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter outPut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        // BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        String msgReceived;
        String msgToSend;

        while ((msgReceived = inputReader.readLine()) != null) {
            System.out.println("Message received: ".concat(msgReceived));
            outPut.write(user + ": " + msgReceived);
            outPut.newLine();
            outPut.flush();

            // msgToSend = consoleInput.readLine();
        }
    }
}