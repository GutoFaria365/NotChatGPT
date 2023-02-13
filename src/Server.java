import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        BufferedReader inputReader;
        BufferedWriter outPut;
        BufferedReader consoleInput;

        Socket clientSocket;
        try {
            serverSocket = new ServerSocket(8080);

            while (true) {
                clientSocket = serverSocket.accept();

                System.out.println("new client");

                inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outPut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                consoleInput = new BufferedReader(new InputStreamReader(System.in));
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}