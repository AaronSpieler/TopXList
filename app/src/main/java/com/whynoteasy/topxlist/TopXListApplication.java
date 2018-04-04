package com.whynoteasy.topxlist;

import android.app.Activity;
import android.app.Application;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Whatever on 04.04.2018.
 */

public class TopXListApplication extends Application {

    public static EditText configureEditText(final EditText mContent, final View nextFocus, final Activity mContext){
        mContent.setRawInputType(InputType.TYPE_CLASS_TEXT);
        //dont know what string.done is supposed to be
        //mContent.setImeActionLabel(mContent.getResources().getString(R.string.done), EditorInfo.IME_ACTION_DONE);
        mContent.setImeOptions(EditorInfo.IME_ACTION_DONE);

        mContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Capture soft enters in a singleLine EditText that is the last EditText
                        // This one is useful for the new list case, when there are no existing ListItems
                        //mContent.clearFocus();

                        //My addition
                        nextFocus.requestFocus();

                        //I dont want to clear the sonftInputWindow
                        //InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        //inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        // Capture soft enters in other singleLine EditTexts
                    } else if (actionId == EditorInfo.IME_ACTION_GO) {
                    } else {
                        // Let the system handle all other null KeyEvents
                        return false;
                    }
                } else if (actionId == EditorInfo.IME_NULL) {
                    // Capture most soft enters in multi-line EditTexts and all hard enters;
                    // They supply a zero actionId and a valid keyEvent rather than
                    // a non-zero actionId and a null event like the previous cases.

                    //My addition
                    nextFocus.requestFocus();

                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        // We capture the event when the key is first pressed.
                    } else {
                        // We consume the event when the key is released.
                        return true;
                    }
                } else {
                    // We let the system handle it when the listener is triggered by something that
                    // wasn't an enter.
                    return false;
                }
                return true;
            }
        });

        return mContent;
    }
}
