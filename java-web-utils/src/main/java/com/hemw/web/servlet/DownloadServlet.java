package com.hemw.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

/**
 * 下载文件或在线打开文件的 Servlet<br>
 * 创建日期：2012-8-28
 * 
 * @author <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @since 1.0
 */
public class DownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");
        File file = new File(request.getSession().getServletContext().getRealPath("/uploadFiles"), fileName);
        if (!file.exists())
            throw new ServletException("文件[" + fileName + "]不存在。");

        response.setContentType(FilenameUtils.getExtension(fileName));
        response.addHeader("Content-Length", "" + file.length());

        fileName = URLEncoder.encode(fileName, "UTF-8").toLowerCase();
        // 目前支持的在线打开的文件类型如下：doc|pdf|txt|mht|xls|ppt|jpg|gif|bmp|png|swf|csv
        if (fileName.matches(".+?\\.(doc|pdf|txt|mht|xls|ppt|jpg|gif|bmp|png|swf|csv)$")) { // 是否能直接在线打开
            response.addHeader("Content-Disposition", "inline; filename=" + fileName);
        } else {
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
        }

        byte[] b = new byte[1024];
        int i = 0;
        InputStream in = null;
        OutputStream output = null;
        try {
            in = new FileInputStream(file);
            output = response.getOutputStream();
            while ((i = in.read(b)) > 0) {
                output.write(b, 0, i);
            }
            output.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
            if (output != null) {
                output.close();
                output = null;
            }
        }
    }
}