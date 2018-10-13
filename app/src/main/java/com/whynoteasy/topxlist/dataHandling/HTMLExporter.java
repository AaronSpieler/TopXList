package com.whynoteasy.topxlist.dataHandling;

import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class HTMLExporter {
    private Context activityContext;
    private DataRepository myRep;
    private View myView;

    static final String LOG_TAG = "EXPORT_FAILURE";

    public HTMLExporter(View view){
        this.myView = view;
        this.activityContext = view.getContext();
        this.myRep = DataRepository.getRepository();
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

        String htmlDocument = "<html><body><h1>"+activityContext.getString(R.string.html_export_title)+"</h1>"+htmlBody+"</body></html>";

        //do the actual exporting
        try {
            //get the sd card folder
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            //get new File
            String filename = "MyTopXListLists";
            String extension = "html";
            File newFile = new File(downloadFolder, filename + "." + extension);
            int counter = 1;
            while (newFile.exists()) {
                newFile = new File(downloadFolder, filename + "_" + counter + "." + extension);
                counter++;
            }

            System.out.println(newFile.getPath());

            //try to create the file
            if (newFile.createNewFile()){
                //write the htmldocument into the file
                FileOutputStream fOut = new FileOutputStream(newFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write(htmlDocument);
                myOutWriter.close();

                //show snackbar on success
                Snackbar mySnackbar = Snackbar.make(myView,  activityContext.getString(R.string.html_export_success), LENGTH_LONG);
                mySnackbar.show();
            } else {
                //couldn't create document
                Snackbar mySnackbar = Snackbar.make(myView, R.string.html_export_failure, LENGTH_SHORT);
                mySnackbar.show();
            }

        } catch (Exception e) {
            //external card not accessible
            Snackbar mySnackbar = Snackbar.make(myView, R.string.html_external_storage_not_accessible, LENGTH_SHORT);
            mySnackbar.show();
            e.printStackTrace();
        }
    }
}
