package cn.com.igisvity.sqliteorm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cn.com.igisvity.sqliteormlib.DBHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper.initDBHelper("test.db");
    }
}
