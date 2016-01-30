package com.uspaun.coffee;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class downlActivity extends AppCompatActivity {

    /*ListView lvTest;
    SimpleAdapter simpleAdapter;
    ArrayList<Map<String, Object>> data;
    Map<String, Object> m;
    final String ATTRIBUTE_NAME_NAME = "name";
    final String ATTRIBUTE_NAME_COUNT = "count";
    final String ATTRIBUTE_NAME_BARCODE = "barcode";
    final String ATTRIBUTE_NAME_ID = "id";
    final String ATTRIBUTE_NAME_PRICE = "price";
    final String ATTRIBUTE_NAME_ORIGPRICE = "original_price";
    final String ATTRIBUTE_NAME_USER = "user";
    String[] from;
    int[] to;*/
    TextView tvAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downl);
        tvAll = (TextView)findViewById(R.id.textView23);
            viewGoods();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_downl, menu);
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
            String jsonString = bundle.getString("jsonStr");
            String res = "";
            try {
                JSONArray mJsonArray = new JSONArray(jsonString);
                JSONObject mJsonObject = new JSONObject();
                for (int i = 1; i < mJsonArray.length(); i++) {
                    mJsonObject = mJsonArray.getJSONObject(i);
                    res += mJsonObject.getString("id") + "|";
                    res += mJsonObject.getString("name") + "|";
                    res += mJsonObject.getString("barcode") + "|";
                    res += mJsonObject.getString("count") + "|";
                    res += mJsonObject.getString("price") + "|";
                    res += mJsonObject.getString("original_price") + "|";
                    res += mJsonObject.getString("user") + "|";
                    res += mJsonObject.getString("changed") + " | NEXT | ";
                }
                tvAll.setText(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void getAllGoods(List<NameValuePair> urlParameters)
    {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        final String url = "http://uspaun.pp.ua/coffee/mobile/operations.php";
        try {
            String res = CoffeeUtils.httpPost(url, urlParameters);
            bundle.putString("jsonStr", res);
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewGoods()
    {
        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("operation", "allgoods"));
        urlParameters.add(new BasicNameValuePair("user", Integer.toString(LoginActivity.worker.ID)));
        urlParameters.add(new BasicNameValuePair("token", LoginActivity.worker.Token));
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
}
