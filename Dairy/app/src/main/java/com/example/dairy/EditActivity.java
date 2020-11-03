package com.example.dairy;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    DBService myDb;
    private Button btnCancel;
    private Button btnSave;
    private EditText titleEditText;
    private EditText contentEditText;
    private TextView timeTextView;
    private TextView authorTextView;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dairy_editor);

        pref = getSharedPreferences("com.example.dairy_preferences", Context.MODE_PRIVATE);
        String author = pref.getString("author","");

        init();
        if(timeTextView.getText().length()==0){
            timeTextView.setText(getTime());
        }
        if(authorTextView.getText().length()==0){
            authorTextView.setText(author);
        }
    }

    private void init() {

        myDb = new DBService(this);
        SQLiteDatabase db = myDb.getReadableDatabase();
        titleEditText = findViewById(R.id.et_title);
        contentEditText = findViewById(R.id.et_content);
        timeTextView = findViewById(R.id.edit_time);
        authorTextView = findViewById(R.id.edit_author);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);

        //按钮点击事件
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();


                String title= titleEditText.getText().toString();
                String content=contentEditText.getText().toString();
                String time=timeTextView.getText().toString();
                String author=authorTextView.getText().toString();

                if("".equals(titleEditText.getText().toString())){
                    Toast.makeText(EditActivity.this,"标题不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if("".equals(contentEditText.getText().toString())) {
                    Toast.makeText(EditActivity.this,"内容不能为空",Toast.LENGTH_LONG).show();
                    return;
                }

                values.put(DBService.TITLE,title);
                values.put(DBService.CONTENT,content);
                values.put(DBService.TIME,time);
                values.put(DBService.AUTHOR,author);
                db.insert(DBService.TABLE,null,values);
                Toast.makeText(EditActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
                db.close();
                finish();
            }
        });
    }

    //获取当前时间
    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String str = sdf.format(date);
        return str;
    }

}
