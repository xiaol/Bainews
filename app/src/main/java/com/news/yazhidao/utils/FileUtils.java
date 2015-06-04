package com.news.yazhidao.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils
{
	/**
	 * 获取文件操作对象
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static File getFile(String path)
	{
		File file = new File(path);
		return file;
	}

	/**
	 * 
	 * 获取目录,如果目录不存在会创建
	 * 
	 * @return 目录路径：./
	 */
	public static String getDirectory(String path)
	{
		if (!path.endsWith("/"))
		{
			path += "/";
		}
		File f = new File(path);
		try
		{
			if (!f.exists())
			{
				if (!f.mkdirs())
				{
					throw new Exception("创建目录" + path + "失败");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * 文件是否存在
	 * 
	 * @param fpath
	 *            文件完整路径
	 */
	public static boolean isFileExists(String fpath)
	{
		File f = new File(fpath);
		return f.exists();
	}

	/**
	 * 删除文件
	 * 
	 * @param fpath
	 *            文件完整路径
	 */
	public static void deleteFile(String fpath)
	{
		File f = new File(fpath);
		if (f.exists())
		{
			f.delete();
		}
	}

	/**
	 * 功能描述:获取文件大小
	 * 
	 * @param fpath
	 * @return
	 */
	public static long getFileLength(String fpath)
	{
		long lng = 0;
		File f = new File(fpath);
		lng = f.length();
		return lng;
	}

	/**
	 * 功能描述:获取扩展名
	 * 
	 * @param fpath
	 * @return 扩展名
	 */
	public static String getFileExtension(String fpath)
	{
		String extension = "";
		File f = new File(fpath);
		if (f.isFile())
		{
			int dotIdx = fpath.lastIndexOf(".");
			extension = fpath.substring(dotIdx + 1, fpath.length());
		}
		return extension;
	}

	/**
	 * 功能描述:获取文件名
	 * 
	 * @param fpath
	 * @return 文件名
	 */
	public static String getFileName(String fpath)
	{
		String fileName = "";
		File f = new File(fpath);
		if (f.isFile())
		{
			fileName = f.getName();
		}
		return fileName;
	}

	/**
	 * 创建新文件，如果文件已经存在将删除后重新创建
	 * 
	 * @param fpath
	 *            文件完整路径
	 * @return 文件是否创建成功
	 */
	public static boolean createNewFile(String fpath)
	{
		boolean brtn = false;
		File f = new File(fpath);
		if (f.exists())
		{
			f.delete();
		}
		try
		{
			int nend = fpath.lastIndexOf("/");
			String strdir = fpath.substring(0, nend);
			getDirectory(strdir);
			brtn = f.createNewFile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return brtn;
	}

	/**
	 * 功能描述:获取文件的字节数组
	 * 
	 * @param filePath
	 *            文件完整路徑
	 * @return 字节数组
	 */
	public static byte[] getBytes(String filePath)
	{
		byte[] buffer = null;
		try
		{
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1)
			{
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 功能描述:字节数组转换为文件
	 * 
	 * @param bfile
	 *            字节数组
	 * @param filePath
	 *            文件保存目录
	 * @param fileName
	 *            文件名
	 */
	public static void getFile(byte[] bfile, String filePath, String fileName)
	{
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try
		{
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory())
			{// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (bos != null)
			{
				try
				{
					bos.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}

}
