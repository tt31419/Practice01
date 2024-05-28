package com.tt.test.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

//Sammgle
public class ProjectContextExporter_WORK{
	private static final String OUTPUT_FILE_NAME_1 = "output_"; // Static output file name
	private static final String OUTPUT_FILE_NAME_2 = ".txt"; // Static output file name
	
	private static final String PROJECT_DIRECTORY = "D:\\NewWrokspace\\"; // Change to your project directory
	
    private static final String OUTPUT_DIRECTORY = "D:\\NewWrokspace\\testExport\\"; // Change to your desired output directory

    private static List<String> FILECONTENTS = new ArrayList<>(); 
    
    private static int count = 0;
    
    
    public static void main(String[] args) {
        String projectName = getProjectNameFromConsole();

        if (projectName != null) {
        	scanProjectContents(projectName);
//        	List<String> fileContents = scanProjectContents(projectName);//no use
        	
//            exportToTxtFile();

//            System.out.println("Project context exported to " + OUTPUT_FILE);

//            importProjectStructure(OUTPUT_FILE, projectName);
            
            System.out.println("Project context imported complete: " + projectName);
        } else {
            System.out.println("Project name not provided. Exiting.");
        }
    }

    private static String getProjectNameFromConsole() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the name of the Java project in Eclipse: ");
        return scanner.nextLine();
    }
    
    private static List<String> scanProjectContents(String projectName, boolean nouse) {
        List<String> fileContents = new ArrayList<>();

        String projectDirectory = PROJECT_DIRECTORY + projectName;

//        scanDirectory(projectName, projectDirectory, fileContents); // Pass projectName and projectDirectory
        scanDirectory(projectName, projectDirectory); // Pass projectName and projectDirectory

        return fileContents;
    }
    
    private static void scanProjectContents(String projectName) {
//        List<String> fileContents = new ArrayList<>();

        String projectDirectory = PROJECT_DIRECTORY + projectName;

        scanDirectory(projectName, projectDirectory); // Pass projectName and projectDirectory

//        return null;
    }
    
    
    private static void scanDirectory_BACK(String projectName, String projectDirectory) {
//    	List<String> fileContents = new ArrayList<>();
        int count = 0;
        int saveCount = 1;
    	try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(projectDirectory))) {
            for (Path filePath : directoryStream) {
                String fileName = filePath.getFileName().toString();
//
                System.out.println("projectName: " + projectName);
                System.out.println("projectDirectory: " + projectDirectory);
                System.out.println("filePath: " + filePath);

//                if (fileName.startsWith(".") 
//                		|| fileName.endsWith("build")
//                		|| fileName.endsWith(OUTPUT_FILE)) {
//                	//we're figure out someday
////                	String content = readTextFile(filePath);
////                    fileContents.add("----_" + relativePath + "\n" + content);
//                	continue;
//                }
                
                // Construct the relativePath correctly
                if (Files.isDirectory(filePath)) {
                    // If it's a directory, recursively scan its contents
//                    scanDirectory(projectName, filePath.toString(), FILECONTENTS);//error here
                } else if (fileName.endsWith(".java") || fileName.endsWith(".xml") || fileName.endsWith(".properties")
                        || fileName.endsWith(".project") || fileName.endsWith(".classpath")) {
                    String content = readTextFile(filePath);
                    // Include the full path relative to the project's folder name
                    FILECONTENTS.add("----_" + filePath);
                    FILECONTENTS.add(content);
                    
                    System.out.println("fileContents" + FILECONTENTS);
//                    
                    
                } else if (fileName.endsWith(".jar")) {
                	System.out.println("filePath/*-*" + filePath);
                	System.out.println("fileName----" + fileName);
                    if (filePath.toString().contains("WebContent")
                    		&&filePath.toString().contains("WEB-INF")
                    		&&filePath.toString().contains("lib")) {
                        // Include JAR files located in /WebContent/WEB-INF/lib/
                        FILECONTENTS.add("----_" + fileName);
                    }
                }
                System.out.println("Processed: " + filePath);
                count++;
                if(count > 20) {
//                	exportToTxtFile(FILECONTENTS, saveCount);
                	saveCount++;
                }
            }
//            exportToTxtFile(FILECONTENTS, saveCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void scanDirectory(String projectName, String projectDirectory) {
//    	List<String> fileContents = new ArrayList<>();
        int saveCount = 1;
        System.out.println("projectName: " + projectName);
        try {
            if(processDirectory(projectDirectory)) {
            	exportToTxtFile(saveCount);
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static boolean processDirectory(String projectDirectory) {
//    	List<String> fileContents = new ArrayList<>();
        boolean result = true;
        int saveCount = 1;
        System.out.println("scanDirectory: " + projectDirectory);
        try {
        	DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(projectDirectory));
            for(Path filePath : directoryStream){
            	System.out.println("projectDirectory: " + projectDirectory);
                System.out.println("filePath.toString(): " + filePath.toString());
                if (Files.isDirectory(filePath)) {
                	if(!processDirectory(filePath.toString())) {
                		return false;
                	}
            	}else {
            		if(!setContents(filePath)) {
            			return false;
            		}
            	}
            }
//            exportToTxtFile(FILECONTENTS, saveCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static boolean setContents(Path filePath) {
    	try {
    		System.out.println("count ====]" + count);
    		if (count >= 500 ) {

                System.out.println("fileContents ---  >>>>20 " + filePath);
    			return true;    			
    		}
    		String fileName = filePath.getFileName().toString();
    		if (fileName.endsWith(".java") || fileName.endsWith(".xml") || fileName.endsWith(".properties")
                    || fileName.endsWith(".project") || fileName.endsWith(".classpath")) {
                String content = readTextFile(filePath);
                // Include the full path relative to the project's folder name
                FILECONTENTS.add("----_" + filePath);
                FILECONTENTS.add(content);
                count++;
                System.out.println("fileContents" + FILECONTENTS);
//                
                
//            } else if (fileName.endsWith(".jar")) {
//            	System.out.println("filePath/*-*" + filePath);
//            	System.out.println("fileName----" + fileName);
//            	FILECONTENTS.add("----_" + fileName);
//            	if (filePath.toString().contains("WebContent")
//                		&&filePath.toString().contains("WEB-INF")
//                		&&filePath.toString().contains("lib")) {
//                    // Include JAR files located in /WebContent/WEB-INF/lib/
//                    FILECONTENTS.add("----_" + fileName);
//                }
    		} else if (fileName.endsWith(".class")) {
    			//do nothing
            }else {
            	FILECONTENTS.add("----_" + fileName);
            	count++;
            }
    		Thread.sleep(1000);
    	} catch (Exception e) {
    		System.out.println("----setContents----");
            e.printStackTrace();
            return false;
        }
    	return true;
    }
    
    private static String readTextFile(Path filePath) {
        try {
            byte[] bytes = Files.readAllBytes(filePath);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

//    private static void exportToTxtFile(List<String> fileContents) {
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILE), StandardCharsets.UTF_8)) {
//            for (String content : fileContents) {
//                writer.write(content);
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    
//    private static void exportToTxtFile(List<String> fileContents) {
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILE), StandardCharsets.UTF_8)) {
//            for (String content : fileContents) {
//                // Split the content into lines and write each line separately
//                String[] lines = content.split("\n");
//                for (String line : lines) {
//                    writer.write(line);
//                }
////                writer.write("----_");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    
    private static void exportToTxtFile(int count) {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILE_NAME_1 + Integer.toString(count) + OUTPUT_FILE_NAME_2), StandardCharsets.UTF_8);
            for (String content : FILECONTENTS) {
//            	System.out.println("content ===" + content);
                String[] lines = content.split("\n");
//                System.out.println("lines ===" + lines);
                for (String line : lines) {
                	 System.out.println("line: " + line);
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    private static void importProjectStructure(String outputFileName, String projectName) {
    	Path filePath = Paths.get(outputFileName);
    	BufferedReader reader = null;
    	
        try {
        	reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
            String line;
            boolean readingContent = false;
            StringBuilder currentContent = new StringBuilder();
            String currentFileName = null;
            
            System.out.println("outputFileName" + outputFileName);
            int count = 0;
            
            while ((line = reader.readLine()) != null) {
            	if (line.startsWith("----_")) {
            		if (readingContent) {
            			createFiles(currentFileName, currentContent);
                        currentContent = new StringBuilder();
                        readingContent = false;
//                        break;
            		}
        			
            		currentFileName = line.replace("----_", "").replace(PROJECT_DIRECTORY, OUTPUT_DIRECTORY);
                    readingContent = true;
                } else if (readingContent) {
                    // Append the line to the current content
                    currentContent.append(line).append("\n");
//                    currentContent.append(line);
                }
            }

            // Add the last content if any
            if (readingContent && currentFileName != null) {
            	createFiles(currentFileName, currentContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    private static void createFiles(String currentFileName, StringBuilder currentContent) throws IOException {
    	System.out.println("currentFileName = createFiles" + currentFileName);
    	System.out.println("currentContent1" + currentContent);
    	
    	Path directoryPath = Paths.get(currentFileName).getParent();
        if (directoryPath != null) {
            Files.createDirectories(directoryPath);
        }

//        // Create a file and write the currentContent into it
//        Path filePath = Paths.get(currentFileName + ".txt");
//        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
//            writer.write(currentContent.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
        Path filePath = Paths.get(currentFileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
        	System.out.println("currentContent2" + currentContent.toString());
            writer.write(currentContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static List<String> getFILECONTENTS() {
		return FILECONTENTS;
	}

	public static void setFILECONTENTS(List<String> fILECONTENTS) {
		FILECONTENTS = fILECONTENTS;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


}


