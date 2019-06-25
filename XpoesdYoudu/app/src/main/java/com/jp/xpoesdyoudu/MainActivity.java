package com.jp.xpoesdyoudu;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Switch swOpen;
    Button btnSelectTime;
    TextView tvTime;
    int mYear, mMonth, mDay, hourOfDay, minute, second;
    Calendar calendar;
    boolean isOpen;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swOpen = findViewById(R.id.sw_open_youdu);
        btnSelectTime = findViewById(R.id.btn_select_time);
        tvTime = findViewById(R.id.btn_current_time);
        swOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferenceUtils.setParam(MainActivity.this, "is_open", b);
                SharedPreferenceUtils.setParam(MainActivity.this, "time", DateUtils.dateToStamp2(tvTime.getText().toString()));
            }
        });
        findViewById(R.id.btn_cur_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = System.currentTimeMillis();
                SharedPreferenceUtils.setParam(MainActivity.this, "time", time);
                calendar.setTimeInMillis(time);
                refreshTime(calendar);
            }
        });
        isOpen = (boolean)SharedPreferenceUtils.getParam(this, "is_open", Boolean.FALSE);
        time = (long)SharedPreferenceUtils.getParam(this, "time", System.currentTimeMillis());
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        refreshTime(calendar);
        swOpen.setChecked(isOpen);
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });
    }

    private void showTimeDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hourOfDay = i;
                minute = i1;
                tvTime.setText(mYear + "-" + mMonth + "-" + mDay + " " + getDoubleBit(hourOfDay) + ":" + getDoubleBit(minute) + ":00");
                SharedPreferenceUtils.setParam(MainActivity.this, "time", DateUtils.dateToStamp2(tvTime.getText().toString()));
            }
        }, hourOfDay, minute, true);
        timePickerDialog.show();
    }

    private void refreshTime(Calendar calendar){
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        tvTime.setText(mYear + "-" + mMonth + "-" + mDay + " " + getDoubleBit(hourOfDay) + ":" + getDoubleBit(minute) + ":00");
    }

    String getDoubleBit(int bit) {
        if (bit < 10) {
            return "0" + bit;
        }
        return bit + "";
    }

}
