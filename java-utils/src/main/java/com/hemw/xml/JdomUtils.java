package com.hemw.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathFactory;

public class JdomUtils {
    public static String ENCODING_UTF8 = "UTF-8";

    /**
     * 读取 XML 文件， 返回一个 {@link org.jdom2.Document Document} 对象
     * 
     * @param filePath XML 文件的路径
     * @return
     * @throws IOException 
     * @throws JDOMException 
     */
    public static Document readXml(String filePath) throws JDOMException, IOException {
        return new SAXBuilder().build(new File(filePath));
    }

    /**
     * 读取输入流， 返回一个 {@link org.jdom2.Document Document} 对象， 并在读取完成后将输入流关闭
     * 
     * @param in XML片段输入流
     * @return
     * @throws IOException 
     * @throws JDOMException 
     */
    public static Document readXml(InputStream in) throws JDOMException, IOException  {
        return new SAXBuilder().build(in);
    }

    /**
     * 将 XML 片段转换为 {@link org.jdom2.Document Document} 对象
     * 
     * @param xml
     * @return
     * @throws IOException 
     * @throws JDOMException 
     */
    public static Document parseXml(String xml) throws JDOMException, IOException {
        Document doc = null;
        StringReader reader = null;
        try {
            reader = new StringReader(xml);
            doc = new SAXBuilder().build(reader);
        } finally {
            if (reader != null)
                reader.close();
        }
        return doc;
    }

    /**
     * 保存Document对象到 XML 文件(采用UTF-8编码)
     * 
     * @param document {@link org.jdom2.Document Document} 对象
     * @param filePath XML文件的保存路径
     * @throws IOException
     */
    public static void savaXML(Document document, String filePath) throws IOException {
        savaXML(document, filePath, ENCODING_UTF8);
    }

    /**
     * 保存Document对象到 XML 文件
     * 
     * @param document Document对象
     * @param filePath XML文件的文件名
     * @param encoding 文件编码
     * @throws IOException
     */
    public static void savaXML(Document document, String filePath, String encoding) throws IOException {
        Format format = Format.getPrettyFormat();
        format.setEncoding(encoding);

        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(filePath), encoding);
            XMLOutputter xo = new XMLOutputter(format);
            xo.output(document, writer);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    /**
     * 格式化 XML 片段
     * 
     * @param document Document对象
     * @param encoding XML 编码
     * @throws IOException
     */
    public static String formatXML(Document document, String encoding) throws IOException {
        Format format = Format.getPrettyFormat();
        format.setEncoding(encoding);
        XMLOutputter xo = new XMLOutputter(format);
        return xo.outputString(document);
    }

    /**
     * 创建 XML 文件的示例
     * @return 创建的示例 XML 片段
     * @throws IOException
     */
    public static String createXmlDemo() throws IOException {
        // 创建一个空白的 Document 对象
        Document doc = new Document();
        
        // 创建根结点
        Element demo = new Element("demo");
        doc.addContent(demo);
        
        // 添加一行注释
        demo.addContent(new Comment("有特殊字符的时候用 <![CDATA[]]> 括起来"));
        
        // 添加子元素，并设置属性和文本值
        Element user1 = new Element("user").setAttribute("id", "123").setContent(new CDATA("       a&b<<c   "));
        demo.addContent(user1);

        Element user2 = new Element("user").setAttribute("id", "321").setText("普通文本");
        demo.addContent(user2);

        return formatXML(doc, ENCODING_UTF8);
    }

    /**
     * 解析 XML 文件的示例
     * @throws IOException 
     * @throws JDOMException 
     */
    public static void analisysXmlDemo() throws IOException, JDOMException {
        String xml = createXmlDemo();
        Document doc = parseXml(xml);
        // 获取根节点
        Element demo = doc.getRootElement();
        // 取得第一个 user 节点
        Element user1 = (Element) demo.getChildren().get(0);

        // 获取属性值
        String id = user1.getAttributeValue("id");
        System.out.println("id == " + id);

        // 获取属性值2
        String name = user1.getAttributeValue("name", "默认属性值");
        System.out.println("name == " + name);

        // 获取文本值
        String text = user1.getText();
        System.out.println("text == " + text);

        // 通过 XPath 的方式选取节点
//        XPath xpath = XPath.newInstance("/demo/user[last()]");
//        Element user2 = (Element)xpath.selectSingleNode(doc);
        Element user2 = (Element) XPathFactory.instance().compile("/demo/user[last()]").getFilter().filter(doc);
        
        // 移除属性
        user2.removeAttribute("id");
        // 修改文本值
        user2.setText("移除了 ID 属性后的文本");
        System.out.println("user2 == " + new XMLOutputter().outputString(user2));
    }

    public static void main(String[] args) throws Exception {
        String xml = createXmlDemo();
        System.out.println(xml);
        
        analisysXmlDemo();
    }
}
