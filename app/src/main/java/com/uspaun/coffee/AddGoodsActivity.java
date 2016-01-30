package com.uspaun.coffee;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.MenuView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;


public class AddGoodsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    Goods gdAdd;
    EditText teCode, tePrice, teOrigPrice, teAmount, teID;
    Toast toast;
    AutoCompleteTextView teName;
    //ProgressBar progressBar;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        gdAdd = new Goods();
        teCode = (EditText)findViewById(R.id.etCode);
        tePrice = (EditText)findViewById(R.id.etPrice);
        teOrigPrice = (EditText)findViewById(R.id.etOrigPrice);
        teAmount = (EditText)findViewById(R.id.etAmount);
        teID = (EditText)findViewById(R.id.etID);
        teName = (AutoCompleteTextView)findViewById(R.id.etName);
        teName.setAdapter(getAutocompleteAdapter());
        teName.setOnItemClickListener(this);
        //progressBar = (ProgressBar)findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);
        progress = new ProgressDialog(this);
        progress.setMessage("Пошук");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        teName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(teName, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public ArrayAdapter<String> getAutocompleteAdapter()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, gdAdd.getGoodsNamesArray());
        return adapter;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String Name = bundle.getString("gdName");
            String Code = bundle.getString("gdCode");
            teID.setText("no");
            teName.setText(Name);
            teCode.setText(Code);
            progress.hide();
        }
    };

    public void searchInWeb(String scanCntnt)
    {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        String strGdName = "";
        Goods gdThread = new Goods();
        try {
            strGdName = gdThread.GetGoodsNameFromWebByBarCode(scanCntnt);
            bundle.putString("gdName", strGdName);
            bundle.putString("gdCode", scanCntnt);
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (Exception e) {
            CoffeeUtils.showToast(getApplicationContext(), "Помилка з’єднання!", Toast.LENGTH_SHORT);
        }
    }

    public void searchGoodsAlertDialog(final String scnContent)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("Шукати в інтернеті?");
            alert.setTitle("Товар відсутній в базі!");
        alert.setPositiveButton("Так", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progress.show();
                new Thread(new Runnable() {
                    public void run() {
                        searchInWeb(scnContent);
                    }
                }).start();

            }
        });

        alert.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                teCode.setText(scnContent);
            }
            });
            alert.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            gdAdd.GoodsId = "";
            Goods gdAddDB = gdAdd.GetDoodsInfoByBarCode(scanContent);
            if(gdAddDB != null) {
                teID.setText(gdAddDB.GoodsId);
                teName.setText(gdAddDB.Name);
                teCode.setText(gdAddDB.BarCode);
                teAmount.setText(Integer.toString(gdAddDB.Count));
                tePrice.setText(Float.toString(gdAddDB.Price));
                teOrigPrice.setText(Float.toString(gdAddDB.OriginalPrice));
            }
            else {
                searchGoodsAlertDialog(scanContent);
            }
        }
        else{
            toast = Toast.makeText(getApplicationContext(),
                    "Штрихкод не розпізнано!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onScanAddClick(View v) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void onAddClick(View v)
    {
        String nm = teName.getText().toString();
        String pr = tePrice.getText().toString();
        String am = teAmount.getText().toString();
        String op = teOrigPrice.getText().toString();
        boolean bbc = CoffeeUtils.checkInt(teCode.getText().toString());
        if(!teName.getText().toString().equals("") && !tePrice.getText().toString().equals("") && !teAmount.getText().toString().equals("")
                && !teOrigPrice.getText().toString().equals("") && CoffeeUtils.checkFloat(tePrice.getText().toString())
                && CoffeeUtils.checkFloat(teOrigPrice.getText().toString()) && CoffeeUtils.checkInt(teAmount.getText().toString()))
        {
            String gdId = teID.getText().toString();
            String strNo = "no";
            if(!gdId.equalsIgnoreCase(strNo))
            {
                gdAdd.updateGoodsById(new Goods(teName.getText().toString(), teCode.getText().toString(),
                        Integer.parseInt(teAmount.getText().toString()),
                        Float.parseFloat(tePrice.getText().toString()),
                        Float.parseFloat(teOrigPrice.getText().toString())), teID.getText().toString());
                teName.setAdapter(getAutocompleteAdapter());
                CoffeeUtils.showToast(getApplicationContext(), "Товар додано/змінено!", Toast.LENGTH_SHORT);
                teID.setText("no");
            }
            else {
                gdAdd.AddGoods(new Goods(teName.getText().toString(), teCode.getText().toString(),
                        Integer.parseInt(teAmount.getText().toString()), Float.parseFloat(tePrice.getText().toString()),
                        Float.parseFloat(teOrigPrice.getText().toString())));
                teName.setAdapter(getAutocompleteAdapter());
                toast = Toast.makeText(getApplicationContext(),
                        "Товар успішно доданий!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else
        {
            toast = Toast.makeText(getApplicationContext(),
                    "Введіть всі значення вірно!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Goods gdChanged = gdAdd.GetDoodsInfoByName(teName.getText().toString());
        teID.setText(gdChanged.GoodsId);
        //teName.setText(gdChanged.Name);
        teCode.setText(gdChanged.BarCode);
        teAmount.setText(Integer.toString(gdChanged.Count));
        tePrice.setText(Float.toString(gdChanged.Price));
        teOrigPrice.setText(Float.toString(gdChanged.OriginalPrice));
    }
}
