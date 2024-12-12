package com.qualys.qint.service.delta;

import com.google.gson.Gson;
import com.qualys.qint.model.Delta;
import com.qualys.qint.model.DeltaMeta;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jyadav@qualys.com
 */

public class DeltaServiceImpl implements DeltaService {


    /**
     * This method sets the delta metadata while starting the execution.
     *
     * @param deltaMsg
     * @param profileUuid
     * @return
     */
    @Override
    public DeltaMeta fetchDelta(String deltaMsg, String profileUuid) {
        DeltaMeta deltaMeta = null;
        Gson gson = new Gson();
        if (StringUtils.isNotEmpty(deltaMsg)) {
            deltaMeta = gson.fromJson(deltaMsg, DeltaMeta.class);
            if (MapUtils.isEmpty(deltaMeta.getDeltaMap())) {
                deltaMeta = setDeltaMeta(profileUuid);
            } else {
                if (!deltaMeta.getDeltaMap().containsKey(profileUuid)) {
                    deltaMeta.getDeltaMap().put(profileUuid, getDelta(profileUuid));
                }
            }
        } else {
            deltaMeta = setDeltaMeta(profileUuid);
        }
        return deltaMeta;
    }

    /**
     * This method sets the Delta Metadata for the first time of execution.
     *
     * @param profileUuid
     * @return
     */
    private DeltaMeta setDeltaMeta(String profileUuid) {
        Map<String, Delta> deltaMap = new HashMap<>();
        deltaMap.put(profileUuid, getDelta(profileUuid));
        DeltaMeta deltaMeta = new DeltaMeta();
        deltaMeta.setDeltaMap(deltaMap);
        return deltaMeta;
    }

    /**
     * This method sets the Delta object for the first time execution.
     *
     * @param profileUuid
     * @return
     */
    private Delta getDelta(String profileUuid) {
        Delta delta = new Delta();
        delta.setProfileUuid(profileUuid);
        delta.setIsDelta(false);
        delta.setOpcNextPage(Strings.EMPTY);
        return delta;
    }

}
