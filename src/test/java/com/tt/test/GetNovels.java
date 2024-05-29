package com.tt.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.houbb.opencc4j.util.ZhTwConverterUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.tt.Constrain;
import com.tt.model.Masiro;
import com.tt.util.CommonUtils;
import com.tt.webservice.GwWebService;

/**
 * wait until daemon to move in
 */
public class GetNovels {

    public static void main(String[] args) {
        try {
            CommonUtils.loadLog4j();
            Logger log = Logger.getLogger("NovelTest");
            log.info("local main.");

            Map<String, Map<String, String>> configsMap = new HashMap<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(Constrain.LOCAL_CONFIG_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty() || line.trim().equals("")) {
                        continue;
                    }
                    String book = null;
                    Map<String, String> configMap = new HashMap<>();
                    String[] parts = line.split(";");
                    for (String part : parts) {
                        String[] keyValue = part.split(":");
                        if (book == null) {
                            book = keyValue[1].trim();
                        }
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();
                            configMap.put(key, value);
                            // System.out.println("key: " + key + " value: " + value);
                        }
                    }
                    // System.out.println("Book " + book);
                    // System.out.println(configMap);
                    configsMap.put(book, configMap);
                }
            } catch (IOException e) {
                System.out.println("main get config " + e.getMessage());
            }

            File directory = new File(Constrain.FILEDIRECTORY);
            File[] folders = directory.listFiles(File::isDirectory);
            if (folders != null) {
                for (File folder : folders) {
                    String folderName = folder.getName();
                    // System.out.println("Folder Name: " + folderName);
                    // testing ignore other folders
                    if (!"linovelib".equals(folderName)) {
                        continue;
                    }

                    Map<String, String> configMap = configsMap.get(folderName);
                    if (configMap != null) {
                        System.out.println("configMap Success");
                        File[] txtFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
                        if (txtFiles != null) {
                            for (File txtFile : txtFiles) {
                                StringBuilder txtContent = new StringBuilder();
                                String txtName = txtFile.getName();
                                System.out.println("txtName: " + txtName);
                                // txtName : "小說名稱_urlID_總共章節數量_更新日期.txt"
                                // ".*_\\d+_\\d+_\\d+\\.txt"
                                if (!txtName.matches(configMap.get("regex"))) {
                                    System.out.println("Illegal txt file name: " + txtName);
                                } else {
                                    System.out.println("======Starting: " + txtName);
                                    // Class<?> clazz = Class.forName("tt.model." + folderName.substring(0,
                                    // 1).toUpperCase() + folderName.substring(1).toLowerCase());
                                    // //assume the constructor is Masiro(String txtName)
                                    // Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
                                    // Object instance = constructor.newInstance(txtName);
                                    // instance.getClass().getMethod("getFileName").invoke(instance);
                                    if ("Masiro_XXX".equals(folderName)) {
                                        try {
                                            System.out.println("Masiro prepare to get novel");
                                            Masiro novel = new Masiro(txtName);
                                            Map<String, String> novelConfig = new HashMap<>();
                                            String viewUrl = "https://" + configMap.get("view") + novel.getUrlID();
                                            novelConfig.put("url", viewUrl);
                                            novelConfig.put("Cookie", configMap.get("Cookie"));
                                            novelConfig.put("Referer", viewUrl);

                                            System.out.println("novelConfig: " + novelConfig);

                                            InputStream is = GwWebService.getInstance(null)
                                                    .urlToInputStream(novelConfig);
                                            Document doc = Jsoup.parse(is, "UTF-8", "");
                                            System.out.println("is: " + is);
                                            // InputStream is = new URL(url).openStream();
                                            if (CommonUtils.getInstance().inputStreamHasData(is)) {
                                                doc = Jsoup.parse(is, "UTF-8", "");

                                                Element title = doc.select("title").first();

                                                System.out.println("title ------== " + title.text());
                                                System.out.println("traditionalTitle ------== "
                                                        + CommonUtils.getInstance().toTraditional(title.text()));
                                                // System.out.print("doc ------== " + doc.html());

                                                Elements fChapterScripts = doc.select("#f-chapters-json");
                                                com.google.gson.JsonArray fChapters = null;
                                                if (fChapterScripts != null && !fChapterScripts.isEmpty()) {
                                                    // only one element expected
                                                    for (Element chapterStr : fChapterScripts) {
                                                        fChapters = new JsonParser().parse(chapterStr.data())
                                                                .getAsJsonArray();
                                                    }
                                                }

                                                // Elements chapterScripts = doc.select("#chapters-json");
                                                // for (Element chapterStr : chapterScripts) {
                                                // com.google.gson.JsonArray chapters = new
                                                // JsonParser().parse(chapterStr.data()).getAsJsonArray();
                                                // List<com.google.gson.JsonObject> chaptersList =
                                                // sortJsonArrToList(chapters);

                                                // for (JsonObject chapterJson : chaptersList) {

                                                // String chapterTitle =
                                                // CommonUtils.getInstance().toTraditional(chapterJson.get("title").getAsString());
                                                // String chapterUrl = chapterJson.get("id").getAsString();
                                                // System.out.println("Chapter Title: " + chapterTitle + ", Chapter URL:
                                                // " + chapterUrl);

                                                // }
                                                // }
                                            } else {
                                                System.out.println("is is null");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println("Masiro " + e.getMessage());
                                        }

                                    } else if ("linovelib".equals(folderName)) {
                                        // view = + "*/catalog"
                                        System.out.println("linovelib prepare to get novel");
                                        // TODO linovelib & Masiro combine to urlNovel
                                        Masiro novel = new Masiro(txtName);
                                        Map<String, String> urlConfigMap = new HashMap<>();
                                        String urlBase = (configMap.get("view").startsWith("https")
                                                ? configMap.get("view")
                                                : "https://" + configMap.get("view")) + novel.getUrlID();
                                        String viewUrl = urlBase + "/catalog";
                                        urlConfigMap.put("url", viewUrl);
                                        urlConfigMap.put("Cookie", configMap.get("Cookie"));
                                        urlConfigMap.put("Referer", viewUrl);

                                        System.out.println("novelConfig: " + urlConfigMap);

                                        // InputStream is =
                                        // GwWebService.getInstance(null).urlToInputStream(novelConfig);
                                        // Document doc = Jsoup.parse(is, "UTF-8", "");
                                        Document viewDoc = GwWebService.getInstance(log).urlToJsoupDoc(urlConfigMap);
                                        // start view
                                        String title = viewDoc.select("title").first().text();
                                        System.out.println("title ------== " + title);
                                        String nextUrlString = "";
                                        
                                        String chapterUrlName = "";
                                        Elements chaptersElements = viewDoc.select(".chapter-li.jsChapter a");
                                        for (Element chapterElement : chaptersElements) {
                                            nextUrlString = chapterElement.attr("href");
                                            chapterUrlName = chapterElement.text();
                                            System.out.println("nextUrlString : " + nextUrlString
                                                    + ", Chapter URL Name: " + chapterUrlName);
                                            // consider will stciprt is javascript.cid(0)
                                            if (nextUrlString.startsWith("j")) {
                                                continue;
                                            } else {
                                                // only need to get the first chapter, next chapter will be int +1
                                                break;
                                            }
                                        }
                                        // end view

                                        // start chapter loop
                                        int loopcount = 0;
                                        int chapterCount = 0;
                                        if (!StringUtils.isBlank(nextUrlString)) {
                                            while (!StringUtils.isBlank(nextUrlString)) {
                                                Thread.sleep(5500);
                                                System.out.println(loopcount + " loopcount: " + nextUrlString);
                                                // if (loopcount > 2) {
                                                //     break;
                                                // }

                                                if (nextUrlString.endsWith(".html")) {
                                                    boolean isNewChapter = true;
                                                    if(!StringUtils.contains(nextUrlString, "_")) {
                                                        chapterCount++;
                                                        isNewChapter = false;
                                                    }
                                                    
                                                    urlConfigMap.replace("Referer", urlConfigMap.get("url"));
                                                    urlConfigMap.replace("url", (nextUrlString.startsWith("https")
                                                            ? nextUrlString
                                                            : "https://" + configMap.get("read") + nextUrlString));
                                                    
                                                    System.out.println("urlConfig: " + urlConfigMap);
                                                    Document chapterDoc = GwWebService.getInstance(log)
                                                            .urlToJsoupDoc(urlConfigMap);
                                                    
                                                    if (isNewChapter) {
                                                        String chapterTitle = chapterDoc.select("#atitle").first().text();
                                                        txtContent.append("\n\n"); // Add a newline before each chapter
                                                        txtContent.append(chapterTitle + "\n\n");
                                                    }
                                                    


                                                    Elements paragraphs = chapterDoc.select("div#acontent1 p");
                                                    Elements lineBreaks = chapterDoc.select("div#acontent1 br");

                                                    Elements combinedElements = new Elements();
                                                    combinedElements.addAll(paragraphs);
                                                    combinedElements.addAll(lineBreaks);

                                                    combinedElements.sort((element1, element2) -> Integer.compare(
                                                            element1.elementSiblingIndex(),
                                                            element2.elementSiblingIndex()));

                                                    for (Element paragraph : paragraphs) {
                                                        if (paragraph.tagName().equals("p")) {
                                                            txtContent.append(paragraph.text());
                                                        } else if (paragraph.tagName().equals("br")) {
                                                            txtContent.append("\n\n");
                                                        }

                                                        txtContent.append("\n"); // Add a newline after each paragraph
                                                    }   // end of for (Element paragraph : paragraphs)

                                                    // System.out.println("Chapter Title: " + chapterTitle);
                                                    // System.out.println("Chapter Content: " + txtContent);

                                                    Element linkElement = chapterDoc.select("link[rel=prerender]").first();
                                                    nextUrlString = linkElement.attr("href");
                                                    // System.out.println("Next URL: " + nextUrlString);
                                                }else{
                                                    nextUrlString = "";
                                                }  // end if (nextUrlString.endsWith(".html"))
                                                loopcount++;
                                            } // end of while (!StringUtils.isBlank(nextUrlString))
                                        } // end of if (!StringUtils.isBlank(nextUrlString))

                                        // txtContent
                                        // newTxtName : "小說名稱_urlID_總共章節數量_更新日期
                                        
                                        if(novel.isUpdate(chapterCount)){
                                            System.out.println("chapterCount: ===" + chapterCount);
                                            if (CommonUtils.getInstance(log).createTxtFile(folder.getAbsolutePath(), novel.getNewTxtName(chapterCount) + novel.getFileType(), txtContent)){
                                                System.out.println("Create txt file success: " + novel.getNewTxtName(chapterCount));
                                                System.out.println("txtFile.getAbsolutePath(): " + txtFile.getAbsolutePath());
                                                // txtFile.delete();
                                                CommonUtils.getInstance(log).moveFile(txtFile.getAbsolutePath(), folder.getAbsolutePath() + "\\old\\" + novel.getOldTxtName() + novel.getFileType());
                                            }
                                        }
                                        System.out.println("chapterCount: " + chapterCount);
                                        System.out.println("novel.getNewTxtName(chapterCount): " + novel.getNewTxtName(chapterCount));
                                    } // end of if ("Masiro_XXX".equals(folderName))
                                } // end of if (!txtName.matches)
                            } // end of for (File txtFile : txtFiles)
                        } // end of if (txtFiles != null)
                    } else {
                        System.out.println("Missing configMap" + folderName);
                    } // end of if (configMap != null)
                } // end of for (File folder : folders)
            } else {
                System.out.println("Error: folders is null: " + Constrain.FILEDIRECTORY);
            } // end of if (folders != null)
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("main " + e.getMessage());
        }
        System.out.println("===================ends======================");

    }

    public List<com.google.gson.JsonObject> sortJsonArrToList(com.google.gson.JsonArray jsonArray,
            List<String> sortKeys, List<String> lightColList) {
        List<com.google.gson.JsonObject> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            com.google.gson.JsonObject oJsonObject = jsonArray.get(i).getAsJsonObject();
            list.add(oJsonObject);
        }
        Collections.sort(list, new Comparator<com.google.gson.JsonElement>() {
            @Override
            public int compare(com.google.gson.JsonElement e1, com.google.gson.JsonElement e2) {
                return e1.toString().compareTo(e2.toString());
            }
        });
        return list;
    }

    // public com.google.gson.JsonArray sortJsonArrToArray(com.google.gson.JsonArray
    // jsonArray) {
    // List<com.google.gson.JsonObject> list = sortJsonArrToList(jsonArray);
    // com.google.gson.JsonArray sortedArray = new com.google.gson.JsonArray();
    // for (com.google.gson.JsonElement element : list) {
    // sortedArray.add(element);
    // }
    // return sortedArray;
    // }
}
