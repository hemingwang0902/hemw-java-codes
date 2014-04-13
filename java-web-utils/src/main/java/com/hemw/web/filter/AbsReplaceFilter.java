package com.hemw.web.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 替换网页中的关键字的过滤器，可用于替换敏感词。
 * <p>
 * Filter that replaces all occurrences of a given string with a replacement.
 * This is an abstract class: you <I>must</I> override the getTargetString and
 * getReplacementString methods in a subclass. The first of these methods
 * specifies the string in the response that should be replaced. The second of
 * these specifies the string that should replace each occurrence of the target
 * string.
 * </p>
 * NOTE：可以根据实际情况，将该过滤器的改造成支持同时替换多个敏感词和支持通过正则表达式进行替换
 * @author Carl He
 */
public abstract class AbsReplaceFilter implements Filter {
	private FilterConfig config;

    protected FilterConfig getFilterConfig() {
        return (config);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    @Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		CharArrayWrapper responseWrapper = new CharArrayWrapper((HttpServletResponse) response);
		// Invoke resource, accumulating output in the wrapper.
		chain.doFilter(request, responseWrapper);
		// Turn entire output into one big String.
		String responseString = responseWrapper.toString();
		// In output, replace all occurrences of target string with replacement string.
		responseString = replace(responseString, getTargetString(), getReplacementString());
		// Update the Content-Length header.
		updateHeaders(response, responseString);
		PrintWriter out = response.getWriter();
		out.write(responseString);
	}

    @Override
	public void destroy() {
	}

	/**
	 * The string that needs replacement. <br>
	 * Override this method in your subclass.
	 */
	public abstract String getTargetString();

	/**
	 * The string that replaces the target. <br>
	 * Override this method in your subclass.
	 */
	public abstract String getReplacementString();

    /**
     * Change all occurrences of orig in mainString to replacement.
     */
    public static String replace(String mainString, String orig, String replacement) {
        String result = "";
        int oldIndex = 0;
        int index = 0;
        int origLength = orig.length();
        while ((index = mainString.indexOf(orig, oldIndex)) != -1) {
            result = result + mainString.substring(oldIndex, index) + replacement;
            oldIndex = index + origLength;
        }
        result = result + mainString.substring(oldIndex);
        return result;
    }
	/**
	 * Updates the response headers. This simple version just sets the
	 * Content-Length header, assuming that we are using a character set that
	 * uses 1 byte per character. For other character sets, override this method
	 * to use different logic or to give up on persistent HTTP connections. In
	 * this latter case, have this method set the Connection header to "close".
	 */
	public void updateHeaders(ServletResponse response, String responseString) {
		response.setContentLength(responseString.length());
	}

}