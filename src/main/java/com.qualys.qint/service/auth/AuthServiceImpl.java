package com.qualys.qint.service.auth;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.computeinstanceagent.ComputeInstanceAgentClient;
import com.oracle.bmc.computeinstanceagent.requests.ListInstanceAgentCommandsRequest;
import com.oracle.bmc.computeinstanceagent.responses.ListInstanceAgentCommandsResponse;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.http.client.ProxyConfiguration;
import com.oracle.bmc.http.client.StandardClientProperties;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.osmanagementhub.ManagedInstanceClient;
import com.oracle.bmc.osmanagementhub.requests.ListManagedInstancesRequest;
import com.oracle.bmc.osmanagementhub.responses.ListManagedInstancesResponse;
import com.oracle.bmc.vulnerabilityscanning.VulnerabilityScanningClient;
import com.oracle.bmc.vulnerabilityscanning.model.HostScanTargetSummary;
import com.oracle.bmc.vulnerabilityscanning.requests.ListHostScanTargetsRequest;
import com.oracle.bmc.vulnerabilityscanning.requests.ListHostVulnerabilitiesRequest;
import com.oracle.bmc.vulnerabilityscanning.responses.ListHostScanTargetsResponse;
import com.oracle.bmc.vulnerabilityscanning.responses.ListHostVulnerabilitiesResponse;
import com.qualys.ccf.contract.integration.response.Status;
import com.qualys.ccf.contract.integration.response.TestConnectionResponse;
import com.qualys.ccf.contract.model.AuthDetails;
import com.qualys.ccf.contract.model.ProxyConfig;
import com.qualys.qint.constants.Constants;
import com.qualys.qint.helper.ResponseHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.function.Supplier;

/**
 * @author jyadav@qualys.com
 */
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LogManager.getLogger(AuthServiceImpl.class);
    private ResponseHelper responseHelper;

    public AuthServiceImpl() {
        this.responseHelper = new ResponseHelper();
    }

    /**
     * This method
     *
     * @param vulnerabilityScanningClient
     * @param authDetails
     * @return
     */
    @Override
    public TestConnectionResponse testConnection(VulnerabilityScanningClient vulnerabilityScanningClient,
                                                 ComputeInstanceAgentClient computeInstanceAgentClient, AuthDetails authDetails) {
        TestConnectionResponse response = new TestConnectionResponse();
        if (vulnerabilityScanningClient == null) {
            responseHelper.getResponseForOtherFailures(Constants.FAILED_TO_CREATE_CLIENT);
        }
        try {
            log.info("Successfully fetched Host Details");

            ListInstanceAgentCommandsRequest listHostVulnerabilitiesRequest = ListInstanceAgentCommandsRequest.builder()
                    .compartmentId((String) authDetails.getAuthField().get(Constants.COMPARTMENT_ID))
                    .limit(1000)
                    .build();

            ListInstanceAgentCommandsResponse listInstanceAgentCommandsResponse = computeInstanceAgentClient.listInstanceAgentCommands(listHostVulnerabilitiesRequest);

            /*for (HostScanTargetSummary target : listManagedInstancesResponse.getHostScanTargetSummaryCollection().getItems()) {
                ListHostVulnerabilitiesRequest listHostVulnerabilitiesRequest = ListHostVulnerabilitiesRequest.builder()
                        .compartmentId((String) authDetails.getAuthField().get(Constants.COMPARTMENT_ID))
                        .limit(1000)
                        .build();

                ListHostVulnerabilitiesResponse vulnerabilitiesResponse = vulnerabilityScanningClient.listHostVulnerabilities(listHostVulnerabilitiesRequest);
                log.info("Successfully fetched Host Vulnerabilities");
                log.info("Test connection successful");
                response.setStatus(Status.SUCCESS);
                response.setMessage("Test connection successful");
            }*/
        } catch (BmcException e) {
            responseHelper.getResponseForBmcException(e, response, Constants.FAILURE_MESSAGE);
        } catch (Exception e) {
            responseHelper.getResponseForException(e, response, Constants.FAILURE_MESSAGE);
        } finally {
            assert vulnerabilityScanningClient != null;
            vulnerabilityScanningClient.close();
        }
        return response;
    }

    /**
     * This method will return Client for execution
     *
     * @param authDetails
     * @param proxyConfig
     * @param id
     * @return
     */
    @Override
    public VulnerabilityScanningClient getVulnerabilityScanningClient(AuthDetails authDetails, ProxyConfig proxyConfig, Integer id) {
        ProxyConfiguration proxyConfiguration = getProxyConfiguration(proxyConfig);
        VulnerabilityScanningClient.Builder clientBuilder = VulnerabilityScanningClient.builder()
                .region(Region.valueOf((String) authDetails.getAuthField().get(Constants.REGION)));
        if (proxyConfiguration != null) {
            clientBuilder.clientConfigurator(builder -> {
                builder.property(StandardClientProperties.PROXY, proxyConfiguration);
            });
        }
        return clientBuilder.build(getAuthProvider(authDetails, id));
    }

    /**
     * This method will return Managed Instance Client for execution
     *
     * @param authDetails
     * @param proxyConfig
     * @param id
     * @return
     */
    @Override
    public ManagedInstanceClient getManagedInstanceClient(AuthDetails authDetails, ProxyConfig proxyConfig, Integer id) {
        ProxyConfiguration proxyConfiguration = getProxyConfiguration(proxyConfig);
        ManagedInstanceClient.Builder instanceClientBuilder = ManagedInstanceClient.builder()
                .region(Region.valueOf((String) authDetails.getAuthField().get(Constants.REGION)));
        if (proxyConfiguration != null) {
            instanceClientBuilder.clientConfigurator(builder -> {
                builder.property(StandardClientProperties.PROXY, proxyConfiguration);
            });
        }
        return instanceClientBuilder.build(getAuthProvider(authDetails, id));
    }

    /**
     * This method will return Compute Instance Client for execution
     *
     * @param authDetails
     * @param proxyConfig
     * @param id
     * @return
     */
    @Override
    public ComputeInstanceAgentClient getComputeInstanceAgentClient(AuthDetails authDetails, ProxyConfig proxyConfig, Integer id) {
        ProxyConfiguration proxyConfiguration = getProxyConfiguration(proxyConfig);
        ComputeInstanceAgentClient.Builder instanceClientBuilder = ComputeInstanceAgentClient.builder()
                .region(Region.valueOf((String) authDetails.getAuthField().get(Constants.REGION)));
        if (proxyConfiguration != null) {
            instanceClientBuilder.clientConfigurator(builder -> {
                builder.property(StandardClientProperties.PROXY, proxyConfiguration);
            });
        }
        return instanceClientBuilder.build(getAuthProvider(authDetails, id));
    }

    /**
     * This method will return Compute Instance Client for execution
     *
     * @param authDetails
     * @param proxyConfig
     * @param id
     * @return
     */


    @Override
    public ComputeClient getComputeClient(AuthDetails authDetails, ProxyConfig proxyConfig, Integer id) {
        ProxyConfiguration proxyConfiguration = getProxyConfiguration(proxyConfig);
        ComputeClient.Builder clientBuilder = ComputeClient.builder()
                .region(Region.valueOf((String) authDetails.getAuthField().get(Constants.REGION)));
        if (proxyConfiguration != null) {
            clientBuilder.clientConfigurator(builder -> {
                builder.property(StandardClientProperties.PROXY, proxyConfiguration);
            });
        }
        return clientBuilder.build(getAuthProvider(authDetails, id));
    }

    /**
     * This method returns proxy configurator used to set the Client
     *
     * @param proxyConfig
     * @return
     */


    private ProxyConfiguration getProxyConfiguration(ProxyConfig proxyConfig) {
        if (StringUtils.isEmpty(proxyConfig.getProxyUrl())) {
            return null;
        } else {
            ProxyConfiguration.Builder proxyBuilder = ProxyConfiguration.builder();
            proxyBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyConfig.getProxyUrl(), proxyConfig.getProxyPort())));
            if (StringUtils.isNotEmpty(proxyConfig.getProxyUsername()) && StringUtils.isNotEmpty(proxyConfig.getProxyPassword())) {
                proxyBuilder.username(proxyConfig.getProxyUsername());
                proxyBuilder.password(proxyConfig.getProxyPassword().toCharArray());
            }
            return proxyBuilder.build();
        }
    }

    /**
     * This method returns the Auth Provider
     *
     * @param authDetails
     * @param id
     * @return
     */
    private AuthenticationDetailsProvider getAuthProvider(AuthDetails authDetails, Integer id) {
        return SimpleAuthenticationDetailsProvider.builder()
                .tenantId((String) authDetails.getAuthField().get(Constants.TENANCY_ID))
                .userId((String) authDetails.getAuthField().get(Constants.USER_ID))
                .fingerprint((String) authDetails.getAuthField().get(Constants.FINGERPRINT))
                .privateKeySupplier(getFileSupplierInputStream(authDetails, id))
                .region(Region.valueOf((String) authDetails.getAuthField().get(Constants.REGION)))
                .build();
    }

    /**
     * This method returns the Supplier Input Stream for the OCI Private Key
     *
     * @param authDetails
     * @param id
     * @return
     */
    private Supplier<InputStream> getFileSupplierInputStream(AuthDetails authDetails, Integer id) {
        File tmpFile;
        FileWriter writer = null;
        try {
            tmpFile = File.createTempFile(String.valueOf(id), Constants.FILE_SUFFIX_PEM);
            writer = new FileWriter(tmpFile);
            writer.write((String) authDetails.getAuthField().get(Constants.PRIVATE_KEY));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new SimplePrivateKeySupplier(tmpFile.getAbsolutePath());
    }

}
