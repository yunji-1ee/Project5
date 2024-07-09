import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class TcpIpClientGUI2 extends JFrame {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private JTextPane textPane;
    private JTextField textField;
    private JButton sendButton;
    private StyledDocument doc;

    public TcpIpClientGUI2(String serverIp, int serverPort) {
        //프레임---------------------------------------------------------------------------------------------------------
        setTitle("채팅하기");
        setSize(350, 600); // 휴대폰 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textPane = new JTextPane();
        textPane.setEditable(false);
        doc = textPane.getStyledDocument();

        textField = new JTextField();
        sendButton = new JButton("보내기");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER); //입력창
        panel.add(sendButton, BorderLayout.EAST); //보내기버튼

        add(new JScrollPane(textPane), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        //보내기 버튼 누르면 보내기-------------------------------------------------------------------------------------------
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        //엔터치면 보내기
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        setVisible(true);
//-----------------------------------------------------------------------------------------------------------------------
        try {
            socket = new Socket(serverIp, serverPort);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            new Thread(new Receiver()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() { //--------------------------------------------------------------------------
        try {
            String msg = textField.getText();
            if (!msg.isEmpty()) {
                out.writeUTF(msg);
                textField.setText(""); //텍스트필드 지워주기
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String msg, int alignment) {
        try {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attrs, alignment);
            doc.insertString(doc.getLength(), msg + "\n", attrs);
            doc.setParagraphAttributes(doc.getLength(), 1, attrs, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }




    private class Receiver implements Runnable { // 받은 메세지 화면에 출력-------------------------
        public void run() {
            try {
                while (in != null) {
                    String msg = in.readUTF();
                    appendMessage( msg, StyleConstants.ALIGN_RIGHT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new TcpIpClientGUI2("localhost", 20000);

    }
}
