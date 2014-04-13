package com.hemw.temp;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

/**
 * 修改通过 Teleport Ultra 抓取过来的网页源代码 <br>
 * 创建日期：2013-1-8
 * 
 * @author <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @since 1.0
 */
public class TeleportPageReplace {

    public static void main(String[] args) throws Exception {
        TeleportPageReplace t = new TeleportPageReplace();
        t.replaceOutterLink(new File("E:/temp/test"), "UTF-8");
    }

    public void replaceOutterLink(File dir, String encoding) throws IOException {
        Collection<File> files = FileUtils.listFiles(dir, new String[] { "html" }, false);
        for (File file : files) {
            String s = FileUtils.readFileToString(file, encoding);
            s = replaceOutterLink(s);
            FileUtils.write(file, s, encoding);
        }
    }

    public String replaceOutterLink(String s) {
        return s.replaceAll("href=\"javascript:if.*?\" tppabs=(\".*?\")", "href=$1");
    }
}
