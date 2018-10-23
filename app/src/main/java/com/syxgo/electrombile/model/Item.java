package com.syxgo.electrombile.model;

import java.io.Serializable;

/**
 * Created by ruichengrui on 2017/5/29.
 */

public class Item implements Serializable {
    private int id;
    private String name;
    private int imgurl;

    public Item(int id, String name, int imgurl){
        this.id = id;
        this.name = name;
        this.imgurl = imgurl;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setImgurl(int imgurl){
        this.imgurl = imgurl;
    }
    public int getImgurl(){
        return this.imgurl;
    }

}