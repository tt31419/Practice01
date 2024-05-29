package com.tt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.houbb.opencc4j.util.ZhTwConverterUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.tt.enums.CustErrorCode;
import com.tt.CustException;
import com.tt.Constrain;

public class CommonUtils {

	private String LOG_TYPE_INFO = Constrain.LOG_TYPE_INFO;

	private String LOG_TYPE_DEBUG = Constrain.LOG_TYPE_DEBUG;

	private String LOG_TYPE_FINE = Constrain.LOG_TYPE_FINE;

	private String LOG_TYPE_WARNING = Constrain.LOG_TYPE_WARNING;

	protected String className = this.getClass().getSimpleName();

	private static CommonUtils instance = null;

	protected String hostName = null;

	protected Logger log = null;

	private boolean init = false;

	protected CommonUtils() {
	}

	protected CommonUtils(Logger log) {
		this();
		this.initLog(log);
	}

	public static CommonUtils getInstance() {
		try {
			if (instance == null) {
				instance = new CommonUtils();
			}
			instance.initLog(null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return instance;
	}

	public static CommonUtils getInstance(Logger log) {
		try {
			if (instance == null) {
				instance = new CommonUtils(log);
			} else {
				instance.initLog(log);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return instance;
	}

	public static void loadLog4j() {
		try {
			Properties props = new Properties();
			props.load(CommonUtils.class.getResourceAsStream("/log4j.properties"));
			PropertyConfigurator.configure(props);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected void initLog(Logger log) {
		try {
			if (!this.init) {
				loadLog4j();
			}
			this.log = ((log == null) ? Logger.getLogger(this.className) : log);
		} catch (Throwable e) {
			this.logPrintStackTrace(this.log, e);
		} finally {
			this.init = true;
		}
	}

	/**
	 * 依據指定長度切割字串
	 * 
	 * @param value     字串
	 * @param valueSize 指定長度
	 * @return List&lt;String&gt;
	 * @author pochenliu
	 */
	public List<String> splitValues(String value, int valueSize) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		List<String> contents = new ArrayList<String>();
		if (StringUtils.length(value) > valueSize) {
			StringBuffer subContent = new StringBuffer();
			int subContentLength = 0;
			char[] charArray = value.toCharArray();
			for (int i = 0; i < charArray.length; i++) {
				char c = charArray[i];
				if (CharUtils.isAscii(c)) {
					subContentLength += 1;
				} else {
					subContentLength += 3;
				}
				subContent.append(c);
				if (subContentLength > valueSize || i == charArray.length - 1) {
					contents.add(subContent.toString());
					subContent = new StringBuffer();
					subContentLength = 0;
				}
			}
		} else {
			contents.add(value);
		}
		return ((contents.size() == 0) ? null : contents);
	}

	public String splitValue(String value, int valueSize) {
		String val = ((value == null) ? "" : value);
		if (StringUtils.length(val) > valueSize) {
			StringBuffer subContent = new StringBuffer();
			int subContentLength = 0;
			char[] charArray = val.toCharArray();
			for (int i = 0; i < charArray.length; i++) {
				char c = charArray[i];
				if (CharUtils.isAscii(c)) {
					subContentLength += 1;
				} else {
					subContentLength += 3;
				}
				subContent.append(c);
				if (subContentLength > valueSize) {
					return subContent.toString();
				}
			}
		}
		return val;
	}

	/**
	 * 計算執行所耗費的時間
	 * 
	 * @param sDate 起始時間
	 * @param eDate 結束時間
	 * @return timeStr
	 * @author pochenliu
	 */
	public String calCostTime(Date sDate, Date eDate) {
		try {
			if (sDate == null || eDate == null || sDate.after(eDate)) {
				return null;
			}
			Calendar sCal = Calendar.getInstance();
			sCal.setTime(sDate);
			Calendar eCal = Calendar.getInstance();
			eCal.setTime(eDate);
			long delta = eCal.getTimeInMillis() - sCal.getTimeInMillis();
			String timeStr = "";
			long day = delta / (24 * 60 * 60 * 1000);
			if (day > 0l) {
				timeStr += day + "d";
			}
			delta %= 24 * 60 * 60 * 1000;
			long hour = ((delta > 0) ? delta / (60 * 60 * 1000) : 0l);
			if (hour > 0l) {
				timeStr += ((hour < 10) ? "0" : "") + hour + "hr";
			}
			delta %= 60 * 60 * 1000;
			long minute = ((delta > 0) ? delta / (60 * 1000) : 0l);
			if (minute > 0l) {
				timeStr += ((minute < 10) ? "0" : "") + minute + "min";
			}
			delta %= 60 * 1000;
			long second = ((delta > 0) ? delta / 1000 : 0l);
			if (second > 0l) {
				timeStr += ((second < 10) ? "0" : "") + second + "sec";
			}
			long milliseconds = ((delta > 0) ? delta % 1000 : 0l);
			if (milliseconds > 0l) {
				timeStr += ((milliseconds < 10) ? "00" : ((milliseconds < 100) ? "0" : "")) + milliseconds + "ms";
			}
			return timeStr;
		} catch (Throwable e) {
			this.logPrintStackTrace(e);
		}
		return null;
	}

	public String dateFormat(String source) {
		try {
			if (StringUtils.isBlank(source)) {
				return null;
			}
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			return new SimpleDateFormat("yyyyMMdd").format(df.parse(source));
		} catch (Throwable e) {
			this.logPrintStackTrace(e);
		}
		return null;
	}

	public String dateTimeFormat(String source) {
		try {
			if (StringUtils.isBlank(source)) {
				return null;
			}
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			return new SimpleDateFormat("yyyyMMddHHmmss").format(df.parse(source));
		} catch (Throwable e) {
			this.logPrintStackTrace(e);
		}
		return null;
	}

	public String timeToString(Date date) {
		return this.timeToString(date, null);
	}

	public String timeToString(Date date, String pattern) {
		if (date == null) {
			return null;
		} else if (StringUtils.isBlank(pattern)) {
			pattern = "yyyy/MM/dd HH:mm:ss";
		}
		return new SimpleDateFormat(pattern).format(date.getTime());
	}

	/**
	 * 郵遞區號5碼取前三碼
	 */
	public String zipCodeFormat(String source) {
		return ((source != null && source.length() > 3) ? source.substring(0, 3) : "");
	}

	public String findStartTime(int deltaDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, deltaDay);
		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTime());
	}

	public String findCurrentTime() {
		return this.findCurrentTime(null);
	}

	public String findCurrentTime(String pattern) {
		if (StringUtils.isBlank(pattern)) {
			pattern = "yyyy/MM/dd HH:mm:ss";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return new SimpleDateFormat(pattern).format(cal.getTime());
	}

	public <T> Map<String, String> popFieldMap(Class<T> klass) throws CustException {
		Map<String, String> map = new HashMap<String, String>();
		try {
			if (klass == null) {
				throw new CustException(CustErrorCode.ERROR_90003, "klass");
			}
			String[] propOrders = ((XmlType) klass.getAnnotation(XmlType.class)).propOrder();
			if (propOrders == null || propOrders.length == 0) {
				throw new CustException(CustErrorCode.ERROR_90003, "propOrders");
			}
			for (String propOrder : propOrders) {
				try {
					if (StringUtils.isBlank(propOrder)) {
						continue;
					}
					Field field = klass.getDeclaredField(propOrder);
					if (field == null || !propOrder.equals(field.getName())) {
						continue;
					}
					map.put(field.getAnnotation(XmlElement.class).name(), propOrder);
				} catch (Throwable e) {
				}
			}
		} catch (CustException e) {
			this.logPrintStackTrace(e);
			throw e;
		} catch (Throwable e) {
			this.logPrintStackTrace(e);
			throw new CustException(e);
		}
		return ((map != null && map.size() > 0) ? map : null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Object> populateMap(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		Class klass = obj.getClass();
		String[] propOrders = null;
		try {
			propOrders = ((XmlType) klass.getAnnotation(XmlType.class)).propOrder();
		} catch (Throwable e) {
		}
		boolean isEmptyPropOrders = propOrders == null || propOrders.length == 0;
		boolean includeSuperClass = klass.getClassLoader() != null;
		Method[] methods = (includeSuperClass) ? klass.getMethods() : klass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i += 1) {
			try {
				Method method = methods[i];
				if (Modifier.isPublic(method.getModifiers())) {
					String name = method.getName();
					String key = "";
					if (name.startsWith("get")) {
						if ((",getClass,getDeclaringClass,").indexOf("," + name + ",") != -1) {
							key = "";
						} else {
							key = name.substring(3);
						}
					} else if (name.startsWith("is")) {
						for (String propOrder : propOrders) {
							// for boolean Parameter
							if (StringUtils.equals(propOrder, name)) {
								map.put(name, method.invoke(obj, (Object[]) null));
							}
						}
						if (map.get(name) != null) {
							continue;
						}
						key = name.substring(2);
					}
					if (key.length() > 0 && Character.isUpperCase(key.charAt(0))
							&& method.getParameterTypes().length == 0) {
						if (key.length() == 1) {
							key = key.toLowerCase();
						} else if (!Character.isUpperCase(key.charAt(1))) {
							key = key.substring(0, 1).toLowerCase() + key.substring(1);
						}
						if (isEmptyPropOrders) {
							map.put(key, method.invoke(obj, (Object[]) null));
						} else {
							for (String propOrder : propOrders) {
								if (StringUtils.equals(propOrder, key)) {
									map.put(key, method.invoke(obj, (Object[]) null));
								}
							}
						}
					}
				}
			} catch (Throwable e) {
			}
		}
		return ((map != null && map.size() > 0) ? map : null);
	}

	public String objToJSON(Object obj) {
		return this.objToJSON(obj, Constrain.DEFAULT_INDENTFACTOR);
	}

	public String objToJSON(Object obj, int indentFactor) {
		try {
			String json = new JSONObject(populateMap(obj)).toString(indentFactor);
			if (!StringUtils.isBlank(json)) {
				json = json.replace("\"null\"", "null");
			}
			return json;
		} catch (Throwable e) {
		}
		return null;
	}

	public String formatJSON(String json) {
		return this.formatJSON(json, Constrain.DEFAULT_INDENTFACTOR);
	}

	public String formatJSON(String json, int indentFactor) {
		try {
			if (StringUtils.isBlank(json)) {
				return null;
			}
			return new JSONObject(json).toString(indentFactor);
		} catch (Throwable e) {
		}
		return null;
	}

	public <T> List<T> jsonToObjs(String json, Class<T> classObj) throws CustException {
		try {
			if (StringUtils.isBlank(json)) {
				throw new CustException(CustErrorCode.ERROR_90000, "json");
			}
			if (!json.startsWith("[")) {
				json = "[" + json;
			}
			if (!json.endsWith("]")) {
				json += "]";
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, classObj));
		} catch (CustException e) {
			this.logPrintStackTrace(e);
			throw e;
		} catch (Throwable e) {
			this.logPrintStackTrace(e);
			throw new CustException(e);
		}
	}

	public <T> T jsonToObj(String json, Class<T> classObj) throws CustException {
		List<T> pojos = this.jsonToObjs(json, classObj);
		return ((pojos != null && pojos.size() > 0) ? (T) pojos.get(0) : null);
	}

	public JsonObject jsonStrToJavaxJson(String json) {
		return (json != null) ? Json.createReader(new StringReader(json)).readObject() : null;
	}

	public String findHostName() {
		try {
			if (!StringUtils.isBlank(this.hostName)) {
				return this.hostName;
			}
			String hostNames = InetAddress.getLocalHost().getHostName();
			if (hostNames != null && !hostNames.equals("")) {
				hostNames = hostNames.toUpperCase();
				int dotPos = hostNames.indexOf(".");
				this.hostName = ((dotPos != -1) ? hostNames.substring(0, dotPos) : hostNames);
			}
		} catch (Throwable e) {
			this.logPrintStackTrace(e);
		}
		return this.hostName;
	}

	public boolean isNaN(String charStr) {
		if (StringUtils.isBlank(charStr)) {
			return true;
		}
		return !Pattern.compile("^[0-9]+$").matcher(charStr.trim()).matches();
	}

	public void logInfoPrint(String msg) {
		this.logPrint(this.log, this.LOG_TYPE_INFO, msg);
	}

	public void logInfoPrint(Logger log, String msg) {
		this.logPrint(log, this.LOG_TYPE_INFO, msg);
	}

	public void logDebugPrint(String msg) {
		this.logPrint(this.log, this.LOG_TYPE_DEBUG, msg);
	}

	public void logDebugPrint(Logger log, String msg) {
		this.logPrint(log, this.LOG_TYPE_DEBUG, msg);
	}

	public void logFinePrint(String msg) {
		this.logPrint(this.log, this.LOG_TYPE_FINE, msg);
	}

	public void logFinePrint(Logger log, String msg) {
		this.logPrint(log, this.LOG_TYPE_FINE, msg);
	}

	public void logWarningPrint(String msg) {
		this.logPrint(this.log, this.LOG_TYPE_WARNING, msg);
	}

	public void logWarningPrint(Logger log, String msg) {
		this.logPrint(log, this.LOG_TYPE_WARNING, msg);
	}

	public void logPrint(Logger log, String type, String msg) {
		try {
			if (StringUtils.isBlank(msg)) {
				return;
			}
			boolean isInfo = this.LOG_TYPE_INFO.equalsIgnoreCase(type);
			boolean isDebug = this.LOG_TYPE_DEBUG.equalsIgnoreCase(type);
			boolean isFine = this.LOG_TYPE_FINE.equalsIgnoreCase(type);
			boolean isWarning = this.LOG_TYPE_WARNING.equalsIgnoreCase(type);
			if (log != null) {
				if (isInfo) {
					log.info(msg);
				} else if (isDebug) {
					log.debug(msg);
				} else if (isFine) {
					log.debug(msg);
				} else if (isWarning) {
					log.error(msg);
				} else { // default
					log.info(msg);
				}
			} else {
				if (isInfo || isDebug || isFine) {
					System.out.println(msg);
				} else if (isWarning) {
					System.err.println(msg);
				} else { // default
					System.out.println(msg);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			if (log != null) {
				log.error("CommonUtils.logPrint() Throwable == ", e);
			} else {
				System.err.print("CommonUtils.logPrint() Throwable == " + e.getMessage());
			}
		}
	}

	public void logPrintStackTrace(Throwable e) {
		this.logPrintStackTrace(this.log, e);
	}

	public void logPrintStackTrace(Logger log, Throwable e) {
		String msg = "";
		String lineSeparator = System.getProperty("line.separator");
		try {
			if (e == null) {
				String exType = "Throwable";
				if (e instanceof CustException) {
					exType = "CPCException";
				} else if (e instanceof SQLException) {
					exType = "SQLException";
				}
				msg = exType + " is null !!" + lineSeparator;
				return;
			}
			String traceMsg = "";
			StackTraceElement[] traces = e.getStackTrace();
			if (traces != null && traces.length > 0) {
				for (StackTraceElement trace : traces) {
					if (trace == null) {
						continue;
					}
					traceMsg += trace + lineSeparator;
				}
			}
			msg = e + lineSeparator
					+ ((StringUtils.isBlank(traceMsg)) ? "No Stack Trace !!" + lineSeparator : traceMsg);
		} catch (Throwable ee) {
			msg = ee + lineSeparator;
		} finally {
			this.logPrint(log, this.LOG_TYPE_WARNING, msg);
		}
	}

	public boolean inputStreamHasData(InputStream is) {

		if (is == null) {
			return false;
		}

		try {
			if (is.available() > 0) {
				return true;
			}

			// Stream may not support available(), try reading a byte
			is.mark(1);
			int b = is.read();
			is.reset();
			return b != -1;

		} catch (Exception e) {
			return false; // Error reading stream
		}
	}

	public void printMap(Map<String, Object> map) {

		if (map == null) {
			return;
		}

		try {

			for (Map.Entry<String, Object> entry : map.entrySet()) {

				if (entry.getValue() != null) {
					System.out.println(entry.getValue());
				} else {
					System.out.println("Null value for key: " + entry.getKey());
				}

			}

		} catch (Exception e) {
			System.out.println("Error printing map");
		}

	}

	public void printClass(Object obj, int depth) {
		if (obj == null || depth > 4) { //
			return;
		}

		Class<?> clazz = obj.getClass();
		if (clazz.isArray()) {
			int length = Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				Object arrayElement = Array.get(obj, i);
				printClass(arrayElement, depth + 1);
			}
		} else if (obj instanceof Collection) {
			Collection<?> collection = (Collection<?>) obj;
			for (Object element : collection) {
				printClass(element, depth + 1);
			}
		} else if (obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) obj;
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				System.out.println("Key: " + entry.getKey());
				printClass(entry.getValue(), depth + 1);
			}
		} else {
			// Output field names and values
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					Object value = field.get(obj);
					System.out.println(indent(depth) + field.getName() + ": " + value);
					// Recursively print nested fields
					printClass(value, depth + 1);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// printClass用
	private String indent(int depth) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			sb.append("  ");
		}
		return sb.toString();
	}

	public String toTraditional(String input) {
		return ZhTwConverterUtil.toTraditional(input);
	}

	public com.google.gson.JsonArray convertStringToJsonArray(String input) {
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		com.google.gson.JsonElement element = parser.parse(input);
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		} else {
			return null;
		}
	}

	public boolean createTxtFile(String filePath, String fileName, StringBuilder content) {
		try {
			if (!filePath.endsWith("/")) {
				filePath += "/";
			}
			Path path = Paths.get(filePath);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath + fileName), StandardCharsets.UTF_8);
			writer.write(content.toString());
			writer.close();

			System.out.println(filePath + fileName + ".txt");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean moveFile(String sourcePath, String targetPath) {
		try {
			File sourceFile = new File(sourcePath);
			File targetFile = new File(targetPath);
			if (sourceFile.exists()) {
				sourceFile.renameTo(targetFile);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
