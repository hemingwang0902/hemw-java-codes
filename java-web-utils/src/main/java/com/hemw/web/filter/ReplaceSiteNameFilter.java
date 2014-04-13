package com.hemw.web.filter;

/**
 * 字符替换过滤器，此过滤器为 {@link AbsReplaceFilter} 的一个测试实现类<br/>
 * 将网页中的 {@code google.com.cn} 替换为 {@code baidu.com}
 * <pre>
 * web.xml
 * &lt;filter&gt;
 *	&lt;filter-name&gt;ReplaceSiteNameFilter&lt;/filter-name&gt;
 *	&lt;filter-class&gt;com.hmw.filter.ReplaceSiteNameFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 *  
 * &lt;filter-mapping&gt;
 *	&lt;filter-name&gt;ReplaceSiteNameFilter&lt;/filter-name&gt;
 *	&lt;url-pattern&gt;/login.jsp&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 * 
 * @author Carl He
 *
 */
public class ReplaceSiteNameFilter extends AbsReplaceFilter {
    @Override
    public String getTargetString() {
        return ("google.com.cn");
    }
  
    @Override
     public String getReplacementString() {
        return ("baidu.com");
     }
 }