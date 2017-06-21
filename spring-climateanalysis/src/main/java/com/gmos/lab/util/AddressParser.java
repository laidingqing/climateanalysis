package com.gmos.lab.util;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddressParser {
    public static String getCountry(String json){
        JSONObject obj = new JSONObject(json);
        JSONArray rets = obj.getJSONArray("results");
        for (int i = 0; i < rets.length(); i++) {
            JSONObject address = rets.getJSONObject(i);
            JSONArray addressTypes = address.getJSONArray("types");
            for (int j = 0; j < addressTypes.length(); j++) {
                if (addressTypes.getString(j).equalsIgnoreCase("country")) {
                    JSONArray addressComponents = address.getJSONArray("address_components");
                    for (int k = 0; k < addressComponents.length(); k++) {
                        JSONObject country = addressComponents.getJSONObject(k);
                        JSONArray countries = country.getJSONArray("types");
                        if (countries.getString(j).equalsIgnoreCase("country")) {
                            return (String) country.get("long_name");
                        }
                    }
                }
            }
        }
        return "";
    }
}
