package com.qualys.qint.service.executor;

import com.oracle.bmc.computeinstanceagent.ComputeInstanceAgentClient;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.InstanceSummary;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.responses.ListInstancesResponse;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.vulnerabilityscanning.VulnerabilityScanningClient;
import com.oracle.bmc.vulnerabilityscanning.model.HostScanTargetSummary;
import com.oracle.bmc.vulnerabilityscanning.requests.ListHostScanTargetsRequest;
import com.oracle.bmc.vulnerabilityscanning.requests.ListHostVulnerabilitiesRequest;
import com.oracle.bmc.vulnerabilityscanning.responses.ListHostScanTargetsResponse;
import com.oracle.bmc.vulnerabilityscanning.responses.ListHostVulnerabilitiesResponse;
import com.qualys.ccf.contract.integration.request.ConnectorExecutionRequest;
import com.qualys.ccf.contract.integration.response.ConnectorExecutionResponse;
import com.qualys.ccf.contract.integration.response.Status;
import com.qualys.qint.constants.Constants;
import com.qualys.qint.helper.ResponseHelper;
import com.qualys.qint.model.DeltaMeta;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author jyadav@qualys.com
 */
public class OCIServiceImpl implements OCIService {

    private static final Logger log = LogManager.getLogger(OCIServiceImpl.class);
    private ResponseHelper responseHelper;
    public OCIServiceImpl() {
        this.responseHelper = new ResponseHelper();
    }


    public ConnectorExecutionResponse fetchData(ConnectorExecutionRequest request, VulnerabilityScanningClient client,
                                                DeltaMeta deltaMeta) {
        ConnectorExecutionResponse response = new ConnectorExecutionResponse();
        try {
            ListHostScanTargetsRequest listHostScanTargetsRequest = ListHostScanTargetsRequest.builder()
                    .compartmentId((String) request.getConnectorConfigDetails().getAuthDetails().get(0).getAuthField().get(Constants.COMPARTMENT_ID))
                    .limit(100)
                    .build();
            ListHostScanTargetsResponse listHostScanTargetsResponse = client.listHostScanTargets(listHostScanTargetsRequest);
            log.info("Successfully fetched Host Details");
            for (HostScanTargetSummary target : listHostScanTargetsResponse.getHostScanTargetSummaryCollection().getItems()) {
                ListHostVulnerabilitiesRequest listHostVulnerabilitiesRequest = ListHostVulnerabilitiesRequest.builder()
                        .name(target.getDisplayName())
                        .limit(1000)
                        .build();

                ListHostVulnerabilitiesResponse vulnerabilitiesResponse = client.listHostVulnerabilities(listHostVulnerabilitiesRequest);

                if (StringUtils.isNotEmpty(vulnerabilitiesResponse.getOpcNextPage())) {

                }
                log.info("Successfully fetched Host Vulnerabilities");
                log.info("Test connection successful");
                response.setStatus(Status.SUCCESS);
                response.setMessage("Test connection successful");
            }
        } catch (BmcException e) {
            responseHelper.getResponseForBmcException(e, response, Constants.FAILURE_MESSAGE);
        } catch (Exception e) {
            responseHelper.getResponseForException(e, response, Constants.FAILURE_MESSAGE);
        } finally {
            assert client != null;
            client.close();
        }
        return response;
    }

    @Override
    public ConnectorExecutionResponse fetchData(ConnectorExecutionRequest request, VulnerabilityScanningClient
            vulnerabilityScanningClient, ComputeInstanceAgentClient managedInstanceClient, DeltaMeta deltaMeta) {
        ConnectorExecutionResponse response = new ConnectorExecutionResponse();
        return response;
    }

    @Override
    public ConnectorExecutionResponse fetchAssetData(ConnectorExecutionRequest request, ComputeClient client, DeltaMeta deltaMeta) {
        ConnectorExecutionResponse response = new ConnectorExecutionResponse();
        try {
            ListInstancesRequest listInstancesRequest = ListInstancesRequest.builder()
                    .compartmentId((String) request.getConnectorConfigDetails().getAuthDetails().get(0).getAuthField().get(Constants.COMPARTMENT_ID))
                    .limit(100)
                    .build();

            ListInstancesResponse listInstancesResponse = client.listInstances(listInstancesRequest);
            log.info("Successfully fetched Asset Data Details");
            for (Instance instance : listInstancesResponse.getItems()) {
                if (StringUtils.isNotEmpty(listInstancesResponse.getOpcNextPage())) {
                    listInstancesRequest = ListInstancesRequest.builder()
                            .compartmentId((String) request.getConnectorConfigDetails().getAuthDetails().get(0).getAuthField().get(Constants.COMPARTMENT_ID))
                            .limit(100)
                            .build();
                }
                log.info("Successfully fetched Asset Data");
                response.setStatus(Status.SUCCESS);
                response.setMessage("Test connection successful");
            }
        } catch (BmcException e) {
            responseHelper.getResponseForBmcException(e, response, Constants.FAILURE_MESSAGE);
        } catch (Exception e) {
            responseHelper.getResponseForException(e, response, Constants.FAILURE_MESSAGE);
        } finally {
            assert client != null;
            client.close();
        }
        return response;
    }

    private void getHostScanTargetRequestBuilder(ConnectorExecutionRequest request, DeltaMeta deltaMeta) {
        ListHostScanTargetsRequest.Builder builder = ListHostScanTargetsRequest.builder()
                .compartmentId((String) request.getConnectorConfigDetails().getAuthDetails().get(0).getAuthField().get(Constants.COMPARTMENT_ID))
                .limit(100);

        if (deltaMeta.getDeltaMap().get(request.getProfileUuid()).getIsDelta() &&
                StringUtils.isNotEmpty(deltaMeta.getDeltaMap().get(request.getProfileUuid()).getOpcNextPage())) {
            builder.page(deltaMeta.getDeltaMap().get(request.getProfileUuid()).getOpcNextPage());
        }
    }

    private void executeHostScanTarget(ConnectorExecutionRequest request, ListHostScanTargetsRequest hostScanRequest,
                                       VulnerabilityScanningClient client) {
        //ListHostScanTargetsResponse response = client.listHostScanTargets(listHostScanTargetsRequest);
    }

    private void getHostVulnerabilityRequestBuilder(ConnectorExecutionRequest request, String displayName) {
        ListHostVulnerabilitiesRequest.Builder builder = ListHostVulnerabilitiesRequest.builder()
                .compartmentId((String) request.getConnectorConfigDetails().getAuthDetails().get(0).getAuthField().get(Constants.COMPARTMENT_ID))
                .name(displayName)
                .limit(1000);
    }

}
