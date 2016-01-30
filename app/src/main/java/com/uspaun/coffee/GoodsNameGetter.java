package com.uspaun.coffee;

import android.content.Intent;

import android.os.Bundle;

import java.io.IOException;

/**
 * Created by root on 08.07.15.
 */
public class GoodsNameGetter implements Runnable {
    private volatile String value = "";
    private String parametr;
    private Goods threadGoods = new Goods();
    private int i=0;
    public GoodsNameGetter(String param){
        this.parametr = param;
    }
    @Override
    public void run() {
        try {
            value = "";
            value = threadGoods.GetGoodsNameFromWebByBarCode(parametr);
        } catch (Exception e) {
            value = "Товар не знайдено!";
        }
    }

    public String getValue() {
        while (value == "")
        {
            i++;
        }
        return value;
    }
}
/*EditText edtTxtCode = (EditText)findViewById(R.id.etCode);
        EditText edtTxtID = (EditText)findViewById(R.id.etID);
        EditText edtTxtName = (AutoCompleteTextView)findViewById(R.id.etName);*/
/*String strGdName = "";
Goods gdThread = new Goods();
try {
        teID.setText("no");

            GoodsNameGetter gnmAdd = new GoodsNameGetter(scanCntnt);
            new Thread(gnmAdd).start();
            do {
                strGdName = gnmAdd.getValue();
            }
            while (strGdName == "");
        } catch (Exception e) {
        toast = Toast.makeText(getApplicationContext(),
        "Помилка з’єднання!", Toast.LENGTH_SHORT);
        toast.show();
        }
        teName.setText(strGdName);
        teCode.setText(scanCntnt);*/