package com.hemw.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Java 抓取网页内容
 * <br><b>创建日期</b>：2011-3-2
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class FetchHtml {
	/**
	 * 最简单的抓取网页内容的方法<br>
	 * 这种方法抓取一般的网页应该没有问题，但当有些网页中存在一些嵌套的redirect连接时，
	 * 它就会报 <code>Server redirected too many times<code> 这样的错误，
	 * 这是因为此网页内部又有一些代码是转向其它网页的，循环过多导致程序出错。 <br>
	 * <b>创建日期</b>：2011-1-20
	 * @param urlStr 网页的 URL
	 * @param charset 网页编码
	 * @return
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
    public String getMethod(String urlStr, String charset) {
        StringBuffer html = new StringBuffer();
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            reader = new BufferedReader(new InputStreamReader(url.openStream(), charset));
            String line = null;
            while ((line = reader.readLine()) != null) {
                html.append(line).append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                }
        }
        return html.toString();
    }
	
	/**
	 * 抓取网页内容，当有些网页中存在一些嵌套的redirect连接时，此方法只会抓取指定 URL 对应网页中的内容，而不是抓取跳转后的网页中的内容
	 * <br><b>创建日期</b>：2011-1-20
	 * @param urlStr
	 * @param charset
	 * @return
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public String getMethod2(String urlStr, String charset) {
		StringBuffer html = new StringBuffer();
		BufferedReader reader = null;
		try {
//			HttpURLConnection.setFollowRedirects(true);	//默认值为 true
//			System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
//        	System.setProperty("sun.net.client.defaultReadTimeout", "5000");
//			 如果是我们是处于内部网的话，还需要专门给它加上代理, Java以特殊的系统属性为代理服务器提供支持
//			System.setProperty("http.proxyHost", proxyHost);
//			System.setProperty("http.proxyPort", proxyPort);
		    
            HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
            con.setInstanceFollowRedirects(false);
            con.connect();

            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append("\r\n");
            }
            con.disconnect(); // 释放连接
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                }
        }
        return html.toString();
	}
	
	/**
	 * POST 提交数据，并返回响应数据
	 * @param urlStr 要请求的 URL
	 * @param charset 请求编码
	 * @return
	 * @throws IOException 
	 */
    public String postMethod(String urlStr, String charset) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("Content-Type", "text/html; charset=" + charset); // 设置请求头
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("POST"); // 使用 POST 方法
        con.setConnectTimeout(50000); // 设置获取连接超时时间（毫秒）
        con.setReadTimeout(50000); // 设置接收响应数据的超时时间（毫秒）
        con.connect(); // 获取连接
        
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        try {
            out = new OutputStreamWriter(con.getOutputStream(), charset);
            // POST 提交数据
            out.write(String.format("username=%s&passwd=%s", "hemw", "pwd"));
            // 或者
            // out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><username>hemw</user><password>pwd</password></root>");
            out.flush();

            StringBuffer responseString = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                responseString.append(line).append("\r\n");
            }
            return responseString.toString();
        } finally {
            if (out != null)
                out.close();
            if (reader != null)
                reader.close();
            con.disconnect(); // 释放连接
        }
    }

}
