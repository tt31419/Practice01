package com.tt.test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import com.tt.Constrain;
import com.tt.util.CommonUtils;
import java.util.zip.GZIPInputStream;

public class GetNovelByUrl {

	private static String FILENAME = "novelXXX.txt";
	private static String FILEDIRECTORY = "G:\\TXT\\Novel";

	public static void main(String[] args) {
		try {
			String URL = "";
			downloadAndSaveContent(URL, FILENAME, FILEDIRECTORY);
		} catch (Exception e) {

		}
	}

	private static void downloadAndSaveContent(String url, String fileName, String fileDirectory) {
		// StringBuilder sbContent = getJsonContentFromUrl(url);

		if (fileExists(fileName, fileDirectory)) {
			fileName = generateFileName(fileName, fileDirectory);
		}
	}

	private static StringBuilder getJsonContentFromUrl(String url) {
		StringBuilder content = new StringBuilder();
		// JSONParser jsonParser = new JSONParser();

		try {
			// String encodedURL = URLEncoder.encode(url,
			// StandardCharsets.UTF_8.toString());
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml");
			connection.setRequestProperty("Accept-Encoding", "gzip");
			connection.setRequestProperty("Accept-Language", "zh-TW,zh");
			connection.setRequestProperty("Cache-Control", "max-age=0");
			connection.setRequestProperty("Cookie", "night=1;" + Constrain.TEST_URL);	// load from local file / SQL /TXT
			connection.setRequestProperty("Referer", Constrain.TEST_URL);	// load from local file / SQL /TXT
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0");

			boolean showHtml = false;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				if (showHtml) {
					// try (BufferedReader reader = new BufferedReader(
					// new InputStreamReader(connection.getInputStream()))) {
					// String line;
					// while ((line = reader.readLine()) != null) {
					// System.out.println(line);
					// }
					// }
				} else {
					System.out.println("===11");

					InputStream is = null;
					try {
						is = new GZIPInputStream(connection.getInputStream());
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("TEST" + e.getMessage());
					}

					System.out.println("===222");

					if (CommonUtils.getInstance().inputStreamHasData(is)) {
						Document doc = Jsoup.parse(is, "UTF-8", "");

						Element title = doc.select("title").first();
						System.out.println("title ------== " + title);
						// System.out.print("doc ------== " + doc.html());

						Elements links = doc.select("div.chapter-content a");

						// System.out.println("links ------== " + links.text());
						// for (Element link : links) {
						// 	String href = link.attr("href");
						// 	String text = link.text();
						// 	System.out.println("Href: " + href);
						// 	System.out.println("Text: " + text);
						// }
						// String nextPageUrl = "";

						// JsonObject jsonChapters = Json.createReader(new
						// StringReader((doc.select("#f-chapters-json").first().outerHtml()))).readObject();
						// System.out.println("jsonChapters == " + jsonChapters);

						Elements scripts = doc.select("#chapters-json");
						// System.out.println("scripts == " + scripts.outerHtml() + "===");
						// System.out.println(scripts.outerHtml());
						// if (scripts != null && !scripts.isEmpty()) {
						//// System.out.println("scripts == " + scripts.outerHtml());
						// for (Element script : scripts) {
						// String strScript = script.outerHtml();
						// System.out.println("scripts =1111 ");
						// System.out.println(strScript);
						// if (!StringUtils.isBlank(strScript)) {
						// System.out.println("scripts =222 " + strScript);
						//// String jsonString = strScript.replaceAll("\"", "'");
						//// jsonString = strScript.replaceAll("<script[^>]*>(.*?)</script>", "");
						//// System.out.println(jsonString);
						// }
						// }
						// }

						int i = 0;
						// // 取得下一個連結
						//// Elements scripts = doc.select("script");
						// for (Element script : scripts) {
						//// i++;
						// String scriptText = script.outerHtml();
						// System.out.println("scriptText == " + i);
						// System.out.println("scriptText == " + scriptText);
						//
						// Pattern p = Pattern.compile("nextpage=\"([^\"]*)\"");
						// Matcher m = p.matcher(scriptText);
						//
						// if (m.find()) {
						// nextPageUrl = m.group(1);
						// break;
						// }
						// }

						doc.select("div.box-body.nvl-content").first();
						Elements paragraphs = doc.select("div.box-body.nvl-content p");
						Elements lineBreaks = doc.select("div.box-body.nvl-content br");

						Elements combinedElements = new Elements();
						combinedElements.addAll(paragraphs);
						combinedElements.addAll(lineBreaks);

						combinedElements.sort((element1, element2) -> Integer.compare(element1.elementSiblingIndex(),
								element2.elementSiblingIndex()));

						String tempStr = "";
						for (Element element : combinedElements) {
							tempStr = "";
							if (element.tagName().equals("p")) {
								tempStr = element.text();
							} else if (element.tagName().equals("br")) {

							}

							// System.out.println("tempStr ====== " + tempStr);
							content.append(tempStr);
							content.append("\n");
						}
					} else {
						System.out.println("sb is null!!!");
					}
				}
			} else {
				System.out.println("Failed to fetch content. Response Code: " + connection.getResponseMessage());
			}

			System.out.println("url == " + url);
			// System.out.println("content" + content);
			System.out.println("connection" + connection.getResponseCode());
			// System.out.println("connection" + connection.getResponseMessage());
			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("e" + e);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("e" + e);
		}

		return content;
	}

	private static void saveJsonContentToFile(JSONObject jsonContent, String fileName, String fileDirectory) {
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
			writer.write(jsonContent.toString(4));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getContentFromUrl(String url) {
		StringBuilder content = new StringBuilder();
		try {
			String urlTemp = "https://ncode.syosetu.com/n2710db/";
			urlTemp = url;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new URL(urlTemp).openStream(), StandardCharsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			System.out.println(reader);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}

	public static StringBuilder readFromUrl(final String url) throws IOException, JSONException {
		InputStream is = null;
		try {
			System.out.println("url ==" + url);
			is = new URL(url).openStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			final StringBuilder jsonText = readAll_StringBuilder(rd);
			System.out.println(jsonText);
			return jsonText;
		} finally {
			is.close();
		}
	}

	private static StringBuilder readAll_StringBuilder(final Reader rd) throws IOException {
		final StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb;
	}

	private static void saveContentToFile(String content, String fileName, String fileDirectory) {
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean fileExists(String fileName, String fileDirectory) {
		File file = new File(fileDirectory, fileName);
		return file.exists();
	}

	private static String generateFileName(String fileName, String fileDirectory) {
		int index = fileName.lastIndexOf(".");
		String baseName = index != -1 ? fileName.substring(0, index) : fileName;
		String extension = index != -1 ? fileName.substring(index) : "";

		int counter = 1;
		String newFileName;
		do {
			newFileName = baseName + "_" + counter + extension;
			counter++;
		} while (fileExists(newFileName, fileDirectory));

		return newFileName;
	}
}
