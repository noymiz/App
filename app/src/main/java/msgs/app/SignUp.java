package msgs.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUp extends AppCompatActivity {
    private AutoCompleteTextView username;
    private EditText password;
    private AutoCompleteTextView email;
    private AutoCompleteTextView name;
    private Button signUp;
    private RadioGroup icon;
    private UserSignUpTask mAuthTask;
    private String iconImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        changeActivitySignUp();
        username = (AutoCompleteTextView) findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        name = (AutoCompleteTextView) findViewById(R.id.name);
        icon = (RadioGroup) findViewById(R.id.icons);
        icon.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_img1) {
                    iconImg = "img1.png";
                } else if (checkedId == R.id.radio_img2){
                    iconImg = "img2.png";
                }else{
                    iconImg = "img3.png";
                }
            }
        });

    }

    private void changeActivitySignUp(){
        Button btn = (Button) findViewById(R.id.email_sign_up_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allFilled = !username.getText().toString().isEmpty() &&
                                    !password.getText().toString().isEmpty() &&
                                    !email.getText().toString().isEmpty() &&
                                    !name.getText().toString().isEmpty();
                if(allFilled) {
                    mAuthTask = new UserSignUpTask(username.getText().toString(),password.getText().toString(),
                            email.getText().toString(),name.getText().toString(),iconImg);
                    mAuthTask.execute();

                }else{
                    Toast.makeText(SignUp.this, "All fields are required!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public class UserSignUpTask extends AsyncTask<Void, Void, String> {
        private String ic;
        private String ema;
        private String nam;
        private String user;
        private String pass;

        public UserSignUpTask(String u,String p, String n, String e, String i){
            this.user = u;
            this.pass = p;
            this.nam = n;
            this.ema = e;
            this.ic = i;
        }
        @Override
        protected String doInBackground(Void... params) {
            String ans = null;
            try {
                StringBuilder req = new StringBuilder();
                req.append("http://advprog.cs.biu.ac.il:8080/NoyRoi/SignUp?username=").append(user.replaceAll(" ", "%20"))
                        .append("&password=").append(pass).append("&email=").append(ema)
                        .append("&name=").append(nam).append("&icon=").append(ic);
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
                Intent i = new Intent(SignUp.this, MenuActivity.class);
                startActivity(i);
            }
            else
            {
                if (answer == null)
                    Toast.makeText(SignUp.this, "Failed", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(SignUp.this, answer, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
