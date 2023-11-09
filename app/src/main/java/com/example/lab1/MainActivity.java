package com.example.lab1;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    protected Button sendButton;
    protected EditText questionText;
    protected RecyclerView chatMessageList;
    protected TextToSpeech textToSpeech;
    protected MessageListAdapter messageListAdapter;
    protected AI ai;
    protected SharedPreferences sPref;
    public static final String APP_PREFERENCES = "mysettings";
    private boolean isLight = true;
    private String THEME = "THEME";
    DBHelper dBHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        dBHelper = new DBHelper(this);
        database = dBHelper.getWritableDatabase();
        isLight = sPref.getBoolean(THEME, true);
        if (!isLight)
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);
        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);
        messageListAdapter = new MessageListAdapter();
        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);
/*        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                    textToSpeech.setPitch(1);
                }
            }
        });*/
        textToSpeech = new TextToSpeech(this, this);
        ai = new AI();
        if (savedInstanceState == null){
            Cursor cursor = database.query(dBHelper.TABLE_MESSAGES, null, null, null, null, null, null);
            if (cursor.moveToFirst()){
                int messageIndex = cursor.getColumnIndex(dBHelper.FIELD_MESSAGE);
                int dateIndex = cursor.getColumnIndex(dBHelper.FIELD_DATE);
                int sendIndex = cursor.getColumnIndex(dBHelper.FIELD_SEND);
                do{
                    MessageEntity entity = new MessageEntity(cursor.getString(messageIndex),
                            cursor.getString(dateIndex), cursor.getInt(sendIndex));
                    Message message = new Message(entity);
                    messageListAdapter.messageList.add(message);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable("messageList", messageListAdapter.messageList);
        //outState.
    }
    public void OnClick(View view) {
        String question = questionText.getText().toString();
        if (question.length() == 0)
            return;
        messageListAdapter.messageList.add(new Message(question, true));
        ai.getAnswer(question, new Consumer<String>() {
            @Override
            public void accept(String s) {
                messageListAdapter.messageList.add(new Message(s, false));
                messageListAdapter.notifyDataSetChanged();
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size() - 1);
                questionText.setText("");
                textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });


    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        messageListAdapter.messageList = (ArrayList<Message>) savedInstanceState.get("messageList");
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.getDefault());
            textToSpeech.setPitch(1);
        }
        else if (status == TextToSpeech.ERROR){

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.day_setting:
                isLight = true;
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                return true;
            case R.id.night_setting:
                isLight = false;
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                return true;
            default:
                return false;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isLight);
        editor.apply();
        database.delete(dBHelper.TABLE_MESSAGES, null, null);
        for (int i = 0; i < messageListAdapter.messageList.size(); i++) {
            MessageEntity entity = new MessageEntity(messageListAdapter.messageList.get(i));
            ContentValues contentValues = new ContentValues();
            contentValues.put(dBHelper.FIELD_MESSAGE, entity.text);
            contentValues.put(dBHelper.FIELD_SEND, entity.isSend);
            contentValues.put(dBHelper.FIELD_DATE, entity.date);
            database.insert(dBHelper.TABLE_MESSAGES, null, contentValues);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        database.close();
    }
}
