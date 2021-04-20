import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerWorkerNew extends Thread {

  private final Server server;
  private Socket clientSocket;

  public ServerWorkerNew(Server server, Socket clientSocket) {
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
    //Welcome message and choose nickname
    clientSocket.getOutputStream().write("Welcome to my chat server! What is your nickname?".getBytes());
    String username = readMsg();

    while (username != null && !username.equals("") && server.users.contains(username)) {
      clientSocket.getOutputStream().write("Nickname taken. Choose another one\n".getBytes());
      username = readMsg();
    }
    server.users.add(username);
    // broadcast online status
    broadcastMsg("*" + username + " has joined the chat*\n");

    //Show currently online users
    int totalUsers = server.users.size() - 1;
    String message1 = " You are connected with " + totalUsers + " other users:";
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    int i = 1;
    for (String user : server.users) {
      if (!user.equals(username)) {
        sb.append(user);
        if (i < totalUsers) {
          sb.append(", ");
        }
      }
      i++;
    }
    sb.append("]\n");
    clientSocket.getOutputStream().write((message1 + sb.toString()).getBytes());

    // handle messages
    while (true) {
      String msg = readMsg();
      if (msg == null) {
        //broadcast logout
        broadcastMsg("*" + username + " has left the chat*\n");
        break;
      }
      broadcastMsgFromUser(username, msg);
    }
  }

  private String readMsg() {
    try {
      InputStream inputStream = clientSocket.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      return bufferedReader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void broadcastMsgFromUser(String user, String msg) throws IOException {
    for (Socket socket : server.clients) {
      if (socket != clientSocket) {
        socket.getOutputStream().write((">" + " <" + user + "> " + msg + "\n").getBytes());
      }
    }
  }

  private void broadcastMsg(String msg) throws IOException {
    for (Socket socket : server.clients) {
      if (socket != clientSocket) {
        socket.getOutputStream().write((">" + msg + "\n").getBytes());
      }
    }
  }
}

