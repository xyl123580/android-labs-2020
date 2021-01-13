package edu.hzuapps.androidlabs.net;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class Net1814080903110Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText scheduleInput01;
    private Context context;
    private Button addSchedule01,checkAdd01;
    private String dateToday;//用于记录今天的日期
    private TestCourseData mySQLiteOpenHelper;
    private String schedule;
    private SQLiteDatabase myDatabase;
    private CalendarView calendarView;
    private TextView mySchedule[] = new TextView[5];


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net1814080903110);

        initView();

        //这里不这样的话一进去就设置当天的日程会报错
        Calendar time = Calendar.getInstance();
        int year = time.get(Calendar.YEAR);
        int month = time.get(Calendar.MONTH)+1;//注意要+1，0表示1月份
        int day = time.get(Calendar.DAY_OF_MONTH);
        dateToday = year+"-"+month+"-"+day;
        //还要直接查询当天的日程，这个要放在initView的后面，不然会出问题
        queryByDate(dateToday);

    }
    private void initView() {
        mySQLiteOpenHelper = new TestCourseData(this);
        myDatabase = mySQLiteOpenHelper.getWritableDatabase();

        context = this;
        addSchedule01 = findViewById(R.id.addSchedule01);
        addSchedule01.setOnClickListener(this);
        checkAdd01 = findViewById(R.id.checkAdd01);
        checkAdd01.setOnClickListener(this);

        calendarView = findViewById(R.id.calendar01);
        scheduleInput01 = findViewById(R.id.scheduleDetailInput01);

        calendarView.setOnDateChangeListener(mySelectDate);

        mySchedule[0] = findViewById(R.id.schedule01);
        mySchedule[1] = findViewById(R.id.schedule02);
        mySchedule[2] = findViewById(R.id.schedule03);
        mySchedule[3] = findViewById(R.id.schedule04);
        mySchedule[4] = findViewById(R.id.schedule05);
        for(TextView v:mySchedule){
            v.setOnClickListener(this);
        }
    }
    private CalendarView.OnDateChangeListener mySelectDate = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            dateToday = year+"-"+(month+1)+"-"+dayOfMonth;
            Toast.makeText(context, "你选择了:"+dateToday, Toast.LENGTH_SHORT).show();

            //得把用别的日期查出来的日程删除并将其隐藏
            for(TextView v:mySchedule){
                v.setText("");
                v.setVisibility(View.GONE);
            }
            queryByDate(dateToday);
        }
    };
    //根据日期查询日程
    private void queryByDate(String date) {
        //columns为null 查询所有列


        Cursor cursor = myDatabase.query("schedules", null, "time=?", new String[]{date}, null, null, null);
        if (cursor.moveToFirst()) {
            int scheduleCount = 0;
            do {
                String aScheduleDetail = cursor.getString(cursor.getColumnIndex("scheduleDetail"));
                mySchedule[scheduleCount].setText("日程" + (scheduleCount + 1) + "：" + aScheduleDetail);
                mySchedule[scheduleCount].setVisibility(View.VISIBLE);
                scheduleCount++;
                //一定要有这句 不然TextView不够多要数组溢出了
                if (scheduleCount >= 5)
                    break;
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addSchedule01:
                addMySchedule();
                break;
            case R.id.checkAdd01:
                checkAddSchedule();
                break;
            case R.id.schedule01:case R.id.schedule02:case R.id.schedule03:case R.id.schedule04:case R.id.schedule05:
                editSchedule(v);
                break;
            case R.id.settingSchedule:
                remindSchedule(v);
                break;

        }
    }

    private void editSchedule(View v) {
        Intent intent = new Intent(Net1814080903110Activity.this, SettingActivity.class);
        String sch = ((TextView) v).getText().toString().split("：")[1];
        intent.putExtra("schedule",sch);
        startActivity(intent);
    }
    private void remindSchedule(View v) {
        Intent intent = new Intent(Net1814080903110Activity.this, RemindActivity.class);
        startActivity(intent);
    }

    private void checkAddSchedule() {
        ContentValues values = new ContentValues();
        //第一个参数是表中的列名
        values.put("scheduleDetail",scheduleInput01.getText().toString());
        values.put("time",dateToday);
        myDatabase.insert("schedules",null,values);
        scheduleInput01.setVisibility(View.GONE);
        checkAdd01.setVisibility(View.GONE);
        queryByDate(dateToday);
        //添加完以后把scheduleInput中的内容清除
        scheduleInput01.setText("");
    }

    private void addMySchedule() {
        scheduleInput01.setVisibility(View.VISIBLE);
        checkAdd01.setVisibility(View.VISIBLE);
    }
}




