package com.qualys.qint.service.executor;

import com.oracle.bmc.computeinstanceagent.ComputeInstanceAgentClient;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.vulnerabilityscanning.VulnerabilityScanningClient;
import com.qualys.ccf.contract.integration.request.ConnectorExecutionRequest;
import com.qualys.ccf.contract.integration.response.ConnectorExecutionResponse;
import com.qualys.qint.model.DeltaMeta;

public interface OCIService {

    ConnectorExecutionResponse fetchData(ConnectorExecutionRequest connectorExecutionRequest,
                                         VulnerabilityScanningClient vulnerabilityScanningClient,
                                         ComputeInstanceAgentClient computeInstanceAgentClient, DeltaMeta deltaMeta);

    ConnectorExecutionResponse fetchAssetData(ConnectorExecutionRequest connectorExecutionRequest,
                                              ComputeClient computeClient, DeltaMeta deltaMeta);


}
