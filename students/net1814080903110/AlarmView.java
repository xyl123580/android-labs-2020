package edu.hzuapps.androidlabs.net;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;

public class AlarmView extends LinearLayout {

    public AlarmView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public AlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public AlarmView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    private ListView alarmListView;
    private Button addAlarmButton;
    private ArrayAdapter<Alarm> adapter;
    private final static String KEY_ALARM_LIST = "clarm_list";
    private AlarmManager alarmManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        alarmListView = (ListView) this.findViewById(R.id.lv_alarm);
        addAlarmButton = (Button) this.findViewById(R.id.btn_add_alarm);

        addAlarmButton.setOnClickListener(new AddAlarmOnClickListener());

        adapter = new ArrayAdapter<AlarmView.Alarm>(getContext(), android.R.layout.simple_list_item_1);
        alarmListView.setAdapter(adapter);
        alarmListView.setOnItemLongClickListener(new ClarmOnItemLongClickListener());

        readSaveCalarm();

        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    private final class ClarmOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            //System.out.println("onItemLongClick=================>");

            new AlertDialog.Builder(getContext()).setTitle("确认删除？").setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("onItemLongClick->onClick:which:" + which);

                            removeAlarm(position);

                        }

                    }).show();

            return true;
        }

    }

    private final class AddAlarmOnClickListener implements View.OnClickListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            // Toast.makeText(getContext(), "add",Toast.LENGTH_SHORT).show();
            addAlarm();
        }
    }

    private void removeAlarm(int index) {
        Alarm alarm = adapter.getItem(index);
        adapter.remove(alarm);
        saveAlarm();
        alarmManager.cancel(PendingIntent.getBroadcast(getContext(), alarm.getId(), new Intent(getContext(),AlarmReceiver.class), 0));
    }

    /**
     * 保存设置的闹钟。覆盖式的，全部重新写入，清空原有的。
     */
    private void saveAlarm() {
        SharedPreferences.Editor edit = getContext().getSharedPreferences(AlarmView.class.getName(), Context.MODE_PRIVATE).edit();
        StringBuffer alarmListStr = new StringBuffer();

        for (int i = 0; i < adapter.getCount(); i++) {
            alarmListStr.append(adapter.getItem(i).getTime()).append(",");
        }

        edit.putString(KEY_ALARM_LIST, alarmListStr.toString());
        edit.commit();
    }

    /**
     * 读取保存的闹钟
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void readSaveCalarm() {
        SharedPreferences share = getContext().getSharedPreferences(AlarmView.class.getName(), Context.MODE_PRIVATE);
        String alarmListStr = share.getString(KEY_ALARM_LIST, "");
        if (alarmListStr != null) {
            String[] alarmArr = alarmListStr.split(",");
            if (alarmArr != null) {
                for (String str : alarmArr) {
                    System.out.println("alarm:" + str);
                    if (str != null && !"".equals(str.trim())) {
                        long time = Long.valueOf(str);
                        adapter.add(new Alarm(time));
                    }
                }
            }
        }

    }

    /**
     * 添加闹钟
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addAlarm() {
        Calendar curCal = Calendar.getInstance();

        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar alarmCal = Calendar.getInstance();
                alarmCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                alarmCal.set(Calendar.MINUTE, minute);
                alarmCal.set(Calendar.SECOND, 0);

                // 如果设置时间小于当前时间，则往后推一天。
                Calendar currentTime = Calendar.getInstance();
                if (alarmCal.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    alarmCal.setTimeInMillis(alarmCal.getTimeInMillis() + 24 * 60 * 60 * 1000);
                }

                // System.out.println(String.format("hourOfDay:%d,minute:%d",
                // hourOfDay,minute));

                // 添加到adapter，显示到列表
                Alarm alarm = new Alarm(alarmCal.getTimeInMillis());
                adapter.add(alarm);

                //每隔10分钟
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTime(), 10 * 60 * 1000, PendingIntent
                        .getBroadcast(getContext(), alarm.getId(), new Intent(getContext(), AlarmReceiver.class), 0));

                // 保存到share中，下次启动再读取
                saveAlarm();
            }
        }, curCal.get(Calendar.HOUR_OF_DAY), curCal.get(Calendar.MINUTE), true).show();
    }

    private class Alarm {
        private long time = 0;
        private String timeLabel = "";
        private Calendar date;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public Alarm(Long time) {
            this.time = time;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);

            timeLabel = String.format("%d-%d %d:%d", cal.get(Calendar.MONTH + 1), cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        }

        public long getTime() {
            return time;
        }

        public String getTimeLabel() {
            return timeLabel;
        }

        public Calendar getDate() {
            return date;
        }

        public int getId() {
            return (int) (getTime() / 1000 / 60);
        }

        @Override
        public String toString() {
            return getTimeLabel();
        }

    }

}
