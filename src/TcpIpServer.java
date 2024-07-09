import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class TcpIpServer {
    private static ConcurrentHashMap<String, DataOutputStream> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(20000);
            System.out.println(getTime() + " 서버가 준비되었습니다.");

            while (true) {
                System.out.println(getTime() + " 연결 요청을 기다립니다.");
                Socket socket = serverSocket.accept();
                System.out.println(getTime() + socket.getInetAddress() + " 로부터 연결 요청이 들어왔습니다.");

                new ServerReceiver(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static String getTime() {
        SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
        return f.format(new Date());
    }

    static class ServerReceiver extends Thread {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        String name;

        ServerReceiver(Socket socket) {
            this.socket = socket;
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                name = socket.getInetAddress().toString() + ":" + socket.getPort();
                clients.put(name, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (in != null) {
                    String msg = in.readUTF();
                    System.out.println(getTime() + " 클라이언트로부터 받은 메시지: " + msg);
                    sendToAll(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clients.remove(name);
                System.out.println(getTime() + name + " 연결 종료");
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void sendToAll(String msg) {
            for (DataOutputStream clientOut : clients.values()) {
                try {
                    clientOut.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
