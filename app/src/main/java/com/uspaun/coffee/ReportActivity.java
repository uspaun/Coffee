package com.uspaun.coffee;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportActivity extends ActionBarActivity {

    Goods gdsReport = new Goods();
    Seller slrMain = new Seller();
    TextView tvAmount, tvSalary, tvLabel;
    ListView lvSold;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_PERCENT = "percent";
    public static final String APP_PREFERENCES_RATE = "rate";
    private static final String APP_PREFERENCES_MAC = "mac";
    private SharedPreferences mSettings;
    SimpleAdapter sAdapter;
    ArrayList<Map<String, Object>> data;
    Map<String, Object> m;
    final String ATTRIBUTE_NAME_NAME = "name";
    final String ATTRIBUTE_NAME_COUNT = "count";
    String[] from;
    int[] to;
    private static final int CM_DELETE_ID = 1;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        tvAmount = (TextView)findViewById(R.id.tvTotalAmount);
        tvSalary = (TextView)findViewById(R.id.tvTotalSalary);
        tvLabel = (TextView)findViewById(R.id.textView5);
        lvSold = (ListView) findViewById(R.id.lvSold);
        data = new ArrayList<Map<String, Object>>();
        from = new String[]{ ATTRIBUTE_NAME_NAME, ATTRIBUTE_NAME_COUNT };
        to = new int[]{ R.id.tvSellName, R.id.tvSellCount };
        sAdapter = new SimpleAdapter(this, data, R.layout.sellitem, from, to);
        lvSold.setAdapter(sAdapter);
        String params = getIntent().getStringExtra("params");
        boolean isLogined = getIntent().getBooleanExtra("logined", false);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        switch (params)
        {
            case "admRem":
                tvLabel.setText("Залишок:");
                viewGoodsRemaining(true);
                break;
            case "sellerAll":
                if(isLogined)
                    doReport();
                else
                    listToShowAlertDialog();
                break;
            default:
                CoffeeUtils.showToast(getApplicationContext(), "Невірний параметр!", Toast.LENGTH_SHORT);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_report)
            listToShowAlertDialog();
        else
        if(id == R.id.action_send_report)
            sendReport();

        return super.onOptionsItemSelected(item);
    }

    private void listToShowAlertDialog()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //alert.setTitle("Список проданих товарів буде очищений!");
        alert.setMessage("Показати:");
        alert.setPositiveButton("Залишок товарів", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                tvLabel.setText("Залишок:");
                viewGoodsRemaining(false);
            }
        });

        alert.setNegativeButton("Список проданих", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                tvLabel.setText("Продано:");
                viewSoldGoods();
            }
        });
        alert.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Відмінити");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            m = data.get(acmi.position);
            String delName = (String)m.get(ATTRIBUTE_NAME_NAME);
            int delCount = (int)m.get(ATTRIBUTE_NAME_COUNT);
            cancelAlertDialog(delName, delCount);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void cancelAlertDialog(final String GoodsName, final int OldCount)
    {
        EditText edttxFirst = new EditText(this);
        edttxFirst.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext= edttxFirst;
        if(GoodsName != null) {
            alert.setMessage("Введіть кількість: ");
            alert.setView(edittext);
            alert.setTitle(GoodsName);
            alert.setPositiveButton("Відмінити", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String CountValue = edittext.getText().toString();
                    if(CountValue != "" && CoffeeUtils.checkInt(CountValue)) {
                        final int NewSodlCount = OldCount - Integer.parseInt(CountValue);
                        if(NewSodlCount >= 0) {
                            gdsReport.changeGoodsCountByName(GoodsName, NewSodlCount);
                            CoffeeUtils.showToast(getApplicationContext(),
                                    "Видалено " + Integer.parseInt(CountValue) + "шт.!", Toast.LENGTH_SHORT);
                        }
                        else {
                            gdsReport.changeGoodsCountByName(GoodsName, 0);
                            CoffeeUtils.showToast(getApplicationContext(),
                                    "Видалено " + OldCount + "шт.!", Toast.LENGTH_SHORT);
                        }
                        viewGoodsRemaining(true);
                    }
                    else {
                        CoffeeUtils.showToast(getApplicationContext(), "Кількість товару не введена!", Toast.LENGTH_SHORT);
                    }
                }
            });

            alert.setNegativeButton("Скасувати", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.show();
        }
        else {
            CoffeeUtils.showToast(getApplicationContext(), "Товару немає в наявності!", Toast.LENGTH_SHORT);
        }
    }

    public void viewSoldGoods()
    {
        unregisterForContextMenu(lvSold);
        data.clear();
        Goods[] gdsArr = gdsReport.getSoldGoodsArray();
        //String[] GoodsNamesCounts = new String[gdsArr.length];
        float Amount = 0, Salary = 0, mPercent, mRate;
        if (mSettings.contains(APP_PREFERENCES_PERCENT) && mSettings.contains(APP_PREFERENCES_RATE)) {
            mPercent = mSettings.getFloat(APP_PREFERENCES_PERCENT, 0);
            mRate = mSettings.getFloat(APP_PREFERENCES_RATE, 0);
        }
        else {
            mPercent = 0;
            mRate = 0;
        }
        for(int i = 0; i < gdsArr.length; i++)
        {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_NAME, gdsArr[i].Name);
            m.put(ATTRIBUTE_NAME_COUNT, gdsArr[i].Count);
            data.add(m);
            sAdapter.notifyDataSetChanged();
            //GoodsNamesCounts[i] = gdsArr[i].Name + ", " + gdsArr[i].Count + "шт.";
            Amount += gdsArr[i].Count * gdsArr[i].Price;
        }
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, GoodsNamesCounts);
        lvSold.setAdapter(adapter);*/
        Salary = ((Amount * mPercent)/100) + mRate;
        tvAmount.setText(Float.toString(Amount) + "грн");
        tvSalary.setText(Float.toString(CoffeeUtils.round(Salary, 2)) + "грн");
    }

    public void viewGoodsRemaining(boolean isAdmin)
    {
        data.clear();
        Goods[] gdsArr = gdsReport.getGoodsRemaining();
        for(int i = 0; i < gdsArr.length; i++)
        {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_NAME, gdsArr[i].Name);
            m.put(ATTRIBUTE_NAME_COUNT, gdsArr[i].Count);
            data.add(m);
            sAdapter.notifyDataSetChanged();
        }
        if(isAdmin)
            registerForContextMenu(lvSold);
        else
            unregisterForContextMenu(lvSold);
        tvAmount.setText("");
        tvSalary.setText("");
    }


    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                Bundle bundle = msg.getData();
                String user_result = bundle.getString("reportRes");
                progress.hide();
                if(user_result.equalsIgnoreCase("reported"))
                {
                    CoffeeUtils.showToast(getApplicationContext(), "Звіт відправлено!", Toast.LENGTH_SHORT);
                    listToShowAlertDialog();
                }
                else {
                    CoffeeUtils.showToast(getApplicationContext(), "Помилка!", Toast.LENGTH_SHORT);
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
            bundle.putString("reportRes", res);
            msg.setData(bundle);
            handler2.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doReport()
    {
        String strJson = gdsReport.prepareJson();
        if(strJson == "empty")
        {
            CoffeeUtils.showToast(getApplicationContext(), "Немає що відпраляти!", Toast.LENGTH_SHORT);
            return;
        }
        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("company", Integer.toString(LoginActivity.worker.Company)));
        urlParameters.add(new BasicNameValuePair("user", Integer.toString(LoginActivity.worker.ID)));
        urlParameters.add(new BasicNameValuePair("token", LoginActivity.worker.Token));
        urlParameters.add(new BasicNameValuePair("operation", "report"));
        if (mSettings.contains(APP_PREFERENCES_MAC))
            urlParameters.add(new BasicNameValuePair("mac", mSettings.getString(APP_PREFERENCES_MAC, "")));
        urlParameters.add(new BasicNameValuePair("jsold", strJson));
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
    }

    public void sendReport()
    {
        Intent intent = new Intent(ReportActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
