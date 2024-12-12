package com.qualys.qint.helper;

import com.oracle.bmc.model.BmcException;
import com.qualys.ccf.contract.integration.response.ConnectorBaseResponse;
import com.qualys.ccf.contract.integration.response.ConnectorExecutionResponse;
import com.qualys.ccf.contract.integration.response.Status;
import com.qualys.qint.constants.Constants;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;


/**
 * @author jyadav@qualys.com
 */

public class ResponseHelper {

    private static final Logger log = LogManager.getLogger(ResponseHelper.class);

    /**
     * This method updates Connector Base Response in case of BmcException from OCI
     *
     * @param e
     * @param response
     */
    public void getResponseForBmcException(BmcException e, ConnectorBaseResponse response, String message) {
        log.error(message, e);
        response.setStatus(Status.REQUEST_ERROR);
        response.setMessage(e.getMessage());
        response.setCode(e.getServiceCode());
        response.setException(e);
    }

    /**
     * This method updates Connector Base Response in case of Exception other than Microsoft Graph API execution
     *
     * @param e
     * @param response
     */
    public void getResponseForException(Exception e, ConnectorBaseResponse response, String message) {
        log.error(message, e);
        response.setStatus(Status.REQUEST_ERROR);
        response.setMessage(e.getMessage());
        response.setCode(HttpStatusReasonPhrase.getReasonPhrase(HttpStatus.SC_INTERNAL_SERVER_ERROR));
        response.setException(e);
    }

    /**
     * This method updates Connector Base Response in case of other failures
     */
    public ConnectorExecutionResponse getResponseForOtherFailures(String message) {
        ConnectorExecutionResponse response = new ConnectorExecutionResponse();
        response.setStatus(Status.REQUEST_ERROR);
        response.setMessage(message);
        response.setCode(HttpStatusReasonPhrase.getReasonPhrase(HttpStatus.SC_BAD_REQUEST));
        return response;
    }


}
