package com.hemw.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于检测用户是否登陆（资源保护）的过滤器，如果未登录，则重定向到指的登录页面<br/>
 * 
 * <pre>
 * 配置参数： 
 * <b>checkSessionKey</b> 需检查的在 Session 中保存的关键字，如果没有配置此参数，则此过滤器不起作用
 * <b>redirectUrl</b> 如果用户未登录，则重定向到指定的页面，Url不包括 ContextPath,  默认：/login.jsp
 * <b>restrictedResourceList</b> 受访问限制的Url列表，以英文逗号或分号(, or ;)分开，并且 Url 中不包括 ContextPath
 * 
 * 在web.xml文件中添加如下配置信息：
 * &lt;!—用户是否登陆过滤器配置 --&gt;
 * &lt;filter&gt;
 * 	&lt;filter-name&gt;SecurityFilter&lt;/filter-name&gt;
 * 	&lt;filter-class&gt;com.hmw.filter.SecurityFilter&lt;/filter-class&gt;
 * 	&lt;init-param&gt;
 * 		&lt;param-name&gt;checkSessionKey&lt;/param-name&gt;
 * 		&lt;param-value&gt;loginInfo&lt;/param-value&gt;
 * 	&lt;/init-param&gt;
 * 	&lt;init-param&gt;
 * 		&lt;param-name&gt;redirectUrl&lt;/param-name&gt;
 * 		&lt;param-value&gt;/login.jsp&lt;/param-value&gt;
 * 	&lt;/init-param&gt;
 * 	&lt;init-param&gt;
 * 		&lt;param-name&gt;restrictedResourceList&lt;/param-name&gt;
 * 		&lt;param-value&gt;/security.jsp;/user/home.do&lt;/param-value&gt;
 * 	&lt;/init-param&gt;
 * &lt;/filter&gt;
 * 
 * &lt;filter-mapping&gt;
 * 	&lt;filter-name&gt;SecurityFilter&lt;/filter-name&gt;
 * 	&lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * &lt;!—用户是否登陆过滤器配置结束 --&gt;
 * </pre>
 * 
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class SecurityFilter implements Filter {
	private String checkSessionKey;
	// the login page uri
	private String redirectUrl;
	// a list of restricted resources
	private List<String> restrictedResourceList;

	public void init(FilterConfig filterConfig) throws ServletException {
		checkSessionKey = filterConfig.getInitParameter("checkSessionKey");
		redirectUrl = filterConfig.getInitParameter("redirectUrl");
		if (redirectUrl == null) {
			redirectUrl = "/login.jsp";
		}
		String restrictedResourceListStr = filterConfig.getInitParameter("restrictedResourceList");
		if (restrictedResourceListStr != null) {
			restrictedResourceList = Arrays.asList(restrictedResourceListStr.split(",|;"));
		} else {
			restrictedResourceList = new ArrayList<String>();
		}
	}

	public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
		String contextPath = request.getContextPath();
		String requestUri = request.getRequestURI();
		System.out.println("contextPath = " + contextPath + "\nrequestUri = " + requestUri);

		if (restrictedResourceList.contains(requestUri) && checkSessionKey != null && request.getSession().getAttribute(checkSessionKey) == null) {
			System.out.println("没有权限访问此资源：");
			// request.getRequestDispatcher(redirectUrl).forward(req, res);
			response.sendRedirect(contextPath + redirectUrl);
			return;
		}
		chain.doFilter(req, rep);
	}

	public void destroy() {
	}

}
