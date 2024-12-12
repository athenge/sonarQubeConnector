package com.qualys.qint.service.connector;

import com.oracle.bmc.computeinstanceagent.ComputeInstanceAgentClient;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.osmanagementhub.ManagedInstanceClient;
import com.oracle.bmc.vulnerabilityscanning.VulnerabilityScanningClient;
import com.qualys.ccf.contract.integration.request.*;
import com.qualys.ccf.contract.integration.response.*;
import com.qualys.ccf.contract.interfaces.Connector;
import com.qualys.qint.constants.Constants;
import com.qualys.qint.helper.ConnectorHelper;
import com.qualys.qint.helper.ResponseHelper;
import com.qualys.qint.model.DeltaMeta;
import com.qualys.qint.service.auth.AuthService;
import com.qualys.qint.service.auth.AuthServiceImpl;
import com.qualys.qint.service.delta.DeltaService;
import com.qualys.qint.service.delta.DeltaServiceImpl;
import com.qualys.qint.service.executor.OCIService;
import com.qualys.qint.service.executor.OCIServiceImpl;
import lombok.Data;


@Data
public class OCIConnector implements Connector {

    private AuthService authService;
    private OCIService ociService;
    private ConnectorHelper connectorHelper;
    private DeltaService deltaService;
    private ResponseHelper responseHelper;

    public OCIConnector() {
        this.authService = new AuthServiceImpl();
        this.ociService = new OCIServiceImpl();
        this.connectorHelper = new ConnectorHelper();
        this.deltaService = new DeltaServiceImpl();
        this.responseHelper = new ResponseHelper();
    }

    @Override
    public ConnectorExecutionResponse executeConnector(ConnectorExecutionRequest request) {
        ConnectorExecutionResponse response = null;
        if (Constants.OCI.equalsIgnoreCase(request.getType())) {
            connectorHelper.updateAuthDetails(request);
            VulnerabilityScanningClient vulnerabilityScanningClient = authService.getVulnerabilityScanningClient(request.getConnectorConfigDetails().
                    getAuthDetails().get(0), request.getProxyConfig(), request.getConnectorConfigDetails().getId());
            ComputeInstanceAgentClient computeInstanceAgentClient = authService.getComputeInstanceAgentClient(request.getConnectorConfigDetails().
                    getAuthDetails().get(0), request.getProxyConfig(), request.getConnectorConfigDetails().getId());
            ComputeClient computeClient = authService.getComputeClient(request.getConnectorConfigDetails().
                    getAuthDetails().get(0), request.getProxyConfig(), request.getConnectorConfigDetails().getId());
            authService.testConnection(vulnerabilityScanningClient, computeInstanceAgentClient, request.getConnectorConfigDetails().getAuthDetails().get(0));

            DeltaMeta deltaMeta = deltaService.fetchDelta(request.getConnectorConfigDetails().getDeltaMsg(),
                    request.getProfileUuid());

            String entityType = connectorHelper.getEntityType(request);
            if (Constants.ENTITY_TYPE_HOST_AND_VULNERABILITY.equalsIgnoreCase(entityType)) {
                response = ociService.fetchData(request, vulnerabilityScanningClient, computeInstanceAgentClient, deltaMeta);
                response = ociService.fetchAssetData(request, computeClient, deltaMeta);
            } else {
                response = responseHelper.getResponseForOtherFailures(Constants.INVALID_ENTITY_TYPE_REQUEST);
            }
        } else {
            response = responseHelper.getResponseForOtherFailures(Constants.INVALID_CONNECTOR_TYPE);
        }

        return response;
    }

    @Override
    public TestConnectionResponse testConnection(TestConnectionRequest request) {
        VulnerabilityScanningClient vulnerabilityScanningClient = authService.getVulnerabilityScanningClient(request.getConnectorConfigDetails().
                getAuthDetails().get(0), request.getProxyConfig(), request.getConnectorConfigDetails().getId());
        ComputeInstanceAgentClient computeInstanceAgentClient = authService.getComputeInstanceAgentClient(request.getConnectorConfigDetails().
                getAuthDetails().get(0), request.getProxyConfig(), request.getConnectorConfigDetails().getId());
        return authService.testConnection(vulnerabilityScanningClient, computeInstanceAgentClient, request.getConnectorConfigDetails().getAuthDetails().get(0));
    }

    @Override
    public UIConfigResponse getUIConfig(UIConfigRequest uiConfigRequest) {
        return null;
    }

    @Override
    public ConnectorSchemaResponse getSchemaDetails(ConnectorSchemaRequest connectorSchemaRequest) {
        return null;
    }

    @Override
    public ConnectorPreviewResponse connectorPreview(ConnectorPreviewRequest connectorPreviewRequest) {
        return null;
    }
}
