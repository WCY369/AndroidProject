package com.android.MyWeather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.android.MyWeather.R;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ShowCityItems extends AppCompatActivity {

    //控件
    private ListView listView;
    private Button searchBtn;
    private TextView text1;
    //private String city="北京";
    public List<String> cityitems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_weather);

        //初始化
        init();

        SharedPreferences sharedPreferences = getSharedPreferences("CITY_ITEMS", Activity.MODE_PRIVATE);
        String listJson = sharedPreferences.getString("CITY_NAME","");
        if(!listJson.equals("")){
            Gson city_gson = new Gson();
            cityitems = city_gson.fromJson(listJson,new TypeToken<List<String>>(){}.getType());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ShowCityItems.this, android.R.layout.simple_list_item_1,cityitems
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("CityName", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("city",cityitems.get(position));
                editor.commit();
                Intent intent = new Intent(ShowCityItems.this, SearchWeather.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void init() {
        //获取控件
        listView = findViewById(R.id.home_weather);
        searchBtn = findViewById(R.id.search_btn);
        text1 = findViewById(android.R.id.text1);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("CityName", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("city","");
                editor.commit();
                Intent intent = new Intent(ShowCityItems.this, SearchWeather.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
