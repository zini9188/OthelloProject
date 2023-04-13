//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GameServer extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    JTextArea textArea;
    private JTextField txtPortNumber;
    private ServerSocket socket;
    private Socket client_socket;
    private static final int BUF_LEN = 128;
    private final Vector<UserService> UserVec = new Vector<>();
    private final Vector<Integer> userCountV = new Vector<>();
    private final Vector<String> UserList = new Vector<>(10);
    private final Vector<String> JoinList = new Vector<>(20);
    private final Vector<String> roomName = new Vector<>();
    private final Vector<String> roomCount = new Vector<>();
    private final Vector<Integer> roomSize = new Vector<>();
    private int user_id = -1;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                GameServer frame = new GameServer();
                frame.setVisible(true);
            } catch (Exception var2) {
                var2.printStackTrace();
            }

        });
    }

    public GameServer() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setBounds(100, 100, 338, 440);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.contentPane);
        this.contentPane.setLayout((LayoutManager)null);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 300, 298);
        this.contentPane.add(scrollPane);
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        scrollPane.setViewportView(this.textArea);
        JLabel lblNewLabel = new JLabel("Port Number");
        lblNewLabel.setBounds(13, 318, 87, 26);
        this.contentPane.add(lblNewLabel);
        this.txtPortNumber = new JTextField();
        this.txtPortNumber.setHorizontalAlignment(0);
        this.txtPortNumber.setText("30000");
        this.txtPortNumber.setBounds(112, 318, 199, 26);
        this.contentPane.add(this.txtPortNumber);
        this.txtPortNumber.setColumns(10);
        final JButton btnServerStart = new JButton("Server Start");
        btnServerStart.addActionListener(e -> {
            try {
                GameServer.this.socket = new ServerSocket(Integer.parseInt(GameServer.this.txtPortNumber.getText()));
            } catch (IOException | NumberFormatException var3) {
                var3.printStackTrace();
            }

            GameServer.this.AppendText("Chat Server Running..");
            btnServerStart.setText("Chat Server Running..");
            btnServerStart.setEnabled(false);
            GameServer.this.txtPortNumber.setEnabled(false);
            AcceptServer accept_server = GameServer.this.new AcceptServer();
            accept_server.start();
        });
        btnServerStart.setBounds(12, 356, 300, 35);
        this.contentPane.add(btnServerStart);

        for(int i = 0; i < this.JoinList.capacity(); ++i) {
            this.JoinList.add("1");
        }

    }

    public void AppendText(String str) {
        this.textArea.append(str + "\n");
        this.textArea.setCaretPosition(this.textArea.getText().length());
    }

    public void AppendObject(ChatMsg msg) {
        this.textArea.append("code = " + msg.code + "\n");
        this.textArea.append("id = " + msg.UserName + "\n");
        this.textArea.append("data = " + msg.data + "\n");
        this.textArea.setCaretPosition(this.textArea.getText().length());
    }

    class AcceptServer extends Thread {
        AcceptServer() {
        }

        public void run() {
            while(true) {
                try {
                    GameServer.this.AppendText("Waiting new clients ...");
                    GameServer.this.client_socket = GameServer.this.socket.accept();
                    GameServer.this.AppendText("새로운 참가자 from " + GameServer.this.client_socket);
                    UserService new_user = GameServer.this.new UserService(GameServer.this.client_socket);
                    GameServer.this.UserVec.add(new_user);
                    new_user.start();
                    GameServer.this.AppendText("현재 참가자 수 " + GameServer.this.UserVec.size());
                } catch (IOException var2) {
                    GameServer.this.AppendText("accept() error");
                }
            }
        }
    }

    class UserService extends Thread {
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private Socket client_socket;
        private final Vector<UserService> user_vc;
        public String UserName = "";

        public UserService(Socket client_socket) {
            this.client_socket = client_socket;
            this.user_vc = GameServer.this.UserVec;

            try {
                this.oos = new ObjectOutputStream(client_socket.getOutputStream());
                this.oos.flush();
                this.ois = new ObjectInputStream(client_socket.getInputStream());
            } catch (Exception var4) {
                GameServer.this.AppendText("userService error");
            }

        }

        public void Login(ChatMsg cm) {
            this.UserName = cm.UserName;
            ++GameServer.this.user_id;
            GameServer.this.UserList.add(GameServer.this.user_id, this.UserName);
            cm.user_id = GameServer.this.user_id;
            cm.roomName = GameServer.this.roomName;
            cm.roomCountV = GameServer.this.roomCount;
            cm.roomSizeV = GameServer.this.roomSize;
            GameServer.this.AppendText("새로운 참가자 " + this.UserName + " 입장.");
            this.WriteOne("Welcome to Java chat server\n");
            this.WriteOne(this.UserName + "님 환영합니다.\n");
            String msg = "[" + this.UserName + "]님이 입장 하였습니다.\n";
            this.WriteOthers(msg);
        }

        public void Logout() {
            String msg = "[" + this.UserName + "]님이 퇴장 하였습니다.\n";
            GameServer.this.UserVec.removeElement(this);
            this.WriteAll(msg);
            GameServer.this.AppendText("사용자 [" + this.UserName + "] 퇴장. 현재 참가자 수 " + GameServer.this.UserVec.size());
        }

        public void WriteAll(String str) {
            for(int i = 0; i < this.user_vc.size(); ++i) {
                UserService user = (UserService)this.user_vc.elementAt(i);
                user.WriteOne(str);
            }

        }

        public void WriteAllObject(Object ob) {
            for(int i = 0; i < this.user_vc.size(); ++i) {
                UserService user = (UserService)this.user_vc.elementAt(i);
                user.WriteOneObject(ob);
            }

        }

        public void WriteOthers(String str) {
            for(int i = 0; i < this.user_vc.size(); ++i) {
                UserService user = (UserService)this.user_vc.elementAt(i);
                if (user != this) {
                    user.WriteOne(str);
                }
            }

        }

        public byte[] MakePacket(String msg) {
            byte[] packet = new byte[128];
            byte[] bb = null;

            int i;
            for(i = 0; i < 128; ++i) {
                packet[i] = 0;
            }

            try {
                bb = msg.getBytes("euc-kr");
            } catch (UnsupportedEncodingException var6) {
                var6.printStackTrace();
            }

            for(i = 0; i < bb.length; ++i) {
                packet[i] = bb[i];
            }

            return packet;
        }

        public void WriteOne(String msg) {
            try {
                ChatMsg obcm = new ChatMsg("SERVER", "400", msg);
                obcm.room_id = -1;
                this.oos.writeObject(obcm);
            } catch (IOException var5) {
                GameServer.this.AppendText("dos.writeObject() error");

                close();
            }

        }

        private void close() {
            try {
                this.ois.close();
                this.oos.close();
                this.client_socket.close();
                this.client_socket = null;
                this.ois = null;
                this.oos = null;
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            this.Logout();
        }

        public void WriteOneObject(Object ob) {
            try {
                this.oos.writeObject(ob);
            } catch (IOException var5) {
                GameServer.this.AppendText("oos.writeObject(ob) error");

                close();
            }

        }

        public void joinRoom(ChatMsg cm) {
            int count = (Integer) GameServer.this.userCountV.get(cm.room_id);
            ++count;
            GameServer.this.userCountV.set(cm.room_id, count);
            if (count == 1) {
                GameServer.this.JoinList.set(cm.room_id, cm.UserName);
                cm.roomCount = "1/2";
                GameServer.this.roomCount.set(cm.room_id, "1/2");
            } else if (count == 2) {
                cm.UserName = this.UserName;
                cm.UserName2 = (String) GameServer.this.JoinList.get(cm.room_id);
                GameServer.this.JoinList.set(cm.room_id, "1");
                cm.roomCount = "2/2";
                GameServer.this.roomCount.set(cm.room_id, "2/2");
            }

            GameServer.this.AppendText("현재 유저리스트");

            int i;
            for(i = 0; i < GameServer.this.UserList.size(); ++i) {
                if (GameServer.this.UserList != null) {
                    GameServer.this.AppendText(GameServer.this.UserList.get(i));
                }
            }

            for(i = 0; i < this.user_vc.size(); ++i) {
                UserService user = this.user_vc.elementAt(i);
                user.WriteOneObject(cm);
            }

        }

        public void exitRoom(ChatMsg cm) {
            int count = (Integer) GameServer.this.userCountV.get(cm.room_id);
            --count;
            GameServer.this.userCountV.set(cm.room_id, count);
            if (count == 1) {
                cm.roomCount = "1/2";
                this.WriteAllObject(cm);
            }

            if (count <= 0) {
                cm.roomCount = "0/2";
                GameServer.this.userCountV.remove(cm.room_id);
                GameServer.this.roomName.remove(cm.room_id);
                GameServer.this.roomCount.remove(cm.room_id);
                GameServer.this.roomSize.remove(cm.room_id);
                this.WriteAllObject(cm);
            }

        }

        public void run() {
            while(true) {
                while(true) {
                    try {
                        Object obcm = null;
                        String msg = null;
                        ChatMsg cm = null;
                        if (GameServer.this.socket != null) {
                            try {
                                obcm = this.ois.readObject();
                            } catch (ClassNotFoundException var6) {
                                var6.printStackTrace();
                                return;
                            }

                            if (obcm != null) {
                                if (!(obcm instanceof ChatMsg)) {
                                    continue;
                                }

                                cm = (ChatMsg)obcm;
                                GameServer.this.AppendObject(cm);
                                if (cm.code.matches("100")) {
                                    this.Login(cm);
                                    msg = String.format("[%s] %s %d", cm.UserName, cm.data, cm.room_id);
                                    GameServer.this.AppendText(msg);
                                    this.oos.writeObject(cm);
                                    continue;
                                }

                                if (!cm.code.matches("101")) {
                                    if (cm.code.matches("200")) {
                                        this.joinRoom(cm);
                                        continue;
                                    }

                                    if (cm.code.matches("201")) {
                                        this.exitRoom(cm);
                                        continue;
                                    }

                                    if (cm.code.matches("300")) {
                                        msg = String.format("[%s] %d", cm.UserName, cm.turnFlag);
                                        GameServer.this.AppendText(msg);
                                        this.WriteAllObject(cm);
                                        continue;
                                    }

                                    if (cm.code.matches("301")) {
                                        GameServer.this.AppendText(cm.UserName + "의 무르기 요청");
                                        this.WriteAllObject(cm);
                                        continue;
                                    }

                                    if (cm.code.matches("302")) {
                                        GameServer.this.AppendText(cm.UserName + "이 무르기 요청을 거절");
                                        this.WriteAllObject(cm);
                                        continue;
                                    }

                                    if (cm.code.matches("303")) {
                                        GameServer.this.AppendText(cm.UserName + "의 무르기 요청을 수락");
                                        this.WriteAllObject(cm);
                                        continue;
                                    }

                                    if (cm.code.matches("400")) {
                                        msg = String.format("[%s] %s %d", cm.UserName, cm.data, cm.room_id);
                                        GameServer.this.AppendText(msg);
                                        this.WriteAllObject(cm);
                                        continue;
                                    }

                                    if (cm.code.matches("500")) {
                                        msg = String.format("[%s] :[ %s ]의 방 생성", cm.UserName, cm.data);
                                        GameServer.this.userCountV.add(cm.room_id, 0);
                                        GameServer.this.roomName.add(cm.data);
                                        GameServer.this.roomCount.add(cm.room_id, "0/2");
                                        GameServer.this.roomSize.add(cm.room_id, cm.roomSize);
                                        GameServer.this.AppendText(msg);
                                        this.WriteAllObject(cm);
                                        continue;
                                    }

                                    this.WriteAllObject(cm);
                                    GameServer.this.AppendText(msg);
                                    continue;
                                }

                                this.Logout();
                            }
                        }
                    } catch (IOException var7) {
                        GameServer.this.AppendText("ois.readObject() error");

                        try {
                            this.ois.close();
                            this.oos.close();
                            this.client_socket.close();
                            this.Logout();
                        } catch (Exception var5) {
                        }
                    }

                    return;
                }
            }
        }
    }
}
