package com.whynoteasy.topxlist.dataHandling;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.whynoteasy.topxlist.general.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 *
 */

public class ImageHandler {



    private int MaxImageHeight; //= 720 by default
    private int MaxImageWidth; // = 1280 by default
    private final int ImageRatioX = 16;
    private final int ImageRatioY = 9;
    private final int verSmallImageSize = 320;
    private final int smallImageSize = 480;
    private final int normalImageQuality = 720;
    private final int largeImageSize = 1080;
    private final int defaultImageQuality = 3;
    private final int imageQuality = 90;


    private Context context;

    public ImageHandler(Context context) {
        this.context = context;

        //setting up the image sizes
        int imageSetting = defaultImageQuality;
        try {
            imageSetting = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(SettingsActivity.KEY_PREF_IMAGE_SIZE, Integer.toString(defaultImageQuality))); //default option 3
        }catch (Error e) {
            e.printStackTrace();
        }
        switch (imageSetting) {
            case 1:
                MaxImageHeight = verSmallImageSize;
                MaxImageWidth = Math.round(verSmallImageSize*((float) ImageRatioX/ImageRatioY));
                break;
            case 2:
                MaxImageHeight = smallImageSize;
                MaxImageWidth = Math.round(smallImageSize*((float) ImageRatioX/ImageRatioY));
                break;
            case 3:
                MaxImageHeight = normalImageQuality;
                MaxImageWidth = Math.round(normalImageQuality *((float) ImageRatioX/ImageRatioY));
                break;
            case 4:
                MaxImageHeight = largeImageSize;
                MaxImageWidth = Math.round(largeImageSize*((float) ImageRatioX/ImageRatioY));
                break;
        }
    }

    @NonNull
    private File createFile(String fileName) {
        String directoryName = "images";
        File directory = new File(context.getFilesDir(), directoryName);
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("ImageHandler", "Error creating directory " + directory);
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, fileOutputStream); //JPEG creation
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

    private Bitmap reduceSizeOfBitmap(Bitmap bitmap) {
        int currHeight = bitmap.getHeight();
        if (currHeight > MaxImageHeight) {
            return Bitmap.createScaledBitmap(bitmap, MaxImageWidth, MaxImageHeight, false);
        }
        return bitmap;
    }

    public void startPickingAndCropping(Activity thisActivity) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(ImageRatioX, ImageRatioY)
                .setFixAspectRatio(true)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(imageQuality)
                .start(thisActivity);
    }

}
