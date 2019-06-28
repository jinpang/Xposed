package com.jp.xposedtest;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Switch swOpen;
    private EditText etInput;
    private TextView tvResult;
    int index;
    String title, item;
    ClipboardManager myClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        button = (Button) findViewById(R.id.button);
        etInput = findViewById(R.id.ed_index);
        tvResult = findViewById(R.id.content);
        swOpen = findViewById(R.id.sw_open);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, toastMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        title = (String) SharedPreferenceUtils.getParam(this, "title", "未获得结果");
        item = (String) SharedPreferenceUtils.getParam(this, "item", "未获得结果");
        swOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tvResult.setText(getSwitchStr());
                SharedPreferenceUtils.setParam(MainActivity.this, "is_open", b);
            }
        });
        boolean isOpen = (boolean) SharedPreferenceUtils.getParam(this, "is_open", Boolean.TRUE);
        swOpen.setChecked(isOpen);
        index = (int) SharedPreferenceUtils.getParam(this, "index", 1);
        etInput.setText(index + "");
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null && editable.toString().length() > 0) {
                    index = Integer.valueOf(editable.toString());
                    tvResult.setText(getSwitchStr());
                    SharedPreferenceUtils.setParam(MainActivity.this, "index", index);
                }
            }
        });
        tvResult.setText(getSwitchStr());
        tvResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData myClip;
                myClip = ClipData.newPlainText("text", getSwitchStr());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(view.getContext(), "已复制", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getSwitchStr(){
        return "isOpen:" + swOpen.isChecked() + ", index:" + index + "\n" + title + "\n" + item;
    }

    public String toastMessage() {
        return "我未被劫持";
    }
}
