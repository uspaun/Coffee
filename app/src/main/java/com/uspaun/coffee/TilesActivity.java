package com.uspaun.coffee;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TilesActivity extends ActionBarActivity {

    Goods gdsTiles;
    ArrayAdapter<String> adapterTiles;
    ListView lvGoodsToTiles, lvTiles;

    SimpleAdapter sAdapter;
    SimpleAdapter sAdapterTiles;
    ArrayList<Map<String, Object>> data;
    ArrayList<Map<String, Object>> dataTiles;
    Map<String, Object> m;
    final String ATTRIBUTE_NAME_NAME = "name";
    //final String ATTRIBUTE_NAME_COUNT = "image";
    String[] from;
    int[] to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiles);
        lvGoodsToTiles = (ListView)findViewById(R.id.lvToSelect);
        lvTiles = (ListView) findViewById(R.id.lvSelected);
        gdsTiles = new Goods();
        data = new ArrayList<Map<String, Object>>();
        dataTiles = new ArrayList<Map<String, Object>>();
        from = new String[]{ ATTRIBUTE_NAME_NAME };
        to = new int[]{ R.id.goodName };
        sAdapter = new SimpleAdapter(this, data, R.layout.activity_tile_item, from, to);
        lvGoodsToTiles.setAdapter(sAdapter);
        sAdapterTiles = new SimpleAdapter(this, dataTiles, R.layout.activity_tile_item, from, to);
        lvTiles.setAdapter(sAdapterTiles);
        viewGoodsTilesSettings(false);
        lvGoodsToTiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(lvTiles.getCount() <= 5) {
                    m = data.get(position);
                    //String TileName = (String) parent.getItemAtPosition(position);
                    String TileName = (String)m.get(ATTRIBUTE_NAME_NAME);
                    gdsTiles.putTileName(TileName);
                    viewGoodsTilesSettings(true);
                }
                else
                {
                    CoffeeUtils.showToast(getApplicationContext(),
                            "Максимальна кількість плиток - 6!", Toast.LENGTH_SHORT);
                }
            }
        });
        lvTiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                m = dataTiles.get(position);
                String TileName = (String)m.get(ATTRIBUTE_NAME_NAME);
                deleteTilesAlertDialog(TileName);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tiles, menu);
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

    public void deleteTilesAlertDialog(final String Name) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Видалити плитку з головного екрану?");
        alert.setPositiveButton("Так", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                gdsTiles.deleteTilesByName(Name);
                viewGoodsTilesSettings(true);
            }
        });
        alert.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    public void viewGoodsTilesSettings(boolean isInitialised)
    {
        if(!isInitialised)
        {
            String[] GoodsNamesCounts = gdsTiles.getGoodsNamesArray();
            //lvGoodsToTiles = (ListView) findViewById(R.id.lvToSelect);
            for (int i = 0; i < GoodsNamesCounts.length; i++) {
                m = new HashMap<String, Object>();
                m.put(ATTRIBUTE_NAME_NAME, GoodsNamesCounts[i]);
                data.add(m);
                sAdapter.notifyDataSetChanged();
            }
        }
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_tile_item, GoodsNamesCounts);
        lvGoodsToTiles.setAdapter(adapter);*/
        String[] TilesNamesArray = gdsTiles.getTilesNamesArray();
        dataTiles.clear();
        for (int i = 0; i < TilesNamesArray.length; i++) {
            m = new HashMap<String, Object>();
            m.put(ATTRIBUTE_NAME_NAME, TilesNamesArray[i]);
            dataTiles.add(m);
            sAdapterTiles.notifyDataSetChanged();
        }
    }
}
