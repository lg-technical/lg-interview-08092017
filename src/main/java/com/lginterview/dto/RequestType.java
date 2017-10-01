package com.lginterview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RequestType {
    @JsonProperty("init")
    INIT,
    @JsonProperty("save")
    SAVE,
    @JsonProperty("finish")
    FINISH
}
