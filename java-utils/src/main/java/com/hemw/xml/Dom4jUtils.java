package com.hemw.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 使用 Dom4j 操作 XML 的工具类。
 * 本工具类基于 dom4j 1.6.1，如果需要通过 XPath 选择 XML 节点，请将 jaxen 的 jar 包添加到 classpath。
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class Dom4jUtils {
    public static final String ENCODING_UTF8 = "UTF-8";
    public static final OutputFormat PRETTY_FORMAT = OutputFormat.createPrettyPrint();
    public static final OutputFormat COMPACT_FORMAT = OutputFormat.createCompactFormat();
    
    static {
        PRETTY_FORMAT.setEncoding(ENCODING_UTF8);
    }
    
    /**
     * 读取 XML 文件， 返回一个 {@link org.dom4j.Document Document} 对象
     * 
     * @param filePath XML 文件的路径
     * @return
     * @throws DocumentException
     */
    public static Document readXml(String filePath) throws DocumentException {
        return readXml(filePath);
    }
    
    /**
     * 读取 XML 文件， 返回一个 {@link org.dom4j.Document Document} 对象
     * 
     * @param file XML 文件
     * @return
     * @throws DocumentException
     */
    public static Document readXml(File file) throws DocumentException {
        return new SAXReader().read(file);
    }
    
    /**
     * 从 URL 读取 XML 内容， 返回一个 {@link org.dom4j.Document Document} 对象
     * 
     * @param url
     * @return
     * @throws DocumentException
     */
    public static Document readXml(URL url) throws DocumentException {
        return new SAXReader().read(url);
    }

    /**
     * 从字符流中读取 XML， 返回一个 {@link org.dom4j.Document Document} 对象
     * 
     * @param reader
     * @return
     * @throws DocumentException
     */
    public static Document readXml(Reader reader) throws DocumentException {
        return new SAXReader().read(reader);
    }

    /**
     * 读取 XML 输入流， 返回一个 {@link org.dom4j.Document Document} 对象， 并在读取完成后将输入流关闭
     * 
     * @param in XML 输入流
     * @return
     * @throws DocumentException
     */
    public static Document readXml(InputStream in) throws DocumentException {
        return new SAXReader().read(in);
    }

    /**
     * 保存Document对象到 XML 文件(采用UTF-8编码)
     * 
     * @param document {@link org.dom4j.Document Document} 对象
     * @param filePath XML文件的保存路径
     * @throws IOException
     */
    public static void savaXML(Document document, String filePath) throws IOException {
        savaXML(document, new File(filePath));
    }

    /**
     * 保存Document对象到 XML 文件(采用UTF-8编码)
     * 
     * @param document {@link org.dom4j.Document Document} 对象
     * @param file XML 文件
     * @throws IOException
     */
    public static void savaXML(Document document, File file) throws IOException {
        savaXML(document, file, ENCODING_UTF8);
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
        savaXML(document, new File(filePath), encoding);
    }

    /**
     * 保存Document对象到 XML 文件
     * 
     * @param document Document对象
     * @param file XML 文件
     * @param encoding 文件编码
     * @throws IOException
     */
    public static void savaXML(Document document, File file, String encoding) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(encoding);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            outputXML(document, out, format);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * 将 Document 序列化为 XML 字符串后输出到指定的输出流
     * 
     * @param document Document对象
     * @param out 目标输出流
     * @throws IOException
     */
    public static void outputXML(Document document, OutputStream out) throws IOException {
        outputXML(document, out, PRETTY_FORMAT);
    }
    
    /**
     * 将 Document 序列化为 XML 字符串后输出到指定的输出流
     * 
     * @param document Document对象
     * @param out 目标输出流
     * @param format XML 的输出格式，可参见 {@link #PRETTY_FORMAT}、{@link #COMPACT_FORMAT}
     * @throws IOException
     */
    public static void outputXML(Document document, OutputStream out, OutputFormat format) throws IOException {
        if(format == null) {
            format = PRETTY_FORMAT;
        }
        
        if(format.getEncoding() == null || format.getEncoding().length() == 0) {
            format.setEncoding(ENCODING_UTF8);
        }
        
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(out, format.getEncoding());
            outputXML(document, writer, format);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    /**
     * 将 Document 序列化为 XML 字符串后输出到指定的输出流
     * 
     * @param document Document对象
     * @param writer 目标输出流
     * @throws IOException
     */
    public static void outputXML(Document document, Writer writer) throws IOException {
        outputXML(document, writer, PRETTY_FORMAT);
    }
    
    /**
     * 将 Document 序列化为 XML 字符串后输出到指定的输出流
     * 
     * @param document Document对象
     * @param writer 目标输出流
     * @param format XML 的输出格式，可参见 {@link #PRETTY_FORMAT}、{@link #COMPACT_FORMAT}
     * @throws IOException
     */
    public static void outputXML(Document document, Writer writer, OutputFormat format) throws IOException {
        XMLWriter xmlWriter = null;
        try {
            xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
        } finally {
            if (xmlWriter != null)
                xmlWriter.close();
        }
    }

    /**
     * 创建一个空的 Document 对象
     * @return
     * @throws IOException
     */
    public static Document createEmptyDocument() throws IOException {
        return DocumentHelper.createDocument();
    }

    /**
     * 将 XML 片段转换为 {@link org.dom4j.Document Document} 对象
     * 
     * @param xml
     * @return
     * @throws DocumentException
     */
    public static Document parseXml(String xml) throws DocumentException {
        return DocumentHelper.parseText(xml);
    }
    
    /**
     * 将 Document 对象转化为格式化好的 XML 字符串
     * 
     * @param document Document对象
     * @return
     * @throws IOException
     */
    public static String asXML(Document document) throws IOException {
        return asXML(document, ENCODING_UTF8);
    }
    
    /**
     * 将 Document 对象转化为格式化好的 XML 字符串
     * 
     * @param document Document对象
     * @param encoding XML 编码
     * @return
     * @throws IOException
     */
    public static String asXML(Document document, String encoding) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(encoding);

        StringWriter sw = new StringWriter();
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(sw, format);
            writer.write(document);
            return sw.toString();
        } finally {
            if (writer != null) {
                writer.close();
            }
            sw.close();
        }
    }

}
