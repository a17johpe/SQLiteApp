package org.brohede.marcus.sqliteapp;

/**
 * Created by marcus on 2018-04-25.
 */

public class Mountain {

    // You need to create proper member variables, methods, and constructors

    // These member variables should be used
    // location
    // height
    // img_url
    // info_url
    private String name;
    private String location;
    private int height;
    private String imgUrl;
    private String infoUrl;

    public Mountain (String inName, String inLocation, int inHeight, String inImgUrl, String inInfoUrl) {
        name = inName;
        location = inLocation;
        height = inHeight;
        imgUrl = inImgUrl;
        infoUrl = inInfoUrl;
    }

    public Mountain (String inName, String inLocation, int inHeight) {
        name = inName;
        location = inLocation;
        height = inHeight;
    }

    @Override
    public String toString() {
        return name;
    }

    public String info() {
        String str = "Name: " + name + "\n" + "Location: " + location + "\n" + "Height: " + height + " m";
        return str;
    }
}
