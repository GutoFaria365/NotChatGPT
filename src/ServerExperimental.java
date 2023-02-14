import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerExperimental {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\033[34m";
    public static final String YELLOW = "\033[33m";
    private static List<Socket> clientConnections = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
            writeHistory("\nNEW SESSION");

            while (true) {
                ExecutorService chat = Executors.newCachedThreadPool();
                final Socket clientSocket = serverSocket.accept();

                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter outputName = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                outputName.write(YELLOW + "Please insert your username" + RESET);
                outputName.newLine();
                outputName.flush();

                String userName = consoleInput.readLine();
                broadcastMessage(BLUE + userName.concat(" has joined the server") + RESET);
                System.out.println(BLUE + userName.concat(" has joined the server" + RESET).toUpperCase());
                writeHistory(userName.concat(" has joined the server").toUpperCase());


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
        System.out.println(RED + socketList + RESET);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msgReceived;

        while ((msgReceived = inputReader.readLine()) != null) {
            System.out.println("Message received from " + user.concat(": " + msgReceived));

            broadcastMessage(user + ": " + msgReceived);
            writeHistory(user.concat(": " + msgReceived));
        }
        socketList.remove(socket);
        broadcastMessage(YELLOW + user.concat(" has left the server").toUpperCase() + RESET);
        writeHistory(user.concat(" has left the server").toUpperCase());
        System.out.println(YELLOW + user.concat(" has left the server").toUpperCase() + RESET);
        System.out.println(RED + socketList + RESET);
    }

    private static void broadcastMessage(String message) throws IOException {
        for (Socket client : clientConnections) {
            BufferedWriter outPut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            outPut.write(message);
            outPut.newLine();
            outPut.flush();
        }
    }

    private static void writeHistory(String writeThis) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("serverHistory.txt", true));

            writer.write(writeThis);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

    }
}