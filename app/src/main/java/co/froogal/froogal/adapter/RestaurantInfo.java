package co.froogal.froogal.adapter;

/**
 * Created by akhil on 26/5/15.
 */
public class RestaurantInfo {

    public String resID;
    public String resName;
    public String resAddress;
    public String rating;
    public  String phoneNumber;
    public String distance;
    public String coupon;
    public String reward;
    public String latitude;
    public String longitude;


    public static final String RES_NAME = "Restaurant_";
    public static final String ADDRESS = "Address_";

    public boolean checkedIN = false;
    public String isFav = "0";
}
