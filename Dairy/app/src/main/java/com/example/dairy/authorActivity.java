package com.example.dairy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class authorActivity extends AppCompatActivity {
    Button btn_author;
    private EditText editText;
    private SharedPreferences pref;
    //定义一个SharedPreferences对象
    private SharedPreferences.Editor editor;
    //调用SharedPreferences对象的edit()方法来获取一个SharedPreferences.Editor对象，用以添加要保存的数据

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author);

        btn_author=(Button) findViewById(R.id.btn_author);
        editText=(EditText) findViewById(R.id.text_author);
        pref= PreferenceManager.getDefaultSharedPreferences(this);

        btn_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String author=editText.getText().toString();
                editor=pref.edit();
                editor.putString("author",author);
                editor.apply();
                Intent intent = new Intent(authorActivity.this, com.example.dairy.MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
