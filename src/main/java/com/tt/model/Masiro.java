package com.tt.model;

import java.lang.IllegalArgumentException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tt.util.CommonUtils;

 public class Masiro {
    private String fileName = "";
    private String dirName = "";
    private String novelName = "";
    private int urlID = 0;
    private int chapters = 0;
    private String updateDate = "";
    // private List<String> novels = new ArrayList<String>();
    private String fileType = "";

    private int newChapters = 0;

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
        this.updateDate = parts[3].substring(0, parts[3].lastIndexOf("."));
        this.fileType = "." + this.fileName.substring(this.fileName.lastIndexOf(".") + 1);

    }

    public boolean isUpdate() {
        String nowDate =CommonUtils.getInstance().timeToString(new Date(), "yyyyMMddhhmm");
        if (this.newChapters == 0) {
           return false;
        }else if(this.newChapters > this.chapters && !nowDate.equals(this.updateDate)){
            return true;
        }
        return false;
    }

    public boolean isUpdate(int chapters) {
        if(chapters <= 0){
            return false;
        }
        this.newChapters = chapters;
        return this.isUpdate();
    }

    public String getNewTxtName(int chapters) {
        String newName = this.novelName + "_" + this.urlID + "_" + chapters + "_" + CommonUtils.getInstance().timeToString(new Date(), "yyyyMMddhhmm");
        return newName;
    }   

    public String getOldTxtName() {
        return this.novelName + "_" + this.urlID + "_" + this.chapters + "_" + this.updateDate + "OLD";
    }

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

    public String getUpdateDate() {
        return updateDate;
    }

    public String getFileType() {
        return fileType;
    }
}