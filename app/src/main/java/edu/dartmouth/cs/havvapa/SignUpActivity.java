package edu.dartmouth.cs.havvapa;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.dartmouth.cs.havvapa.APIs.TextToSpeechHelper;
import edu.dartmouth.cs.havvapa.utils.Preferences;

public class SignUpActivity extends AppCompatActivity
{
    private static String TAG = "SingUpActivity";
    private FirebaseAuth mAuth;
    private EditText name_et;
    private EditText email_et;
    private EditText password_et;
    private Preferences pref;
    private static TextToSpeechHelper textToSpeechHelper;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_up_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.su_menuitem_save:
                if(pref.isUserLoggedIn()){
                    //save name
                    name_et = findViewById(R.id.su_name_et);
                    pref.setUsername(name_et.getText().toString());
                }else {
                    createProfile();
                }
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sign_up);
        getSupportActionBar().setTitle("Sign Up");  // Set the title. I don't know how it can give null pointer exception.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pref = new Preferences(getApplicationContext());
        //set visited log in
        pref.setHasVisitedLogIn(true);

        email_et = findViewById(R.id.su_email_et);
        password_et = findViewById(R.id.su_password_et);
        name_et = findViewById(R.id.su_name_et);

        mAuth = FirebaseAuth.getInstance();
        textToSpeechHelper = new TextToSpeechHelper();
    }

    public void createProfile() {
        String email = email_et.getText().toString();
        String password = password_et.getText().toString();
        final String name = name_et.getText().toString();

        if(name.isEmpty() && pref.getUsername().isEmpty()){
            textToSpeechHelper.readAloud("I would love to learn your name");
            Toast.makeText(SignUpActivity.this, "Enter your name,please",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!validate(password,email)){
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password) //check for empty string
                .addOnCompleteListener(SignUpActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(SignUpActivity.this, "Authentication worked.",
                                          Toast.LENGTH_LONG).show();


                                    pref.setUserLoggedIn(true);
                                    pref.setUsername(name);
                                    startActivity(new Intent(SignUpActivity.this, GreetingsActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed:Enter proper email"
                                                    + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }

                            }
                        });

    }

    public boolean validate(String email, String password){
       if (password.isEmpty() || email.isEmpty()){
        Toast.makeText(SignUpActivity.this, "Either password or email is empty",
                Toast.LENGTH_LONG).show();
        return false;
       }else if(password.length()<6){
           Toast.makeText(SignUpActivity.this, "Password must be longer than 5 characters",
                   Toast.LENGTH_LONG).show();
           return false;
       }
       return true;
    }

}
