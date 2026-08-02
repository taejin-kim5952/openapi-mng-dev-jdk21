package com.kt.openapi.web.cmm.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * 파일 유틸리티
 * 파일 및 디렉토리의 생성 변경등을 단순하게 처리 할 수 있게 작성된 유틸리티
 */
public class FileUtil {

	private static final Map<String, String> CONTENTTYPES = new HashMap<String, String>();

	/**
	 * 기본 Conetent Type ( Stream )
	 * TODO WebUtil로 이동해야 할 듯..
	 */
	private static final String DEFAULT_CONTENTTYPE = "application/octet-stream";

	static {
		CONTENTTYPES.put("dwg", "application/acad");
		CONTENTTYPES.put("ccad", "application/clariscad");
		CONTENTTYPES.put("dxf", "application/dxf");
		CONTENTTYPES.put("mdb", "application/msaccess");
		CONTENTTYPES.put("doc", "application/msword");
		CONTENTTYPES.put("bin", "application/octet-stream");
		CONTENTTYPES.put("pdf", "application/pdf");
		CONTENTTYPES.put("ai", "application/postscript");
		CONTENTTYPES.put("ps", "application/postscript");
		CONTENTTYPES.put("eps", "application/postscript");
		CONTENTTYPES.put("rtf", "application/rtf");
		CONTENTTYPES.put("cdf", "application/x-cdf");
		CONTENTTYPES.put("csh", "application/x-csh");
		CONTENTTYPES.put("dvi", "application/x-dvi");
		CONTENTTYPES.put("js", "application/x-javascript");
		CONTENTTYPES.put("latex", "application/x-latex");
		CONTENTTYPES.put("mif", "application/x-mif");
		CONTENTTYPES.put("xls", "application/x-msexcel");
		CONTENTTYPES.put("ppt", "application/x-mspowerpoint");
		CONTENTTYPES.put("tcl", "application/x-tcl");
		CONTENTTYPES.put("tex", "application/x-tex");
		CONTENTTYPES.put("texi", "application/x-texinfo");
		CONTENTTYPES.put("t", "application/x-troff");
		CONTENTTYPES.put("tr", "application/x-troff");
		CONTENTTYPES.put("roff", "application/x-troff");
		CONTENTTYPES.put("man", "application/x-troff-man");
		CONTENTTYPES.put("me", "application/x-troff-me");
		CONTENTTYPES.put("ms", "application/x-troff-ms");
		CONTENTTYPES.put("src", "application/x-wais-source");
		CONTENTTYPES.put("zip", "application/zip");
		CONTENTTYPES.put("au", "audio/basic");
		CONTENTTYPES.put("snd", "audio/basic");
		CONTENTTYPES.put("aif", "audio/x-aiff");
		CONTENTTYPES.put("aiff", "audio/x-aiff");
		CONTENTTYPES.put("aifc", "audio/x-aiff");
		CONTENTTYPES.put("wav", "audio/x-wav");
		CONTENTTYPES.put("gif", "image/gif");
		CONTENTTYPES.put("ief", "image/ief");
		CONTENTTYPES.put("jpeg", "image/jpeg");
		CONTENTTYPES.put("jpg", "image/jpeg");
		CONTENTTYPES.put("jpe", "image/jpeg");
		CONTENTTYPES.put("tiff", "image/tiff");
		CONTENTTYPES.put("tif", "image/tiff");
		CONTENTTYPES.put("ras", "image/x-cmu-raster");
		CONTENTTYPES.put("pnm", "image/x-portable-anymap");
		CONTENTTYPES.put("pbm", "image/x-portable-bitmap");
		CONTENTTYPES.put("pgm", "image/x-portable-graymap");
		CONTENTTYPES.put("ppm", "image/x-portable-pixmap");
		CONTENTTYPES.put("rgb", "image/x-rgb");
		CONTENTTYPES.put("xbm", "image/x-xbitmap");
		CONTENTTYPES.put("xpm", "image/x-xpixmap");
		CONTENTTYPES.put("xwd", "image/x-xwindowdump");
		CONTENTTYPES.put("gzip", "multipart/x-gzip");
		CONTENTTYPES.put("zip", "multipart/x-zip");
		CONTENTTYPES.put("css", "text/css");
		CONTENTTYPES.put("html", "text/plain");
		CONTENTTYPES.put("htm", "text/plain");
		CONTENTTYPES.put("txt", "text/plain");
		CONTENTTYPES.put("trx", "text/richtext");
		CONTENTTYPES.put("tsv", "text/tab-separated- values");
		CONTENTTYPES.put("xml", "text/xml");
		CONTENTTYPES.put("etx", "text/x-setext");
		CONTENTTYPES.put("xsl", "text/xsl");
		CONTENTTYPES.put("mpeg", "video/mpeg");
		CONTENTTYPES.put("mpg", "video/mpeg");
		CONTENTTYPES.put("mpe", "video/mpeg");
		CONTENTTYPES.put("qt", "video/quicktime");
		CONTENTTYPES.put("mov", "video/quicktime");
		CONTENTTYPES.put("avi", "video/x-msvideo");
		CONTENTTYPES.put("movie", "video/x-sgi-movie");
		CONTENTTYPES.put("flb", "text/html");
	}

	/**
	 * 파일의 확장자를 반환한다.
	 * @param fileName
	 * @return 확장자(. 제외)
	 */
	public static String getExtension(String fileName) {
		//--[tag:SR-20210427][add][prevent null exception]
		String newFileName = ((fileName == null) ? "" : fileName);
		int idx = newFileName.lastIndexOf(".");
		String ext = idx == -1 ? "" : newFileName.substring(idx + 1);
		return ext;
	}

	/**
	 * 파일명에서 확장자('.'포함)를 제거한 순수 파일명을 반환한다.
	 * @param fileName
	 * @return 확장자가 제거된 파일명
	 */
	public static String removeExtension(String fileName) {
		int idx = fileName.lastIndexOf(".");
		String fureFileName = idx == -1 ? fileName : fileName.substring(0, idx);
		return fureFileName;
	}

	/**
	 * fullPath에 해당하는 디렉토리 위치를 검색한다.
	 * @param fullPath
	 * @return 디렉토리 여부
	 */
	public static boolean isExistDirectory(String fullPath) {
		File dir = new File(fullPath);
		if (dir.exists() && dir.isDirectory()) {
			return true;
		}
		return false;
	}

	/**
	 * fullPath에 해당하는 파일을 작성한다.
	 * @param fullPath
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static boolean makeDirectory(String fullPath) throws IllegalArgumentException {
		if (fullPath == null) {
			throw new IllegalArgumentException("fullPath can't be null");
		}
		if (isExistDirectory(fullPath))
			return true;
		File dir = new File(fullPath);

		return dir.mkdirs();
	}

	/**
	 * 대상 파일을 톡증 위치에 저장한다.
	 * @param file 대상 파일
	 * @param fullPathName 저장될 파일 위치(파일명 포함)
	 * @throws IOException 오류 발생
	 */
	public static void writeFile(File file, String fullPathName) throws IOException {
		OutputStream bos = null;
		FileInputStream fis = new FileInputStream(file);
		try {
			bos = new FileOutputStream(fullPathName);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
		} finally {
			if (bos != null)
				bos.close();
			if (fis != null)
				fis.close();
		}
	}


	/**
	 * 파일을 byte로 반환한다.
	 * 주의) 큰 용량의 파일은 문제를 야기 할 수 있다.
	 * @param file 파일
	 * @return byte
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		final int BUFFER_LEN = 2048;

		byte[] result;
		FileInputStream fis = null;
		ByteArrayOutputStream byteOutputStream = null;
		try {
			fis = new FileInputStream(file);
			byteOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[BUFFER_LEN];
			while (true) {
				int len = fis.read(buffer, 0, BUFFER_LEN);
				if (len == -1)
					break;
				byteOutputStream.write(buffer, 0, len);
			}
			result = byteOutputStream.toByteArray();
		} finally {
			if (fis != null) try { fis.close(); } catch (Exception e) {
			  //-- [2023:codeeyes][empty_block issue]
			}
			if (byteOutputStream != null) try { byteOutputStream.close(); } catch (Exception e) {
			  //-- [2023:codeeyes][empty_block issue]
			}
		}

		return result;
	}

	/**
	 * 디렉토리를 삭제한다.( 내부에 포함된 모든 파일 및 디렉토리를 모두 삭제 한다.)
	 * 주의) 오류 발생시 권한에 주의해 주세요.
	 * @param dir 디렉토리
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * 파일에 대한 ContentType을 반환한다.
	 * TODO WebUtil로 이동해야 할듯.
	 * @param extension
	 * @return
	 */
	public static String getContentTypeOfFile(String extension) {
		String contentType = null;
		if (extension != null) {
			contentType = (String) CONTENTTYPES.get(extension.toLowerCase());
		}
		if (contentType == null) {
			contentType = DEFAULT_CONTENTTYPE;
		}
		return contentType;
	}

    //--[tag:SR-20210427][add][extention filtering][구조점검조치]
    //-- [i]validExtList: ','구분
    public static boolean IsFilteredFileExtention(String filename, String validExtList) {
    	String newValidExtList = ("," + ((validExtList == null) ? "" : validExtList) + ",");
   		String ext = FileUtil.getExtension(filename);
		return (newValidExtList.indexOf("," + ext + ",") != -1);
    }
}
