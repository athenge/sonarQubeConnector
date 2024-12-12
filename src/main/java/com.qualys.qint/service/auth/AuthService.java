package com.qualys.qint.service.auth;

import com.oracle.bmc.computeinstanceagent.ComputeInstanceAgentClient;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.osmanagementhub.ManagedInstanceClient;
import com.oracle.bmc.vulnerabilityscanning.VulnerabilityScanningClient;
import com.qualys.ccf.contract.integration.response.TestConnectionResponse;
import com.qualys.ccf.contract.model.AuthDetails;
import com.qualys.ccf.contract.model.ProxyConfig;

public interface AuthService {

    TestConnectionResponse testConnection(VulnerabilityScanningClient client, ComputeInstanceAgentClient computeInstanceAgentClient, AuthDetails authDetails);

    VulnerabilityScanningClient getVulnerabilityScanningClient(AuthDetails authDetails, ProxyConfig proxyConfig, Integer id);

    ManagedInstanceClient getManagedInstanceClient(AuthDetails authDetails, ProxyConfig proxyConfig, Integer id);

    ComputeInstanceAgentClient getComputeInstanceAgentClient(AuthDetails authDetails, ProxyConfig proxyConfig, Integer id);

    ComputeClient getComputeClient(AuthDetails authDetails, ProxyConfig proxyConfig, Integer id);

}
