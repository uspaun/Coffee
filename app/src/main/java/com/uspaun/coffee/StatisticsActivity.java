package com.uspaun.coffee;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StatisticsActivity extends ActionBarActivity {

    Goods gdsStat = new Goods();
    TextView tvAmount, tvSalary;
    //ArrayAdapter<String> adapter;
    ListView lvSold;
    SimpleAdapter sAdapter;
    ArrayList<Map<String, Object>> data;
    Map<String, Object> m;
    final String ATTRIBUTE_NAME_NAME = "name";
    final String ATTRIBUTE_NAME_COUNT = "count";
    String[] from;
    int[] to;
    private static final int CM_DELETE_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        tvAmount = (TextView)findViewById(R.id.tvTotal);
        tvSalary = (TextView)findViewById(R.id.tvIncome);
        lvSold = (ListView) findViewById(R.id.lvSoldGoods);
        data = new ArrayList<Map<String, Object>>();
        from = new String[]{ ATTRIBUTE_NAME_NAME, ATTRIBUTE_NAME_COUNT };
        to = new int[]{ R.id.tvSellName, R.id.tvSellCount };
        sAdapter = new SimpleAdapter(this, data, R.layout.sellitem, from, to);
        lvSold.setAdapter(sAdapter);
        registerForContextMenu(lvSold);
        viewSoldGoods();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upload_report)
            return true;
        else
        if(id == R.id.action_truncate_report)
            clearSoldGoodsAlertDialog();

        return super.onOptionsItemSelected(item);
    }

    private static float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    public void viewSoldGoods()
    {
        data.clear();
        Goods[] gdsArr = gdsStat.getSoldGoodsArray();
        //String[] GoodsNamesCounts = new String[gdsArr.length];
        //final int[] arrCount = new int[gdsArr.length];
        float Amount = 0, OrigPriceAmount = 0, Income = 0;
        for(int i = 0; i < gdsArr.length; i++)
        {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_NAME, gdsArr[i].Name);
            m.put(ATTRIBUTE_NAME_COUNT, gdsArr[i].Count);
            data.add(m);
            sAdapter.notifyDataSetChanged();
            //arrCount[i] = gdsArr[i].Count;
            Amount += gdsArr[i].Count * gdsArr[i].Price;
            OrigPriceAmount += gdsArr[i].Count * gdsArr[i].OriginalPrice;
        }
        lvSold.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //String TileName = (String) parent.getItemAtPosition(position);
                //cancelSoldAlertDialog(TileName, arrCount[position]);
            }
        });
        Income = Amount - OrigPriceAmount;
        tvAmount.setText(Float.toString(Amount) + "грн");
        tvSalary.setText(Float.toString(round(Income, 2)) + "грн");
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
            cancelSoldAlertDialog(delName, delCount);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void clearSoldGoodsAlertDialog()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Стерти дані?");
        alert.setTitle("Список проданих товарів буде очищений!");
        alert.setPositiveButton("Так, стерти", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                gdsStat.clearSoldGoods();
                viewSoldGoods();
            }
        });

        alert.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    public void cancelSoldAlertDialog(final String GoodsName, final int OldCount)
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
                        final int OldGoodsCount = gdsStat.GetDoodsInfoByName(GoodsName).Count;
                        final int NewSodlCount = OldCount - Integer.parseInt(CountValue);
                        if(NewSodlCount >= 0)
                            gdsStat.changeSoldGoodsCountByName(GoodsName, NewSodlCount);
                        else
                            gdsStat.deleteSoldGoodsByName(GoodsName);
                        final int NewGoodsCount = OldGoodsCount + Integer.parseInt(CountValue);
                        gdsStat.changeGoodsCountByName(GoodsName, NewGoodsCount);
                        viewSoldGoods();
                        CoffeeUtils.showToast(getApplicationContext(),
                                "Відмінено " + Integer.parseInt(CountValue) + "шт.!", Toast.LENGTH_SHORT);
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
}
