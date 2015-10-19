package com.example.Client_for_chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.Client_for_chat.R.*;

/**
 * Created by FireEagle on 10.10.2015.
 */
public class EnteryActivity extends Activity implements View.OnClickListener {
    EditText nick;
    EditText ip;
    Button enterButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.setnick);

        nick = (EditText)findViewById(id.nickfield);
        ip = (EditText)findViewById(id.ipfield);
        enterButton = (Button) findViewById(id.enterToChatButton);

        enterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == id.enterToChatButton) {
            Intent intent = new Intent(EnteryActivity.this, MainActivity.class);

            intent.putExtra("ip", ip.getText().toString());
            intent.putExtra("nick", nick.getText().toString());
            startActivity(intent);

        }
    }
}
