package com.hemw.html;

import java.util.LinkedHashSet;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * 抓取中华培训网 chinahtml/zixunzhongxin 目录下的文件
 * 说明：循环网址下所有链接，根据一定规则过滤掉一部分链接，读出页面指定节点下的内容
 * @author sam.zhang
 * 
 */
public class AnalysisHtmlByHtmlparser {
	/**中华培训网网址*/
    public static final String TRAINING_URL = "http://www.china-training.com";
    
    /**中华培训网资讯根网址*/
    private static final String NEWS_URL = "http://www.china-training.com/news/";

	public static void main(String[] args) {
		AnalysisHtmlByHtmlparser tt = new AnalysisHtmlByHtmlparser();
		Set<String> urls = tt.getAllUrls(NEWS_URL);
		for (String url : urls) {
			tt.analysisHtml(url);
		}
	}
    
    /**
     * 得到指定网址下所有链接
     * @param url
     */
	public Set<String> getAllUrls(String url) {
		Set<String> urls = new LinkedHashSet<String>();
		NodeList nodeList = null;
		try {
			Parser p = new Parser(url);
			// nodeList = p.parse(new TagNameFilter( "A ")); // 使用TagNameFilter(两种写法都可以)
			nodeList = p.parse(new NodeClassFilter(LinkTag.class)); // 使用NodeClassFilter
		} catch (ParserException e) {
			e.printStackTrace();
		}

		if (nodeList == null || nodeList.size() == 0)
			return urls;

		String link = null;
		for (int i = 0, size = nodeList.size(); i < size; i++) {
			link = ((LinkTag) nodeList.elementAt(i)).getLink();
			if(link != null && link.startsWith(NEWS_URL + "viewnews-"))
				urls.add(link);
		}
		return urls;
	}

    /**
     * 分析网页内容
     * @param url
     */
	public void analysisHtml(String url) {
		try {
//			Parser p = new Parser(URLEncoder.encode(url, "GBK") );
			Parser p = new Parser(url);
			p.setEncoding("GBK");
			OrFilter filter = new OrFilter();
			filter.setPredicates(new NodeFilter[] {new CssSelectorNodeFilter("h1"), new CssSelectorNodeFilter("#article_body") });
			NodeList nodeList = p.parse(filter);
			String title = null; // 标题
			String content = null; // 内容
			Node node = null;
			for (int i = 0, size = nodeList.size(); i < size; i++) {
				node = nodeList.elementAt(i);
				if (node instanceof HeadingTag) {
					title = ((HeadingTag) node).toPlainTextString();
					System.out.println(title);
				} else if (nodeList.elementAt(i) instanceof Div) {
					content = ((Div) node).getStringText();
					System.out.println(content);
				}
			}
		} catch (ParserException pe) {
			System.out.println(url + "    解析失败");
			pe.printStackTrace();
		}
	}
}
