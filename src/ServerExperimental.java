import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerExperimental {
    private static List<Socket> clientConnections = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket serverSocket;
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
                System.out.println(userName.concat(" has joined the server"));

                chat.submit(new Thread(() -> {
                    try {
                        newClient(userName, clientSocket, clientConnections);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void newClient(String user, Socket socket, List<Socket> socketList) throws IOException {
        socketList.add(socket);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msgReceived;

        while ((msgReceived = inputReader.readLine()) != null) {
            System.out.println("Message received: ".concat(msgReceived));

            // Broadcast the received message to all clients
            broadcastMessage(user + ": " + msgReceived);
        }
        socketList.remove(socket);
    }

    private static void broadcastMessage(String message) throws IOException {
        for (Socket client : clientConnections) {
            BufferedWriter outPut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            outPut.write(message);
            outPut.newLine();
            outPut.flush();
        }
    }
}