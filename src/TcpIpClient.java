/*import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TcpIpClient {
    public static void main(String[] args) {
        Socket socket = null;

        try {
            String serverIp = "localhost";
            socket = new Socket(serverIp, 20000);
            System.out.println("서버에 연결되었습니다.");

            Thread sender = new Thread(new ClientSender(socket));
            Thread receiver = new Thread(new ClientReceiver(socket));

            sender.start();
            receiver.start();
        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ClientSender extends Thread {
        Socket socket;
        DataOutputStream out;
        Scanner scanner;

        ClientSender(Socket socket) {
            this.socket = socket;
            try {
                out = new DataOutputStream(socket.getOutputStream());
                scanner = new Scanner(System.in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (out != null) {
                try {
                    out.writeUTF(scanner.nextLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ClientReceiver extends Thread {
        Socket socket;
        DataInputStream in;

        ClientReceiver(Socket socket) {
            this.socket = socket;
            try {
                in = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (in != null) {
                try {
                    System.out.println("서버로부터 받은 메시지: " + in.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
*/