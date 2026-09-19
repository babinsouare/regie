package com.api.regie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponseSoutra {

    @JsonProperty("status_code")
    private int statusCode;

    private DataResponseSoutra data;
}
