package com.server.backend;

import java.io.File;

public class DirectoryLister {
    public static void main(String[] args) {
        // Replace "YOUR_DIRECTORY_PATH" with the path of the directory you want to list
        String directoryPath = "./backend/src/main/resources/ontologies/schemaorg.owl";
        
        // Create a File object for the specified directory
        File directory = new File(directoryPath);
        
        // Check if the directory exists
        if (directory.exists() && directory.isDirectory()) {
            // Get the list of files and directories in the specified directory
            File[] files = directory.listFiles();
            
            // Display the contents of the directory
            System.out.println("Contents of " + directoryPath + ":");
            if (files != null) {
                for (File file : files) {
                    System.out.println(file.getName());
                }
            } else {
                System.out.println("Unable to list directory contents.");
            }
        } else {
            System.out.println("Specified directory does not exist or is not a directory.");
        }
    }
}
