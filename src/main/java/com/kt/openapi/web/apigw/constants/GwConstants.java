package com.kt.openapi.web.apigw.constants;

public class GwConstants {
    public static final String DEPLOYMENT_TARGET_ENDPOINT = "endpoints";
    public static final String DEPLOYMENT_TARGET_API = "apis";

    public static final int CONNECTION_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 5000;

    public static final String ENDPOINT_LIST_URI = "/endpoints";
    public static final String DEPLOYMENT_URI = "/groups/%s/deployments";
    public static final String NEW_DEPLOYMENT_URI = "/adp/groups/%s/deployments";
    public static final String API_BASE_URI = "/apis";
    public static final String NEW_API_BASE_URI = "/adp/apis";
    //-- [tag:SR-20210222][chg]
    public static final String API_COMMON_PARAM_TYPE_B = "TYPE_B";
    //-- [tag:SR-20210222][add] {
    public static final String API_COMMON_PARAM_TYPE_J = "TYPE_J";
    public static final String API_DIVISION_ALONE = "ALONE";
    public static final String API_ROUTE_INFO_ROUTE_TARGET = "all";
    public static final String API_ENDPOINT_ID_CAPRI = "Capri";
    //-- [tag:SR-20210222][add] }
    public static final String API_DEFAULT_AUTH_STAGE = "authStage";
    public static final String API_REQUEST_STAGE = "requestMessageStage";
    public static final String API_RESPONSE_STAGE = "responseMessageStage";

    public static final String SERVER_BD_PREFIX = "BD_";
    public static final String SERVER_DR_PREFIX = "DR_";

    public static final String API_PARAMETER_DEPTH_SEPARATOR = ".";

    /* common request key */
    public static class REQUEST_KEY {
        public static final String HANDLER_TYPE = "HANDLER_TYPE";
        public static final String REQUIRED_PARAM = "REQUIRED_PARAM";
        public static final String DECRYPT_TARGET = "DECRYPT_TARGET";
        public static final String CLIENT_IP_RULE = "CLIENT_IP_RULE";
        public static final String HEADER_PARAM = "HEADER_PARAM";
        public static final String HEADER_FIXED_RULE = "HEADER_FIXED_RULE";

        // common handler request

        // any common request
        public static final String EXCEPT_PARAM = "EXCEPT_PARAM";
        public static final String BODY_FIXED_RULE = "BODY_FIXED_RULE";
        public static final String MAPPING_RULE = "MAPPING_RULE";

        // kos json
        public static final String NULLSET_PARAM = "NULLSET_PARAM";

        // kos soap
        public static final String FIXED_PARAM = "FIXED_PARAM";
        public static final String FIXED_PARAM_COMMON_HEADER = "commonHeader";
        public static final String FIXED_PARAM_APP_NAME = "appName";
        public static final String FIXED_PARAM_SVC_NAME = "svcName";
        public static final String FIXED_PARAM_FN_NAME = "fnName";
        public static final String FIXED_PARAM_CHANNEL_TYPE = "chnlType";

        //-- [tag:SR-20210222][add] {
        // scap, capri, sb
        public static final String API_NAME = "API_NAME";
        public static final String CONFIG_TO_BODY = "CONFIG_TO_BODY";
        public static final String HEADER_TO_BODY = "HEADER_TO_BODY";
        public static final String MAPPING_TO_BODY = "MAPPING_TO_BODY";
        public static final String URL_ENCODE = "URL_ENCODE";
        public static final String URL_DECODE = "URL_DECODE";
        public static final String UPLOAD_TARGET = "UPLOAD_TARGET";
        //-- [tag:SR-20210222][add] }
    }


    /* response key */
    public static class RESPONSE_KEY {
        public static final String HANDLER_TYPE = "HANDLER_TYPE";
        public static final String ENCRYPT_TARGET = "ENCRYPT_TARGET";

        // any common handler
        public static final String EXCEPT_PARAM = "EXCEPT_PARAM";
        public static final String BODY_FIXED_RULE = "BODY_FIXED_RULE";
        public static final String MAPPING_RULE = "MAPPING_RULE";
        public static final String RESULT_MAPPING_RULE = "RESULT_MAPPING_RULE";
        public static final String SUCCESS_CODE = "SuccessCodeField";
        public static final String SUCCESS_VALUE = "SuccessInfo";
        public static final String ERROR_CODE = "ErrorCodeField";
        public static final String ERROR_MESSAGE = "ErrorDescField";

        // kos soap
        public static final String JSON_ARRAY_TARGET = "JSON_ARRAY_TARGET";

        //-- [tag:SR-20210222][add] {
        // scap, capri, sb
        public static final String MAPPING_TO_BODY = "MAPPING_TO_BODY";
        public static final String URL_ENCODE = "URL_ENCODE";
        public static final String PROVIED_PARAM = "PROVIED_PARAM";
        public static final String ARRAY_TARGET = "ARRAY_TARGET";
        //-- [tag:SR-20210222][add] }
    }

    /* cp api*/
    public static class CP_API {
        public static final String TRANSACTION_ID = "transactionid";
        public static final String SEQUENCE_NO = "sequenceno";
        public static final String REQUEST = "request";
        public static final String RESPONSE = "response";
        public static final String AUTHORIZATION = "Authorization";
    }
}
