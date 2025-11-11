import java.io.*;
import java.net.*;

public class FileClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8080);
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Connected to server. Type commands:");

        String input;
        while ((input = console.readLine()) != null) {
            out.println(input);
            String response = in.readLine();
            System.out.println("Server: " + response);
        }

        socket.close();
    }
}
