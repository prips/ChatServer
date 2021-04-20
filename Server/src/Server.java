import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Server extends Thread {

  private final int port;
  List<Socket> clients = new ArrayList<>();
  HashSet<String> users = new HashSet<>();

  public Server(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    try {
      ServerSocket serverSocket = new ServerSocket(port);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        clients.add(clientSocket);

        //ServerWorker serverWorker = new ServerWorker(this, clientSocket);
        ServerWorkerNew serverWorker = new ServerWorkerNew(this, clientSocket);
        serverWorker.start();
      }
    }  catch (IOException e) {
      e.printStackTrace();
    }
  }
}
