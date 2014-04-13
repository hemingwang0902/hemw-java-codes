package com.hemw.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 设置禁用站点（黑名单）的过滤器
 * <p>
 * 不允许从禁用的站点（IP）访问当前应用，也不允许从禁用的站点链接到当前应用。<br>
 * 为了简单起见，设置禁用站点时，暂不支持使用通配符。只是抛砖引玉了。<br>
 * 比如：禁止其他的网站引用本站的图片资源，只需在此基础上稍作修改即可。
 * </p>
 * <pre>
 * web.xml
 * &lt;filter&gt;
 * 	&lt;filter-name&gt;BannedAccessFilter&lt;/filter-name&gt;
 * 	&lt;filter-class&gt;com.hmw.filter.BannedAccessFilter&lt;/filter-class&gt;
 * 	&lt;init-param&gt;
 * 		 &lt;param-name&gt;bannedSites&lt;/param-name&gt;
 * 		&lt;param-value&gt;
 * 			192.168.1.21
 * 			www.baidu.com
 * 			www.cnblogs.com
 * 		&lt;/param-value&gt;
 * 	&lt;/init-param&gt;
 * &lt;/filter&gt;
 * 
 * &lt;filter-mapping&gt;
 * 	&lt;filter-name&gt;BannedAccessFilter&lt;/filter-name&gt;
 * 	&lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 * 
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class BannedAccessFilter implements Filter {
    static final Logger logger = Logger.getLogger(BannedAccessFilter.class);
    
	private HashSet<String> bannedSiteTable;
	
	/**
	 * 将配置的禁用站点列表初始化到一个 HashSet 中
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		bannedSiteTable = new HashSet<String>();
		String bannedSites = config.getInitParameter("bannedSites");
		// Default token set: white space.
		StringTokenizer tok = new StringTokenizer(bannedSites);
		while (tok.hasMoreTokens()) {
			String bannedSite = tok.nextToken();
			bannedSiteTable.add(bannedSite);
			logger.info("Banned " + bannedSite);
		}
	}
	
	/**
	 * 如果请求来自被禁用的站点，或是从被禁用的站点链接过来的，则拒绝访问。
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		logger.debug("BannedAccessFilter: Filtering the Request...");
		
		HttpServletRequest req = (HttpServletRequest) request;
		String requestingHost = req.getRemoteHost();
		String referringHost = getReferringHost(req.getHeader("Referer"));
		
		String bannedSite = null;
		boolean isBanned = false;
		if (bannedSiteTable.contains(requestingHost)) {
			bannedSite = requestingHost;
			isBanned = true;
		} else if (bannedSiteTable.contains(referringHost)) {
			bannedSite = referringHost;
			isBanned = true;
		}
		
		if (isBanned) {
			showWarning(response, bannedSite);
		} else {
			chain.doFilter(request, response);
		}
		
		logger.debug("BannedAccessFilter: Filtering the Response...");
	}

	@Override
	public void destroy() {
	}

	/**
	 * 根据 URL 链接地址，取得该链接地址所在的站点
	 * @param refererringURLString URL链接地址
	 * @return 该 URL 链接地址所在的站点，如果传入的参数不是一个符合URL规范的字符串，则返回 <code>null</code>
	 */
	private String getReferringHost(String refererringURLString) {
	    if(StringUtils.isBlank(refererringURLString))
	        return null;
	    
		try {
			URL referringURL = new URL(refererringURLString);
			return referringURL.getHost();
		} catch (MalformedURLException mue) { // Malformed
			return null;
		}
	}

	/**
	 * 如果用户是从禁用站点访问的该应用，或是从禁用站点链接过来的，则调用此方法将警告信息展现给用户。
	 * @param response HTTP请求响应对象
	 * @param bannedSite 禁止的站点
	 * @throws ServletException
	 * @throws IOException
	 * @author <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
	 */
	private void showWarning(ServletResponse response, String bannedSite) throws ServletException, IOException {
	    String htmlCode  = "";
	    htmlCode += "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
	    htmlCode += "<html xmlns=\"http://www.w3.org/1999/xhtml\">";
	    htmlCode += "  <head>";
	    htmlCode += "      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />";
	    htmlCode += "      <title>禁止访问</title>";
	    htmlCode += "  </head>";
	    htmlCode += "  <body>";
	    htmlCode += "      <h1>禁止访问</h1>";
	    htmlCode += "      <p>对不起，您无法访问该资源，因为您的站点已经被列入我们的黑名单！</p>";
	    htmlCode += "      <p>您的站点是：<strong>" + bannedSite + "</strong></p>";
	    htmlCode += "  </body>";
	    htmlCode += "</html>";

		response.setContentType("text/html");
		PrintWriter out = null;
		try{
		    out = response.getWriter();
    		out.println(htmlCode);
    	}finally{
    	    if(out != null){
    	        out.flush();
    	        out.close();
    	    }
    	}
    	
    	/*
    	 * 也可以使用下面的方法直接转发或重定向到指定的警告页面
    	 * 转发：
    	 *     ((HttpServletRequest)request).getRequestDispatcher("/warn.html").forward(request, response);
    	 * 重定向：
    	 *     ((HttpServletResponse)response).sendRedirect("webAppContext/warn.html");
    	 */
	}
}