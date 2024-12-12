package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualys.ccf.contract.integration.request.ConnectorExecutionRequest;
import com.qualys.ccf.contract.integration.response.ConnectorExecutionResponse;
import com.qualys.ccf.contract.integration.response.Status;
import com.qualys.ccf.contract.interfaces.EncryptionHelper;
import com.qualys.ccf.contract.interfaces.ExecutionHelper;
import com.qualys.ccf.contract.model.ConnectorConfigLog;
import com.qualys.ccf.contract.model.DataTransformationEvent;
import com.qualys.ccf.contract.model.DeltaPublishEvent;
import com.qualys.ccf.contract.model.MetricPublishEvent;
import com.qualys.qint.service.connector.OCIConnector;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class OciConnectorIntegrationTest {

    private static final Logger log = LogManager.getLogger(OciConnectorIntegrationTest.class);
    @InjectMocks
    private OCIConnector ociConnector;

    @Test
    public void fetchData() {
        ConnectorExecutionRequest request = getExecuteConnectionRequest("/oci/ociRequest.json");
        setExecutionHelper(request);
        setEncryptionHelper(request);
        ConnectorExecutionResponse response = ociConnector.executeConnector(request);
        assertEquals(Status.SUCCESS, response.getStatus());
    }

    private ConnectorExecutionRequest getExecuteConnectionRequest(String fileName) {
        ConnectorExecutionRequest connectorExecutionRequest = null;
        try {
            String executeConnectorData = getFileContents(fileName);
            if (StringUtils.isNotEmpty(executeConnectorData)) {
                connectorExecutionRequest = new ObjectMapper().readValue(executeConnectorData, ConnectorExecutionRequest.class);
                log.info(connectorExecutionRequest);
            }
        } catch (Exception ex) {
            log.error("Exception: Unable to get test connection data. Reason: {}", ex.getMessage());
        }
        return connectorExecutionRequest;
    }

    private String getFileContents(String fileName) {
        byte[] bytes = null;
        try (InputStream resource = OciConnectorIntegrationTest.class.getResourceAsStream(fileName)) {
            bytes = resource.readAllBytes();
        } catch (Exception ex) {
            log.error("Exception: Unable to read contents of the file: {}. Reason: {}", fileName, ex.getMessage());
        }
        return bytes != null ? new String(bytes, StandardCharsets.UTF_8) : Strings.EMPTY;
    }

    private void setExecutionHelper(ConnectorExecutionRequest request) {
        request.setExecutionHelper(new ExecutionHelper() {
            @Override
            public void publishDataTransformationEvent(DataTransformationEvent dataTransformationEvent) {

            }

            @Override
            public void publishDelta(DeltaPublishEvent deltaPublishEvent) {

            }

            @Override
            public void publishMetrics(MetricPublishEvent metricPublishEvent) {

            }

            @Override
            public void publishConnectorConfigLog(ConnectorConfigLog connectorConfigLog, ConnectorConfigLog.LogType logType) {

            }
        });
    }

    private void setEncryptionHelper(ConnectorExecutionRequest request) {
        request.setEncryptionHelper(new EncryptionHelper() {
            @Override
            public String decrypt(String s) {
                return "b9:3c:fd:ea:03:3a:ba:12:f7:a0:07:d6:87:fb:86:39";
            }
        });
    }

}
