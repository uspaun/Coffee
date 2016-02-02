package com.uspaun.coffee;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class TileItemActivity extends AppCompatActivity {

    ImageView tileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tile_item);
        tileImage = (ImageView)findViewById(R.id.goodImage);
    }

    public void onClick(View v)
    {
        CoffeeUtils.showToast(getApplicationContext(), "something", Toast.LENGTH_SHORT);
    }
}
