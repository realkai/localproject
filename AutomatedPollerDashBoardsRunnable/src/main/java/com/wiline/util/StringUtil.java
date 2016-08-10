package com.wiline.util;

public class StringUtil {

    public static boolean isEmpty(String string) {
	if ("".equals(string) || string == null)
	    return true;
	return false;
    }

}
