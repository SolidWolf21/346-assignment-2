import java.net.*;

public class FileServer {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        FileSystem fs = new FileSystem("filesystem.sim");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("File server running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                new ClientHandler(clientSocket, fs).start();
            }
        }
    }
}
