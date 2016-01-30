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
import android.widget.Toast;

import java.util.Set;


public class TilesActivity extends ActionBarActivity {

    Goods gdsTiles;
    ArrayAdapter<String> adapterTiles;
    ListView lvGoodsToTiles, lvTiles;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiles);
        gdsTiles = new Goods();
        viewGoodsTilesSettings();
        lvGoodsToTiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(lvTiles.getCount() <= 5) {
                    String TileName = (String) parent.getItemAtPosition(position);
                    gdsTiles.putTileName(TileName);
                    viewGoodsTilesSettings();
                }
                else
                {

                    toast = Toast.makeText(getApplicationContext(),
                            "Максимальна кількість плиток - 6!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        lvTiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String TileName = (String)parent.getItemAtPosition(position);
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
                viewGoodsTilesSettings();
            }
        });
        alert.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    public void viewGoodsTilesSettings()
    {
        String[] GoodsNamesCounts = gdsTiles.getGoodsNamesArray();
        lvGoodsToTiles = (ListView) findViewById(R.id.lvToSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, GoodsNamesCounts);
        lvGoodsToTiles.setAdapter(adapter);
        String[] TilesNamesArray = gdsTiles.getTilesNamesArray();
        lvTiles = (ListView) findViewById(R.id.lvSelected);
        adapterTiles = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, TilesNamesArray);
        lvTiles.setAdapter(adapterTiles);
    }
}
