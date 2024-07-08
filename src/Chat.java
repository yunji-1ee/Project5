import java.net.*;
import java.io.*;

public class Chat {
    public static boolean listening = true;

    public static void main(String[] args) throws IOException{  //메인
        //환경설정 및 초기화
        //serverSocket 객체 생성
        //while문을 돌며 생성한 ServerSocket에 accept()메소드 호출
        //----> accept호출 후 접속 요청이 올 때마다 클라이언트와 통신할 수 있는 Socket을 서버측에 하나 열어서 반환하기 (멀티스레딩기법)
    int PORT = 20000;
    ServerSocket server = new ServerSocket(PORT);
    System.out.println("port" + PORT + "으로 채팅서버가 실행되었습니다. ");

    //각종 환경변수 설정하는 부분 추가필요
        System.out.println("클라이언트 접속대기 중 \n");

        while (Chat.listening) {
            Socket client = server.accept();
            ChatThread thd = new ChatThread(client);
            thd.start();
        }
        server.close();
        System.exit(0);

    }
}
