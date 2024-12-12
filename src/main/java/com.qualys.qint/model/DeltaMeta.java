package com.qualys.qint.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

/**
 * @author jyadav@qualys.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeltaMeta {

    @JsonProperty("deltaMap")
    private Map<String, Delta> deltaMap;

}
