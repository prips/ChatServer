import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;


public class ServerWorker extends Thread {

  private final Server server;
  private Socket clientSocket;

  public ServerWorker(Server server, Socket clientSocket) {
    this.server = server;
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    try {
      clientHandler();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void clientHandler() throws IOException {

    //Welcome message
    clientSocket.getOutputStream().write("Welcome to my chat server! What is your nickname?".getBytes());

    InputStream inputStream = clientSocket.getInputStream();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String message = bufferedReader.readLine();

    server.users.add(message);


    //Show currently online users
    String message1 = " You are connected to: \n";
    StringBuilder sb  = new StringBuilder();
    for (String user: server.users) {
      sb.append(user);
      sb.append(" ");
    }
    clientSocket.getOutputStream().write((message1 + sb.toString()).getBytes());

    while (true) {
      OutputStream outputStream = clientSocket.getOutputStream();

      if (message == null) {
        break;
      }

      for (Socket socket : server.clients) {
        if (socket != clientSocket) {
          socket.getOutputStream().write((">" + message + "\n").getBytes());
        }

      }
    }
  }
}
