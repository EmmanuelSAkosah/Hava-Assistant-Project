package edu.dartmouth.cs.havvapa;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SignUpActivity extends AppCompatActivity
{
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
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
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

        Spinner topicsSpinner = findViewById(R.id.su_topics_spinner);
        ArrayAdapter<CharSequence> inputArrayAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sampe_topics, android.R.layout.simple_spinner_item);
        inputArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicsSpinner.setAdapter(inputArrayAdapter);
        



    }

}
