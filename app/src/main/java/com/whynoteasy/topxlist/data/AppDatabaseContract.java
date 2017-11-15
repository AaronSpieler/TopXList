package com.whynoteasy.topxlist.data;

import android.provider.BaseColumns;

/**
 * Created by Whatever on 31.10.2017.
 */

public final class AppDatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private AppDatabaseContract() {}

    /* Inner class that defines the table contents
    *  implements BaseColumns for _id column */
    public static class XListTable implements BaseColumns {
        public static final String TABLE_NAME = "xlisttable";
        public static final String COLUMN_NAME_XLISTTITLE = "xlisttitle";
        public static final String COLUMN_NAME_XLISTSHORTDESCRIPTION = "xlistshortdescription";
        public static final String COLUMN_NAME_XLISTLONGDESCRIPTION = "xlistlongdescription";
        public static final String COLUMN_NAME_XLISTNUM = "xlistnum";
    }

    public static class XElemTable implements BaseColumns {
        public static final String TABLE_NAME = "xelemtable";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_XELEMTITLE = "xelemtitle";
        public static final String COLUMN_XELEMDESCRIPTION = "xelemdescription";
        public static final String COLUMN_XELEMNUM = "xelemnum";
        public static final String COLUMN_XLISTID = "xlistid";
    }

    public static class XTagTable implements BaseColumns {
        public static final String TABLE_NAME = "xtagtable";
        public static final String COLUMN_NAME_XTAGNAME = "xtagname";
        public static final String COLUMN_NAME_XLISTID = "xlistid";

    }
}
