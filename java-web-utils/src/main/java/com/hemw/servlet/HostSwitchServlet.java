package com.hemw.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

/**
 * Servlet implementation class HostSwitchServlet
 */
public class HostSwitchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static File hostFile = new File("C:/Windows/System32/Drivers/etc/hosts");
	private static Map<Integer, String> lines = new HashMap<Integer, String>();
	private static List<String> otherlines = new ArrayList<String>();
	private static int index;
	private static int max;

	@Override
    public void init(ServletConfig servletconfi) throws ServletException {
	    try {
	        List<String> allLines = FileUtils.readLines(hostFile);
	        String line = null;
	        for (int i = 0, size = allLines.size(); i < size; i++) {
	            line = allLines.get(i);
                if(line.contains("kyfw.12306.cn")){
                    lines.put(index, line.startsWith("#") ? line : "#" + line);
                    index++;
                } else {
                    otherlines.add(line);
                }
            }
	        index = 0;
	        max = lines.size() - 1;
	        
	        Set<Entry<Integer, String>> set= lines.entrySet();
	        for (Entry<Integer, String> entry : set) {
	            System.out.println(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Map<Integer, String> linesCopy = new HashMap<Integer, String>(lines);
        String newline = linesCopy.get(index).substring(1);
        linesCopy.put(index, newline);

        List<String> allLines = new ArrayList<String>(otherlines);
        allLines.addAll(linesCopy.values());

        FileUtils.writeLines(hostFile, allLines);

        if (index == max)
            index = 0;
        else
            index++;

        PrintWriter out = response.getWriter();
        out.write(newline);
        out.flush();
        out.close();
	}

}
