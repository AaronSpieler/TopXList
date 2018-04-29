package com.whynoteasy.topxlist.importExport;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.data.LocalDataRepository;
import com.whynoteasy.topxlist.object.XElemModel;
import com.whynoteasy.topxlist.object.XListTagsSharesPojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class HTMLExporter {
    private Context activityContext;
    private LocalDataRepository myRep;
    private View myView;

    static final String LOG_TAG = "EXPORT_FAILURE";

    public HTMLExporter(View view){
        this.myView = view;
        this.activityContext = view.getContext();
        myRep = new LocalDataRepository(activityContext.getApplicationContext());
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public void exportToHTML(){
        String htmlBody = "";
        List<XListTagsSharesPojo> allLists = myRep.getListsWithTagsShares();
        for (XListTagsSharesPojo listPJ : allLists) {
            htmlBody = htmlBody + "<h2>"+listPJ.getXListModel().getXListTitle()+"</h2>\n" +
                    "<p>"+listPJ.getXListModel().getXListShortDescription()+"</p>\n" +
                    "<p>"+listPJ.getXListModel().getXListLongDescription()+"</p>\n" +
                    "<p>"+listPJ.tagsToString()+"</p>\n";
            List<XElemModel> allElems = myRep.getElementsByListID(listPJ.getXListModel().getXListID());
            for (XElemModel elem : allElems) {
                htmlBody = htmlBody + "<h3>"+"("+elem.getXElemNum()+".) "+elem.getXElemTitle()+"</h3>\n" +
                        "<p>"+elem.getXElemDescription()+"</p>\n";
            }
            htmlBody = htmlBody + "<br>";
        }

        String filename = "MyTopXListLists.html";
        String htmlDocument = "<html><body><h1>"+activityContext.getString(R.string.html_export_title)+"</h1>"+htmlBody+"</body></html>";

        //write the file if external storage writable
        if (isExternalStorageWritable()) {
            //important that this is outside the try block
            File newFile = getPublicStorageDir(filename);
            //File newFile = new File("//sdcard//Download//", filename); //workds as well
            try {
                //try to create the file
                boolean success = newFile.createNewFile();
                if (!success) {
                    File file = new File(newFile.getAbsolutePath());
                    boolean deleted = file.delete();
                    if (!deleted) {
                        Snackbar mySnackbar = Snackbar.make(myView, R.string.html_file_creation_failed, LENGTH_SHORT);
                        mySnackbar.show();
                        return;
                    } else {
                        newFile.createNewFile();
                    }
                }

                //write the htmldocument into the file
                FileOutputStream fOut = new FileOutputStream(newFile, false);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write(htmlDocument);
                myOutWriter.close();

                DownloadManager downloadManager = (DownloadManager) activityContext.getSystemService(DOWNLOAD_SERVICE);
                downloadManager.addCompletedDownload(newFile.getName(), newFile.getName(), true, "text/html",newFile.getAbsolutePath(),newFile.length(),true);

                Snackbar mySnackbar = Snackbar.make(myView, R.string.html_export_success, LENGTH_SHORT);
                mySnackbar.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Snackbar mySnackbar = Snackbar.make(myView, R.string.html_external_storage_not_accessible, LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicStorageDir(String filename) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory Downloads not found");
        }
        return file;
    }

}
