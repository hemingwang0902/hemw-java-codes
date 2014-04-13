package com.hemw.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * 页面编码过滤器<br/>
 * 在web.xml文件中加入如下配置信息：
 * 
 * <pre>
 * &lt;!-- 页面请求编码过滤器 注意每个页面的pageEncoding="GB2312" --&gt;
 * &lt;filter&gt;
 * 	&lt;filter-name&gt;encodingFilter&lt;/filter-name&gt;
 * 	&lt;filter-class&gt;com.hmw.filter.EncodingFilter&lt;/filter-class&gt;
 * 	&lt;init-param&gt;
 * 		&lt;param-name&gt;encoding&lt;/param-name&gt;
 * 		&lt;param-value&gt;GB2312&lt;/param-value&gt;
 * 	&lt;/init-param&gt;
 * &lt;/filter&gt;
 * 
 * &lt;filter-mapping&gt;
 * 	&lt;filter-name&gt;encodingFilter&lt;/filter-name&gt;
 * 	&lt;!-- 对全部的jsp页面有效，比较郁闷的是没有太多的配置方式 --&gt;
 * 	&lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * &lt;!--页面请求编码过滤器结束--&gt;
 * </pre>
 * 
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class EncodingFilter implements Filter {
    protected String encoding = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 从web.xml配置文件中获取编码配置
        this.encoding = filterConfig.getInitParameter("encoding");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!StringUtils.isBlank(encoding)) {
            // 设置request的编码方式
            request.setCharacterEncoding(encoding);
            response.setCharacterEncoding(encoding);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
