package com.hemw.jdbc.utils;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;

/**
 * 将 CLOB 类型转换为 String 类型的工具类
 * @author Carl He
 *
 */
public class LobTool {
	// 将字CLOB转成STRING类型
	public static String ClobToString(Clob clob) {
		String reString = "";
		StringBuffer sb = new StringBuffer();
		if (clob != null){
			Reader is = null;
			BufferedReader br = null;
			try {
				is = clob.getCharacterStream();// 得到流
				br = new BufferedReader(is);
				// String s = br.readLine();
				// System.out.println();

				char[] charBuffer = new char[512];
				int length = 0;
				while ((length = br.read(charBuffer)) != -1) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
					sb.append(charBuffer, 0, length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		reString = sb.toString();
		return reString;
	}

	public static String ClobToString(Object clob) {
		String value = (String) clob;
		return value;
	}
}
