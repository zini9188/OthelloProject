import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class GameRoom extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtInput;
    protected String UserName;
    protected String UserName2;
    private JButton btnSend;
    private JTextPane textArea;
    protected ImageIcon pinkIcon;
    protected ImageIcon greenIcon;
    protected JButton[][] buttonArray;
    protected Font font = new Font("궁서", 1, 20);
    protected boolean turn = false;
    private boolean checkP1 = false;
    protected int[][] saveButtonFlag;
    protected int[][] buttonFlag;
    protected int turnFlag;
    protected int X;
    protected int Y;
    protected int player1Count = 2;
    protected int player2Count = 2;
    protected int curRoomIndex;
    protected int putCount = 0;
    protected JLabel lblUserName;
    protected JLabel lblRoomNum;
    protected JLabel MyName;
    protected JLabel OpponentName;
    protected JLabel PlayerTurn;
    protected JLabel P1Count;
    protected JLabel P2Count;
    private JButton cancelBtn = new JButton();
    private GameClientView mainView;

    public GameRoom(final GameClientView gameClientView, String username, int roomN, int X, int Y, String roomName) {
        super(roomName);
        curRoomIndex = roomN;
        mainView = gameClientView;
        this.X = X;
        this.Y = Y;
        pinkIcon = new ImageIcon("images/slimeObj2.png");
        greenIcon = new ImageIcon("images/pinkbeanObj.png");
        setResizable(false);
        setDefaultCloseOperation(3);
        setBounds(100, 100, 1200, 720);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout((LayoutManager) null);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 280, 471);
        contentPane.add(scrollPane);
        textArea = new JTextPane();
        textArea.setEditable(true);
        textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
        scrollPane.setViewportView(textArea);
        txtInput = new JTextField();
        txtInput.setBounds(10, 489, 209, 40);
        contentPane.add(txtInput);
        txtInput.setColumns(10);
        btnSend = new JButton("Send");
        btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
        btnSend.setBounds(220, 489, 69, 40);
        contentPane.add(btnSend);
        lblUserName = new JLabel("Name");
        lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblUserName.setBackground(Color.WHITE);
        lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
        lblUserName.setHorizontalAlignment(0);
        lblUserName.setBounds(12, 539, 62, 40);
        contentPane.add(lblUserName);
        setVisible(true);
        lblRoomNum = new JLabel("RoomNum");
        lblRoomNum.setBorder(new LineBorder(new Color(0, 0, 0)));
        lblRoomNum.setBackground(Color.WHITE);
        lblRoomNum.setFont(new Font("굴림", Font.BOLD, 14));
        lblRoomNum.setHorizontalAlignment(0);
        lblRoomNum.setBounds(12, 580, 62, 40);
        contentPane.add(lblRoomNum);
        UserName = username;
        lblUserName.setText(username);
        lblRoomNum.setText(Integer.toString(roomN));
        JButton btnNewButton = new JButton("나가기");
        btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
        btnNewButton.addActionListener(e -> {
            ChatMsg msg = new ChatMsg(UserName, "201", "exit");
            msg.room_id = curRoomIndex;
            gameClientView.sendObject(msg);
            gameClientView.setVisible(true);
            dispose();
        });
        btnNewButton.setBounds(200, 539, 89, 40);
        contentPane.add(btnNewButton);
        CenterBoard cb = new CenterBoard();
        cb.setBounds(350, 20, 600, 600);
        EastPanel ep = new EastPanel();
        ep.setBounds(950, 20, 200, 500);
        contentPane.add(cb);
        contentPane.add(ep);
        TextSendAction action = new TextSendAction();
        btnSend.addActionListener(action);
        txtInput.addActionListener(action);
        txtInput.requestFocus();
    }

    public int getRoomIndex() {
        return curRoomIndex;
    }

    public void setTurnLabel() {
        if (turn) {
            PlayerTurn.setText("Your Turn");
            cancelBtn.setEnabled(false);
        } else {
            PlayerTurn.setText("Wait Your Turn");
            cancelBtn.setEnabled(true);
        }

        if (turnFlag == 0) {
            checkP1 = true;
            MyName.setIcon(greenIcon);
            OpponentName.setIcon(pinkIcon);
        } else {
            checkP1 = false;
            MyName.setIcon(pinkIcon);
            OpponentName.setIcon(greenIcon);
        }

    }

    public void getBackBoard(ChatMsg cm) {
        if (turn) {
            fun(cm);
        } else {
            fun(cm);
        }
    }

    private void fun(ChatMsg cm) {
        int i;
        int j;
        turn = !turn;
        setTurnLabel();
        for (i = 0; i < X; ++i) {
            for (j = 0; j < Y; ++j) {
                buttonFlag[i][j] = cm.saveButtonFlag[i][j];
                if (buttonFlag[i][j] == 1) {
                    buttonArray[i][j].setIcon(greenIcon);
                } else if (buttonFlag[i][j] == 2) {
                    buttonArray[i][j].setIcon(pinkIcon);
                } else {
                    buttonArray[i][j].setIcon(null);
                }
            }
        }
    }

    public void acceptCancel(ChatMsg cm) {
        if (!turn) {
            JOptionPane.showMessageDialog(this, UserName + "님이 무르기를 수락했습니다", "수락창", 1);
            AppendText(UserName2 + "님이 무르기를 수락");
        }
        getBackBoard(cm);
        setCount();
    }

    public void rejectCancel(ChatMsg cm) {
        if (!turn) {
            JOptionPane.showMessageDialog(this, UserName + "님이 무르기를 거절했습니다", "거절창", 1);
            AppendText(UserName2 + "님이 무르기를 거절");
        }
    }

    public void cancelFlag() {
        for (int i = 0; i < X; ++i) {
            if (Y >= 0) System.arraycopy(buttonFlag[i], 0, saveButtonFlag[i], 0, Y);
        }
    }

    public boolean isChange(int x, int y, int flag, boolean bool) {
        int changeCount = 0;

        changeCount = getChangeCount(x, y, flag, bool, changeCount);

        return changeCount > 0;
    }

    public void setCount() {
        player1Count = 0;
        player2Count = 0;
        putCount = 0;

        for (int i = 0; i < X; ++i) {
            for (int j = 0; j < X; ++j) {
                if (buttonFlag[i][j] == 1) {
                    ++player1Count;
                } else if (buttonFlag[i][j] == 2) {
                    ++player2Count;
                } else if (buttonFlag[i][j] == 0) {
                    ++putCount;
                }
            }
        }

        if (checkP1) {
            P1Count.setText("Pieces     " + player1Count);
            P2Count.setText("Pieces     " + player2Count);
        } else {
            P1Count.setText("Pieces     " + player2Count);
            P2Count.setText("Pieces     " + player1Count);
        }

        if (putCount == 0) {
            if (player1Count < player2Count) {
                if (checkP1) {
                    winner(UserName2);
                } else {
                    winner(UserName);
                }
            } else if (player2Count < player1Count) {
                if (checkP1) {
                    winner(UserName);
                } else {
                    winner(UserName2);
                }
            } else {
                JOptionPane.showMessageDialog(this, "무승부입니다.", "Message", 1);
            }
        }
    }

    public void winner(String winner) {
        JOptionPane.showMessageDialog(this, "승자는 " + winner + " 입니다.", "Message", 1);
    }

    protected void putStone(int x, int y, int flag) {
        int changeCount = 0;
        for (int i = 0; i < X; ++i) {
            if (Y >= 0) System.arraycopy(buttonFlag[i], 0, saveButtonFlag[i], 0, Y);
        }

        changeCount = getChangeCount(x, y, flag, false, changeCount);

        if (changeCount > 0) {
            changeStone(x, y, flag);
            setCount();
        }
    }

    private int getChangeCount(int x, int y, int flag, boolean bool, int changeCount) {
        for (Locations location : Locations.values()) {
            int nx = x + location.x;
            int ny = y + location.y;
            if (nx < 0 || nx > X - 1) {
                continue;
            }
            if (ny < 0 || ny > Y - 1) {
                continue;
            }
            int state = buttonFlag[nx][ny];
            if (state == 0 || state == flag + 1) {
                continue;
            }
            switch (location) {
                case North:
                    changeCount += putStoneNorth(x, y, flag, bool);
                    break;
                case Northeast:
                    changeCount += putStoneNorthEast(x, y, flag, bool);
                    break;
                case East:
                    changeCount += putStoneEast(x, y, flag, bool);
                    break;
                case Southeast:
                    changeCount += putStoneSouthEast(x, y, flag, bool);
                    break;
                case South:
                    changeCount += putStoneSouth(x, y, flag, bool);
                    break;
                case Southwest:
                    changeCount += putStoneSouthWest(x, y, flag, bool);
                    break;
                case West:
                    changeCount += putStoneWest(x, y, flag, bool);
                    break;
                case Northwest:
                    changeCount += putStoneNorthWest(x, y, flag, bool);
                    break;

                default:
                    break;
            }

        }
        return changeCount;
    }

    private int putStoneNorth(int x, int y, int flag, boolean bool) {
        List<Location> tempList = new ArrayList();
        ++x;

        while (x < X) {
            int state = buttonFlag[x][y];
            if (state == 0) {
                return 0;
            }

            if (flag == 0 && state == 2 || flag == 1 && state == 1) {
                tempList.add(new Location(x, y));
            }

            if (flag == 0 && state == 1 || flag == 1 && state == 2) {

                for (Location location : tempList) {
                    if (!bool) {
                        changeStone(location.x, location.y, flag);
                    }
                }

                return tempList.size();
            }

            ++x;
        }

        return 0;
    }

    private int putStoneNorthEast(int x, int y, int flag, boolean bool) {
        List<Location> tempList = new ArrayList();
        ++x;
        --y;

        while (x < X && y >= 0) {
            int state = buttonFlag[x][y];
            if (state == 0) {
                return 0;
            }

            if (flag == 0 && state == 2 || flag == 1 && state == 1) {
                tempList.add(new Location(x, y));
            }

            if (flag == 0 && state == 1 || flag == 1 && state == 2) {
                for (Location location : tempList) {
                    if (!bool) {
                        changeStone(location.x, location.y, flag);
                    }
                }
                return tempList.size();
            }

            ++x;
            --y;
        }

        return 0;
    }

    private int putStoneEast(int x, int y, int flag, boolean bool) {
        List<Location> tempList = new ArrayList();
        --y;

        while (y >= 0) {
            int state = buttonFlag[x][y];
            if (state == 0) {
                return 0;
            }

            if (flag == 0 && state == 2 || flag == 1 && state == 1) {
                tempList.add(new Location(x, y));
            }

            if (flag == 0 && state == 1 || flag == 1 && state == 2) {

                for (Location location : tempList) {
                    if (!bool) {
                        changeStone(location.x, location.y, flag);
                    }
                }

                return tempList.size();
            }

            --y;
        }

        return 0;
    }

    private int putStoneSouthEast(int x, int y, int flag, boolean bool) {
        List<Location> tempList = new ArrayList();
        --x;
        --y;

        while (x >= 0 && y >= 0) {
            int state = buttonFlag[x][y];
            if (state == 0) {
                return 0;
            }

            if (flag == 0 && state == 2 || flag == 1 && state == 1) {
                tempList.add(new Location(x, y));
            }

            if (flag == 0 && state == 1 || flag == 1 && state == 2) {

                for (Location location : tempList) {
                    if (!bool) {
                        changeStone(location.x, location.y, flag);
                    }
                }

                return tempList.size();
            }

            --x;
            --y;
        }

        return 0;
    }

    private int putStoneSouth(int x, int y, int flag, boolean bool) {
        List<Location> tempList = new ArrayList();
        --x;

        while (x >= 0) {
            int state = buttonFlag[x][y];
            if (state == 0) {
                return 0;
            }

            if (flag == 0 && state == 2 || flag == 1 && state == 1) {
                tempList.add(new Location(x, y));
            }

            if (flag == 0 && state == 1 || flag == 1 && state == 2) {

                for (Location location : tempList) {
                    if (!bool) {
                        changeStone(location.x, location.y, flag);
                    }
                }

                return tempList.size();
            }

            --x;
        }

        return 0;
    }

    private int putStoneSouthWest(int x, int y, int flag, boolean bool) {
        List<Location> tempList = new ArrayList();
        --x;
        ++y;

        while (x >= 0 && y < Y) {
            int state = buttonFlag[x][y];
            if (state == 0) {
                return 0;
            }

            if (flag == 0 && state == 2 || flag == 1 && state == 1) {
                tempList.add(new Location(x, y));
            }

            if (flag == 0 && state == 1 || flag == 1 && state == 2) {

                for (Location location : tempList) {
                    if (!bool) {
                        changeStone(location.x, location.y, flag);
                    }
                }

                return tempList.size();
            }

            --x;
            ++y;
        }

        return 0;
    }

    private int putStoneWest(int x, int y, int flag, boolean bool) {
        List<Location> tempList = new ArrayList();
        ++y;

        while (y < Y) {
            int state = buttonFlag[x][y];
            if (state == 0) {
                return 0;
            }

            if (flag == 0 && state == 2 || flag == 1 && state == 1) {
                tempList.add(new Location(x, y));
            }

            if (flag == 0 && state == 1 || flag == 1 && state == 2) {

                for (Location location : tempList) {
                    if (!bool) {
                        changeStone(location.x, location.y, flag);
                    }
                }

                return tempList.size();
            }

            ++y;
        }

        return 0;
    }

    private int putStoneNorthWest(int x, int y, int flag, boolean bool) {
        List<Location> tempList = new ArrayList();
        ++x;
        ++y;

        while (x < X && y < Y) {
            int state = buttonFlag[x][y];
            if (state == 0) {
                return 0;
            }

            if (flag == 0 && state == 2 || flag == 1 && state == 1) {
                tempList.add(new Location(x, y));
            }

            if (flag == 0 && state == 1 || flag == 1 && state == 2) {

                for (Location location : tempList) {
                    if (!bool) {
                        changeStone(location.x, location.y, flag);
                    }
                }

                return tempList.size();
            }

            ++x;
            ++y;
        }

        return 0;
    }

    private void changeStone(int x, int y, int flag) {
        if (flag == 0) {
            buttonArray[x][y].setIcon(greenIcon);
            buttonFlag[x][y] = 1;
        } else if (flag == 1) {
            buttonArray[x][y].setIcon(pinkIcon);
            buttonFlag[x][y] = 2;
        }

    }

    private void init() {
        if (X == 8 && Y == 8) {
            buttonArray[3][3].setIcon(pinkIcon);
            buttonArray[3][4].setIcon(greenIcon);
            buttonArray[4][3].setIcon(greenIcon);
            buttonArray[4][4].setIcon(pinkIcon);
            buttonFlag[3][3] = 2;
            buttonFlag[3][4] = 1;
            buttonFlag[4][3] = 1;
            buttonFlag[4][4] = 2;
            saveButtonFlag[3][3] = 2;
            saveButtonFlag[3][4] = 1;
            saveButtonFlag[4][3] = 1;
            saveButtonFlag[4][4] = 2;
        } else if (X == 10 && Y == 10) {
            buttonArray[4][4].setIcon(pinkIcon);
            buttonArray[4][5].setIcon(greenIcon);
            buttonArray[5][4].setIcon(greenIcon);
            buttonArray[5][5].setIcon(pinkIcon);
            buttonFlag[4][4] = 2;
            buttonFlag[4][5] = 1;
            buttonFlag[5][4] = 1;
            buttonFlag[5][5] = 2;
            saveButtonFlag[4][4] = 2;
            saveButtonFlag[4][5] = 1;
            saveButtonFlag[5][4] = 1;
            saveButtonFlag[5][5] = 2;
        } else if (X == 12 && Y == 12) {
            buttonArray[5][5].setIcon(pinkIcon);
            buttonArray[5][6].setIcon(greenIcon);
            buttonArray[6][5].setIcon(greenIcon);
            buttonArray[6][6].setIcon(pinkIcon);
            buttonFlag[5][5] = 2;
            buttonFlag[5][6] = 1;
            buttonFlag[6][5] = 1;
            buttonFlag[6][6] = 2;
            saveButtonFlag[5][5] = 2;
            saveButtonFlag[5][6] = 1;
            saveButtonFlag[6][5] = 1;
            saveButtonFlag[6][6] = 2;
        }

    }

    public void Join(ChatMsg cm) {
        if (cm.roomCount.equals("1/2")) {
            turn = true;
        }

        if (cm.UserName.equals(UserName) && cm.room_id == getRoomIndex()) {
            if (turn) {
                turnFlag = 0;
            } else {
                turnFlag = 1;
            }

            UserName2 = cm.UserName2;
            MyName.setText(UserName);
            OpponentName.setText(UserName2);
            setTurnLabel();
        } else {
            UserName = cm.UserName2;
            UserName2 = cm.UserName;
            MyName.setText(UserName);
            OpponentName.setText(UserName2);
            setTurnLabel();
        }

    }

    public void Set(ChatMsg cm) {
        if (cm.UserName.equals(UserName)) {
            if (buttonFlag[cm.x][cm.y] == 0) {
                putStone(cm.x, cm.y, cm.turnFlag);
            }

            PlayerTurn.setText("Wait Your Turn");
        } else {
            if (buttonFlag[cm.x][cm.y] == 0) {
                putStone(cm.x, cm.y, cm.turnFlag);
            }

            turn = true;
            cancelBtn.setEnabled(false);
            PlayerTurn.setText("Your Turn");
        }

    }

    public void Cancel(ChatMsg cm) {
        if (turn) {
            int result = JOptionPane.showConfirmDialog(this, cm.UserName + "님의 무르기 요청을 수락하시겠습니까?", "무르기 요청", 1);
            ChatMsg cm2;
            if (result == 0) {
                cm2 = new ChatMsg(UserName, "303", "cancleY");
                cm2.saveButtonFlag = saveButtonFlag;
            } else {
                cm2 = new ChatMsg(UserName, "302", "cancelN");
            }
            cm2.room_id = curRoomIndex;
            mainView.sendObject(cm2);
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
        } catch (BadLocationException var5) {
            var5.printStackTrace();
        }

        int len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len);
    }

    public void AppendTextRight(String msg) {
        msg = msg.trim();
        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, 2);
        StyleConstants.setForeground(right, Color.BLUE);
        doc.setParagraphAttributes(doc.getLength(), 1, right, false);

        try {
            doc.insertString(doc.getLength(), msg + "\n", right);
        } catch (BadLocationException var5) {
            var5.printStackTrace();
        }

        int len = textArea.getDocument().getLength();
        textArea.setCaretPosition(len);
    }

    class CenterBoard extends JPanel {

        public CenterBoard() {
            setLayout(new GridLayout(X, Y));
            buttonArray = new JButton[X][Y];
            buttonFlag = new int[X][Y];
            saveButtonFlag = new int[X][Y];

            for (int i = 0; i < X; ++i) {
                for (int j = 0; j < Y; ++j) {
                    JButton button = new JButton();
                    Color boardColor1 = new Color(190, 166, 134);
                    Color boardColor2 = new Color(148, 125, 95);
                    if (i % 2 == 0) {
                        if (j % 2 == 0) {
                            button.setBackground(boardColor1);
                        } else {
                            button.setBackground(boardColor2);
                        }
                    } else if (j % 2 != 0) {
                        button.setBackground(boardColor1);
                    } else {
                        button.setBackground(boardColor2);
                    }

                    Color boardLine = new Color(102, 86, 65);
                    LineBorder lineBorder = new LineBorder(boardLine);
                    button.setBorder(lineBorder);
                    button.addActionListener(new myListener());
                    button.setActionCommand(String.valueOf(i) + "," + j);
                    buttonArray[i][j] = button;
                    saveButtonFlag[i][j] = 0;
                    buttonFlag[i][j] = 0;
                    add(button);
                }
            }

            init();
        }
    }

    class EastPanel extends JPanel {
        public EastPanel() {
            setLayout(new GridLayout(10, 1, 5, 5));
            setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 0));
            PlayerTurn = new JLabel("연결중", 0);
            PlayerTurn.setForeground(new Color(100, 100, 100));
            MyName = new JLabel("연결중", 2);
            P1Count = new JLabel("Pieces     " + Integer.toString(player1Count), 2);
            OpponentName = new JLabel("연결중", 2);
            P2Count = new JLabel("Pieces     " + Integer.toString(player2Count), 2);
            cancelBtn = new JButton("무르기");
            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ChatMsg cm = new ChatMsg(UserName, "301", "cancel");
                    cm.turnFlag = turnFlag;
                    cm.room_id = curRoomIndex;
                    mainView.sendObject(cm);
                }
            });
            PlayerTurn.setFont(font);
            P1Count.setFont(font);
            P2Count.setFont(font);
            MyName.setFont(font);
            OpponentName.setFont(font);
            cancelBtn.setFont(font);
            add(MyName);
            add(P1Count);
            add(OpponentName);
            add(P2Count);
            add(PlayerTurn);
            add(cancelBtn);
        }
    }

    public class Location {
        public int x;
        public int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static enum Locations {
        North(1, 0),
        Northeast(1, -1),
        East(0, -1),
        Southeast(-1, -1),
        South(-1, 0),
        Southwest(-1, 1),
        West(0, 1),
        Northwest(1, 1);

        private final int x;
        private final int y;

        Locations(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class TextSendAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == btnSend || e.getSource() == txtInput) {
                String msg = null;
                msg = txtInput.getText();
                ChatMsg cm = new ChatMsg(UserName, "400", msg);
                cm.room_id = curRoomIndex;
                mainView.sendObject(cm);
                txtInput.setText("");
                txtInput.requestFocus();
            }

        }
    }

    public class myListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String point = e.getActionCommand();
            String[] pos = point.split(",", 0);
            int x = Integer.parseInt(pos[0]);
            int y = Integer.parseInt(pos[1]);
            if (turn && buttonFlag[x][y] == 0 && isChange(x, y, turnFlag, true) && UserName2 != null) {
                cancelFlag();
                ChatMsg cm = new ChatMsg(UserName, "300", "Set");
                cm.x = x;
                cm.y = y;
                cm.turnFlag = turnFlag;
                cm.room_id = curRoomIndex;
                turn = false;
                cancelBtn.setEnabled(true);
                mainView.sendObject(cm);
            }
        }
    }
}
