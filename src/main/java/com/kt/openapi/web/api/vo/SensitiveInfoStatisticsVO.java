package com.kt.openapi.web.api.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SensitiveInfoStatisticsVO {
    private int totalParams;
    private int grade1Count;
    private int grade2Count;
}