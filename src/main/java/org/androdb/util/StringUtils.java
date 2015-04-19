package org.androdb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author martin.s.schumacher
 * 
 */
public class StringUtils {
  public static boolean isEmpty(String str) {
    return (str == null) || (str.trim().length() == 0);
  }

  public static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return sb.toString();
  }

  public static List<String> readLines(InputStream stream) throws IOException {
    return readLines(new InputStreamReader(stream));
  }

  public static List<String> readLines(Reader input) throws IOException {
    BufferedReader reader = new BufferedReader(input);
    List<String> list = new ArrayList<String>();
    String line = reader.readLine();
    while (line != null) {
      list.add(line);
      line = reader.readLine();
    }
    return list;
  }

  public static String removeLastChar(String str) {
    return str.substring(0, str.length() - 1);
  }

  public static String firstCharToUpper(String str) {
    String firstChar = str.substring(0, 1);
    String rest = str.substring(1);
    str = firstChar.toUpperCase() + rest;

    return str;
  }
}