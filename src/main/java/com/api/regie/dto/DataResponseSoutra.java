package com.api.regie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataResponseSoutra {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expiry_time")
    private String expiryTime;

    @JsonProperty("company_id")
    private String companyId;
}
