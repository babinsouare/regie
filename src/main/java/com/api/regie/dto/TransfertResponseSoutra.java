package com.api.regie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransfertResponseSoutra {

    @JsonProperty("status_code")
    private int statusCode;

    private int success;

    private String data;

    private List<String> error;

    private JsonNode decryptedData;
}
