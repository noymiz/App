package msgs.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView username;
    private EditText password;
    private Button signIn;
    private Button register;
    private UserLoginTask mAuthTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        changeActivitySignIn();
        changeActivitySignUp();
        username = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
}


    private void changeActivitySignIn(){
        Button btn = (Button) findViewById(R.id.email_sign_in_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                     mAuthTask = new UserLoginTask(username.getText().toString(),password.getText().toString());
                    mAuthTask.execute();

                }else{
                    Toast.makeText(LoginActivity.this, "All fields are required!",
                    Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void changeActivitySignUp(){
        Button btn = (Button) findViewById(R.id.sign_up_button);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(LoginActivity.this,SignUp.class);
                startActivity(i);

            }
        });
    }

    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        private String user;
        private String pass;

        public UserLoginTask(String u,String p){
            this.user = u;
            this.pass = p;
        }
        @Override
        protected String doInBackground(Void... params) {
            String ans = null;
            try {
                StringBuilder req = new StringBuilder();
                req.append("http://192.168.1.11:8080/Server/Login?username=").append(user.replaceAll(" ", "%20")).append("&")
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
                if (answer != null && answer.equals("Success")) {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username", user);
                    editor.putString("password", pass);
                    editor.commit();
                    finish();
                    Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(i);
                }
                else
                {
                    if (answer == null)
                        Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(LoginActivity.this, answer, Toast.LENGTH_LONG).show();
                }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}


