package com.uspaun.coffee;

/**
 * Created by root on 16.07.15.
 */
public class Seller extends Worker {
    private float SalaryCoef = Float.parseFloat("0.15");
    Goods gdsSell = new Goods();

    public void setSalaryCoef(float Coefficient)
    {
        this.SalaryCoef = Coefficient;
    }

    public float getSalaryCoef()
    {
        return this.SalaryCoef;
    }

    public void viewSoldGoods()
    {
        Goods[] gdsArr = gdsSell.getSoldGoodsArray();
        for(int i  =0; i < gdsArr.length; i++)
        {

        }
    }
}
