package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calculator.R;

public class CalculatorActivity extends AppCompatActivity {

    //创建Button对象
    //数字按钮
    Button button_NUM0,button_NUM1,button_NUM2,button_NUM3,button_NUM4,button_NUM5,button_NUM6,button_NUM7,button_NUM8,button_NUM9;
    //运算符号按钮
    Button button_ADD,button_SUB,button_MUL,button_DIV,button_PN;
    //清屏、删除、等于、小数点和百分号按钮
    Button button_AC,button_DEL,button_EQ,button_DecimalPoint,button_jc;

    //可编辑文本框
    TextView Show;
    boolean show_empty;//判断编辑文本框Show是否为空

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_layout);
        //实例化对象
        /*数字键对象：0~9*/
        button_NUM0=(Button) findViewById(R.id.button_NUM0);
        button_NUM1=(Button) findViewById(R.id.button_NUM1);
        button_NUM2=(Button) findViewById(R.id.button_NUM2);
        button_NUM3=(Button) findViewById(R.id.button_NUM3);
        button_NUM4=(Button) findViewById(R.id.button_NUM4);
        button_NUM5=(Button) findViewById(R.id.button_NUM5);
        button_NUM6=(Button) findViewById(R.id.button_NUM6);
        button_NUM7=(Button) findViewById(R.id.button_NUM7);
        button_NUM8=(Button) findViewById(R.id.button_NUM8);
        button_NUM9=(Button) findViewById(R.id.button_NUM9);
        /*运算符对象：加、减、乘、除和正负变换*/
        button_ADD=(Button) findViewById(R.id.button_ADD);
        button_SUB=(Button) findViewById(R.id.button_SUB);
        button_MUL=(Button) findViewById(R.id.button_MUL);
        button_DIV=(Button) findViewById(R.id.button_DIV);
        button_PN=(Button) findViewById(R.id.button_PN);
        /*功能键：清屏、删除、等于、小数点和百分号*/
        button_AC=(Button) findViewById(R.id.button_AC);
        button_DEL=(Button) findViewById(R.id.button_DEL);
        button_EQ=(Button) findViewById(R.id.button_EQ);
        button_DecimalPoint=(Button) findViewById(R.id.button_DecimalPoint);
        button_jc=(Button) findViewById(R.id.button_jc);
        //可编辑文本框
        Show=(TextView) findViewById(R.id.Show);

        //给按钮设置的点击事件
        //数字键
        button_NUM0.setOnClickListener(listener);
        button_NUM1.setOnClickListener(listener);
        button_NUM2.setOnClickListener(listener);
        button_NUM3.setOnClickListener(listener);
        button_NUM4.setOnClickListener(listener);
        button_NUM5.setOnClickListener(listener);
        button_NUM6.setOnClickListener(listener);
        button_NUM7.setOnClickListener(listener);
        button_NUM8.setOnClickListener(listener);
        button_NUM9.setOnClickListener(listener);
        //运算符号
        button_ADD.setOnClickListener(listener);
        button_SUB.setOnClickListener(listener);
        button_MUL.setOnClickListener(listener);
        button_DIV.setOnClickListener(listener);
        button_PN.setOnClickListener(listener);
        //功能键
        button_AC.setOnClickListener(listener);
        button_DEL.setOnClickListener(listener);
        button_EQ.setOnClickListener(listener);
        button_DecimalPoint.setOnClickListener(listener);
        button_jc.setOnClickListener(listener);
    }

    View.OnClickListener listener= new View.OnClickListener(){
      public void onClick(View v){
          String text=Show.getText().toString();//获取显示屏幕中的内容，并转换为字符串
          //判断点击的按钮，并显示相应的数字、符号或者执行对应的操作
          switch (v.getId()){
              case R.id.button_NUM0:
              case R.id.button_NUM1:
              case R.id.button_NUM2:
              case R.id.button_NUM3:
              case R.id.button_NUM4:
              case R.id.button_NUM5:
              case R.id.button_NUM6:
              case R.id.button_NUM7:
              case R.id.button_NUM8:
              case R.id.button_NUM9:
                  //初始化显示屏幕
                  if(show_empty){
                      show_empty=false;
                      text="";
                      Show.setText("");
                  }
                  Show.setText(text+((Button)v).getText());
                  break;
              case R.id.button_DecimalPoint:
                  if(show_empty){
                      show_empty=false;
                      text="";
                      Show.setText("");
                  }
                  XSD();//调用输入小数点函数
                  break;
              case R.id.button_ADD:
              case R.id.button_SUB:
              case R.id.button_MUL:
              case R.id.button_DIV:
                  if(show_empty){
                      show_empty=false;
                      text="";
                      Show.setText("");
                  }
                  String add=" + ";
                  String sub=" - ";
                  String mul=" × ";
                  String div=" ÷ ";
                  //禁止输入多个运算符号
                  if(text.contains(add)||text.contains(sub)||text.contains(mul)||text.contains(div)) {
                      text=text.substring(0,text.indexOf(" "));
                  }
                  Show.setText(text+" "+((Button)v).getText()+" ");
                  break;
              case R.id.button_AC:
                  if(show_empty)
                      show_empty=false;
                  text="";
                  Show.setText("");
                  break;
              case R.id.button_DEL:
                  if(show_empty){
                      show_empty=false;
                      text="";
                      Show.setText("");
                  }
                  Delete();//调用删除函数
                  break;
              case R.id.button_EQ:
                  Answer();//计算结果
                  break;
              case R.id.button_PN:
                  if(show_empty){
                      show_empty=false;
                      text="";
                      Show.setText("");
                  }
                  ZFBH();//调用正负数转换函数
                  break;
              case R.id.button_jc:
                  if(show_empty){
                      show_empty=false;
                      text="";
                      Show.setText("");
                  }
                  JC();//调用阶乘计算函数
                  break;
          }
      }
    };

    //字符串比较函数
    public int CompareString(String s1,String s2){
        if(s1.length()>s2.length()){
            return 1;
        }
        else if(s1.length()==s2.length()){
            return s1.compareTo(s2);
        }
        else{
            return -1;
        }
    }

    //删除函数
    public void Delete(){
        String exp=Show.getText().toString();//获取屏幕上目前的字符串
        if(exp.equals("")){//判断获取内容是否为空
            Show.setText("");
        }
        else if(!exp.contains(" ")){//用于判断是否只有一个操作数
            Show.setText(exp.substring(0,exp.length()-1));
        }
        else{
            String s1,s3;
            s1=exp.substring(0,exp.indexOf(" "));//第一个操作数
            s3=exp.substring(exp.indexOf(" ")+3);//第二个操作数
            //判断第二个操作数是否为空，若为空删除操作删除运算符
            if(!s3.equals("")){
                Show.setText(exp.substring(0,exp.length()-1));
            }
            if(s3.equals("")){
                Show.setText(s1);
            }
        }
    }

    //小数点输入函数
    public void XSD(){
        String exp=Show.getText().toString();
        //显示屏为空时只输入小数点
        if(exp.equals("")){
            Show.setText(".");
        }
        else if(!exp.contains(" ")){//只有一个操作数
            if(!exp.contains(".")){//若该操作数未包含小数点，添加小数点
                Show.setText(exp+".");
            }
            if(exp.contains(".")){//若该操作数包含小数点，则重置小数点后面的部分
                Show.setText(exp.substring(0,exp.indexOf(".")+1));
            }
        }
        else{//第二个操作数，添加小数点的操作同上
            String s=exp.substring(exp.indexOf(" ")+3);
            if(!s.contains(".")){
                Show.setText(exp+".");
            }
            if(s.contains(".")){
                String exp1=exp.substring(0,exp.indexOf(" ")+3);
                String num=s.substring(0,exp.indexOf(".")+1);
                Show.setText(exp1+num);
            }
        }
    }

    //阶乘函数
    public void JC(){
        String exp=Show.getText().toString();
        String IntRange="2147483647";//用于判断输入的整数是否超过整数范围
        double answer=1;
        if(exp.equals("")){
            Show.setText("");
        }
        else if(!exp.contains(" ")){
            if(exp.contains(".")||exp.contains("-")){//小数和负数不支持阶乘计算
                Toast ts = Toast.makeText(getBaseContext(),"小数和负数没有阶乘，请删除小数点或负号后重试", Toast.LENGTH_SHORT);
                ts.show();
                Show.setText(exp);
            }
            else{
                if(CompareString(exp,IntRange)>0){
                    Toast ts = Toast.makeText(getBaseContext(),"操作数超出整数取值范围", Toast.LENGTH_SHORT);
                    ts.show();
                    Show.setText(exp);
                }
                else{
                    /**/
                    int num=Integer.parseInt(exp);
                    while(num!=0){
                        answer=answer*num;
                        num--;
                    }
                    if(answer>2147483647){//当阶乘的计算结果超过整数的取值范围，报错
                        Toast ts = Toast.makeText(getBaseContext(),"结果超出整数取值范围", Toast.LENGTH_SHORT);
                        ts.show();
                        Show.setText(exp);
                    }
                    else{
                        Show.setText(answer+"");
                    }
                }
            }
        }
        else{
            String s=exp.substring(exp.indexOf(" ")+3);
            if(s.contains(".")||s.contains("-")){
                Toast ts = Toast.makeText(getBaseContext(),"小数和负数没有阶乘，请删除小数点或负号后重试", Toast.LENGTH_SHORT);
                ts.show();
                Show.setText(exp);
            }
            else if(s.equals("")){
                Show.setText(exp+answer+"");
            }
            else{
                int num=Integer.parseInt(s);
                if(CompareString(s,IntRange)>0){
                    Toast ts = Toast.makeText(getBaseContext(),"操作数超出整数取值范围", Toast.LENGTH_SHORT);
                    ts.show();
                    Show.setText(exp);
                }
                else{
                    while(num!=0){
                        answer=answer*num;
                        num--;
                    }
                    if(answer>2147483647){
                        Toast ts = Toast.makeText(getBaseContext(),"结果超出整数取值范围", Toast.LENGTH_SHORT);
                        ts.show();
                        Show.setText(exp);
                    }
                    else{
                        Show.setText(exp.substring(0,exp.indexOf(" ")+3)+answer+"");
                    }
                }
            }
        }
    }

    //正负变换函数
    public void ZFBH(){
        String exp=Show.getText().toString();
        double answer=0;
        if(exp.equals("")){
            Show.setText("");
        }
        else if(exp.equals(".")){//小数点不支持正负变换
            Show.setText(exp);
        }
        else if(!exp.contains(" ")){//只有一个操作数
            answer=0-Double.parseDouble(exp);
            Show.setText(answer+"");
        }
        else{
            String s=exp.substring(exp.indexOf(" ")+3);
            if(s==null||s.equals("")){
                Show.setText(exp);
            }
            else{
                answer=0-Double.parseDouble(s);
                Show.setText(exp.substring(0,exp.indexOf(" ")+3)+answer+"");
            }
        }
    }

    //表达式计算函数
    public void Answer(){
        String exp=Show.getText().toString();
        double num1=0,num2=0;//操作数1和2
        double answer=0;
        String s1="",s2="",s3="";//s1、s3为操作数1的字符串，s2为运算符
        if(exp.equals("")){
            Show.setText("");
        }
        else if(exp.equals(".")){
            Show.setText(exp);
        }
        else if(!exp.contains(" ")){
            answer=Double.parseDouble(exp);
            Show.setText(answer+"");
        }
        else{
            s1=exp.substring(0,exp.indexOf(" "));;
            s2=exp.substring(exp.indexOf(" ")+1,exp.indexOf(" ")+2);
            s3=exp.substring(exp.indexOf(" ")+3);
            //将第一个操作数转换
            if(s1.equals("")){
                num1=0;
            }
            else if((s1.charAt(0)+"").equals("-")){
                num1=0-Double.parseDouble(s1.substring(1));
            }
            else{
                num1=Double.parseDouble(s1);
            }
            //将第二个操作数转换
            if(s3.equals("")){
                num2=0;
            }
            else if((s3.charAt(0)+"").equals("-")){
                num2=0-Double.parseDouble(s3.substring(1));
            }
            else{
                num2=Double.parseDouble(s3);
            }

            //根据运算符运算
            if(s2.equals("+")){
                answer=num1+num2;
                Show.setText(answer+"");
            }
            else if(s2.equals("-")){
                answer=num1-num2;
                Show.setText(answer+"");
            }
            else if(s2.equals("×")){
                answer=num1*num2;
                Show.setText(answer+"");
            }
            else if(s2.equals("÷")){
                if(num2==0){
                    Toast ts = Toast.makeText(getBaseContext(),"除数不能为0！", Toast.LENGTH_SHORT);
                    ts.show();
                    Show.setText(exp);
                }
                else{
                    answer=num1/num2;
                    Show.setText(answer+"");
                }
            }
        }
    }
}