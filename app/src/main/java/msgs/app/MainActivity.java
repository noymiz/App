package msgs.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView message;
    private String msg;
    private UserLoginMainTask mAuthTask;

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
                editor.clear();editor.commit();
                Intent intent;
                if(settings.getString("firstTime", "Yes").equals("Yes")){
                    editor.putString("firstTime", "No");
                    editor.commit();
                    intent = new Intent(MainActivity.this, ExpActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
                else if (settings.getString("username", "User not found").equals("User not found")) {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
                else{
                    mAuthTask = new UserLoginMainTask(settings.getString("username",""),
                                                                settings.getString("password", ""));
                   mAuthTask.execute();
                }
            }
        });
        t.start();
    }

    public class UserLoginMainTask extends AsyncTask<Void, Void, String> {
        private String user;
        private String pass;

        public UserLoginMainTask(String u,String p){
            this.user = u;
            this.pass = p;
        }
        @Override
        protected String doInBackground(Void... params) {
            String ans = null;
            try {
                StringBuilder req = new StringBuilder();
                req.append("http://192.168.1.11:8080/Server/Login?username=").append(user).append("&")
                        .append("password=").append(pass);
                URL url = new URL(req.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    ans = responseStrBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ans;
        }

        @Override
        protected void onPostExecute(final String answer) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", user);
                editor.putString("password", pass);
                editor.commit();
                Intent i = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(i);
                finish();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

}
