package com.uspaun.coffee;

import android.content.Context;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by root on 30.07.15.
 */

public class CoffeeUtils {
    private static Toast toast;
    private static Goods gdsUtils;
    public static boolean checkInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean checkFloat(String string) {
        try {
            Float.parseFloat(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void showToast(Context context, String Text, int Length)
    {
        toast = Toast.makeText(context, Text, Length);
        toast.show();
    }

    public static String getName(String Name, int Count)
    {
        return Name.substring(0, Name.indexOf(", " + Count + "шт."));
    }

    public static float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    public static String httpPost(String url, List<NameValuePair> urlParameters) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null)
        {
            result.append(line);
        }
        return result.toString();
    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
}
