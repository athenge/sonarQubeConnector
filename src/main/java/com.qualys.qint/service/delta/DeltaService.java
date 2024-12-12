package com.qualys.qint.service.delta;

import com.qualys.qint.model.DeltaMeta;

public interface DeltaService {

    DeltaMeta fetchDelta(String deltaMsg, String profileUuid);

}
