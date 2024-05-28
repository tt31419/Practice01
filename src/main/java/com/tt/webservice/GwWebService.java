package com.tt.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import com.tt.enums.CustErrorCode;
import com.tt.util.CommonUtils;
import com.tt.CustException;
import com.tt.enums.HttpType;

/**
 * base on HLR
 * 
 * @author davidchen
 */
public class GwWebService {

	private String className = this.getClass().getSimpleName();

	private static GwWebService instance = null;

	private Logger log4jLog = null;

	private GwWebService() {
	}

	public static GwWebService getInstance(Logger log4jLog) throws CustException {
		try {
			if (instance == null) {
				instance = new GwWebService();
			}
			instance.log4jLog = null;
			if (log4jLog != null) {
				instance.log4jLog = log4jLog;
			} else {
				CommonUtils.loadLog4j();
				instance.log4jLog = Logger.getLogger(GwWebService.class.getSimpleName());
			}
		} catch (Throwable e) {
			CommonUtils.getInstance().logPrintStackTrace(instance.log4jLog, e);
			throw new CustException(e);
		}
		return instance;
	}

	@SuppressWarnings("unused")
	private void printLog(String action, boolean isStart, Date startDate, Date endDate) {
		this.logInfoPrint("---- " + action + " " + ((isStart) ? "Start" : "End") + " at "
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format((((isStart) ? startDate : endDate))) + " ----");
		if (!isStart) {
			this.logInfoPrint(
					"---- " + action + " Cost " + CommonUtils.getInstance().calCostTime(startDate, endDate) + " ----");
		}
	}

	// public String callGetRestfulWebService(String url, String reqJSON) throws
	// CPCException {
	// return this.callRestfulWebServiceByHttpURLConnection(url,
	// HttpType.HTTP_GET.getType(), reqJSON);
	// }

	public String callPostRestfulWebService(String url, String reqJSON) throws CustException {
		return this.callRestfulWebServiceByHttpURLConnection(url, HttpType.HTTP_POST.getType(), reqJSON);
	}

	// public String callDeleteRestfulWebService(String url, String reqJSON) throws
	// CPCException {
	// return this.callRestfulWebServiceByHttpURLConnection(url,
	// HttpType.HTTP_DELETE.getType(), reqJSON);
	// }

	// public String callPutRestfulWebService(String url, String reqJSON) throws
	// CPCException {
	// return this.callRestfulWebServiceByHttpURLConnection(url,
	// HttpType.HTTP_PUT.getType(), reqJSON);
	// }

	public String callRestfulWebService(String url, String reqJSON) throws CustException {
		return this.callPostRestfulWebService(url, reqJSON);
	}

	private String callRestfulWebServiceByHttpURLConnection(String url, String httpType, String reqJSON)
			throws CustException {
		HttpURLConnection conn = null;
		PrintWriter out = null;
		OutputStreamWriter os = null;
		BufferedReader br = null;
		String resp = "";
		try {

			// this.logInfoPrint(this.className +
			// ".callRestfulWebServiceByHttpURLConnection() url == " + url);
			// this.logInfoPrint(this.className +
			// ".callRestfulWebServiceByHttpURLConnection() httpType == " + httpType);
			// this.logInfoPrint(this.className +
			// ".callRestfulWebServiceByHttpURLConnection() reqJSON == " + reqJSON);

			boolean isGet = StringUtils.equals(HttpType.HTTP_GET.getType(), httpType);
			boolean isPost = StringUtils.equals(HttpType.HTTP_POST.getType(), httpType);
			boolean isDelete = StringUtils.equals(HttpType.HTTP_DELETE.getType(), httpType);
			boolean isPut = StringUtils.equals(HttpType.HTTP_PUT.getType(), httpType);

			if (StringUtils.isBlank(url)) {
				throw new CustException(CustErrorCode.ERROR_90000, "webservice url");
			} else if (!isGet && !isPost && !isDelete && !isPut) {
				throw new CustException(CustErrorCode.ERROR_90008, "webservice type", httpType);
			} else if (StringUtils.isBlank(reqJSON)) {
				throw new CustException(CustErrorCode.ERROR_90000, "webservice request");
			}

			// if(isGet || isDelete) {
			// url += "?processParams=" + URLEncoder.encode(reqJSON, "UTF-8");
			// }

			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod(httpType);
			conn.setRequestProperty("Content-type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(isPost || isPut);
			conn.setDoInput(true);
			if (isDelete) {
				conn.setInstanceFollowRedirects(false);
				conn.setUseCaches(false);
			}

			int timeout = 10; // 單位(秒)
			try {
				// JDK 1.5, JDK 1.5+
				conn.setConnectTimeout(timeout * 1000); // 連接主機的超時時間（單位:毫秒）
				conn.setReadTimeout(timeout * 3 * 1000); // 從主機讀取數據的超時時間（單位:毫秒）
			} catch (Throwable e) {
				// JDK 1.5-
				System.setProperty("sun.net.client.defaultConnectTimeout", "" + (timeout * 1000)); // 連接主機的超時時間（單位:毫秒）
				System.setProperty("sun.net.client.defaultReadTimeout", "" + (timeout * 3 * 1000)); // 從主機讀取數據的超時時間（單位:毫秒）
			}

			try {
				os = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				out = new PrintWriter(os, true);
				if (isPost || isPut) {
					out.write(reqJSON);
				}
			} catch (Throwable e) {
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (Throwable e) {
					this.logPrintStackTrace(e);
				}
				try {
					if (out != null) {
						out.flush();
					}
				} catch (Throwable e) {
					this.logPrintStackTrace(e);
				}
			}

			int responseCode = conn.getResponseCode();
			boolean isRespOK = this.isByPassRespCode(responseCode);
			br = new BufferedReader(
					new InputStreamReader(((isRespOK) ? conn.getInputStream() : conn.getErrorStream())));
			String errMsg = ((isRespOK) ? "" : "HttpURLConnection ResponseCode == " + responseCode);
			String line = "";
			while ((line = br.readLine()) != null) {
				resp = resp.concat(line);
			}

			this.logInfoPrint(errMsg + "\r\nResponse:\r\n" + resp);

			if (!isRespOK) {
				throw new CustException(CustErrorCode.ERROR_90009, "HttpURLConnection ResponseCode", "" + responseCode);
			}

		} catch (CustException e) {
			e.printStackTrace();
			this.logPrintStackTrace(e);
			this.logWarningPrint(
					this.className + ".callRestfulWebServiceByHttpURLConnection() CPCException == " + e.getMessage());
			throw e;
		} catch (Throwable e) {
			this.logPrintStackTrace(e);
			this.logWarningPrint(
					this.className + ".callRestfulWebServiceByHttpURLConnection() Throwable == " + e.getMessage());
			throw new CustException(e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Throwable e) {
				this.logPrintStackTrace(e);
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (Throwable e) {
				this.logPrintStackTrace(e);
			}
			try {
				if (conn != null) {
					conn.disconnect();
				}
			} catch (Throwable e) {
				this.logPrintStackTrace(e);
			}
		}
		return ((StringUtils.isBlank(resp)) ? null : resp);
	}

	private boolean isByPassRespCode(int respCode) {
		return respCode == HttpURLConnection.HTTP_OK;
	}

	private void logInfoPrint(String msg) {
		CommonUtils.getInstance(this.log4jLog).logInfoPrint(msg);
	}

	@SuppressWarnings("unused")
	private void logDebugPrint(String msg) {
		CommonUtils.getInstance(this.log4jLog).logDebugPrint(msg);
	}

	private void logWarningPrint(String msg) {
		CommonUtils.getInstance(this.log4jLog).logWarningPrint(msg);
	}

	private void logPrintStackTrace(CustException e) {
		this.logPrintStackTrace((Throwable) e);
	}

	private void logPrintStackTrace(Throwable e) {
		CommonUtils.getInstance(this.log4jLog).logPrintStackTrace(e);
	}

	@SuppressWarnings("unused")
	private String mergeUrl(String url1, String url2) {
		if (StringUtils.isBlank(url1)) {
			return url2;
		} else if (StringUtils.isBlank(url2)) {
			return url1;
		}
		String url = ((url1.endsWith("/")) ? url1.substring(0, url1.length() - 1) : url1)
				+ ((!url2.startsWith("/")) ? "/" + url2 : url2);
		return url;
	}

	/*
	 * T 開始
	 *
	 */
	public StringBuilder urlToSB(String urlStr) {
		StringBuilder content = new StringBuilder();
		try {
			if (urlStr == null) {
				return content;
			}

			if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) {
				urlStr = "http://" + urlStr;
			}

			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			char[] buffer = new char[1024];
			int bytesRead;
			while ((bytesRead = reader.read(buffer, 0, buffer.length)) != -1) {
				content.append(buffer, 0, bytesRead);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public Document urlToJsoupDoc(Map<String, String> urlData) {
		System.out.println("urlToIs");
		HttpURLConnection connection = null;
		InputStream is = null;
		Document doc = null;
		try {
			if (urlData == null || urlData.isEmpty()) {
				return null;
			}
			String urlStr = urlData.get("url");
			if (StringUtils.isBlank(urlStr) || !urlStr.startsWith("http://") && !urlStr.startsWith("https://")) {
				return null;
			}
			String cookie = urlData.get("Cookie");
			String referer = urlData.get("Referer");
			if (StringUtils.isBlank(cookie)) {
				return null;
			}
			if (StringUtils.isBlank(referer)) {
				return null;
			}
			System.out.println("done check urlData");
			connection = (HttpURLConnection) new URL(urlStr).openConnection();
			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml");
			connection.setRequestProperty("Accept-Encoding", "gzip");// TODO other encoding
			connection.setRequestProperty("Accept-Language", "zh-TW,zh");
			connection.setRequestProperty("Cache-Control", "max-age=0");
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Cookie", urlData.get("Cookie"));
			connection.setRequestProperty("Referer", urlData.get("Referer"));
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0");
			System.out.println("connection." + connection.toString());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("HTTP_OK");
				is = connection.getInputStream();
				try {
					if (CommonUtils.getInstance().inputStreamHasData(is)) {
						// TODO other encoding
						is = new GZIPInputStream(is);
						doc = org.jsoup.Jsoup.parse(is, "UTF-8", "");
					} else {
						System.out.println("url IS no data");
					}
				} catch (Exception e) {
					e.printStackTrace();
					// System.out.println("GZIPInputStream" + e.getMessage());
				}
			} else {
				System.out.println("HTTP Error: " + connection.getResponseCode());
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// Handle the exception
				}
			}
		}
		return doc;
	}

}
