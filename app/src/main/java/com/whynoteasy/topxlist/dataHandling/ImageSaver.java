package com.whynoteasy.topxlist.dataHandling;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 *
 */

public class ImageSaver {

    public static final int MaxImageHeight = 720;
    public static final int MaxImageWidth = 1280;
    public static final int ImageRatioX = 16;
    public static final int ImageRatioY = 9;

    private Context context;

    public ImageSaver(Context context) {
        this.context = context;
    }

    @NonNull
    private File createFile(String fileName) {
        String directoryName = "images";
        File directory = new File(context.getFilesDir(), directoryName);
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("ImageSaver", "Error creating directory " + directory);
        }
        return new File(directory, fileName);
    }

    @NonNull
    private File createFileFromRelativePath(String relativePath) {
        return createFile(relativePath.substring(relativePath.lastIndexOf("/") + 1));
    }

    public Bitmap convertUriToBitmap(Uri uriLink) {
        try {
            //also reduce Size if necessary
            return reduceSizeOfBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriLink));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File saveFromBitmap(Bitmap bitmap, String fileName) {
        bitmap = reduceSizeOfBitmap(bitmap);
        FileOutputStream fileOutputStream = null;
        try {
            File file = createFile(fileName); //!!!
            if (file.exists()) {
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
        return saveFromBitmap(bitmap, relativePath.substring(relativePath.lastIndexOf("/") + 1));
    }

    public boolean deleteFileByRelativePath(String relPath) {
        File target = new File(context.getFilesDir(), relPath);
        try {
            return target.delete();
        } catch (Error e) {
            e.printStackTrace();
        }
        return false;
    }

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

    public File saveFromBitmapUniquely(Bitmap bitmap) {
        int newID = generateNewUnique8CharacterIDForImage();
        return saveFromBitmap(bitmap, Integer.toString(newID) + ".jpg");
    }

    private int generateNewUnique8CharacterIDForImage() {
        Random rnd = new Random();
        int newID = 0;
        boolean retry = true;
        while (retry) {
            newID = 10000000 + rnd.nextInt(90000000);
            retry = createFile(Integer.toString(newID) + ".jpg").exists();
        }
        if (newID == 0) {
            Error e = new Error("Image ID couldnt be generated");
            e.printStackTrace();
        }
        return newID;
    }

    public String getRelativePathOfImage(String imageName) {
        return "images/" + imageName;
    }

    public static Bitmap reduceSizeOfBitmap(Bitmap bitmap) {
        int currHeight = bitmap.getHeight();
        if (currHeight > MaxImageHeight) {
            return Bitmap.createScaledBitmap(bitmap, MaxImageWidth, MaxImageHeight, false);
        }
        return bitmap;
    }

    public static void startPickingAndCropping(Activity thisActivity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(ImageSaver.ImageRatioX, ImageSaver.ImageRatioY)
                .setFixAspectRatio(true)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(90)
                .start(thisActivity);
    }
}
