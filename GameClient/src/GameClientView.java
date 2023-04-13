import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GameClientView extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtInput;
    private String UserName;
    private JButton btnSend;
    private static final int BUF_LEN = 128;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    JPanel panel;
    private String ip_addr;
    private String port_no;
    private GameRoom gameRoom;
    private GameClientView view;
    private int user_id;
    private JLabel lblUserName;
    private JLabel lblRoomNum;
    private JTextPane textArea;
    protected int curRoomIndex = -1;
    protected int selectRoom;
    private Vector<String> roomNumV = new Vector<>(10);
    private Vector<String> roomNameV = new Vector<>(10);
    private Vector<Integer> roomSizeV = new Vector<>(10);
    private Vector<String> roomCountV = new Vector<>(10);
    private JList<String> roomNumList;
    private JList<String> roomNameList;
    private JList<Integer> roomSizeList;
    private JList<String> roomCountList;

    public GameClientView(final String username, String ip_addr, String port_no) {
        roomNumList = new JList<>(this.roomNumV);
        roomNameList = new JList<>(this.roomNameV);
        roomSizeList = new JList<>(this.roomSizeV);
        roomCountList = new JList<>(this.roomCountV);
        view = this;
        ip_addr = ip_addr;
        port_no = port_no;

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 634);
        setVisible(true);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(this.contentPane);
        contentPane.setLayout(null);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 280, 471);
        contentPane.add(scrollPane);
        textArea = new JTextPane();
        textArea.setEditable(true);
        textArea.setFont(new Font("굴림체", 0, 14));
        scrollPane.setViewportView(textArea);
        txtInput = new JTextField();
        txtInput.setBounds(10, 489, 209, 40);
        contentPane.add(txtInput);
        txtInput.setColumns(10);
        btnSend = new JButton("Send");
        btnSend.setFont(new Font("굴림", 0, 14));
        btnSend.setBounds(220, 489, 69, 40);
        contentPane.add(this.btnSend);
        lblUserName = new JLabel("Name");
        lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblUserName.setBackground(Color.WHITE);
        lblUserName.setFont(new Font("굴림", 1, 14));
        lblUserName.setHorizontalAlignment(0);
        lblUserName.setBounds(12, 539, 62, 40);
        contentPane.add(lblUserName);
        lblRoomNum = new JLabel("room");
        lblRoomNum.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblRoomNum.setBackground(Color.WHITE);
        lblRoomNum.setFont(new Font("굴림", 1, 14));
        lblRoomNum.setHorizontalAlignment(0);
        lblRoomNum.setBounds(76, 539, 62, 40);
        contentPane.add(lblRoomNum);
        AppendText("User " + username + " connecting " + ip_addr + " " + port_no + " " + curRoomIndex);
        UserName = username;
        lblUserName.setText(UserName);
        if (curRoomIndex == -1) {
            lblRoomNum.setText("로비");
        } else {
            lblRoomNum.setText(Integer.toString(curRoomIndex));
        }

        JButton btnNewButton = new JButton("종 료");
        btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));

        btnNewButton.addActionListener(e -> {
            ChatMsg cm = new ChatMsg(GameClientView.this.UserName, "101", "Bye");
            GameClientView.this.sendObject(cm);
            System.exit(0);
        });

        btnNewButton.setBounds(220, 539, 69, 40);
        contentPane.add(btnNewButton);
        JLabel roomNum = new JLabel("방번호", 0);
        roomNum.setBorder(new LineBorder(Color.BLACK));
        roomNum.setBackground(new Color(200, 150, 20));
        roomNum.setOpaque(true);
        roomNum.setBounds(300, 10, 50, 20);
        JLabel roomTitle = new JLabel("방제목", 0);
        roomTitle.setBorder(new LineBorder(Color.BLACK));
        roomTitle.setBackground(new Color(200, 150, 20));
        roomTitle.setOpaque(true);
        roomTitle.setBounds(350, 10, 300, 20);
        JLabel roomSize = new JLabel("방크기", 0);
        roomSize.setBorder(new LineBorder(Color.BLACK));
        roomSize.setBackground(new Color(200, 150, 20));
        roomSize.setOpaque(true);
        roomSize.setBounds(650, 10, 50, 20);
        JLabel numOfPeople = new JLabel("인원수", 0);
        numOfPeople.setBorder(new LineBorder(Color.BLACK));
        numOfPeople.setBackground(new Color(200, 150, 20));
        numOfPeople.setOpaque(true);
        numOfPeople.setBounds(700, 10, 50, 20);
        contentPane.add(roomNum);
        contentPane.add(roomTitle);
        contentPane.add(roomSize);
        contentPane.add(numOfPeople);
        roomNumList.setBounds(300, 30, 50, 500);
        roomNameList.setBounds(350, 30, 300, 500);
        roomSizeList.setBounds(650, 30, 50, 500);
        roomCountList.setBounds(700, 30, 50, 500);
        roomNumList.setEnabled(false);
        roomSizeList.setEnabled(false);
        roomCountList.setEnabled(false);
        DefaultListCellRenderer titleList1 = (DefaultListCellRenderer) roomNumList.getCellRenderer();
        DefaultListCellRenderer titleList2 = (DefaultListCellRenderer) roomNameList.getCellRenderer();
        DefaultListCellRenderer titleList3 = (DefaultListCellRenderer) roomSizeList.getCellRenderer();
        DefaultListCellRenderer titleList4 = (DefaultListCellRenderer) roomCountList.getCellRenderer();
        titleList1.setHorizontalAlignment(0);
        titleList2.setHorizontalAlignment(0);
        titleList3.setHorizontalAlignment(0);
        titleList4.setHorizontalAlignment(0);
        roomNameList.addListSelectionListener(e -> selectRoom = roomNameList.getSelectedIndex());
        contentPane.add(roomNumList);
        contentPane.add(roomNameList);
        contentPane.add(roomCountList);
        contentPane.add(roomSizeList);
        JButton btn = new JButton("방 생성");
        btn.setBounds(650, 530, 100, 50);
        btn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(view, "방 이름을 입력하세요");
            String[] buttons = new String[]{"8 x 8", "10 x 10", "12 x 12"};
            int size = JOptionPane.showOptionDialog(view, "방 사이즈를 선택하세요", "방 사이즈", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, "10 x 10");
            switch (size) {
                case 0:
                    size = 8;
                    break;
                case 1:
                    size = 10;
                    break;
                case 2:
                    size = 12;
            }

            ChatMsg cm = new ChatMsg(UserName, "500", "room");
            cm.roomSize = size;
            cm.data = name;
            cm.room_id = roomNameV.size();
            sendObject(cm);
        });
        JButton btn2 = new JButton("방 입장");
        btn2.setBounds(300, 530, 100, 50);
        btn2.addActionListener(e -> {
            if (roomNameList.isSelectedIndex(selectRoom)) {
                if (roomCountV.get(selectRoom).equals("2/2")) {
                    JOptionPane.showMessageDialog(view, "입장 가능 인원이 초과되었습니다. 다른 방을 이용하세요", null, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                curRoomIndex = selectRoom;
                lblRoomNum.setText(Integer.toString(curRoomIndex));
                gameRoom = new GameRoom(view, username, curRoomIndex, roomSizeV.get(curRoomIndex), roomSizeV.get(curRoomIndex), roomNameV.get(curRoomIndex));
                gameRoom.setLocation(view.getX(), view.getY());
                ChatMsg cm = new ChatMsg(UserName, "200", "join");
                cm.room_id = selectRoom;
                sendObject(cm);
                view.setVisible(false);
            }

        });
        contentPane.add(btn);
        contentPane.add(btn2);

        try {
            socket = new Socket(ip_addr, Integer.parseInt(port_no));
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
            ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
            obcm.room_id = this.curRoomIndex;
            sendObject(obcm);
            ListenNetwork net = new ListenNetwork();
            net.start();
            TextSendAction action = new TextSendAction();
            btnSend.addActionListener(action);
            txtInput.addActionListener(action);
            txtInput.requestFocus();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            AppendText("connect error");
        }

    }

    public void login(ChatMsg cm) {
        if (cm.room_id == -1 && cm.UserName.equals(UserName)) {
            this.user_id = cm.user_id;
            if (cm.roomName != null) {
                for (int i = 0; i < cm.roomName.size(); ++i) {
                    roomNameV.add(cm.roomName.get(i));
                    roomCountV.add(cm.roomCountV.get(i));
                    roomNumV.add(Integer.toString(i));
                    roomSizeV.add(cm.roomSizeV.get(i));
                    roomNameList.setListData(roomNameV);
                    roomCountList.setListData(roomCountV);
                    roomNumList.setListData(roomNumV);
                    roomSizeList.setListData(roomSizeV);
                }
            }
        }

    }

    public void receiveMessage(ChatMsg cm, String msg) {
        if (cm.room_id == -1) {
            if (cm.UserName.equals(UserName)) {
                AppendTextR(msg);
            } else {
                AppendText(msg);
            }
        } else if (cm.room_id == curRoomIndex) {
            if (cm.UserName.equals(UserName)) {
                gameRoom.AppendTextRight(msg);
            } else if (gameRoom != null) {
                gameRoom.AppendText(msg);
                gameRoom.repaint();
            }
        }

    }

    public void createRoom(ChatMsg cm) {
        roomNameV.add(cm.data);
        roomNameList.setListData(roomNameV);
        roomCountV.add(cm.room_id, "0/2");
        roomCountList.setListData(roomCountV);
        roomNumV.add(Integer.toString(cm.room_id));
        roomNumList.setListData(roomNumV);
        roomSizeV.add(cm.roomSize);
        roomSizeList.setListData(roomSizeV);
        cm.room_id = curRoomIndex;
    }

    public void deleteRoom(ChatMsg cm) {
        if (cm.roomCount.equals("1/2")) {
            roomCountV.set(cm.room_id, "1/2");
            roomCountList.setListData(roomCountV);
        } else if (cm.roomCount.equals("0/2")) {
            roomNameV.remove(cm.room_id);
            roomNameList.setListData(roomNameV);
            roomCountV.remove(cm.room_id);
            roomCountList.setListData(roomCountV);
            roomNumV.remove(cm.room_id);
            setRoomNum();
            roomNumList.setListData(roomNumV);
            roomSizeV.remove(cm.room_id);
            roomSizeList.setListData(roomSizeV);
        }

    }

    public void setRoomNum() {
        for (int i = 0; i < roomNumV.size(); ++i) {
            int n = Integer.parseInt(roomNumV.get(i));
            if (n != i) {
                roomNumV.set(i, Integer.toString(n - 1));
            }
        }

    }

    public void AppendText(String msg) {
        msg = msg.trim();
        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, 0);
        StyleConstants.setForeground(left, Color.BLACK);
        doc.setParagraphAttributes(doc.getLength(), 1, left, false);

        try {
            doc.insertString(doc.getLength(), msg + "\n", left);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        int len = textArea.getDocument().getLength();
        this.textArea.setCaretPosition(len);
    }

    public void AppendTextR(String msg) {
        msg = msg.trim();
        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, 2);
        StyleConstants.setForeground(right, Color.BLUE);
        doc.setParagraphAttributes(doc.getLength(), 1, right, false);

        try {
            doc.insertString(doc.getLength(), msg + "\n", right);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        int len = this.textArea.getDocument().getLength();
        this.textArea.setCaretPosition(len);
    }

    public byte[] makePacket(String msg) {
        byte[] packet = new byte[BUF_LEN];
        byte[] bb = null;
        int i;
        for (i = 0; i < BUF_LEN; i++)
            packet[i] = 0;
        try {
            bb = msg.getBytes("euc-kr");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(0);
        }
        for (i = 0; i < bb.length; i++)
            packet[i] = bb[i];
        return packet;
    }

    public void sendMessage(String msg) {
        try {
            ChatMsg obcm = new ChatMsg(UserName, "400", msg);
            obcm.room_id = curRoomIndex;
            oos.writeObject(obcm);
        } catch (IOException e) {
            AppendText("oos.writeObject() error");
            try {
                ois.close();
                oos.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                System.exit(0);
            }
        }
    }

    public void sendObject(Object ob) {
        try {
            oos.writeObject(ob);
        } catch (IOException ioException) {
            AppendText("SendObject Error");
        }

    }

    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                try {
                    Object obcm = null;
                    String msg = null;
                    ChatMsg cm;
                    try {
                        obcm = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (obcm == null) {
                        break;
                    }
                    if (obcm instanceof ChatMsg) {
                        cm = (ChatMsg) obcm;
                        msg = String.format("[%s]\n%s", cm.UserName, cm.data);
                        switch (cm.code) {
                            case "100":
                                login(cm);
                                repaint();
                                break;
                            case "200":
                                if (curRoomIndex == cm.room_id) {
                                    gameRoom.Join(cm);
                                }
                                roomCountV.set(cm.room_id, cm.roomCount);
                                roomCountList.setListData(roomCountV);
                                repaint();
                                break;
                            case "201":
                                if (cm.room_id == curRoomIndex) {
                                    curRoomIndex = -1;
                                    lblRoomNum.setText("로비");
                                }
                                deleteRoom(cm);
                                repaint();
                                break;
                            case "300":
                                if (cm.room_id == curRoomIndex) {
                                    gameRoom.Set(cm);
                                    repaint();
                                }
                                break;
                            case "301":
                                if (cm.room_id == curRoomIndex) {
                                    gameRoom.Cancel(cm);
                                    repaint();
                                }
                                break;
                            case "302":
                                if (cm.room_id == curRoomIndex) {
                                    gameRoom.rejectCancel(cm);
                                }
                                repaint();
                                break;
                            case "303":
                                if (cm.room_id == curRoomIndex) {
                                    gameRoom.acceptCancel(cm);
                                }
                                repaint();
                                break;
                            case "400":
                                receiveMessage(cm, msg);
                                repaint();
                                break;
                            case "500":
                                createRoom(cm);
                                repaint();
                                break;
                        }
                    }
                } catch (IOException e) {
                    AppendText("ois.readObject() error");
                    try {
                        ois.close();
                        oos.close();
                        socket.close();

                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }

        }
    }

    class TextSendAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = null;
                msg = txtInput.getText();
                sendMessage(msg);
                txtInput.setText("");
                txtInput.requestFocus();
            }

        }
    }
}
