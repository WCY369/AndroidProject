package com.android.MyWeather.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.android.MyWeather.R;
import com.android.MyWeather.bean.Casts;
import com.android.MyWeather.bean.Weather;
import com.android.MyWeather.util.WeatherUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchWeather extends AppCompatActivity {
    private EditText edit_city;
    private Button search_btn;
    private Button guanzhu_btn;
    private Button return_btn;
    private ListView listView;

    //请求的API，详细参考https://lbs.amap.com/api/webservice/guide/api/weatherinfo/
    private String url = "https://restapi.amap.com/v3/weather/weatherInfo?key=9e8b753f8eb079a12a350ae41a4cf7ec&extensions=all&out=json";
    //使用OkHttpClient进行网络请求
    private OkHttpClient httpClient = new OkHttpClient();
    //使用Gson解析json字符串
    private Gson gson = new Gson();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(SearchWeather.this, "暂不支持改城市天气查询！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(SearchWeather.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(SearchWeather.this, "城市编号：" + (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    List<Map<String, String>> list = (List<Map<String, String>>) msg.obj;
                    //创建Adapter
                    final SimpleAdapter simpleAdapter = new SimpleAdapter(SearchWeather.this
                            , list, R.layout.weather_listview_item
                            , new String[]{"date", "updateTime", "day_weather", "day_temp", "day_wind", "day_power"
                            , "night_weather", "night_temp", "night_wind", "night_power"}
                            , new int[]{R.id.date,R.id.updateTime,R.id.day_weather, R.id.day_temp, R.id.day_wind, R.id.day_power
                            , R.id.night_weather, R.id.night_temp, R.id.night_wind, R.id.night_power});
                    //绑定Adapter
                    listView.setAdapter(simpleAdapter);
                    Toast.makeText(SearchWeather.this, "查询成功", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_weather);
        //初始化
        init();
    }

    private void init() {
        edit_city = findViewById(R.id.edit_city);
        search_btn = findViewById(R.id.search_w_btn);
        guanzhu_btn = findViewById(R.id.guanzhu_btn);
        return_btn = findViewById(R.id.return_btn);

        SharedPreferences sharedPreferences = getSharedPreferences("CityName", Activity.MODE_PRIVATE);
        String address = sharedPreferences.getString("city","");
        if(!address.equals("")){
            edit_city.setText(address);
            getAdcode(address);
        }

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = edit_city.getText().toString();
                //调用方法获取该城市的城市编码
                getAdcode(address);
            }
        });
        guanzhu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = edit_city.getText().toString();
                //从SharedPreferences中取出关注的城市列表
                List<String> cityitems = new ArrayList<String>();
                SharedPreferences sharedPreferences = getSharedPreferences("CITY_ITEMS", Activity.MODE_PRIVATE);
                String listJson = sharedPreferences.getString("CITY_NAME","");
                if(!listJson.equals("")){//json：字符串与list之间的转换
                    Gson city_gson = new Gson();
                    cityitems = city_gson.fromJson(listJson,new TypeToken<List<String>>(){}.getType());
                }

                if(!address.equals("")&&!cityitems.contains(address)){
                    cityitems.add(address);
                    Toast.makeText(SearchWeather.this, "关注成功！可返回列表查看", Toast.LENGTH_SHORT).show();
                    getAdcode(address);
                }else if(cityitems.contains(address)){
                    cityitems.remove(address);
                    Toast.makeText(SearchWeather.this, "取消关注成功！可返回列表查看", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SearchWeather.this, "关注城市不能为空！", Toast.LENGTH_SHORT).show();
                }

                //把list存进SharedPreferences中
                Gson city_gson = new Gson();
                String cityitem = city_gson.toJson(cityitems);//json：把list转换成字符串
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("CITY_NAME",cityitem);
                editor.commit();
            }
        });

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchWeather.this,ShowWeather.class);
                startActivity(intent);
                finish();
            }
        });
        listView = findViewById(R.id.search_weather);
    }

    /**
     * 高德开发平台请求天气查询API中区域编码adcode是必须项，使用高德地理编码服务获取区域编码
     * address:结构化地址信息，规则遵循：国家、省份、城市、区县、城镇、乡村、街道、门牌号码、屋邨、大厦
     */
    private void getAdcode(String address) {
        String url = "https://restapi.amap.com/v3/geocode/geo?key=9e8b753f8eb079a12a350ae41a4cf7ec&address=" + address;

        final Request request = new Request.Builder().url(url).get().build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = httpClient.newCall(request).execute();
                    //请求成功
                    if (response.isSuccessful()) {
                        String result = response.body().string();

                        Log.i("result", result);

                        //转JsonObject
                        JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                        //转JsonArray
                        JsonArray array = object.get("geocodes").getAsJsonArray();
                        JsonObject info = array.get(0).getAsJsonObject();

                        //获取adcode
                        String adcode = info.get("adcode").getAsString();

                        Log.i("测试获取adcode", adcode);
                        //请求查询天气
                        getWeather(adcode);


                        Message message = Message.obtain();
                        message.what = 2;
                        message.obj = adcode;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.i("SearchMainActivity.java", "服务器异常:" + e.toString());

                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = "服务器异常";
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 查询天气
     */
    private void getWeather(String adcode) {
        String newUrl = url + "&city=" + adcode;
        final Request request = new Request.Builder().url(newUrl).get().build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = httpClient.newCall(request).execute();
                    //请求成功
                    if (response.isSuccessful()) {
                        String result = response.body().string();

                        Log.i("服务器返回的结果:", result);

                        //存储解析json字符串得到的天气信息
                        List<Map<String, String>> weatherList = new ArrayList<>();

                        //使用Gson解析
                        Weather weather = gson.fromJson(result, Weather.class);
                        //获取更新时间
                        String updateTime = weather.getForecasts().get(0).getReporttime().toString();
                        //获取今天天气信息
                        Casts today = weather.getForecasts().get(0).getCasts().get(0);
                        //添加Map数据到List
                        Map<String, String> map1 = new HashMap<>();
                        map1.put("date", today.getDate());
                        map1.put("updateTime","更新时间："+updateTime);
                        map1.put("day_weather", "天气："+today.getDayweather());
                        map1.put("day_temp", "温度："+today.getDaytemp() + "℃");
                        if (WeatherUtil.noWindDirection(today.getDaywind())) {
                            map1.put("day_wind", today.getDaywind());
                        } else {
                            map1.put("day_wind", "风向："+today.getDaywind() + "风");
                        }
                        map1.put("day_power", "风力："+today.getDaypower() + "级");
                        map1.put("night_weather", "天气："+today.getNightweather());
                        map1.put("night_temp", "温度："+today.getNighttemp() + "℃");
                        if (WeatherUtil.noWindDirection(today.getNightwind())) {
                            map1.put("night_wind", today.getNightwind());
                        } else {
                            map1.put("night_wind", "风向："+today.getNightwind() + "风");
                        }
                        map1.put("night_power", "风力："+today.getNightpower() + "级");
                        weatherList.add(map1);

                        //获取明天天气信息
                        Casts tomorrow = weather.getForecasts().get(0).getCasts().get(1);
                        //添加Map数据到List
                        Map<String, String> map2 = new HashMap<>();
                        map2.put("date", tomorrow.getDate());
                        map2.put("updateTime","更新时间："+updateTime);
                        map2.put("day_weather", "天气："+tomorrow.getDayweather());
                        map2.put("day_temp", "温度："+tomorrow.getDaytemp() + "℃");
                        if (WeatherUtil.noWindDirection(tomorrow.getDaywind())) {
                            map2.put("day_wind", tomorrow.getDaywind());
                        } else {
                            map2.put("day_wind", "风向："+tomorrow.getDaywind() + "风");
                        }
                        map2.put("day_power", "风力："+tomorrow.getDaypower() + "级");
                        map2.put("night_weather", "天气："+tomorrow.getNightweather());
                        map2.put("night_temp", "温度："+tomorrow.getNighttemp() + "℃");
                        if (WeatherUtil.noWindDirection(tomorrow.getNightwind())) {
                            map2.put("night_wind", tomorrow.getNightwind());
                        } else {
                            map2.put("night_wind", "风向："+tomorrow.getNightwind() + "风");
                        }
                        map2.put("night_power", "风力："+tomorrow.getNightpower() + "级");
                        weatherList.add(map2);

                        //获取后天天气信息
                        Casts afterTomorrow = weather.getForecasts().get(0).getCasts().get(2);
                        //添加Map数据到List
                        Map<String, String> map3 = new HashMap<>();
                        map3.put("date", afterTomorrow.getDate());
                        map3.put("updateTime","更新时间："+updateTime);
                        map3.put("day_weather", "天气："+afterTomorrow.getDayweather());
                        map3.put("day_temp", "温度："+afterTomorrow.getDaytemp() + "℃");
                        if (WeatherUtil.noWindDirection(afterTomorrow.getDaywind())) {
                            map3.put("day_wind", afterTomorrow.getDaywind());
                        } else {
                            map3.put("day_wind", "风向："+afterTomorrow.getDaywind() + "风");
                        }
                        map3.put("day_power", afterTomorrow.getDaypower() + "级");
                        map3.put("night_weather", "天气："+afterTomorrow.getNightweather());
                        map3.put("night_temp", "温度："+afterTomorrow.getNighttemp() + "℃");
                        if (WeatherUtil.noWindDirection(afterTomorrow.getNightwind())) {
                            map3.put("night_wind", afterTomorrow.getNightwind());
                        } else {
                            map3.put("night_wind", "风向："+afterTomorrow.getNightwind() + "风");
                        }
                        map3.put("night_power", "风力："+afterTomorrow.getNightpower() + "级");
                        weatherList.add(map3);

                        //获取大后天天气信息
                        Casts afterTomorrow2 = weather.getForecasts().get(0).getCasts().get(3);
                        //添加Map数据到List
                        Map<String, String> map4 = new HashMap<>();
                        map4.put("date", afterTomorrow2.getDate());
                        map4.put("updateTime","更新时间："+updateTime);
                        map4.put("day_weather", "天气："+afterTomorrow2.getDayweather());
                        map4.put("day_temp", "温度："+afterTomorrow2.getDaytemp() + "℃");
                        if (WeatherUtil.noWindDirection(afterTomorrow2.getDaywind())) {
                            map4.put("day_wind", afterTomorrow2.getDaywind());
                        } else {
                            map4.put("day_wind", "风向："+afterTomorrow2.getDaywind() + "风");
                        }
                        map4.put("day_power", "风力："+afterTomorrow2.getDaypower() + "级");
                        map4.put("night_weather", "天气："+afterTomorrow2.getNightweather());
                        map4.put("night_temp", "温度："+afterTomorrow2.getNighttemp() + "℃");
                        if (WeatherUtil.noWindDirection(afterTomorrow2.getNightwind())) {
                            map4.put("night_wind", afterTomorrow2.getNightwind());
                        } else {
                            map4.put("night_wind", "风向："+afterTomorrow2.getNightwind() + "风");
                        }
                        map4.put("night_power", "风力："+afterTomorrow2.getNightpower() + "级");
                        weatherList.add(map4);


                        //将服务器返回数据写入Handler
                        Message message = Message.obtain();
                        message.what = 3;
                        message.obj = weatherList;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.i("SearchWeather.java", "服务器异常:" + e.toString());

                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = e.toString();
                    e.printStackTrace();
                }
            }
        }).start();
    }

}