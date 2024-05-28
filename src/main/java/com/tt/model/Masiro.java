package com.tt.model;

import java.lang.IllegalArgumentException;

public class Masiro {
    private String fileName = "";
    private String dirName = "";
    private String novelName = "";
    private int urlID = 0;
    private int chapters = 0;
    private int updateDate = 0;
    // private List<String> novels = new ArrayList<String>();

    private boolean isNew = true;

    public Masiro() {
    }

    // txtName : "小說名稱_urlID_總共章節數量_更新日期.txt"
    public Masiro(String txtName) {
        if (txtName == null || txtName.isEmpty()) {
            throw new IllegalArgumentException("Invalid txtName");
        }
        this.fileName = txtName;
        this.dirName = txtName.substring(0, txtName.lastIndexOf("/") + 1);
        String[] parts = this.fileName.split("_");
        this.novelName = parts[0];
        this.urlID = Integer.parseInt(parts[1]);
        this.chapters = Integer.parseInt(parts[2]);
        this.updateDate = Integer.parseInt(parts[3].substring(0, parts[3].lastIndexOf(".")));

        // default
        this.isNew = true;
    }

    // public void addNovel(String novel) {
    //     this.novels.add(novel);
    // }

    public boolean hasUpdate() {

        return true;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDirName() {
        return dirName;
    }

    public String getNovelName() {
        return novelName;
    }

    public int getUrlID() {
        return urlID;
    }

    public int getChapters() {
        return chapters;
    }

    public int getUpdateDate() {
        return updateDate;
    }

    // public List<String> getNovels() {
    //     return novels;
    // }

    public boolean isNew() {
        return isNew;
    }
}