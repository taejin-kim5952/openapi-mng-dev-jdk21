package com.kt.openapi.web.apigw.entity.api;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class GwUrlEncDec implements Serializable {
	@Serial
	private static final long serialVersionUID = -2963950572745350727L;

	@NotEmpty(message = "[charset] field is required")
    private String charset;
    @NotEmpty(message = "[target] field is required")
    private List<String> target;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }
}
