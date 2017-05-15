package com.android.charan.shareride.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Utils {

	public static long convertDateToString(Date date) {

		SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
		return Long.parseLong(df.format(date));
	}

	public static Date convertStringToDate(Long date) {
		SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
		try {
			return df.parse(date.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object JSONToObject(String JSON, Class objectType) {
		Gson gson = new Gson();
		return gson.fromJson(JSON, objectType);
	}

	public static List<Object> JSONToObjectList(String JSON, Class objectType) {

		List<Object> list = new ArrayList<Object>();

		StringBuilder str = new StringBuilder(JSON);

		if(str.indexOf("[") != -1) {
			int startIndex = str.indexOf("[");
			JSON = str.substring(startIndex, JSON.length()-1);

			JsonElement json = new JsonParser().parse(JSON); 
			JsonArray array= json.getAsJsonArray();

			Iterator iterator = array.iterator();
			while(iterator.hasNext()){
				JsonElement json2 = (JsonElement)iterator.next();
				Gson gson = new Gson();
				list.add(gson.fromJson(json2, objectType));
			}
		} else {
			int startIndex = str.indexOf(":");

			JSON = str.substring(startIndex+1, JSON.length()-1);

			list.add(JSONToObject(JSON, objectType));
		}
		return list;
	}

//	This works while working on local host
//	public static List<Object> JSONToObjectList(String JSON, Class objectType) {
//
//		List<Object> list = new ArrayList<Object>();
//		
//		JsonElement json = new JsonParser().parse(JSON); 
//		JsonArray array= json.getAsJsonArray();
//
//		Iterator iterator = array.iterator();
//		while(iterator.hasNext()){
//			JsonElement json2 = (JsonElement)iterator.next();
//			Gson gson = new Gson();
//			list.add(gson.fromJson(json2, objectType));
//		}
//
//		return list;
//	}
}