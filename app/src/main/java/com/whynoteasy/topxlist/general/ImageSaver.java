package com.whynoteasy.topxlist.general;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Ilya Gazman on 3/6/2016.
 * Extended: by Aaron Spieler
 */
//TODO: Delete unused functions
public class ImageSaver {

    private Context context;

    public ImageSaver(Context context) {
        this.context = context;
    }

    @NonNull
    private File createFile (String fileName) {
        String directoryName = "images";
        File directory = new File(context.getFilesDir(), directoryName);
        if(!directory.exists() && !directory.mkdirs()){
            Log.e("ImageSaver","Error creating directory " + directory);
        }
        return new File(directory, fileName);
    }

    @NonNull
    private File createFileFromRelativePath(String relativePath) {
        return createFile(relativePath.substring(relativePath.lastIndexOf("/")+1));
    }

    public Bitmap convertUriToBitmap (Uri uriLink) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriLink);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /*TODO delete if not needed
    public String saveFromUri (Uri uriLink, String fileName) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriLink);
            File newFile = saveFromBitmap(bitmap,fileName);
            return newFile.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String saveFromUriTemp (Uri uriLink) {
        return saveFromUri(uriLink, "temp.jpg");
    }
    
    */

    private File saveFromBitmap(Bitmap bitmap, String fileName) {
        FileOutputStream fileOutputStream = null;
        try {
            File file = createFile(fileName); //!!!
            if(file.exists()) {
                file.delete(); //dont want old remains
            }
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream); //JPEG creation
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public File reSaveFromBitmap(Bitmap bitmap, String relativePath) {
        return saveFromBitmap(bitmap, relativePath.substring(relativePath.lastIndexOf("/")+1));
    }
    
    public boolean deleteFileByRelativePath(String relPath){
        File target =  new File(context.getFilesDir(), relPath);
        try {
            return target.delete();
        } catch (Error e) {
            e.printStackTrace();
        }
        return false;
    }

    /* TODO Delete if not needed
    private File getAlbumStorageDir(String albumName) {
        return new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    
    

    public Bitmap loadFromFileName(String fileName) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile(fileName));
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
*/
    
    public Bitmap loadFileByPath(String path) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(path));
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Bitmap loadFileByRelativePath(String relPath) {
        File file = new File(context.getFilesDir(), relPath);
        return loadFileByPath(file.getPath());
    }

    /* TODO DElete
    public Uri temporarilySaveImage(String path){
        Bitmap imgBitmap = loadFileByPath(path);
        File file = saveFromBitmap(imgBitmap,"temp.jpg");

        if(file.exists()) {
            System.out.println("Fuckin exists");
            System.out.println(file.getAbsolutePath());
        }
        //file = createFile("images","temp.jpg");
        //Uri contentUri = Uri.fromFile(file);
        Uri contentUri = FileProvider.getUriForFile(context, "com.whynoteasy.topxlist.provider", file); //If error look in provider xml
        return contentUri;
    }
    */

    public File saveFromBitmapUniquely(Bitmap bitmap) {
        int newID = generateNewUnique8CharacterIDForImage();
        return saveFromBitmap(bitmap,Integer.toString(newID)+".jpg");
    }

    private int generateNewUnique8CharacterIDForImage(){
        Random rnd = new Random();
        int newID = 0;
        boolean retry = true;
        while (retry) {
            newID = 10000000 + rnd.nextInt(90000000);
            retry = createFile(Integer.toString(newID)+".jpg").exists();
        }
        if (newID == 0) {
            Error e = new Error("Image ID couldnt be generated");
            e.printStackTrace();
        }
        return newID;
    }

    public String getRelativePathOfImage(String imageName) {
        return "images/"+imageName;
    }
}
