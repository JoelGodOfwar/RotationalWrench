package com.github.joelgodofwar.rw.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class StrUtils {
	/** StringRight */
	public static String Right(String input, int chars){
		if (input.length() > chars){
			//System.out.println("Right input=" + input);
			return input.substring(input.length() - chars);
		} 
		else{
			return input;
		}
	}
	
	/** StringLeft */
	public static String Left(String input, int chars){
		if (input.length() > chars){
			//System.out.println("Left input=" + input);
			//System.out.println("Left chars=" + chars);
			return input.substring(0, chars);
		} 
		else{
			return input;
		}
	}
	
	public  static boolean stringContains(String string, String string2){
		String[] string3 = string.split(", ");
		for(int i = 0; i < string3.length; i++){
			if(string3[i].equals(string2)){
				return true;
			}
		}
		return false;
	}
	
	// §x§1§1§1§1§1§1D§x§2§2§2§2§2§2e§x§3§3§3§3§3§3M§x§4§4§4§4§4§4o§x§5§5§5§5§5§5N
	public static String parseRGBNameColors(String string){
		String nickPrefix = "\"}";
		String nickString = ",{\"text\":\"<text>\",\"color\":\"<color>\"}";
		String nickSuffix = ",{\"text\":\"";
		//String[] string2 = null;
		List<String> string2 = new ArrayList<String>();
		String string3 = "";
		String string4 = "";
		String color;
		String text;       // #FF00FF
		//int counter = 0; // SxSFSFS0S0SFSF
		int index;
		if(string.contains("§x")){
			int count = StringUtils.countMatches(string, "§x");
			System.out.println("count=" + count);
			for(int i = 0; i < count; i++){//while(string.length() > 0){
				index = string.indexOf("§x");
				System.out.println("i=" + i);
				System.out.println("index=" + index);
				System.out.println("index2=" + string.indexOf("§x", index + 1));
				int index2 = string.indexOf("§x", index + 1);
				if(index2 <= 1){
					string2.add(Left(string, string.length()));
				}else{
					string2.add(Left(string, index2)); // §x§1§1§1§1§1§1D
				}
				string = string.replace(string2.get(i), "");
			}
			for(int i = 0; i < count; i++){
				string4 = string2.get(i).replace("§x", "#");
				string4 = string4.replace("§", "");
				System.out.println("String4=" + string4);
			    color = Left(string4, 7).replace("§x", "#").replace("§", "");
			    System.out.println("color=" + color);
			    text = Right(string4, string4.length() - 7);
			    System.out.println("text=" + text);
			    string3 = string3 + nickString.replace("<color>", color).replace("<text>", text);
			}
			return nickPrefix + string3 + nickSuffix;
		}
		return string;
	}
	
	public static String stripRGBColors(String string){
		List<String> string2 = new ArrayList<String>();
		String string3 = "";
		String string4 = "";
		//String color;
		String text;       // #FF00FF
		//int counter = 0; // SxSFSFS0S0SFSF
		int index;
		if(string.contains("§x")){
			int count = StringUtils.countMatches(string, "§x");
			System.out.println("count=" + count);
			for(int i = 0; i < count; i++){//while(string.length() > 0){
				index = string.indexOf("§x");
				System.out.println("i=" + i);
				System.out.println("index=" + index);
				System.out.println("index2=" + string.indexOf("§x", index + 1));
				int index2 = string.indexOf("§x", index + 1);
				if(index2 <= 1){
					string2.add(Left(string, string.length()));
				}else{
					string2.add(Left(string, index2)); // §x§1§1§1§1§1§1D
				}
				string = string.replace(string2.get(i), "");
			}
			for(int i = 0; i < count; i++){
				string4 = string2.get(i).replace("§x", "#");
				string4 = string4.replace("§", "");
				System.out.println("String4=" + string4);
			    //color = Left(string4, 7).replace("§x", "#").replace("§", "");
			    //System.out.println("color=" + color);
			    text = Right(string4, string4.length() - 7);
			    System.out.println("text=" + text);
			    string3 = string3 + text;
			}
			return string3;
		}
		return string;
	}
}
