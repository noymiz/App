package msgs.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView message;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (TextView) findViewById(R.id.textView);
        changeMessage();
    }

    private void changeMessage() {
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                ArrayList<String> msgs = new ArrayList<>();
                msgs.add("Text");
                msgs.add("Chat");
                msgs.add("Message");
                msgs.add("Fun");

                for (int i = 0; i<4; i++) {
                    msg = msgs.get(i);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            message.setText(msg);
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                Intent intent;
                if(settings.getString("firstTime", "Yes").equals("Yes")){
                    editor.putString("firstTime", "No");
                    editor.commit();
                    intent = new Intent(MainActivity.this, ExpActivity.class);
                }
                else if (settings.getString("username", "User not found").equals("User not found")) {
                    // TODO connect to web server
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }
                else{
                    intent = new Intent(MainActivity.this, MenuActivity.class);
                }
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
        t.start();
    }



}
