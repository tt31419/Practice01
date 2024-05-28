package com.tt;

import java.io.File;

public class ForRename {
	public static void main(String[] args) {
		ForRename obj = new ForRename();
		obj.re();
//		String getSecureRandomNumberRS = obj.getSecureRandomNumber(5);						
//		System.out.println("getSecureRandomNumberRS = " + getSecureRandomNumberRS);						
//		boolean ckstatus = true;						
//		for (int i = 0; i < 2000; i++) {						
//			//System.out.println(obj.getSERKey("0911222333"));					
//			String test = obj.getSERKey("0911222333");					
//			if(test.length() != 5) {					
//				System.out.println("error!!!test = " + test + "--test.length()" + test.length());				
//				ckstatus = false;				
//			}					
//		}						
//		System.out.println("result = " + ckstatus);						
	}

// 重新命名							
	public void rename(String folderPath, String orgName, String newPath) {
		System.out.println("newPath =" + newPath + "===");
		
		File fl = new File(folderPath);
		File f = new File(folderPath + File.separator + orgName);
		String filename = "";
		filename = f.getName();
		// System.out.println(filename);
		File ckFile = new File(folderPath + newPath);
		if (ckFile.exists()) {
			f.renameTo(new File(fl.getAbsolutePath() + "//" + filename.replace(orgName, newPath + "(2)")));
		} else {
			f.renameTo(new File(fl.getAbsolutePath() + "//" + filename.replace(orgName, newPath)));
		}
	}

	public void re() {
//		String path = "D:\\davidchen\\Desktop\\SQL";
		String path = "E:\\µTorrent";
		File desfile = new File(path);
		String[] tempList = desfile.list();
		StringBuilder sb = null;

		for (int i = 0; i < tempList.length; i++) {
//		for (int i = 0; i < 1; i++) {
			// System.out.println(path + File.separator + tempList[i]);
			File file = new File(path + File.separator + tempList[i]);
			// 是資料夾
			if (file.isDirectory()) {

				System.out.println(path + "\\+++" + tempList[i] + "---");
				try {
					String[] fNameArr = tempList[i].split("\\)");
					// if (tempList[i].matches("[(]{1}")) {
					System.out.println("if" + fNameArr[0].charAt(0));
					if ("(".equals(String.valueOf(fNameArr[0].charAt(0)))) {
						sb = new StringBuilder();
						for (int o = 1; o < fNameArr.length; o++) {
							String addName = "";
							addName = fNameArr[o];
							if (fNameArr[o].contains("(")) {
								addName += ")";
							}
							sb.append(addName);
						}
						sb.append(fNameArr[0] + ")");
						System.out.println("--" + sb.toString());
						if (Character.isWhitespace(sb.toString().charAt(0))) {
							rename(path, tempList[i], sb.toString().substring(1));
						} else {
							rename(path, tempList[i], sb.toString());
						}

					}
				} catch (Exception e) {
					System.out.println("error" + tempList[i]);
					e.printStackTrace();
				}
			}
		}
	}

}
