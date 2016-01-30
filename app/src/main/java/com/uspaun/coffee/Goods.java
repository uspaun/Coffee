package com.uspaun.coffee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by root on 02.07.15.
 */
public class Goods {
    String Name, BarCode;
    int Count = 0;
    float Price = 0, OriginalPrice = 0;
    String GoodsId = "";
    static private GoodsDB gdsDB = new GoodsDB();

    public Goods()
    {
        Name = "";
        BarCode = "";
        Count = 0;
        Price = 0;
        GoodsId = "";
    }

    public Goods(Goods gd)
    {
        this.Name = gd.Name;
        this.BarCode = gd.BarCode;
        this.Count = gd.Count;
        this.Price = gd.Price;
        this.OriginalPrice = gd.OriginalPrice;
    }

    public Goods(String GoodsName, String GoodsBarCode, int GoodsCount, float GoodsPrice, float GoodsOriginalPrice)
    {
        this.Name = GoodsName;
        this.BarCode = GoodsBarCode;
        if(GoodsCount > 0)
            this.Count = GoodsCount;
        else
            this.Count = 0;
        this.Price = GoodsPrice;
        this.OriginalPrice = GoodsOriginalPrice;
    }

    public Goods(int id, String GoodsName, String GoodsBarCode, int GoodsCount, float GoodsPrice, float GoodsOriginalPrice)
    {
        this.Name = GoodsName;
        this.BarCode = GoodsBarCode;
        if(GoodsCount > 0)
            this.Count = GoodsCount;
        else
            this.Count = 0;
        this.Price = GoodsPrice;
        this.OriginalPrice = GoodsOriginalPrice;
        this.GoodsId = Integer.toString(id);
    }

    public static void CreateDB(Context context)
    {
        gdsDB.CreateGoodsDB(context);
    }

    public void AddGoods(Goods gd)
    {
        gdsDB.InsertGoods(gd.Name, gd.BarCode, gd.Count, gd.Price, gd.OriginalPrice);
    }

    public void addGoods(Goods gd)
    {
        gdsDB.InsertGoods(Integer.parseInt(gd.GoodsId), gd.Name, gd.BarCode, gd.Count, gd.Price, gd.OriginalPrice);
    }

    //---Getting info about Goods---//

    public String GetPage(String urlsite) throws Exception {
        BufferedReader reader = null;
        String result = "";
        try {
            URL site = new URL(urlsite);
            reader = new BufferedReader(new InputStreamReader(site.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
                if(result.contains("<title/>"))
                    break;

            }
            reader.close();
            return result;
        } catch (Exception ex) {
            return "404 - Сторінку не знайдено";
        }
    }

    public String GetGoodsNameFromWebByBarCode(String BarCode) throws Exception {
        String title;
        String HTMLSTring;
        Document html;
        HTMLSTring = GetPage("https://zakaz.ua/uk/0" + BarCode + "/");
        if(HTMLSTring == "404 - Сторінку не знайдено")
            title = HTMLSTring;
        else {
            html = Jsoup.parse(HTMLSTring);
            title = html.title();
        }
        if(title == "404 - Сторінку не знайдено") {
            HTMLSTring = GetPage("http://www.goodsmatrix.ru/goods/" + BarCode + ".html");
            if(HTMLSTring == "404 - Сторінку не знайдено")
                title = HTMLSTring;
            else {
                html = Jsoup.parse(HTMLSTring);
                title = html.title();
            }
            if(title == "404 - Сторінку не знайдено")
            {
                HTMLSTring = GetPage("http://megamarket.ua/ua/product/" + BarCode);
                if(HTMLSTring == "404 - Сторінку не знайдено")
                    return "Товар не знайдено!";
                else {
                    html = Jsoup.parse(HTMLSTring);
                    title = html.title();
                    return  title.substring(0, title.indexOf('-')-1);
                }
            }
            else
                return title.substring(32, title.length());
        }
        else
            return  title.substring(0, title.indexOf('→')-1);
    }

    public Goods GetDoodsInfoByName(String FullName)
    {
        return gdsDB.GetDoodsInfoByName(FullName);
    }

    public Goods GetDoodsInfoByBarCode(String GoodsBarCode)
    {
        if (gdsDB.GetDoodsInfoByBarCode(GoodsBarCode) != null)
            return gdsDB.GetDoodsInfoByBarCode(GoodsBarCode);
        else
            return null;
    }

    public void DeleteGoodsByBarCode(String GoodsBarCode)
    {
        gdsDB.DeleteGoodsByBarCode(GoodsBarCode);
    }

    public String SellGoodsByName(String FullName, int GoodsCount)
    {
        int CurrentCount = gdsDB.GetGoodsCountByName(FullName);
        if(CurrentCount != 0) {
            int NewCount = CurrentCount - GoodsCount;
            if (NewCount >= 0) {
                gdsDB.ChangeGoodsCountByName(FullName, NewCount);
                int SoldGoodsCount = gdsDB.GetSoldGoodsCountByName(FullName);
                if (SoldGoodsCount == 0)
                    gdsDB.InsertSoldGoods(FullName, gdsDB.getGoodsBarCodeByName(FullName), GoodsCount,
                            gdsDB.getGoodsPriceByName(FullName), gdsDB.getGoodsOriginalPriceByName(FullName));
                else {
                    int NewSoldCount = SoldGoodsCount + GoodsCount;
                    gdsDB.ChangeSoldGoodsCountByName(FullName, NewSoldCount);
                }
                return FullName + ", " + GoodsCount + "шт продано!";
            } else
                return "Недостатньо товару, введіть меншу кількість!";
        }
        else
            return "Товар закінчився!";
    }

    public String SellGoodsByBarCode(String GoodsBarCode, int GoodsCount)
    {
        int CurrentCount = gdsDB.GetGoodsCountByBarCode(GoodsBarCode);
        if(CurrentCount - GoodsCount >= 0) {
            int NewCount = CurrentCount - GoodsCount;
            gdsDB.ChangeGoodsCountByName(GoodsBarCode, NewCount);
            return gdsDB.GetGoodsNameByBarCode(BarCode) + ", " +  GoodsCount + "шт продано!";
        }
        else
            return "Товар закінчився!";
    }

    public Goods GetFirstGoods()
    {
        return gdsDB.GetFirstGoods();
    }

    public void updateGoodsById(Goods gd, String Id)
    {
        gdsDB.updateGoodsById(gd, Id);
    }

    public String[] getGoodsNamesArray()
    {
        return gdsDB.getGoodsNamesArray();
    }

    public Goods[] getSoldGoodsArray()
    {
        return gdsDB.getSoldGoodsArray();
    }

    public Goods[] getGoodsRemaining()
    {
        return gdsDB.getGoodsRemainingArray();
    }

    public String GetGoodsNameByBarCodeFromDB(String GoodsBarcode) {
        return gdsDB.GetGoodsNameByBarCode(GoodsBarcode);
    }

    public void clearSoldGoods()
    {
        gdsDB.truncateSoldGoods();
    }

    public String[] getTilesNamesArray()
    {
        return gdsDB.getTilesNamesArray();
    }

    public int putTileName(String TileName)
    {
        return gdsDB.putTileName(TileName);
    }

    public void deleteTilesByName(String FullName)
    {
        gdsDB.deleteTilesByName(FullName);
    }

    public void changeSoldGoodsCountByName(String FullName, int NewSoldCount)
    {
        gdsDB.ChangeSoldGoodsCountByName(FullName, NewSoldCount);
    }

    public void changeGoodsCountByName(String FullName, int NewSoldCount)
    {
        gdsDB.ChangeGoodsCountByName(FullName, NewSoldCount);
    }

    public void deleteSoldGoodsByName(String FullName)
    {
        gdsDB.deleteSoldGoodsByName(FullName);
    }

    public void deleteGoodsByName(String FullName)
    {
        gdsDB.deleteGoodsByName(FullName);
    }

    public boolean isExist(int id)
    {
        return gdsDB.isExist(id);
    }

    public int getLastID()
    {
        return gdsDB.getLastID();
    }

    public int getLastReceipt()
    {
        return gdsDB.getLastReceipt();
    }

    public String prepareJson()
    {
        Goods[] soldGoods = this.getSoldGoodsArray();
        if(soldGoods.length == 0)
            return "empty";
        JSONArray list = new JSONArray();
        JSONObject obj;
        try {
            for (int i = 0; i < soldGoods.length; i++) {
                obj = new JSONObject();
                obj.put("name", CoffeeUtils.convertToUTF8(soldGoods[i].Name));
                obj.put("barcode", CoffeeUtils.convertToUTF8(soldGoods[i].BarCode));
                obj.put("count", soldGoods[i].Count);
                obj.put("price", soldGoods[i].Price);
                obj.put("original_price", soldGoods[i].OriginalPrice);
                list.put(obj);
            }
            return list.toString();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public String prepareNewGoodsJson()
    {
        Goods[] newGoods = gdsDB.getNewGoodsArray();
        if(newGoods.length != 0) {
            JSONArray list = new JSONArray();
            JSONObject obj;
            try {
                for (int i = 0; i < newGoods.length; i++) {
                    obj = new JSONObject();
                    obj.put("name", CoffeeUtils.convertToUTF8(newGoods[i].Name));
                    obj.put("barcode", CoffeeUtils.convertToUTF8(newGoods[i].BarCode));
                    obj.put("count", newGoods[i].Count);
                    obj.put("price", newGoods[i].Price);
                    obj.put("original_price", newGoods[i].OriginalPrice);
                    list.put(obj);
                }
                return list.toString();
            } catch (Exception e) {
                return "";
            }
        }
        else return null;
    }

    public boolean changeNewGoodsChanged()
    {
        try
        {
            Goods[] newGoodsToChange = gdsDB.getNewGoodsArray();
            for (int i = 0; i < newGoodsToChange.length; i++)
            {
                gdsDB.changeGoodsChanged(newGoodsToChange[i].Name);
            }
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}