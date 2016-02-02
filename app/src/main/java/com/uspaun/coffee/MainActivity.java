package com.uspaun.coffee;

import android.app.AlertDialog;
import android.app.Application;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    Goods myGoods;
    Toast toast;
    AutoCompleteTextView teName;
    ImageButton btnAmer, btnCappuc, btnDop, btnEspres, btnLat, btnRistr;
    ListView lvOrder;
    TextView tvAmount;
    SimpleAdapter sAdapter;
    ArrayList<Map<String, Object>> data;
    Map<String, Object> m;
    final String ATTRIBUTE_NAME_NAME = "name";
    final String ATTRIBUTE_NAME_COUNT = "count";
    String[] from;
    int[] to;
    private static final int CM_DELETE_ID = 1;
    float finSumm = 0;

    private static final String APP_PREFERENCES_ID = "id";
    private static final String APP_PREFERENCES_MAC = "mac";
    private static final String APP_PREFERENCES_TOKEN = "token";
    private static final String APP_PREFERENCES_POSITION = "position";
    public static final String APP_PREFERENCES = "mysettings";
    private SharedPreferences mSettings;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Goods.CreateDB(getApplicationContext());
        myGoods = new Goods();
        teName = (AutoCompleteTextView)
                findViewById(R.id.etName);
        teName.setAdapter(getAutocompleteAdapter());
        teName.setOnItemClickListener(this);
        btnAmer = (ImageButton)findViewById(R.id.btnAmericano);
        btnAmer.setOnClickListener(CoffeeClickListener);
        btnCappuc = (ImageButton)findViewById(R.id.btnCappuccino);
        btnCappuc.setOnClickListener(CoffeeClickListener);
        btnDop = (ImageButton)findViewById(R.id.btnDoppio);
        btnDop.setOnClickListener(CoffeeClickListener);
        btnEspres = (ImageButton)findViewById(R.id.btnEspresso);
        btnEspres.setOnClickListener(CoffeeClickListener);
        btnLat = (ImageButton)findViewById(R.id.btnLatte);
        btnLat.setOnClickListener(CoffeeClickListener);
        btnRistr = (ImageButton)findViewById(R.id.btnRistretto);
        btnRistr.setOnClickListener(CoffeeClickListener);
        lvOrder = (ListView)findViewById(R.id.lvOrder);
        tvAmount = (TextView)findViewById(R.id.tvAmount);
        tvAmount.setText("0");
        data = new ArrayList<Map<String, Object>>();
        // массив имен атрибутов, из которых будут читаться данные
        from = new String[]{ ATTRIBUTE_NAME_NAME, ATTRIBUTE_NAME_COUNT };
        // массив ID View-компонентов, в которые будут вставлять данные
        to = new int[]{ R.id.tvSellName, R.id.tvSellCount };
        sAdapter = new SimpleAdapter(this, data, R.layout.sellitem, from, to);
        lvOrder.setAdapter(sAdapter);
        registerForContextMenu(lvOrder);
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = mSettings.edit();
        String MAC = info.getMacAddress();
        editor.putString(APP_PREFERENCES_MAC, MAC);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_show_reprt:
                intent = new Intent(MainActivity.this, ReportActivity.class);
                intent.putExtra("params", "sellerAll");
                startActivity(intent);
                return true;
            /*case R.id.action_settings:
                intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
                return true;*/
            case R.id.action_sell_goods:
                sellAll();
                return true;
            case R.id.action_about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
                return true;
            case R.id.action_login:
                mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                if (mSettings.contains(APP_PREFERENCES_ID) && mSettings.contains(APP_PREFERENCES_TOKEN) && mSettings.contains(APP_PREFERENCES_POSITION)
                    && mSettings.getInt(APP_PREFERENCES_ID, 0) != -1 && mSettings.getString(APP_PREFERENCES_TOKEN, "") != "") {
                    LoginActivity.worker.ID = mSettings.getInt(APP_PREFERENCES_ID, 0);
                    LoginActivity.worker.Token = mSettings.getString(APP_PREFERENCES_TOKEN, "");
                    LoginActivity.worker.Position = mSettings.getInt(APP_PREFERENCES_POSITION, 0);
                    intent = new Intent(MainActivity.this, AdminActivity.class);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayAdapter<String> getAutocompleteAdapter()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, myGoods.getGoodsNamesArray());
        return adapter;
    }

    public void sellGoodsFromAlertDialog(final String GoodsName)
    {
        EditText edttxFirst = new EditText(this);
        edttxFirst.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext= edttxFirst;
        if(GoodsName != null) {
            alert.setMessage("Введіть кількість: ");
            alert.setView(edittext);
            alert.setTitle(GoodsName);
            alert.setPositiveButton("Продати", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String CountValue = edittext.getText().toString();
                    if(CountValue != "" && CoffeeUtils.checkInt(CountValue)) {
                        addToOrder(GoodsName, Integer.parseInt(CountValue));
                    }
                    else {
                        CoffeeUtils.showToast(getApplicationContext(),
                                "Кількість товару не введена!", Toast.LENGTH_SHORT);
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
            CoffeeUtils.showToast(getApplicationContext(),
                    "Товару немає в наявності!", Toast.LENGTH_SHORT);
        }
    }

    View.OnClickListener CoffeeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.btnAmericano:
                            addToOrder("Американо", 1);
                    break;
                case R.id.btnCappuccino:
                            addToOrder("Капучино", 1);
                    break;
                case R.id.btnDoppio:
                            addToOrder("Допіо", 1);
                    break;
                case R.id.btnEspresso:
                            addToOrder("Еспрессо", 1);
                    break;
                case R.id.btnLatte:
                            addToOrder("Латте", 1);
                    break;
                case R.id.btnRistretto:
                            addToOrder("Рістретто", 1);
                    break;
            }

        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String strGDName = myGoods.GetGoodsNameByBarCodeFromDB(scanContent);
            if(!strGDName.equalsIgnoreCase("Немає в наявності!"))
                sellGoodsFromAlertDialog(strGDName);
            else{
                CoffeeUtils.showToast(getApplicationContext(),
                        "Товару немає в наявності!", Toast.LENGTH_SHORT);
            }
        }
        else{
            CoffeeUtils.showToast(getApplicationContext(),
                    "Штрихкод не розпізнано!", Toast.LENGTH_SHORT);
        }
    }
    public void onScanClick(View v)
    {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String GoodsName = (String)parent.getItemAtPosition(position);
        sellGoodsFromAlertDialog(GoodsName);
        teName.setText("");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Відмінити");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            m = data.get(acmi.position);
            String delName = (String)m.get(ATTRIBUTE_NAME_NAME);
            int delCount = (int)m.get(ATTRIBUTE_NAME_COUNT);
            finSumm -= myGoods.GetDoodsInfoByName(delName).Price * delCount;
            tvAmount.setText(Float.toString(finSumm));
            m.clear();
            data.remove(acmi.position);
            sAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    boolean isInData(String Name)
    {
        boolean result = false;
        Map<String, Object> gMap = new HashMap<String, Object>();
        for(int i = 0; i < data.size(); i++)
        {
            gMap = data.get(i);
            if(gMap.containsValue(Name)) {
                result = true;
            }
        }
        return result;
    }

    int getIndexOfData(String Name)
    {
        int result = -1;
        Map<String, Object> gMap = new HashMap<String, Object>();
        for(int i = 0; i < data.size(); i++)
        {
            gMap = data.get(i);
            if(gMap.containsValue(Name)) {
                result = i;
            }
        }
        return result;
    }

    public void addToOrder(String GoodsName, int Count)
    {
        m = new HashMap<String, Object>();
        Goods gdsToAdd = myGoods.GetDoodsInfoByName(GoodsName);
        if (gdsToAdd != null) {
            if (!isInData(GoodsName)) {
                if (Count <= gdsToAdd.Count) {
                    m.put(ATTRIBUTE_NAME_NAME, GoodsName);
                    m.put(ATTRIBUTE_NAME_COUNT, Count);
                    data.add(m);
                    sAdapter.notifyDataSetChanged();
                    finSumm += gdsToAdd.Price * Count;
                    tvAmount.setText(Float.toString(finSumm));
                } else {
                    toast = Toast.makeText(getApplicationContext(),
                            "Недостатньо товару!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                int IndexOfData = getIndexOfData(GoodsName);
                m = data.get(IndexOfData);
                int hCount = (int)m.get(ATTRIBUTE_NAME_COUNT);
                if((Count + hCount) <= gdsToAdd.Count) {
                    data.remove(IndexOfData);
                    m.clear();
                    m.put(ATTRIBUTE_NAME_NAME, GoodsName);
                    m.put(ATTRIBUTE_NAME_COUNT, Count + hCount);
                    data.add(m);
                    sAdapter.notifyDataSetChanged();
                    finSumm += gdsToAdd.Price * Count;
                    tvAmount.setText(Float.toString(finSumm));
                } else {
                    toast = Toast.makeText(getApplicationContext(),
                            "Недостатньо товару!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        else
        {
            toast = Toast.makeText(getApplicationContext(),
                    "Товару немає в наявності!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void sellAll() {
        if (!data.isEmpty()) {
            Map<String, Object> gMap = new HashMap<String, Object>();
            String gdName;
            int gdCount;
            for (int i = 0; i < sAdapter.getCount(); i++) {
                gMap = data.get(i);
                gdName = (String) gMap.get(ATTRIBUTE_NAME_NAME);
                gdCount = (int) gMap.get(ATTRIBUTE_NAME_COUNT);
                myGoods.SellGoodsByName(gdName, gdCount);
                gMap.clear();
            }
            data.clear();
            sAdapter.notifyDataSetChanged();
            finSumm = 0;
            tvAmount.setText(Float.toString(finSumm));
            toast = Toast.makeText(getApplicationContext(),
                    "Продано!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            CoffeeUtils.showToast(getApplicationContext(), "Список товарів порожній!", Toast.LENGTH_SHORT);
        }
    }

    @Override
     public void onBackPressed()
    {
        moveTaskToBack(true);
        finish();
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    public void onDestroy() {
        super.onDestroy();

        System.runFinalizersOnExit(true);
        System.exit(0);
    }
}
