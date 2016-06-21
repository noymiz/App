package msgs.app;

/**
 * Created by Noy on 21/06/2016.
 */
public class Msg {
    private String text;
    private String username;
    private String time;
    public Msg(String user, String currentTime, String txt) {
        text = txt;
        time = currentTime;
        username = user;
    }

    public String getMsg() {
        return username + ": " + text + "\n" + time;
    }
}
