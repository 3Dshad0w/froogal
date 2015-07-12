package co.froogal.froogal.library;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;


public class UserFunctions {

    private static final String TAG = "UserFunctions";
    private JSONParser jsonParser;


    //URL of the PHP API
    private static String loginURL = "http://froogal.in/files/login.php";
    private static String registerURL = "http://froogal.in/files/signup.php";
    private static String forpassURL = "http://froogal.in/files/forgotpass.php";
    private static String chgpassURL = "http://froogal.in/files/changepass.php";
    private static String gcm_testingURL = "http://ec2-52-10-172-112.us-west-2.compute.amazonaws.com";
    private static String save_token_to_server_URL = "http://froogal.in/files/save_token_to_server.php";
    private static String save_location_to_server_URL = "http://froogal.in/files/save_location_to_server.php";
    private static String save_google_user_data_to_server_URL = "http://froogal.in/files/save_google_user_data_to_server.php";
    private static String save_facebook_user_data_to_server_URL = "http://froogal.in/files/save_facebook_user_data_to_server.php";
    private static String send_mobile_verification_status_URL = "http://froogal.in/files/send_mobile_verification_status.php";
    private static String recharge_URL = "http://froogal.in/files/rechargemobile.php";
    private static String bank_URL = "http://froogal.in/files/bank.php";
    private static String shopex_URL = "http://froogal.in/files/shopex.php";




    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String forpass_tag = "forpass";
    private static String chgpass_tag = "chgpass";


    // constructor
    public UserFunctions() {
        jsonParser = new JSONParser();
    }

    /**
     * Function to save mobile and its status
     */

    public JSONObject send_mobile_verification_status(String uid, String number, String status) {

        // Building Parameters
        Log.d(TAG, "send_mobile");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID", uid));
        params.add(new BasicNameValuePair("mobile", number));
        params.add(new BasicNameValuePair("mobile_status", status));
        JSONObject json = jsonParser.makeHttpRequest(send_mobile_verification_status_URL, "POST", params);
        Log.d(TAG, "Json Response MobileVerfi: " + json);
        Log.d("vah", params.toString());
        return json;
    }

    /**
     * Function to send otp
     */

    public JSONObject send_otp(String otp, String number, String name) {

        // Building Parameters
        Log.d(TAG, "send_otp");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "mg1.froogal"));
        params.add(new BasicNameValuePair("password", "624557932"));
        params.add(new BasicNameValuePair("sendername", "FRUGAL"));
        params.add(new BasicNameValuePair("mobileno", number));
        params.add(new BasicNameValuePair("message", "Dear " + name + " your OTP code is " + otp + ".Thanks for joining Froogal."));
        String url = "http://bulksms.mysmsmantra.com:8080/WebSMS/SMSAPI.jsp";
        JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);
        Log.d(TAG, "Json Response : " + json);
        return json;
    }

    /**
     * Function to store facebook user data to server
     */

    public JSONObject save_facebook_user_data_to_server(String id, String gcm_token, String first_name, String last_name, String image_url, String email, String ip_address, String imei, String registered_through, String latitude, String longitude, String registered_at, String birthday) {
        // Building Parameters
        Log.d(TAG, "save_facebook_user_data_to_server");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("fname", first_name));
        params.add(new BasicNameValuePair("lname", last_name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("ip_address", ip_address));
        params.add(new BasicNameValuePair("imei", imei));
        params.add(new BasicNameValuePair("birthday", birthday));
        params.add(new BasicNameValuePair("longitude", longitude));
        params.add(new BasicNameValuePair("latitude", latitude));
        params.add(new BasicNameValuePair("registered_through", registered_through));
        params.add(new BasicNameValuePair("registered_at", registered_at));
        params.add(new BasicNameValuePair("image_url", image_url));
        params.add(new BasicNameValuePair("gcm_token", gcm_token));
        params.add(new BasicNameValuePair("special_id", id));
        Log.d(TAG, "Params : " + params.toString());
        JSONObject json = jsonParser.makeHttpRequest(save_facebook_user_data_to_server_URL, "POST", params);
        Log.d(TAG, "Json Response : " + json);
        return json;
    }

    /**
     * Function to store google+ user data to server
     */

    public JSONObject save_google_user_data_to_server(String gcm_token, String first_name, String last_name, String image_url, String email, String ip_address, String imei, String registered_through, String latitude, String longitude, String registered_at, String birthday, String id) {
        // Building Parameters
        Log.d(TAG, "save_google_user_data_to_server");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("fname", first_name));
        params.add(new BasicNameValuePair("lname", last_name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("ip_address", ip_address));
        params.add(new BasicNameValuePair("imei", imei));
        params.add(new BasicNameValuePair("birthday", birthday));
        params.add(new BasicNameValuePair("longitude", longitude));
        params.add(new BasicNameValuePair("latitude", latitude));
        params.add(new BasicNameValuePair("registered_through", registered_through));
        params.add(new BasicNameValuePair("registered_at", registered_at));
        params.add(new BasicNameValuePair("image_url", image_url));
        params.add(new BasicNameValuePair("gcm_token", gcm_token));
        params.add(new BasicNameValuePair("special_id", id));
        Log.d(TAG, "Params : " + params.toString());
        JSONObject json = jsonParser.makeHttpRequest(save_google_user_data_to_server_URL, "POST", params);
        Log.d(TAG, "Json Response : " + json);
        return json;
    }

    /**
     * Function to  Register
     */
    public JSONObject registerUser(String fname, String lname, String email, String password, String registered_at, String registered_through, String imei, String ip_address, String image_url, String longitude, String latitude, String gcm_token) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("lname", lname));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("ip_address", ip_address));
        params.add(new BasicNameValuePair("imei", imei));
        params.add(new BasicNameValuePair("longitude", longitude));
        params.add(new BasicNameValuePair("latitude", latitude));
        params.add(new BasicNameValuePair("registered_through", registered_through));
        params.add(new BasicNameValuePair("registered_at", registered_at));
        params.add(new BasicNameValuePair("image_url", image_url));
        params.add(new BasicNameValuePair("gcm_token", gcm_token));
        params.add(new BasicNameValuePair("special_id", null));
        JSONObject json = jsonParser.makeHttpRequest(registerURL, "POST", params);
        Log.d("infunctionreg", json.toString());
        return json;
    }


    /**
     * Function to test gcm
     */

    public JSONObject gcmtest(String alpha) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("alpha", alpha));
        JSONObject json = jsonParser.makeHttpRequest(gcm_testingURL, "POST", params);
        return json;
    }

    /**
     * Function to Login
     */

    // TODO add gcm_token into server after login
    public JSONObject loginUser(String email, String password, String gcm_token) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("gcm_token", gcm_token));
        JSONObject json = jsonParser.makeHttpRequest(loginURL, "POST", params);
        Log.d("infunction", json.toString());
        return json;
    }

    /**
     * Function to change password
     */

    public JSONObject chgPass(String oldpass, String newpass, String uid) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", chgpass_tag));

        params.add(new BasicNameValuePair("newpass", newpass));
        params.add(new BasicNameValuePair("userID", uid));
        params.add(new BasicNameValuePair("oldpass", oldpass));

        JSONObject json = jsonParser.makeHttpRequest(chgpassURL, "POST", params);
        Log.d("inchgpassfunction", json.toString());
        return json;
    }


    /**
     * Function to reset the password
     */

    public JSONObject forPass(String email) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", forpass_tag));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.makeHttpRequest(forpassURL, "POST", params);
        Log.d("inforpassfunction", json.toString());
        return json;
    }


    public JSONObject getMenu(String s) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("resid", s));
        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/getmenu.php", "GET", params);
        Log.d("infunctionregMENU", json.toString());
        return json;

    }

    public JSONObject getRestaurants(String latitude, String longitude, String uid) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("longitude", longitude));
        params.add(new BasicNameValuePair("latitude", latitude));
        params.add(new BasicNameValuePair("userID", uid));

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/getRestaurants.php", "GET", params);
        Log.d("infunctionregRESTAURANT", json.toString());
        return json;

    }

    public JSONObject getReviews(String s, String uid) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("resID", s));
        params.add(new BasicNameValuePair("userID", uid));

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/getReviews.php", "GET", params);
        Log.d("infunctionregREVIEW", json.toString());
        return json;

    }


    public JSONObject addReview(String userID, String resID, String rating, String title, String description) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", userID));
        params.add(new BasicNameValuePair("resID", resID));
        params.add(new BasicNameValuePair("rating", rating));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("description", description));

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/addReview.php", "POST", params);
        Log.d("infunctionregREVIEW", json.toString());
        return json;

    }

    public JSONObject processOrder(String userID, String resID, Map<String, String> products) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", userID));
        params.add(new BasicNameValuePair("resID", resID));

        JSONArray JSONProducts = new JSONArray();

        for (Map.Entry<String, String> entry : products.entrySet()) {
            JSONObject object = new JSONObject();
            try {
                object.put("product_id", entry.getKey());
                object.put("quantity", entry.getValue());
                JSONProducts.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        params.add(new BasicNameValuePair("products", JSONProducts.toString()));

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/processOrder.php", "POST", params);
        Log.d("GAGAGAGAG", json.toString());

        return json;
    }

    public JSONObject processCloseOrder(String userID, String resID) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", userID));
        params.add(new BasicNameValuePair("resID", resID));

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/closeOrder.php", "POST", params);
        Log.d("GAGAGAGAG", json.toString());

        return json;

    }

    public JSONObject getProcessOrder(String userID, String resID) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", userID));
        params.add(new BasicNameValuePair("resID", resID));

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/getProcessOrder.php", "GET", params);
        Log.d("GAGAGAGAG123", json.toString());

        return json;

    }

    public JSONObject updateReview(String userID, String resID, String rating, String title, String description) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", userID));
        params.add(new BasicNameValuePair("resID", resID));
        params.add(new BasicNameValuePair("rating", rating));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("description", description));

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/updateReview.php", "POST", params);
        Log.d("infunctionregREVIEW22", json.toString());
        return json;
    }

    public JSONObject getPopRestaurants(String latitude, String longitude, String uid) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("longitude", longitude));
        params.add(new BasicNameValuePair("latitude", latitude));
        params.add(new BasicNameValuePair("userID", uid));
        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/getPopRestaurants.php", "GET", params);
        Log.d("infunctionregREAURANT2", json.toString());
        return json;
    }

    public JSONObject getFavRestaurants(String uid) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", uid));
        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/getFavRestaurants.php", "GET", params);
        Log.d("infunctionregRRANT1", json.toString());
        return json;
    }


    public JSONObject get_menu_dine_in(String latitude, String longitude, String value) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("latitude", latitude));
        params.add(new BasicNameValuePair("longitude", longitude));
        params.add(new BasicNameValuePair("resID", value));
        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/get_menu_dine_in.php", "POST", params);
        Log.d("infunctionregREVIEW", json.toString());
        return json;
    }

    public JSONObject getRewardsList(String uid) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", uid));
        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/getRewardsList.php", "GET", params);
        Log.d("infunctionregREAURANT2", json.toString());
        return json;
    }

    public JSONObject processFav(String isFav, String uid, String resID) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", uid));
        params.add(new BasicNameValuePair("isFav", isFav));
        params.add(new BasicNameValuePair("resID", resID));
        Log.d("isFav", "Params : " + params.toString());

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/processFav.php", "POST", params);
        Log.d("infunctionregREAURANT2", json.toString());
        return json;
    }

    public JSONObject recharge(String userID,String number, String operator, String amount) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userID",userID));
        params.add(new BasicNameValuePair("operator", operator));
        params.add(new BasicNameValuePair("number", number));
        params.add(new BasicNameValuePair("amount", amount));
        JSONObject json = jsonParser.makeHttpRequest(recharge_URL, "POST", params);
        return json;
    }

    public JSONObject updateUser(String uid, String fname, String lname, String email) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", uid));
        params.add(new BasicNameValuePair("lname", lname));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("email", email));

        Log.d("ookok", "Params : " + params.toString());

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/updateUser.php", "POST", params);
        Log.d("infunctionregREAURANT2", json.toString());
        return json;
    }

    public JSONObject invalidateMobile(String uid, String mobile) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", uid));
        params.add(new BasicNameValuePair("mobile", mobile));
        Log.d("ookok", "Params : " + params.toString());

        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/invalidateMobile.php", "POST", params);
        Log.d("infunctionregREAURANT2", json.toString());
        return json;
    }

    public JSONObject bank(String account_name, String account_number, String bank_name,String ifsc_code,String number,String amount) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("account_name", account_name));
        params.add(new BasicNameValuePair("account_number", account_number));
        params.add(new BasicNameValuePair("bank_name", bank_name));
        params.add(new BasicNameValuePair("ifsc_code", ifsc_code));
        params.add(new BasicNameValuePair("number", number));
        params.add(new BasicNameValuePair("amount", amount));
        JSONObject json = jsonParser.makeHttpRequest(bank_URL, "POST", params);
        return json;

    }

    public JSONObject shopex(String uid,String amount) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("uid", uid));
        params.add(new BasicNameValuePair("amount", amount));
        JSONObject json = jsonParser.makeHttpRequest(shopex_URL, "POST", params);
        return json;
    }

    public JSONObject getRewardsSpentList(String uid) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("userID", uid));
        JSONObject json = jsonParser.makeHttpRequest("http://froogal.in/files/getRewardsSpentList.php", "GET", params);
        Log.d("infunctionregREAURANT2", json.toString());
        return json;
    }
}
