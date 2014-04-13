package com.hemw.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * FTP 工具类<br>
 * 说明：该类依赖于 commons-net-ftp-2.0.jar
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class FtpUtils {
	private final Logger logger = Logger.getLogger(FtpUtils.class);
	private static final String ENCODING_GBK = "GBK";
	private static final String ENCODING_ISO88591 = "ISO-8859-1";
	private FTPClient ftpClient;

	public FtpUtils(){
		ftpClient = new FTPClient();
		//设置将过程中使用到的命令输出到控制台   
//      ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		ftpClient.setControlEncoding(ENCODING_GBK);
	}
	/*
	public FtpUtils(String server, int port, String user, String password, String path){
		this();
		connectServer(server, port, user, password, path);
	}
	*/
	public FTPClient getFtpClient() {
		return ftpClient;
	}

	/**
	 * 根据FTP配置连接并登录到FTP服务器
	 * @param server FTP 服务器地址(IP)
	 * @param port	端口号
	 * @param user	用户名
	 * @param password	密码
	 * @param path	登录后转向的FTP目录
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	public boolean connectServer(String server, int port, String user, String password, String path){
		StringBuffer sb = new StringBuffer();
		sb.append("[ip:").append(server);
		sb.append(", port:").append(port);
		sb.append(", userName:").append(user);
		sb.append(", password:").append(password);
		sb.append(", workDirectory:").append(password).append("]");
		
		boolean connectResult = false;
		try {
			ftpClient.connect(server, port);
			 if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
				connectResult = ftpClient.login(user, password);
			 }else{
				 ftpClient.disconnect();
				 logger.debug("连接FTP" + sb + "失败，返回代码：" + ftpClient.getReplyCode());
			 }
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		
		if (connectResult && path != null && path.length() > 0) {
			try {
				connectResult = ftpClient.changeWorkingDirectory(path);
			} catch (IOException e) {
				logger.debug(e.getMessage(), e);
			}
		}
		return connectResult;
	}
	
	/**
	 * 断开与远程 FTP 服务器的连接  
	 * @throws IOException
	 */
	public void disconnectServer() throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.disconnect();
		}
	}

	 /**  
     * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报  
     * @param remote 远程文件路径  
     * @param local 本地文件路径  
     * @return 上传的状态  
     * @throws IOException  
     */  
	public DownloadStatus download(String remote, String local) throws IOException {
		// 设置被动模式
		ftpClient.enterLocalPassiveMode();
		// 设置以二进制方式传输
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		DownloadStatus result;
		// 检查远程文件是否存在
		FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes(ENCODING_GBK), ENCODING_ISO88591));
		if (files.length != 1) {
			logger.info("远程文件 不存在 [" + remote + "]");
			return DownloadStatus.Remote_File_Noexist;
		}

		long lRemoteSize = files[0].getSize();
		File f = new File(local);
		// 本地存在文件，进行断点下载
		if (f.exists()) {
			long localSize = f.length();
			// 判断本地文件大小是否大于远程文件大小
			if (localSize >= lRemoteSize) {
				logger.info("本地文件大于远程文件，下载中止");
				return DownloadStatus.Local_Bigger_Remote;
			}

			// 进行断点续传，并记录状态
			FileOutputStream out = new FileOutputStream(f, true);
			ftpClient.setRestartOffset(localSize);
			InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes(ENCODING_GBK), ENCODING_ISO88591));
			byte[] bytes = new byte[1024];
			long step = lRemoteSize / 100;
			long process = localSize / step;
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0)
						logger.debug("文件 [" + remote + "] 的下载进度：" + process);
//					 TODO 更新文件下载进度,值存放在process变量中
				}
			}
			in.close();
			out.close();
			boolean isDo = ftpClient.completePendingCommand();
			if (isDo) {
				result = DownloadStatus.Download_From_Break_Success;
			} else {
				result = DownloadStatus.Download_From_Break_Failed;
			}
		} else {
			OutputStream out = new FileOutputStream(f);
			InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes(ENCODING_GBK), ENCODING_ISO88591));
			byte[] bytes = new byte[1024];
			long step = lRemoteSize / 100;
			long process = 0;
			long localSize = 0L;
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localSize += c;
				long nowProcess = localSize / step;
				if (nowProcess > process) {
					process = nowProcess;
					if (process % 10 == 0)
						logger.debug("下载进度：" + process);
					// TODO 更新文件下载进度,值存放在process变量中
				}
			}
			in.close();
			out.close();
			boolean upNewStatus = ftpClient.completePendingCommand();
			if (upNewStatus) {
				result = DownloadStatus.Download_New_Success;
			} else {
				result = DownloadStatus.Download_New_Failed;
			}
		}
		return result;
	}
       
	/**
	 * 下载文件
	 * @param remoteFileName FTP服务器上的文件路径
	 * @param localFileName 文件下载后的本地存放路径
	 * @param ftpConfig ftp配置(供下载发生异常时，记录日志用)
	 * @return 下载成功返回 true， 否则返回 false
	 */
	public boolean download(String remoteFileName, String localFileName, String ftpConfig){
		boolean flag = false;
		OutputStream oStream = null;
		File outfile = new File(localFileName);
		File outDirectory = outfile.getParentFile();
		if(!outDirectory.exists()){
			outDirectory.mkdirs();
		}
		try {
			oStream = new FileOutputStream(outfile);
			flag = ftpClient.retrieveFile(remoteFileName, oStream);
		}catch (IOException e) {
			flag = false;
			logger.debug(e.getMessage(), e);
		} finally {
			if(oStream != null){
				try {
					oStream.close();
				} catch (IOException e) {
					logger.warn(e.getMessage());
				}
			}
		}
		return flag;
	}
	
    /**  
     * 上传文件到FTP服务器，支持断点续传  
     * @param local 本地文件名称，绝对路径  
     * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext或是 http://www.guihua.org/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构  
     * @return 上传结果  
     * @throws IOException  
     */  
	public UploadStatus upload(String local, String remote) throws IOException {
		// 设置PassiveMode传输
		ftpClient.enterLocalPassiveMode();
		// 设置以二进制流的方式传输
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.setControlEncoding(ENCODING_GBK);
		UploadStatus result;
		// 对远程目录的处理
		String remoteFileName = remote;
		if (remote.contains("/")) {
			remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
			// 创建服务器远程目录结构，创建失败直接返回
			if (!createDirectory(remote)) {
				return UploadStatus.Create_Directory_Fail;
			}
		}

		// 检查远程是否存在文件
		FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes(ENCODING_GBK), ENCODING_ISO88591));
		if (files.length == 1) {
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if (remoteSize == localSize) {
				return UploadStatus.File_Exits;
			} else if (remoteSize > localSize) {
				return UploadStatus.Remote_Bigger_Local;
			}

			// 尝试移动文件内读取指针,实现断点续传
			result = uploadFile(remoteFileName, f, ftpClient, remoteSize);

			// 如果断点续传没有成功，则删除服务器上文件，重新上传
			if (result == UploadStatus.Upload_From_Break_Failed) {
				if (!ftpClient.deleteFile(remoteFileName)) {
					return UploadStatus.Delete_Remote_Faild;
				}
				result = uploadFile(remoteFileName, f, ftpClient, 0);
			}
		} else {
			result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
		}
		return result;
	}   
 
	/**  
     * 上传文件到服务器,新上传和断点续传  
     * @param remoteFile 远程文件名，在上传之前已经将服务器工作目录做了改变  
     * @param localFile 本地文件File句柄，绝对路径  
     * @param ftpClient FTPClient引用  
     * @param remoteSize 断点续传时上传文件的开始点
     * @return  
     * @throws IOException  
     */  
	public UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize) throws IOException {
		UploadStatus status;
		// 显示进度的上传
		long step = localFile.length() / 100;
		long process = 0;
		long localreadbytes = 0L;
		RandomAccessFile raf = new RandomAccessFile(localFile, "r");
		OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes(ENCODING_GBK), ENCODING_ISO88591));
		// 断点续传
		if (remoteSize > 0) {
			ftpClient.setRestartOffset(remoteSize);
			process = remoteSize / step;
			raf.seek(remoteSize);
			localreadbytes = remoteSize;
		}
		byte[] bytes = new byte[1024];
		int c;
		while ((c = raf.read(bytes)) != -1) {
			out.write(bytes, 0, c);
			localreadbytes += c;
			if (localreadbytes / step != process) {
				process = localreadbytes / step;
				System.out.println("上传进度:" + process);
				// TODO 汇报上传状态
			}
		}
		out.flush();
		raf.close();
		out.close();
		boolean result = ftpClient.completePendingCommand();
		if (remoteSize > 0) {
			status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
		} else {
			status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
		}
		return status;
	}
	
	/**
	 * 上传文件
	 * @param fileName
	 * @param newName
	 * @return
	 * @throws IOException
	 */
	public boolean uploadFile(String fileName, String newName) throws IOException {
		boolean flag = false;
		InputStream iStream = null;
		try {
			iStream = new FileInputStream(fileName);
			flag = ftpClient.storeFile(newName, iStream);
		} catch (IOException e) {
			flag = false;
			return flag;
		} finally {
			if (iStream != null) {
				iStream.close();
			}
		}
		return flag;
	}

	/**
	 * 上传文件
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public boolean uploadFile(String fileName) throws IOException {
		return uploadFile(fileName, fileName);
	}

	/**
	 * 上传文件
	 * @param iStream
	 * @param newName
	 * @return
	 * @throws IOException
	 */
	public boolean uploadFile(InputStream iStream, String newName) throws IOException {
		boolean flag = false;
		try {
			// can execute [OutputStream storeFileStream(String remote)]
			// Above method return's value is the local file stream.
			flag = ftpClient.storeFile(newName, iStream);
		} catch (IOException e) {
			flag = false;
			return flag;
		} finally {
			if (iStream != null) {
				iStream.close();
			}
		}
		return flag;
	}
	
    /**  
     * 递归创建远程 FTP 服务器目录 
     * @param directory 远程服务器目录路径, 如"/a/b" 或 "x/y/z"
     * @return 目录创建成功返回true, 否则返回false  
     * @throws IOException  
     */  
	public boolean createDirectory(String directory) throws IOException {
		hasText(directory);
		boolean status = true;
		directory = directory.replaceAll("\\\\+", "/").replaceAll("/{2,}", "/");
		ftpClient.setControlEncoding("GBK");
		String workingDirectory = ftpClient.printWorkingDirectory();	//当前的工作目录
		if(ftpClient.changeWorkingDirectory(new String(directory.getBytes(ENCODING_GBK), ENCODING_ISO88591))){
			logger.debug("目录 ["+ftpClient.printWorkingDirectory()+"] 已在存在");
		}else{
			if(directory.startsWith("/")){
				ftpClient.changeWorkingDirectory("/");
				directory = directory.substring(1);
			}
			if(directory.endsWith("/")){
				directory = directory.substring(0, directory.length()-1);
			}
			
			String[] subDirectors = directory.split("/");
			String subDirector = null;
			for (int i = 0; i < subDirectors.length; i++) {
				subDirector = new String(subDirectors[i].getBytes(ENCODING_GBK), ENCODING_ISO88591);
				if (!ftpClient.changeWorkingDirectory(subDirector)) {
					if(ftpClient.makeDirectory(subDirector) && ftpClient.changeWorkingDirectory(subDirector)){
						logger.info("创建目录["+ftpClient.printWorkingDirectory() + "]成功");
					} else {
						logger.info("创建目录["+ftpClient.printWorkingDirectory() + "/" + subDirector +"]失败");
						status = false;
						break;
					}
				}
			}
		}
		ftpClient.changeWorkingDirectory(workingDirectory);
		return status;
	}

	/**
	 * 删除目录
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean removeDirectory(String path) throws IOException {
		return ftpClient.removeDirectory(path);
	}


	/**
	 * 删除目录(及目录下所有文件和子目录)
	 * @param path
	 * @param isAll
	 * @return
	 * @throws IOException
	 */
	public boolean removeDirectory(String path, boolean isAll) throws IOException {
		if (!isAll) {
			return removeDirectory(path);
		}

		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		if (ftpFileArr == null || ftpFileArr.length == 0) {
			return removeDirectory(path);
		}

		for (FTPFile ftpFile : ftpFileArr) {
			String name = ftpFile.getName();
			if (".".equals(name) || "..".equals(name)) {
				continue;
			}
			if (ftpFile.isDirectory()) {
				logger.info("Delete subPath [" + path + "/" + name + "]");
				removeDirectory(path + "/" + name, true);
			} else if (ftpFile.isFile()) {
				logger.info("Delete file [" + path + "/" + name + "]");
				deleteFile(path + "/" + name);
			} else if (ftpFile.isSymbolicLink()) {

			} else if (ftpFile.isUnknown()) {

			}
		}
		return ftpClient.removeDirectory(path);
	}

	/**
	 * 检查目录(或文件)是否存在
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public boolean existDirectory(String path) throws IOException {
		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		for (FTPFile ftpFile : ftpFileArr) {
			if (ftpFile.isDirectory() && ftpFile.getName().equalsIgnoreCase(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 得到FTP指定目录下的所有文件名列表（不包括目录）
	 * @param path 如果 path  为 null, 则列出当前所在目录的所有文件名
	 * @return
	 * @throws IOException
	 */
	public List<String> getFileList(String path) throws IOException {
		return getFileList(path, null);
	}
	
	/**
	 * 得到FTP指定目录下的符合命名规则的文件名列表（不包括目录）
	 * @param path 如果 path  为 null, 则列出当前所在目录的所有文件名
	 * @param regex 文件名的命名规则，即正则表达式, 需要注意的是：此处用正则表达式匹配文件名， 不区分大小写。<br/>如 ".*\.(txt|xml)", 则是只取后缀名为 txt 或 xml 的文件
	 * @return 如果此文件夹下没有文件，则返回一个长度为 0 的List
	 * @throws IOException
	 */
	public List<String> getFileList(String path, String regex) throws IOException {
		// listFiles return contains directory and file, it's FTPFile instance listNames() contains directory,
		//so using following to filer directory.String[] fileNameArr = ftpClient.listNames(path);
		
		FTPFile[] ftpFiles = StringUtils.isBlank(path) ? ftpClient.listFiles() : ftpClient.listFiles(path);
		Pattern pattern = StringUtils.isBlank(regex) ? null : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		List<String> retList = new ArrayList<String>();
		if (ftpFiles == null || ftpFiles.length == 0) {
			return retList;
		}
		
		String fileName = null;
		for (FTPFile ftpFile : ftpFiles) {
			fileName = ftpFile.getName();
			if (ftpFile.isFile() && (pattern == null || pattern.matcher(fileName).matches())) {
				retList.add(fileName);
			}
		}
		return retList;
	}

	/**
	 * 删除文件
	 * @param pathName
	 * @return
	 * @throws IOException
	 */
	public boolean deleteFile(String pathName) throws IOException {
		return ftpClient.deleteFile(pathName);
	}

	/**
	 * 重命名文件(夹)，说明：如果目标文件(夹)存在，则直接覆盖目标文件(夹)
	 * @param from 原文件名
	 * @param to 新文件名
	 * @param fileType 文件类型 [{@link FileType#File}：文件；{@link FileType#Directory}：文件夹]
	 * @return 如果重命名成功，返回true, 否则返回false
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public boolean rename(String from, String to, FileType fileType) throws UnsupportedEncodingException, IOException{
		hasText(from);
		hasText(to);
		
		boolean status = true;
		from = from.replaceAll("\\\\+", "/").replaceAll("/{2,}", "/");
		to = to.replaceAll("\\\\+", "/").replaceAll("/{2,}", "/");
		String from_1 = new String(from.getBytes(ENCODING_GBK), ENCODING_ISO88591);
		String to_1 = new String(to.getBytes(ENCODING_GBK), ENCODING_ISO88591);
		if(fileType == FileType.File){
			status = (ftpClient.listFiles(from_1).length == 1);
		}else if(fileType == FileType.Directory){
			String workingDirectory = ftpClient.printWorkingDirectory();
			if(status = ftpClient.changeWorkingDirectory(from_1)){
				ftpClient.changeWorkingDirectory(workingDirectory);
			}
		}else{
			logger.warn("不支持的文件类型，文件类型(fileType)的值只能是 1(文件) 或 2(文件夹)");
			return false;
		}
		
		if(!status){
			logger.info("远程文件(夹)不存在 [" + from + "]");
			return false;
		}
		
		status = ftpClient.rename(from_1, to_1);
		int i = -1;
		if(!status && (i = to.lastIndexOf('/')) > 0){
			createDirectory(to.substring(0, i));
			status = ftpClient.rename(from_1, to_1);
		}
		return status;
	}
	
	private void hasText(String str){
	    if(StringUtils.isBlank(str))
	        throw new RuntimeException(" this String argument must have text; it must not be null, empty, or blank");
	}
	
	public static void main(String[] args) {
		FtpUtils ftpUtils = new FtpUtils();
		ftpUtils.connectServer("61.152.201.12", 21, "cnstock", "cnstock!@#", "/xml");
		try {
			FTPClient client = ftpUtils.getFtpClient();
			System.out.println(ftpUtils.ftpClient.printWorkingDirectory());

			InputStream is = client.retrieveFileStream("a/bak/0113101410_24917203.xml");

//			File ruleFile = new File("E:\\myEclipseWorkspace\\media360_ftpget_Google\\config\\rule-hailiang.xml");
//			News_Document d = (News_Document) DigesterLoader.createDigester(ruleFile.toURI().toURL()).parse(is);
//			System.out.println(d.getDocHeadline());
			is.close();
			ftpUtils.ftpClient.completePendingCommand();

			ftpUtils.rename("a/bak/0113101410_24917203.xml", "a/bak//1.xml", FileType.File);

			System.out.println("*************************************************");

			System.out.println(ftpUtils.ftpClient.printWorkingDirectory());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ftpUtils.disconnectServer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("============");
	}

	public static enum UploadStatus {
		Create_Directory_Fail, // 远程服务器相应目录创建失败
		Create_Directory_Success, // 远程服务器闯将目录成功
		Upload_New_File_Success, // 上传新文件成功
		Upload_New_File_Failed, // 上传新文件失败
		File_Exits, // 文件已经存在
		Remote_Bigger_Local, // 远程文件大于本地文件
		Upload_From_Break_Success, // 断点续传成功
		Upload_From_Break_Failed, // 断点续传失败
		Delete_Remote_Faild; // 删除远程文件失败
	}

	public enum DownloadStatus {
		Remote_File_Noexist, // 远程文件不存在
		Local_Bigger_Remote, // 本地文件大于远程文件
		Download_From_Break_Success, // 断点下载文件成功
		Download_From_Break_Failed, // 断点下载文件失败
		Download_New_Success, // 全新下载文件成功
		Download_New_Failed; // 全新下载文件失败
	}
	
	public enum FileType {
		File, // 文件
		Directory, // 文件夹
	}
}