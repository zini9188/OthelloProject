import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    public String code;
    public String UserName;
    public String UserName2;
    public String data;
    public int turnFlag;
    public int[][] saveButtonFlag;
    public int room_id;
    public int x;
    public int y;
    public int user_id;
    public int roomSize;
    public Vector<String> roomName;
    public String roomCount;
    public Vector<String> roomCountV;
    public Vector<Integer> roomSizeV;

    public ChatMsg(String UserName, String code, String msg) {
        this.code = code;
        this.UserName = UserName;
        this.data = msg;
    }
}
