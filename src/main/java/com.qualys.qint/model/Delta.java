package com.qualys.qint.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delta {

    @JsonProperty("profileUuid")
    private String profileUuid;
    @JsonProperty("opcNextPage")
    private String opcNextPage;
    @JsonProperty("isDelta")
    private Boolean isDelta;

}
