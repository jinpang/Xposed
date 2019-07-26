package com.jp.xposedtest;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Environment;
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

import com.jp.xposedtest.utils.JSoupUtil;
import com.jp.xposedtest.utils.PropertyUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.jp.xposedtest.utils.JSoupUtil.readFile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private Switch swOpen;
    private EditText etInput;
    private EditText etStorage;
    private TextView tvResult, tvSystemPath;
    String index;
    String title, item, path;
    ClipboardManager myClipboard;
    String systemPath;
    String fileName = "123.txt";
    public final static String CONFIG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/auc/config.properties";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        button = (Button) findViewById(R.id.button);
        findViewById(R.id.btn_write123).setOnClickListener(this);
        findViewById(R.id.btn_refresh_file).setOnClickListener(this);
        findViewById(R.id.btn_refresh_and_copy).setOnClickListener(this);
        etInput = findViewById(R.id.ed_index);
        etStorage = findViewById(R.id.ed_storage);
        tvResult = findViewById(R.id.content);
        swOpen = findViewById(R.id.sw_open);
        tvSystemPath = findViewById(R.id.tv_system_path);
        systemPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        tvSystemPath.setText("建议保存在外部存储路径下，点击复制路径：\n" + systemPath);
        tvSystemPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData myClip;
                myClip = ClipData.newPlainText("text", systemPath);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(view.getContext(), "已复制:" + systemPath, Toast.LENGTH_LONG).show();
            }
        });
        path = PropertyUtil.readValue(CONFIG_PATH, "path", "/storage/emulated/legacy");
        //path = (String) SharedPreferenceUtils.getParam(this, "path", "/storage/emulated/legacy");
        etStorage.setText(path);
        etStorage.setHint("默认路径：/storage/emulated/legacy");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, toastMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        title = PropertyUtil.readValue(CONFIG_PATH, "title", "未获得结果");
        item = PropertyUtil.readValue(CONFIG_PATH, "item", "未获得结果");
        swOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tvResult.setText(getSwitchStr());
                PropertyUtil.writeProperties(CONFIG_PATH, "is_open", b + "");
            }
        });
        boolean isOpen = Boolean.valueOf(PropertyUtil.readValue(CONFIG_PATH, "is_open", "true"));
        swOpen.setChecked(isOpen);
        index = (String) PropertyUtil.readValue(CONFIG_PATH, "index", "1");
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
                    index = editable.toString();
                    tvResult.setText(getSwitchStr());
                    PropertyUtil.writeProperties(CONFIG_PATH, "index", editable.toString());
                }
            }
        });
        etStorage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString() != null && editable.toString().length() > 0) {
                    PropertyUtil.writeProperties(CONFIG_PATH, "path", editable.toString());
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
        tvResult.setText(readFile(getFilePath()));
    }

    private String getSwitchStr() {
        return "isOpen:" + swOpen.isChecked() + /*", index:" + index + */"\n" + title + "\n" + item;
    }

    public String toastMessage() {
        return swOpen.isChecked() ? "UC已被劫持" : "UC未被劫持";
    }

    public String getFilePath(){
        String filePath = etStorage.getText().toString();
        String strFilePath = fileName;
        if (filePath.endsWith("/")) {
            strFilePath = filePath + fileName;
        } else {
            strFilePath = filePath + "/" + fileName;
        }
        return strFilePath;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_write123) {
            boolean success = JSoupUtil.saveAsFileWriter("123", getFilePath());
            String str = success ? "成功" : "失败";
            Toast.makeText(this, str +"写入‘123’到123.txt文件中", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btn_refresh_file) {
            tvResult.setText(readFile(getFilePath()));
            Toast.makeText(this, "成功刷新123.txt文件", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btn_refresh_and_copy) {
            tvResult.setText(readFile(getFilePath()));
            ClipData myClip;
            myClip = ClipData.newPlainText("text", tvResult.getText());
            myClipboard.setPrimaryClip(myClip);
            Toast.makeText(this, "刷新并复制成功", Toast.LENGTH_SHORT).show();
        }
    }
}
