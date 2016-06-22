package msgs.app;

/**
 * Created by Noy on 21/06/2016.
 */
public class Msg {
    private String text;
    private String username;
    private String time;
    public Msg(String user, String currentTime, String txt) {
        setText(txt);
        setTime(currentTime);
        setUsername(user);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username+":";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
