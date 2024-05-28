package com.tt.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

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

import com.tt.model.Masiro;
import com.tt.util.CommonUtils;
import com.tt.webservice.GwWebService;

public class GetNovels {
    private static String CONFIGS = "G:\\TXT\\configs.txt";
    private static String FILEDIRECTORY = "G:\\TXT\\Novel";

    public static void main(String[] args) {
        try {
            Map<String, Map<String, String>> configsMap = new HashMap<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(CONFIGS))) {
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
                    System.out.println("Book " + book);
                    System.out.println(configMap);
                    configsMap.put(book, configMap);
                }
            } catch (IOException e) {
                System.out.println("main get config " + e.getMessage());
            }

            File directory = new File(FILEDIRECTORY);
            File[] folders = directory.listFiles(File::isDirectory);
            if (folders != null) {
                for (File folder : folders) {
                    String folderName = folder.getName();
                    System.out.println("Folder Name: " + folderName);

                    if (!"linovelib".equals(folderName)) {
                        continue;
                    }

                    Map<String, String> configMap = configsMap.get(folderName);
                    if (configMap != null) {
                        System.out.println("configMap != null: ");
                        File[] txtFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
                        if (txtFiles != null) {
                            for (File txtFile : txtFiles) {
                                String txtName = txtFile.getName();
                                System.out.println("txtName: " + txtName);
                                // txtName : "小說名稱_urlID_總共章節數量_更新日期.txt"
                                if (!txtName.matches(".*_\\d+_\\d+_\\d+\\.txt")) {
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

                                            InputStream is = null;
                                            Document doc = GwWebService.getInstance(null).urlToJsoupDoc(novelConfig);
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
                                        //TODO linovelib & Masiro combine to urlNovel
                                        Masiro novel = new Masiro(txtName);
                                        Map<String, String> novelConfig = new HashMap<>();
                                        String viewUrl = (configMap.get("view").startsWith("https")
                                                ? configMap.get("view")
                                                : "https://" + configMap.get("view")) + novel.getUrlID() + "/catalog";
                                        novelConfig.put("url", viewUrl);
                                        novelConfig.put("Cookie", configMap.get("Cookie"));
                                        novelConfig.put("Referer", viewUrl);

                                        System.out.println("novelConfig: " + novelConfig);

                                        InputStream is = null;
                                        // InputStream is = new URL(url).openStream();
                                        Document doc = GwWebService.getInstance(null).urlToJsoupDoc(novelConfig);
                                        if (CommonUtils.getInstance().inputStreamHasData(is)) {
                                            System.out.println("is has data");
                                            doc = Jsoup.parse(is, "UTF-8", "");
                                            is.close();
                                        } else {
                                            System.out.println("is is null");
                                        } // end of if (CommonUtils.getInstance().inputStreamHasData(is))
                                        
                                        

                                        String title = doc.select("title").first().text();
                                        System.out.println("title ------== " + title);
                                        String chapterUrlId = "";
                                        String chapterUrlName = "";
                                        Elements chaptersElements = doc.select(".chapter-li.jsChapter a");
                                        for (Element chapterElement : chaptersElements) {
                                            chapterUrlId = chapterElement.attr("href");
                                            chapterUrlName = chapterElement.text();
                                            System.out.println("Chapter URL ID: " + chapterUrlId
                                                    + ", Chapter URL Name: " + chapterUrlName);
                                            if (chapterUrlId.startsWith("j")) {
                                                continue;
                                            } else {
                                                break;
                                            }
                                        }
                                    } // end of if ("Masiro_XXX".equals(folderName))
                                } // end of if (!txtName.matches)
                            } // end of for (File txtFile : txtFiles)
                        } // end of if (txtFiles != null)
                    }
                }
            }
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
