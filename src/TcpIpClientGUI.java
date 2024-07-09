import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.UUID;

public class TcpIpClientGUI extends JFrame {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private JTextPane textPane;
    private JTextField textField;
    private JButton sendButton;
    private JButton increaseFontButton;
    private JButton decreaseFontButton;
    private StyledDocument doc;
    private int fontSize = 12; // 기본 폰트 크기
    private String clientId; // 클라이언트 식별자

    public TcpIpClientGUI(String serverIp, int serverPort) {
        // 프레임 설정---------------------------------------------------------------------------------------------------------
        setTitle("채팅하기");
        setSize(300, 500); // 휴대폰 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textPane = new JTextPane(); // 대화창
        textPane.setEditable(false); // 텍스트 편집 불가
        doc = textPane.getStyledDocument();

        textField = new JTextField();
        sendButton = new JButton("보내기");
        increaseFontButton = new JButton("+");
        decreaseFontButton = new JButton("-");
        JLabel fontLabel = new JLabel(String.valueOf(fontSize));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER); // 입력창
        panel.add(sendButton, BorderLayout.EAST); // 보내기 버튼

        JPanel fontPanel = new JPanel(new FlowLayout());
        fontPanel.add(increaseFontButton);
        fontPanel.add(fontLabel);
        fontPanel.add(decreaseFontButton);

        add(new JScrollPane(textPane), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
        add(fontPanel, BorderLayout.NORTH);

        // 보내기 버튼 누르면 보내기-------------------------------------------------------------------------------------------
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // 엔터 키로 메시지 보내기
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // 폰트 크기 증가 버튼
        increaseFontButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fontSize += 10;
                fontLabel.setText(String.valueOf(fontSize));
            }
        });

        // 폰트 크기 감소 버튼
        decreaseFontButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fontSize -= 10;
                fontLabel.setText(String.valueOf(fontSize));
            }
        });

        setVisible(true);

        // 클라이언트 식별자 생성
        clientId = UUID.randomUUID().toString();

        // 서버 연결----------------------------------------------------------------------------------------------------------
        try {
            socket = new Socket(serverIp, serverPort);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            new Thread(new Receiver()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() { //----------------------------------------------------------------------------------------
        try {
            String msg = textField.getText();

            if (!msg.isEmpty()) {

                String formattedMsg = clientId + ":" + msg;
                out.writeUTF(formattedMsg);
                textField.setText(""); // 텍스트 필드 지우기
                appendMessage(msg, StyleConstants.ALIGN_RIGHT); // 보낸 메시지 화면에 표시
                System.out.println(formattedMsg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String msg, int alignment) {
        try {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attrs, alignment);
            StyleConstants.setFontSize(attrs, fontSize);
            System.out.println("style: " + attrs);
            doc.insertString(doc.getLength(), msg + "\n", attrs);
            doc.setParagraphAttributes(doc.getLength()-msg.length()-1, 1, attrs, true);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private class Receiver implements Runnable { // 받은 메시지 화면에 출력-----------------------------
        public void run() {
            try {
                while (in != null) {
                    String msg = in.readUTF();
                    String[] parts = msg.split(":", 2);
                    String senderId = parts[0];
                    String message = parts[1];

                    if (!senderId.equals(clientId)) { // 자신의 메시지는 무시
                        appendMessage(message, StyleConstants.ALIGN_LEFT); // 받은 메시지 왼쪽에 표시
                        System.out.println(senderId + " : " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new TcpIpClientGUI("localhost", 20000);

    }
}
