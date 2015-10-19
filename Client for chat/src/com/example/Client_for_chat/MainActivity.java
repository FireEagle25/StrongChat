package com.example.Client_for_chat;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends Activity implements View.OnClickListener, SoundPool.OnLoadCompleteListener{
    /**
     * Called when the activity is first created.
     */
    static String ip;
    static String nick;
    Button sentMsg;
    TextView mainTextView;
    EditText newMsg;
    Socket socket;
    SoundPool sp;
    int soundIdMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //TODO получение с каждого перехода с первой активити
        //TODO ориентация экрана
        Intent intent = getIntent();

        nick = "<" + intent.getStringExtra("nick") + ">";
        ip = intent.getStringExtra("ip");

        TaskReadMessage readMessages = new TaskReadMessage();
        readMessages.execute();

        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);
        try {
            soundIdMsg = sp.load(getAssets().openFd("msg.ogg"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sentMsg = (Button) findViewById(R.id.send);
        mainTextView = (TextView) findViewById(R.id.textView);
        mainTextView.setText("No connection.");
        newMsg = (EditText) findViewById(R.id.editText);

        sentMsg.setOnClickListener(this);

        Log.v("Ok","Let's go.");
    }

    public void setMainTextView(String newMessage) {
        if (mainTextView.getText().length() > 20000)
            mainTextView.setText(mainTextView.getText().toString().substring(1000));
        mainTextView.setText(mainTextView.getText().toString() + "\n" + newMessage);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.send) {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String fullMsg = nick + " " + newMsg.getText().toString() + "\0";
                char[] msgChars = (nick + " " + newMsg.getText().toString() + "\0").toCharArray();
                if (newMsg.getText().length() > 0 && msgChars.length < 1024) {
                    writer.write(msgChars, 0, fullMsg.length());
                    writer.flush();
                }
                else
                    mainTextView.setText(mainTextView.getText() + "\n" + "Error. Unvalid length of message.");
                TaskReadMessage readMessages = new TaskReadMessage();
                readMessages.execute();
            }
            catch (Exception e) {
                Log.v("Send message", e.getMessage());
            }
            newMsg.setText("");
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
        Log.d("onLoadComplete, i = " + i + ", i1 = " + i, "");
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                socket = new Socket(ipAddress, 1313);
            }
            catch (Exception e) {
                Log.v("Connection failed", e.getMessage());
            }
        }
    }

    class TaskReadMessage extends AsyncTask<Void, Void, Void> {
        String msgFromServer = "";
        byte[] buffer = new byte[1024];

        @Override
        protected Void doInBackground(Void... voids) {
            buffer[0] = '\0';
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                Log.v("Point 1", "Wait for message");

                in.read(buffer);
                Log.v("Point 2", "Get message");
            }
            catch(Exception exp){
                Log.v("Error", "Socket is not connected");
                (new Thread(new ClientThread())).start();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(buffer[0] != 0)
                sp.play(soundIdMsg, 0.3f, 0.3f, 0, 0, 2);
            else
                mainTextView.setText("Connected");
            Log.v("Point 3", "We go to recursion");
            super.onPostExecute(result);

            setMainTextView(new String(buffer));
            TaskReadMessage readMessages = new TaskReadMessage();
            readMessages.execute();
        }
    }

    @Override
    public void onBackPressed() {
        ip = "";
        nick = "";
        sentMsg.setText("");
        mainTextView.setText("");
        newMsg.setText("");
        socket = new Socket();
        this.finish();
    }
}


