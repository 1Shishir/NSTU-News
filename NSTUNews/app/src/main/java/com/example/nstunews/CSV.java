//package com.example.nstunews;
//
//import android.content.Context;
//import android.os.Build;
//import android.util.Log;
//
//import androidx.annotation.RequiresApi;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//
//
//public class CSV {
//    Context context;
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    boolean createCSV(Context context, String category, String newsId, int userAction) {
//
//        boolean successful=false;
//        File cacheDir=context.getCacheDir();
//        this.context=context;
//        String fileName="userData.csv";
//        boolean found;
//        int viewCount=0;
//        int reactCount=0;
//        int commentCount=0;
//
//        try{
//
//                File file=new File(cacheDir,fileName);
//
//                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
//
//                    switch (userAction) {
//                        case 1:
//                            viewCount++;
//                            break;
//                        case 2:
//                            reactCount++;
//                            break;
//                        case 3:
//                            commentCount++;
//                    }
//
//            Log.d("fileSize", String.valueOf(file.length()));
//                    if(file.length()==0L){
//                        writer.append("Category,News id,View,React,Comment\n");
//                    }
//                    writer.append(category + "," + newsId + "," + viewCount + "," + reactCount + "," +commentCount+"\n");
//                    writer.close();
//                    successful=true;
//            } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return successful;
//
//    }
//int findDuplicate(){
//
//   return 0;
//}
//}
//
//
package com.example.nstunews;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CSV {
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.O)
    boolean createCSV(Context context, String category, String newsId, int userAction) {
        boolean successful = false;
        File cacheDir = context.getCacheDir();

        this.context = context;
        String directoryName=FirebaseAuth.getInstance().getUid();
        String fileName =  "userData.csv";
        int viewCount = 0;
        int reactCount = 0;
        int commentCount = 0;
        boolean emptyHeader=false;

        try {

            // Create a new directory within the cache directory
            File uidDirectory = new File(cacheDir, directoryName);
            if (!uidDirectory.exists()) {
                if (uidDirectory.mkdir()) {
                    Log.d("DirectoryCreation", "Directory created: " + uidDirectory.getAbsolutePath());
                } else {
                    Log.e("DirectoryCreation", "Failed to create directory");
                    return false;
                }
            }

            File file = new File(uidDirectory,fileName);
            if(!file.exists()){
                file.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//                writer.append("Category,News id,View,React,Comment\n");
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Read existing entries and update counts
            String line;
            boolean found = false;
            StringBuilder csvData = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4 && values[0].equals(newsId)) {
                    found = true;
                    viewCount = Integer.parseInt(values[1]);
                    reactCount = Integer.parseInt(values[2]);
                    commentCount = Integer.parseInt(values[3]);
                } else {
                    csvData.append(line).append("\n");
                }
            }
            reader.close();


            // Update counts based on user action
            switch (userAction) {
                case 1:
                    viewCount++;
                    break;
                case 2:
                    if(reactCount==0){
                        reactCount=1;
                    }else {
                        reactCount=0;
                    }
                    break;
                case 3:
                    commentCount++;
                    break;
            }

            // Append or create CSV file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append(csvData.toString());

            writer.append(newsId + "," + viewCount + "," + reactCount + "," + commentCount + "\n");
            writer.close();
            //add header


//            BufferedReader readerHeaderCheck = new BufferedReader(new FileReader(file));
//            String heading = "Category,News id,View,React,Comment\n";
//            StringBuilder builder;
//            if(readerHeaderCheck.readLine().contains(heading)){
//                builder=new StringBuilder();
//            }
//            else {
//                builder=new StringBuilder("Category,News id,View,React,Comment\n");
//            }
//            readerHeaderCheck.close();
//
//            BufferedReader readerHead = new BufferedReader(new FileReader(file));
//            String line2;
//            while ((line2 = readerHead.readLine()) != null) {
//                builder.append(line2+"\n");
//            }
//            reader.close();
//
//            BufferedWriter writerHead = new BufferedWriter(new FileWriter(file));
//            writerHead.append(builder.toString());
 //           writerHead.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return successful;
    }

}

