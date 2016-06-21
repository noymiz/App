package msgs.app;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Noy on 19/06/2016.
 */
public class User {
    private String username;
    private String password;
    private String name;
    private String Email;
    private String icon;

    public User(JSONObject object){
        try {
            this.name = object.getString("Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
