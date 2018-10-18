package com.whynoteasy.topxlist.dataHandling;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.whynoteasy.topxlist.R;
import com.whynoteasy.topxlist.dataObjects.XElemModel;
import com.whynoteasy.topxlist.dataObjects.XListTagsSharesPojo;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class HTMLExporter {
    private Context activityContext;
    private DataRepository myRep;
    private View myView;

    static final String LOG_TAG = "EXPORT_FAILURE";

    static final int EXPORT_CODE = 1;

    public HTMLExporter(View view){
        this.myView = view;
        this.activityContext = view.getContext();
        this.myRep = DataRepository.getRepository();
    }

    private String createHTMLContent() {
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
        return "<!DOCTYPE html> " +
                "<html>" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>" +
                "<h1>"+activityContext.getString(R.string.html_export_title)+"</h1>"+htmlBody+"" +
                "</body>" +
                "</html>";
    }

    public void saveHTMLtoFile(Uri uri){
        try{
            ParcelFileDescriptor exportFile = activityContext.getContentResolver().openFileDescriptor(uri, "w");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(exportFile.getFileDescriptor()));

            outputStreamWriter.write(createHTMLContent());
            outputStreamWriter.close();
            exportFile.close();

            Snackbar mySnackbar = Snackbar.make(myView,  activityContext.getString(R.string.html_export_success), LENGTH_LONG);
            mySnackbar.show();
        } catch (Exception e) {
            Snackbar mySnackbar = Snackbar.make(myView, R.string.html_export_failure, LENGTH_LONG);
            mySnackbar.show();
            e.printStackTrace();
            e.printStackTrace();
        }
    }
}
