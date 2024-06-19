package com.tt.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.tt.Constrain;
import com.tt.model.Novel;
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
                    System.out.println("Folder Name: " + folderName);
                    // testing ignore other folders
                    // if (!"linovelib".equals(folderName)) {
                    // continue;
                    // }

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
                                    Novel novel = new Novel(txtName);

                                    Map<String, String> novelConfig = new HashMap<>();
                                    // Class<?> clazz = Class.forName("tt.model." + folderName.substring(0,
                                    // 1).toUpperCase() + folderName.substring(1).toLowerCase());
                                    // //assume the constructor is Masiro(String txtName)
                                    // Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
                                    // Object instance = constructor.newInstance(txtName);
                                    // instance.getClass().getMethod("getFileName").invoke(instance);
                                    if ("Masiro".equals(folderName)) {
                                        try {
                                            System.out.println("Masiro prepare to get novel");
                                            
                                            String viewUrl = "https://" + configMap.get("view") + novel.getUrlID();
                                            novelConfig.put("url", viewUrl);
                                            novelConfig.put("Cookie", configMap.get("Cookie"));
                                            novelConfig.put("Referer", viewUrl);

                                            System.out.println("novelConfig: " + novelConfig);

                                            Document doc = GwWebService.getInstance(null)
                                                    .urlToJsoupDoc(novelConfig);

                                            Element title = doc.select("title").first();

                                            System.out.println("traditionalTitle ------== "
                                                    + CommonUtils.getInstance().toTraditional(title.text()));
                                            // System.out.print("doc ------== " + doc.html());

                                            Element fChapterScripts = doc.select("#f-chapters-json").first();
                                            Element chapterScripts = doc.select("#chapters-json").first();
                                            List<com.google.gson.JsonObject> fChaptersList = new ArrayList<>();
                                            List<com.google.gson.JsonObject> chaptersList = new ArrayList<>();
                                            Map<String, List<com.google.gson.JsonObject>> chapterMap = new HashMap<>();
                                            if (fChapterScripts != null && !fChapterScripts.data().isEmpty()
                                                    && chapterScripts != null && !chapterScripts.data().isEmpty()) {
                                                com.google.gson.JsonArray fChapters = new com.google.gson.JsonParser()
                                                        .parse(fChapterScripts.data()).getAsJsonArray();
                                                com.google.gson.JsonArray chapters = new com.google.gson.JsonParser()
                                                        .parse(chapterScripts.data()).getAsJsonArray();
                                                for (com.google.gson.JsonElement fChapterElement : fChapters) {
                                                    fChaptersList.add(fChapterElement.getAsJsonObject());
                                                    // System.out.println("fChapter : " +
                                                    // fChapterElement.getAsJsonObject());
                                                }
                                                for (com.google.gson.JsonElement chapterElement : chapters) {
                                                    chaptersList.add(chapterElement.getAsJsonObject());
                                                    // System.out.println("chapterElement : " + chapterElement);
                                                }
                                                Collections.sort(fChaptersList, (chapter1, chapter2) -> {
                                                    int id1 = chapter1.getAsJsonObject().get("id").getAsInt();
                                                    int id2 = chapter2.getAsJsonObject().get("id").getAsInt();
                                                    return Integer.compare(id1, id2);
                                                });
                                                Collections.sort(chaptersList, (chapter1, chapter2) -> {
                                                    int id1 = chapter1.getAsJsonObject().get("id").getAsInt();
                                                    int id2 = chapter2.getAsJsonObject().get("id").getAsInt();
                                                    return Integer.compare(id1, id2);
                                                });

                                                com.google.gson.JsonObject fChapter = null;
                                                String nextUrlString = "";
                                                String fChapterTitle = "";
                                                int loopcount = 0;
                                                int chapterCount = 0;
                                                // int fChapterLoop = 0;
                                                for (com.google.gson.JsonObject chapter : chaptersList) {
                                                    // if(chapterCount < novel.getChapters()){
                                                    // continue;

                                                    // }
                                                    String chapterTitle = chapter.get("title").getAsString();
                                                    int chapterId = chapter.get("id").getAsInt();
                                                    int parentId = chapter.get("parent_id").getAsInt();

                                                    if (fChapter == null || fChapter.get("id").getAsInt() != parentId) {
                                                        for (int i = 0; i < fChaptersList.size(); i++) {
                                                            if (fChaptersList.get(i).get("id").getAsInt() == parentId) {
                                                                fChapter = fChaptersList.get(i);
                                                                fChapterTitle = fChapter.get("title").getAsString();
                                                                // System.out.println("FChapter Title: " +
                                                                // fChapterTitle);
                                                                txtContent.append(CommonUtils.getInstance()
                                                                        .toTraditional(fChapterTitle) + "\n\n\n");
                                                                break;
                                                            }
                                                        }

                                                    }

                                                    txtContent.append(
                                                            CommonUtils.getInstance().toTraditional(chapterTitle)
                                                                    + "\n\n");

                                                    nextUrlString = novelConfig.get("url");

                                                    novelConfig.replace("Referer", nextUrlString);
                                                    novelConfig.replace("url",
                                                            "https://" + configMap.get("read") + chapterId);

                                                    // System.out.println("novelConfig: " + novelConfig);
                                                    Document chapterDoc = GwWebService.getInstance(log)
                                                            .urlToJsoupDoc(novelConfig);

                                                    if (chapterDoc == null) {
                                                        System.out.println("chapterDoc is null");
                                                        break;
                                                    }
                                                    Elements paragraphs = chapterDoc
                                                            .select("div.box-body.nvl-content p");

                                                    for (Element paragraph : paragraphs) {
                                                        txtContent.append(CommonUtils.getInstance()
                                                                .toTraditional(paragraph.text()));
                                                        txtContent.append("\n"); // Add a newline after each paragraph
                                                    } // end of for (Element paragraph : paragraphs)

                                                    chapterCount++;
                                                    // loopcount++;
                                                    // if(loopcount > 3){
                                                    // System.out.println("loopcount: " + loopcount);
                                                    // break;
                                                    // }
                                                    Thread.sleep(10500);
                                                    // System.out.println("txtContent: " + txtContent);
                                                } // end of for (chaptersList)

                                                // System.out.println("txtContent: " + txtContent);
                                                System.out.println("total chapterCount: " + chapterCount);

                                                if (novel.isUpdate(chapterCount)) {
                                                    System.out.println("chapterCount: ===" + chapterCount);
                                                    if (CommonUtils.getInstance(log).createTxtFile(
                                                            folder.getAbsolutePath(),
                                                            novel.getNewTxtName(chapterCount) + novel.getFileType(),
                                                            txtContent)) {
                                                        System.out.println("Create txt file success: "
                                                                + novel.getNewTxtName(chapterCount));
                                                        System.out.println(
                                                                "txtFile.getAbsolutePath(): "
                                                                        + txtFile.getAbsolutePath());
                                                        // txtFile.delete();
                                                        CommonUtils.getInstance(log).moveFile(txtFile.getAbsolutePath(),
                                                                folder.getAbsolutePath() + "\\old\\"
                                                                        + novel.getOldTxtName()
                                                                        + novel.getFileType());
                                                    }
                                                }

                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println("Masiro " + e.getMessage());
                                        }

                                    } else if ("linovelib".equals(folderName)) {
                                        // view = + "*/catalog"
                                        System.out.println("linovelib prepare to get novel");
                                        // TODO linovelib & Masiro combine to urlNovel
                                        
                                        String urlBase = (configMap.get("view").startsWith("https")
                                                ? configMap.get("view")
                                                : "https://" + configMap.get("view")) + novel.getUrlID();
                                        String viewUrl = urlBase + "/catalog";
                                        novelConfig.put("url", viewUrl);
                                        novelConfig.put("Cookie", configMap.get("Cookie"));
                                        novelConfig.put("Referer", viewUrl);

                                        System.out.println("novelConfig: " + novelConfig);

                                        Document viewDoc = GwWebService.getInstance(log).urlToJsoupDoc(novelConfig);
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
                                                // break;
                                                // }

                                                if (nextUrlString.endsWith(".html")) {
                                                    boolean isNewChapter = true;
                                                    if (!StringUtils.contains(nextUrlString, "_")) {
                                                        chapterCount++;
                                                        isNewChapter = false;
                                                    }

                                                    novelConfig.replace("Referer", novelConfig.get("url"));
                                                    novelConfig.replace("url", (nextUrlString.startsWith("https")
                                                            ? nextUrlString
                                                            : "https://" + configMap.get("read") + nextUrlString));

                                                    System.out.println("urlConfig: " + novelConfig);
                                                    Document chapterDoc = GwWebService.getInstance(log)
                                                            .urlToJsoupDoc(novelConfig);

                                                    if (isNewChapter) {
                                                        String chapterTitle = chapterDoc.select("#atitle").first()
                                                                .text();
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
                                                    } // end of for (Element paragraph : paragraphs)

                                                    // System.out.println("Chapter Title: " + chapterTitle);
                                                    // System.out.println("Chapter Content: " + txtContent);

                                                    Element linkElement = chapterDoc.select("link[rel=prerender]")
                                                            .first();
                                                    nextUrlString = linkElement.attr("href");
                                                    // System.out.println("Next URL: " + nextUrlString);
                                                } else {
                                                    nextUrlString = "";
                                                } // end if (nextUrlString.endsWith(".html"))
                                                loopcount++;
                                            } // end of while (!StringUtils.isBlank(nextUrlString))
                                        } // end of if (!StringUtils.isBlank(nextUrlString))

                                        // txtContent
                                        // newTxtName : "小說名稱_urlID_總共章節數量_更新日期

                                        if (novel.isUpdate(chapterCount)) {
                                            System.out.println("chapterCount: ===" + chapterCount);
                                            if (CommonUtils.getInstance(log).createTxtFile(folder.getAbsolutePath(),
                                                    novel.getNewTxtName(chapterCount) + novel.getFileType(),
                                                    txtContent)) {
                                                System.out.println("Create txt file success: "
                                                        + novel.getNewTxtName(chapterCount));
                                                System.out.println(
                                                        "txtFile.getAbsolutePath(): " + txtFile.getAbsolutePath());
                                                // txtFile.delete();
                                                CommonUtils.getInstance(log).moveFile(txtFile.getAbsolutePath(),
                                                        folder.getAbsolutePath() + "\\old\\" + novel.getOldTxtName()
                                                                + novel.getFileType());
                                            }
                                        }
                                        System.out.println("chapterCount: " + chapterCount);
                                        System.out.println("novel.getNewTxtName(chapterCount): "
                                                + novel.getNewTxtName(chapterCount));
                                    } // end of if (SPEC_Folder.equals(folderName))
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
}
