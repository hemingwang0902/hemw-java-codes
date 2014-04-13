package com.hemw.web.filter;


import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * 用于检查用户是否登录了系统的过滤器<br>
 * 创建日期：2012-01-09
 * <p>
 * 过滤器参数（在 web.xml 中进行配置）： 
 * <ul>
 *  <li>
 *      <strong>sessionKey</strong>
 *      将当前登录的用户的信息保存在 session 中时使用的key，如果没有配置此参数，则该过滤器不起作用
 *  </li>
 *  <li>
 *      <strong>redirectUrl</strong>
 *      如果用户未登录（即在 session 中 key 为 sessionKey 的属性不存在或为空），则将请求重定向到该 url。
 *      该 url 不包含web应用的 ContextPath。
 *      如果不配置此参数，则在用户未登录系统的情况下，直接重定向到web应用的根路径（/）
 *  </li>
 *  <li>
 *      <strong>excepUrlRegex</strong>
 *      不需要进行拦截的 url 的正则表达式，即：如果当前请求的 url 的 servletPath 能匹配该正则表达式，则直接放行（即使未登录系统）。
 *      此参数的值一般为 loginServlet 和 registServlet 等。
 *      另外，参数 redirectUrl 的值不用包含在该正则表达式中，因为 redirectUrl 对应的 url 会被自动放行。
 *      还有一点需要说明的是，该参数的值不包含web应用的 ContextPath。示例： /servlets/(login|regist)Servlet
 *  </li>
 * </ul>
 * </p>
 * 在 web.xml 中添加如下配置信息
 * <pre>
 * &lt;!—检查用户是否登录了系统的过滤器配置  开始 --&gt;
 * &lt;filter&gt;
 *  &lt;filter-name&gt;SessionFilter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;com.hmw.filter.SessionFilter&lt;/filter-class&gt;
 *  &lt;init-param&gt;
 *      &lt;param-name&gt;sessionKey&lt;/param-name&gt;
 *      &lt;param-value&gt;userInfo&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 *  &lt;init-param&gt;
 *      &lt;param-name&gt;redirectUrl&lt;/param-name&gt;
 *      &lt;param-value&gt;/login.jsp&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 *  &lt;init-param&gt;
 *      &lt;param-name&gt;excepUrlRegex&lt;/param-name&gt;
 *      &lt;!-- 不拦截 /servlets/loginServlet 和 /servlets/registServlet --&gt;
 *      &lt;param-value&gt;/servlets/(login|regist)Servlet&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 * &lt;/filter&gt;
 *  
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;SessionFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;/servlets/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;SessionFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;/jsp/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * &lt;!—检查用户是否登录了系统的过滤器配置  结束 --&gt;
 * </pre>
 * 
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class SessionFilter implements Filter {

    /** 要检查的 session 的名称 */
    private String sessionKey;
    
    /** 需要排除（不拦截）的URL的正则表达式 */
    private Pattern excepUrlPattern;
    
    /** 检查不通过时，转发的URL */
    private String redirectUrl;

    @Override
    public void init(FilterConfig cfg) throws ServletException {
        sessionKey = cfg.getInitParameter("sessionKey");

        String excepUrlRegex = cfg.getInitParameter("excepUrlRegex");
        if (!StringUtils.isBlank(excepUrlRegex)) {
            excepUrlPattern = Pattern.compile(excepUrlRegex);
        }

        redirectUrl = cfg.getInitParameter("redirectUrl");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // 如果 sessionKey 为空，则直接放行
        if (StringUtils.isBlank(sessionKey)) {
            chain.doFilter(req, res);
            return;
        }

        /*
         * 请求 http://127.0.0.1:8080/webApp/home.jsp?&a=1&b=2 时
         * request.getRequestURL()： http://127.0.0.1:8080/webApp/home.jsp
         * request.getRequestURI()： /webApp/home.jsp
         * request.getContextPath()： /webApp 
         * request.getServletPath()：/home.jsp
         * request.getQueryString()：a=1&b=2
         */
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String servletPath = request.getServletPath();

        // 如果请求的路径与forwardUrl相同，或请求的路径是排除的URL时，则直接放行
        if (servletPath.equals(redirectUrl) || excepUrlPattern.matcher(servletPath).matches()) {
            chain.doFilter(req, res);
            return;
        }

        Object sessionObj = request.getSession().getAttribute(sessionKey);
        // 如果Session为空，则跳转到指定页面
        if (sessionObj == null) {
            String contextPath = request.getContextPath();
            String redirect = servletPath + "?" + StringUtils.defaultString(request.getQueryString());
            /*
             * login.jsp 的 <form> 表单中新增一个隐藏表单域：
             * <input type="hidden" name="redirect" value="${param.redirect }">
             * 
             *  LoginServlet.java 的 service 的方法中新增如下代码：
             *  String redirect = request.getParamter("redirect");
             *  if(loginSuccess){
             *      if(redirect == null || redirect.length() == 0){
             *          // 跳转到项目主页（home.jsp）
             *      }else{
             *          // 跳转到登录前访问的页面（java.net.URLDecoder.decode(s, "UTF-8")）
             *      }
             *  }else{
             *      //登录失败
             *  }
             */
            response.sendRedirect(contextPath + StringUtils.defaultIfEmpty(redirectUrl, "/") + "?redirect=" + URLEncoder.encode(redirect, "UTF-8"));
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
    }
}
