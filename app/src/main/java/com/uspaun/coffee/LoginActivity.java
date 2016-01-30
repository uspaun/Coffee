package com.uspaun.coffee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static Worker worker = new Worker();
    EditText etLogin, etPassword;
    //TextView tvSmsng;
    public static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_ID = "id";
    private static final String APP_PREFERENCES_TOKEN= "token";
    private SharedPreferences mSettings;
    private ProgressDialog progress;
    private ReportActivity reportActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        etLogin = (EditText)findViewById(R.id.etLogin);
        etPassword = (EditText)findViewById(R.id.etPassword);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_ID, -1);
        editor.putString(APP_PREFERENCES_TOKEN, "");
        editor.apply();
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        reportActivity = new ReportActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Intent intent;
            if(bundle.containsKey("Error"))
            {
                if(bundle.getString("Error").equalsIgnoreCase("login"))
                    CoffeeUtils.showToast(getApplicationContext(), "Помилка авторизації!", Toast.LENGTH_SHORT);
            }
            else {
                if (bundle.containsKey("userID")) {
                    worker.ID = bundle.getInt("userID");
                    worker.Token = bundle.getString("userToken");
                    worker.setPosition(bundle.getInt("userRank"));
                    worker.Company = (bundle.getInt("userCompany"));
                    if(worker.getPosition() == WorkerPositions.Manager)
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    else {
                        intent = new Intent(LoginActivity.this, ReportActivity.class);
                        intent.putExtra("params", "sellerAll");
                        intent.putExtra("logined", true);
                    }
                    startActivity(intent);
                } else
                    CoffeeUtils.showToast(getApplicationContext(), "Помилка!", Toast.LENGTH_SHORT);
            }
            progress.hide();
        }
    };

    public void init_login(List<NameValuePair> urlParameters)
    {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        final String url = "http://uspaun.pp.ua/coffee/mobile/login.php";
        try {
            String res = CoffeeUtils.httpPost(url, urlParameters);
            if(!res.equalsIgnoreCase("error")) {
                String[] arrayResult = res.split("\\&");
                bundle.putInt("userID", Integer.parseInt(arrayResult[0]));
                bundle.putString("userToken", arrayResult[1]);
                bundle.putInt("userRank", Integer.parseInt(arrayResult[2]));
                bundle.putInt("userCompany", Integer.parseInt(arrayResult[3]));
            }
            else
                bundle.putString("Error", "login");
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnLoginClick(View v)
    {
        if(etLogin.getText().toString() != "" && etPassword.getText().toString() != "") {
            worker.Login = etLogin.getText().toString();
            final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("login", etLogin.getText().toString()));
            urlParameters.add(new BasicNameValuePair("password", etPassword.getText().toString()));
            progress.setMessage("Вхід...");
            progress.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        init_login(urlParameters);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else
            CoffeeUtils.showToast(getApplicationContext(), "Введіть логін та пароль!", Toast.LENGTH_SHORT);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
