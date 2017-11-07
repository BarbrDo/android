package com.barbrdo.app.managers;

import com.barbrdo.app.dataobject.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.List;



/**
 * Created by Anurag Sethi
 * The class is used for parsing the data received from the web service
 */
public class DataParser {


    /**
     * This method will parse the JSON String
     * @since 2014-08-20
     * @param data output received from the web service
     * @param parseDataFor parameter to differentiate, value of which key needs to be fetched
     * @return parsed String based on the key for which data needs to be fetched
     * @exception JSONException
     */

    public String parseJsonString(String data, int parseDataFor) {
        String parsedString = "";

        try {
            JSONObject jsonObj = new JSONObject(data);
            switch(parseDataFor) {
                case 1: //return messageId key data
                    parsedString = Integer.toString(jsonObj.getInt("messageId"));
                    break;
                case 2://return result key data
                    parsedString = jsonObj.getString("result");
                    break;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return parsedString;
    }



    /**
     * The method will be used to parse the data received from the web service
     * and will return the object of the required type
     *
     * @param jsonString the string data received from the web service
     * @param objName is the name of the dataobject for which the data is parsed     *
     * @return dataObject of the required type e.g. User class
     */
    public Object parseDataForObject(String jsonString, String objName) {
        Gson gson = new Gson();
      
        if(objName.equalsIgnoreCase("user")) {
            return gson.fromJson(jsonString, User.class);
        }
        
        
        return null;
    }
    
    /**
     * The method will be used to parse the data received from the web service
     * where the data return is in the form of jsonArray
     *
     * @param jsonStringArray the string data received from the web service in the form of jsonArray
     * @param typeOfDataList is the type of arrayList for which the data is parsed
     *
     * @return List of the parsed data
     */
    public List parseJsonArray(String jsonStringArray, Type typeOfDataList) {
        Gson gson = new Gson();

        return gson.fromJson(jsonStringArray, typeOfDataList);
    }
}
