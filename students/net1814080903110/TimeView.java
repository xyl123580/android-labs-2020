package edu.hzuapps.androidlabs.net;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class TimeView extends LinearLayout {

    private TextView timeText;

    public TimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public TimeView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();

        timeText = (TextView) findViewById(R.id.tv_time);

        timeHandler.sendEmptyMessage(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshTime() {

        Calendar cal = Calendar.getInstance();
        String timeStr = String.format("%d:%d:%d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
        timeText.setText(timeStr);

        //System.out.println("timeStr============>" + timeStr);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        // TODO Auto-generated method stub
        super.onVisibilityChanged(changedView, visibility);

        if (getVisibility() == View.VISIBLE) {
            timeHandler.sendEmptyMessageDelayed(0, 1000);
        } else {
            timeHandler.removeMessages(0);
        }
    }

    private Handler timeHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void handleMessage(android.os.Message msg) {
            refreshTime();

            if (getVisibility() == View.VISIBLE) {
                // 每隔一秒给自己发送消息
                timeHandler.sendEmptyMessageDelayed(0, 1000);
            }
        };
    };

}
