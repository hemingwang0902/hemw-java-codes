package com.hemw.web.servlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 * 上传文件的 Servlet<br>
 * 需要将上传文件的表单（form）的 enctype 属性值改为 <code>multipart/form-data</code><br>
 * 创建日期：2012-8-28
 * @author  <a href="mailto:hemw@mochasoft.com.cn">何明旺</a>
 * @version 1.0
 * @since 1.0
 */
public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    // 过滤掉的文件类型
    private static Set<String> errorExtension = new HashSet<String>();
    static {
        errorExtension.add("exe");
        errorExtension.add("bat");
        errorExtension.add("com");
        errorExtension.add("cgi");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查输入请求是否为multipart的表单数据。
        if (!ServletFileUpload.isMultipartContent(request))
            throw new ServletException("表单数据格式不是 multipart/form-data，或者非法提交");

        DiskFileItemFactory dff = new DiskFileItemFactory();
        // 设置最多只允许在内存中存储的数据,单位:字节
        dff.setSizeThreshold(10 * 1024 * 1024);
        // 设置一旦文件大小超过 getSizeThreshold() 的值时数据存放在硬盘的目录
        dff.setRepository(new File("C:\\WINDOWS\\Temp"));

        ServletFileUpload upload = new ServletFileUpload(dff);// 创建文件上传对象
        upload.setSizeMax(1024000);// 设置客户端最大上传，-1为无限大（单位：byte）
        try {
            // 开始读取上传信息
            List<?> fileItems = upload.parseRequest(request);
            FileItem item = null;
            // 依次处理每个上传的文件
            for (int i = 0, length = fileItems.size(); i < length; i++) {
                item = (FileItem) fileItems.get(i);
                if (item.isFormField()) { // 普通的表单域
                    /*
                    String fieldName = item.getFieldName(); // 表单域的名字
                    String fieldValue = item.getString(); // 表单域的值
                    */
                } else {
                    String fileName = item.getName();
                    long size = item.getSize();
                    if ((fileName == null || "".equals(fileName)) && size == 0)
                        continue;

                    String extension = FilenameUtils.getExtension(fileName).toLowerCase();
                    if (errorExtension.contains(extension))
                        throw new IOException("错误的文件类型[" + fileName + "]。");

                    String finalName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + Math.round(Math.random() * 10000) + (extension.length() > 0 ? "." + extension : "");
                    // 保存上传的文件到指定的目录
                    item.write(new File(getServletContext().getRealPath("/uploadFiles/"), finalName));
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
