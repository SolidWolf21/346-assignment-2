import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private FileSystem fs;

    public ClientHandler(Socket socket, FileSystem fs) {
        this.socket = socket;
        this.fs = fs;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(" ", 3);
                String cmd = parts[0].toUpperCase();

                try {
                    switch (cmd) {
                        case "CREATE":
                            fs.createFile(parts[1]);
                            out.println("OK: File created");
                            break;
                        case "DELETE":
                            fs.deleteFile(parts[1]);
                            out.println("OK: File deleted");
                            break;
                        case "WRITE":
                            fs.writeFile(parts[1], parts[2].getBytes());
                            out.println("OK: Written");
                            break;
                        case "READ":
                            byte[] content = fs.readFile(parts[1]);
                            out.println(new String(content));
                            break;
                        case "LIST":
                            out.println(String.join(", ", fs.listFiles()));
                            break;
                        default:
                            out.println("ERROR: Unknown command");
                    }
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Client disconnected.");
        }
    }
}
