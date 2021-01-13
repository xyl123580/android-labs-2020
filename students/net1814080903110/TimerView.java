package edu.hzuapps.androidlabs.net;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class TimerView extends LinearLayout {

    public TimerView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public TimerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    private TextView tvHour;
    private TextView tvMinute;
    private TextView tvSecond;
    private Button btnStart;
    private Button btnStop;

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        init();

    }

    private void init() {
        tvHour = (TextView) this.findViewById(R.id.timer_hour);
        tvMinute = (TextView) this.findViewById(R.id.timer_mini);
        tvSecond = (TextView) this.findViewById(R.id.timer_second);
        btnStart = (Button) this.findViewById(R.id.btn_start_timer);
        btnStop = (Button) this.findViewById(R.id.btn_stop_timer);

        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
    }

    private final int WORK_STATE_STOP = 0; // 计时状态，停止
    private final int WORK_STATE_RUN = 1;// 计时状态
    private int userInputTime = 0;
    private Timer timer;
    private TimerTask task;

    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch(msg.what){
                case WORK_STATE_RUN:
                    int hour =  userInputTime/60/60;
                    int minute = (userInputTime/60)%60;
                    int second = userInputTime%60;
                    tvHour.setText(""+hour);
                    tvMinute.setText(""+minute);
                    tvSecond.setText(""+second);
                    break;
                case WORK_STATE_STOP:
                    new AlertDialog.Builder(getContext()).setTitle("时间到").setNegativeButton("取消", null).show();
                    stopTimer();
                    break;
            }
        };
    };

    private void startTimer() {

        try {
            userInputTime = (Integer.parseInt(tvHour.getText().toString()) * 60 * 60
                    + Integer.parseInt(tvMinute.getText().toString()) * 60
                    + Integer.parseInt(tvSecond.getText().toString()));
        } catch (Exception e) {
            Log.e("info", "TimerView->startTimer"+ e.getMessage());
            return;
        }


        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {

                userInputTime--;
                handler.sendEmptyMessage(WORK_STATE_RUN);
                if(userInputTime <=0){
                    handler.sendEmptyMessage(WORK_STATE_STOP);
                    stopTimer();
                }

            }
        };
        timer.schedule(task, 1000,1000); //延迟一秒，再每隔一秒执行一次timertask.run()
    }

    private void stopTimer() {
        if(task!=null){
            task.cancel();
            task = null;
        }
    }


}