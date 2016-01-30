package com.uspaun.coffee;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16.07.15.
 */
public class Worker {
    int ID;
    int Position;
    String Login;
    String Token;
    int Company;
    public Worker(int id, int position, String login, String token, int company)
    {
        this.ID = id;
        this.Position = position;
        this.Login = login;
        this.Token = token;
        this.Company = company;
    }

    public Worker()
    {
        this.ID = 0;
        this.Position = WorkerPositions.Seller;
        this.Login = "";
        this.Token = "";
        this.Company = 0;
    }

    /*Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            ID = Integer.parseInt(bundle.getString("userID"));
            Token = bundle.getString("userToken");
            if(bundle.containsKey("userCompany"))
                Company = Integer.parseInt(bundle.getString("userCompany"));
        }
    };

    public void init_login(List<NameValuePair> urlParameters)
    {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        final String url = "http://uspaun.pp.ua/coffee/mobile/login.php";
        try {
            String res = CoffeeUtils.httpPost(url, urlParameters);
            String[] arrayResult = res.split("\\&");
            bundle.putInt("userID", Integer.parseInt(arrayResult[0]));
            bundle.putString("userToken", arrayResult[1]);
            //bundle.putString("all", res);
            if(res.contains("$"))
                bundle.putString("userCompany", res.substring(res.indexOf("&")+1, res.indexOf("$")));
            msg.setData(bundle);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(String WorkerLogin, String WorkerPassword)
    {

        final List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("login", WorkerLogin));
        urlParameters.add(new BasicNameValuePair("password", WorkerPassword));
        this.Login = WorkerLogin;
        new Thread(new Runnable() {
            public void run() {
                try {
                    init_login(urlParameters);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/

    public void setPosition(int Position)
    {
        this.Position = Position;
    }

    public int getPosition()
    {
        return this.Position;
    }

    public int getID()
    {
        return this.ID;
    }

    public String getToken()
    {
        return this.Token;
    }
}
