package com.kt.openapi.web.apigw.services.api.cp;

import com.kt.openapi.web.apigw.entity.api.cp.CpApiRequest;
import com.kt.openapi.web.apigw.entity.api.cp.CpApiResponse;
import com.kt.openapi.web.apigw.type.GwProfile;

public interface CpApiService {
    CpApiResponse get(GwProfile profile, CpApiRequest request);
    //-- [tag:PRJ-20220901]
    CpApiResponse get(String apiVeriBaseurl, CpApiRequest request);
}
