package msgs.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView username;
    private EditText password;
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
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                if(!username.getText().equals("") && !password.getText().equals("")) {
                    //TODO - check if text is empty
                    //TODO - connect to server for validation
                    editor.putString("username", username.getText().toString());
                    editor.commit();
                    Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(i);
                }

            }
        });
    }
    private void changeActivitySignUp(){
        Button btn = (Button) findViewById(R.id.sign_up_button);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SignUp.class);
                startActivity(i);
            }
        });
    }
}

