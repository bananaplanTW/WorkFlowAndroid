package com.bananaplan.workflowandroid.data.loading;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by daz on 10/9/15.
 */
public class URLUtils {
    public static String buildURLString(String baseURL, String endPoint, HashMap<String, String> queries) {
        String queryString = "";
        Iterator iter = queries.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            queryString += entry.getKey() + "=" + entry.getValue();
            queryString += "&";
        }
        if (queryString.length() > 0) {
            return baseURL + endPoint + "?" + queryString.substring(0, queryString.length() - 1);
        } else {
            return baseURL + endPoint;
        }
    }
}
