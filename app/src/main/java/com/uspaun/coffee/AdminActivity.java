package com.uspaun.coffee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdminActivity extends ActionBarActivity {

    Intent intent;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_PERCENT = "percent";
    public static final String APP_PREFERENCES_RATE = "rate";
    private static final String APP_PREFERENCES_ID = "id";
    private static final String APP_PREFERENCES_TOKEN= "token";
    private static final String APP_PREFERENCES_POSITION= "position";
    private SharedPreferences mSettings;
    EditText tePercent, teRate;
    Goods gdsAdmin;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        tePercent = (EditText)findViewById(R.id.etPercent);
        teRate = (EditText)findViewById(R.id.etRate);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_PERCENT)) {
            tePercent.setText(Float.toString(mSettings.getFloat(APP_PREFERENCES_PERCENT, 0)));
            teRate.setText(Float.toString(mSettings.getFloat(APP_PREFERENCES_RATE, 0)));
        }
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_ID, LoginActivity.worker.getID());
        editor.putString(APP_PREFERENCES_TOKEN, LoginActivity.worker.getToken());
        editor.putInt(APP_PREFERENCES_POSITION, LoginActivity.worker.getPosition());
        editor.apply();
        gdsAdmin = new Goods();
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
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


    public void changeSalary()
    {
        SharedPreferences.Editor editor = mSettings.edit();
        if(!tePercent.getText().toString().equals("") && !teRate.getText().toString().equals("") &&
                CoffeeUtils.checkFloat(tePercent.getText().toString()) && CoffeeUtils.checkFloat(teRate.getText().toString())) {
            float mPercent = Float.parseFloat(tePercent.getText().toString());
            float mRate = Float.parseFloat(teRate.getText().toString());
            editor.putFloat(APP_PREFERENCES_PERCENT, mPercent);
            editor.putFloat(APP_PREFERENCES_RATE, mRate);
            editor.apply();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Встановлено нову зарплатню!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Заповніть всі поля вірно!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.btnStatistics:
                intent = new Intent(AdminActivity.this, StatisticsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnTiles:
                intent = new Intent(AdminActivity.this, TilesActivity.class);
                startActivity(intent);
                break;
            case R.id.btnChange:
                changeSalary();
                break;
            case R.id.btnRemaining:
                intent = new Intent(AdminActivity.this, ReportActivity.class);
                intent.putExtra("params", "admRem");
                startActivity(intent);
                break;
            case R.id.btnAdd:
                intent = new Intent(AdminActivity.this, AddGoodsActivity.class);
                startActivity(intent);
                break;
            case R.id.btnDownload:
                downloadGoods();
                break;
            case R.id.btnUpload:
                sendGoods();
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String user_result = bundle.getString("userRes");
            if(user_result.equalsIgnoreCase("exited"))
            {
                intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            else {
                CoffeeUtils.showToast(getApplicationContext(), "Помилка!", Toast.LENGTH_SHORT);
            }
            progress.hide();
        }
    };

    private void user_logout(List<NameValuePair> urlParameters)
    {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        final String url = "http://uspaun.pp.ua/coffee/mobile/exit.php";
        try {
            String res = CoffeeUtils.httpPost(url, urlParameters);
            bundle.putString("userRes", res);
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lgout()
    {
        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("id", Integer.toString(LoginActivity.worker.ID)));
        urlParameters.add(new BasicNameValuePair("token", LoginActivity.worker.Token));
        progress.setMessage("Вихід...");
        progress.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    user_logout(urlParameters);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void logout(View v)
    {
        lgout();
    }

    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String jsonString = bundle.getString("jsonStr");
            //String res = "";
            try {
                JSONArray mJsonArray = new JSONArray(jsonString);
                JSONObject mJsonObject = new JSONObject();
                for (int i = 1; i < mJsonArray.length(); i++)
                {
                    mJsonObject = mJsonArray.getJSONObject(i);
                    int id = Integer.parseInt(mJsonObject.getString("id"));
                    boolean isEx = gdsAdmin.isExist(id);
                    if(!isEx)
                    {
                        gdsAdmin.addGoods(new Goods(id, mJsonObject.getString("name"),
                                mJsonObject.getString("barcode"), Integer.parseInt(mJsonObject.getString("count")),
                                Float.parseFloat(mJsonObject.getString("price")), Float.parseFloat(mJsonObject.getString("original_price"))));
                    }
                    else
                    {
                        gdsAdmin.updateGoodsById(new Goods(mJsonObject.getString("name"),
                                mJsonObject.getString("barcode"), Integer.parseInt(mJsonObject.getString("count")),
                                Float.parseFloat(mJsonObject.getString("price")),
                                Float.parseFloat(mJsonObject.getString("original_price"))), mJsonObject.getString("id"));
                    }
                }
                progress.hide();
                CoffeeUtils.showToast(getApplicationContext(), "База оновлена!", Toast.LENGTH_SHORT);
            } catch (Exception e) {
                CoffeeUtils.showToast(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
            }
        }
    };

    public void getAllGoods(List<NameValuePair> urlParameters)
    {
        Message msg = handler1.obtainMessage();
        Bundle bundle = new Bundle();
        final String url = "http://uspaun.pp.ua/coffee/mobile/operations.php";
        try {
            String res = CoffeeUtils.httpPost(url, urlParameters);
            bundle.putString("jsonStr", res);
            msg.setData(bundle);
            handler1.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadGoods()
    {
        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("operation", "allgoods"));
        urlParameters.add(new BasicNameValuePair("user", Integer.toString(LoginActivity.worker.ID)));
        urlParameters.add(new BasicNameValuePair("token", LoginActivity.worker.Token));
        urlParameters.add(new BasicNameValuePair("company", Integer.toString(LoginActivity.worker.Company)));
        urlParameters.add(new BasicNameValuePair("lastid", Integer.toString(gdsAdmin.getLastID())));
        progress.setMessage("Оновлення бази...");
        progress.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    getAllGoods(urlParameters);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                Bundle bundle = msg.getData();
                String user_result = bundle.getString("sendgRes");
                progress.hide();
                if(user_result.equalsIgnoreCase("iamget"))
                {
                    CoffeeUtils.showToast(getApplicationContext(), "Оновлення відправлено!", Toast.LENGTH_SHORT);
                    if(!gdsAdmin.changeNewGoodsChanged()) {
                        CoffeeUtils.showToast(getApplicationContext(), "Помилка!", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                else {
                    CoffeeUtils.showToast(getApplicationContext(), "Помилка!", Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                CoffeeUtils.showToast(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
            }
        }
    };

    public void newgoods_send(List<NameValuePair> urlParameters)
    {
        Message msg = handler2.obtainMessage();
        Bundle bundle = new Bundle();
        final String url = "http://uspaun.pp.ua/coffee/mobile/operations.php";
        try {
            String res = CoffeeUtils.httpPost(url, urlParameters);
            bundle.putString("sendgRes", res);
            msg.setData(bundle);
            handler2.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGoods()
    {
        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("user", Integer.toString(LoginActivity.worker.ID)));
        urlParameters.add(new BasicNameValuePair("token", LoginActivity.worker.Token));
        urlParameters.add(new BasicNameValuePair("operation", "sendnewgoods"));
        String jRes = gdsAdmin.prepareNewGoodsJson();
        if(jRes == null)
        {
            CoffeeUtils.showToast(getApplicationContext(), "Нові товари відсутні!", Toast.LENGTH_SHORT);
            return;
        }
        urlParameters.add(new BasicNameValuePair("jnew", jRes));
        progress.setMessage("Відсилання...");
        progress.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    newgoods_send(urlParameters);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        lgout();
        intent = new Intent(AdminActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
/*Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                Bundle bundle = msg.getData();
                String user_result = bundle.getString("sendgRes");
                if(user_result.equalsIgnoreCase("iamget"))
                {
                    progress.hide();
                    CoffeeUtils.showToast(getApplicationContext(), "Оновлення відправлено!", Toast.LENGTH_SHORT);
                }
                else {
                    CoffeeUtils.showToast(getApplicationContext(), "Помилка!", Toast.LENGTH_SHORT);
                    progress.hide();
                }
            } catch (Exception e) {
                CoffeeUtils.showToast(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
            }
        }
    };

    public void report_send(List<NameValuePair> urlParameters)
    {
        Message msg = handler2.obtainMessage();
        Bundle bundle = new Bundle();
        final String url = "http://uspaun.pp.ua/coffee/mobile/operations.php";
        try {
            String res = CoffeeUtils.httpPost(url, urlParameters);
            bundle.putString("rsendgRes", res);
            msg.setData(bundle);
            handler2.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGoods()
    {
        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("user", Integer.toString(LoginActivity.worker.ID)));
        urlParameters.add(new BasicNameValuePair("token", LoginActivity.worker.Token));
        urlParameters.add(new BasicNameValuePair("operation", "sendall"));
        urlParameters.add(new BasicNameValuePair("jgoods", gdsAdmin.prepareJson()));
        progress.setMessage("Відправка...");
        progress.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    report_send(urlParameters);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/