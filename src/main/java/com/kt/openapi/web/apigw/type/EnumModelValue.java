//-- [tag:PRJ-20220901][i][mpybe_not_used]
package com.kt.openapi.web.apigw.type;

public class EnumModelValue {
    private String code;
    private String title;

    public EnumModelValue(EnumModel e) {
        this.code = e.getKey();
        this.title = e.getValue();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
