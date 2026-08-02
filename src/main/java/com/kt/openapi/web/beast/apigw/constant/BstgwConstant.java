package com.kt.openapi.web.beast.apigw.constant;

public class BstgwConstant {
	//-- [i][using BstgwConfig.java]
    public static class BSTGWCONFIG {
        public static final int CONNECTION_TIMEOUT = 3000;
        public static final int READ_TIMEOUT = 5000;
    }

    public static class PROFILE {
        public static final String TB = "TB";
        public static final String PRD = "PRD";

        public static final String TB_KTC = "TB_KTC";
        public static final String TB_AZURE = "TB_AZURE";
        public static final String PRD_KTC = "PRD_KTC";
        public static final String PRD_AZURE = "PRD_AZURE";
    }

    public static class DPLY_TYPE {
        public static final String DPLY = "DPLY";
        public static final String DEL = "DEL";
    }
    
    public static class RETURN_CD {
        public static final String INIT = "INIT";
        public static final String OK = "OK";
        public static final String NK = "NK";
        public static final String ERR = "ERR";
        public static final String EXCEPT = "EXCEPT";
    }

    public static String SRC_TAG_APILINK = "APILINK";
}
