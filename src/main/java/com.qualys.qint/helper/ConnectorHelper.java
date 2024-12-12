package com.qualys.qint.helper;

import com.qualys.ccf.contract.integration.request.ConnectorExecutionRequest;
import com.qualys.ccf.contract.model.AuthDetails;
import com.qualys.ccf.contract.model.ProfileDetail;
import com.qualys.qint.constants.Constants;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ConnectorHelper {

    /**
     * This method returns the Request entity type
     *
     * @param request
     * @return
     */
    public String getEntityType(ConnectorExecutionRequest request) {
        String entityType = Strings.EMPTY;
        Optional<ProfileDetail> profileModel = request.getConnectorConfigDetails().getProfileDetails()
                .stream().filter(profiles -> profiles.getUuid().equals(request.getProfileUuid())).findFirst();

        if (profileModel.isPresent()) {
            ProfileDetail profileDetail = profileModel.get();
            Map<String, Object> profileFields = profileDetail.getProfileFields();
            if (!MapUtils.isEmpty(profileFields) && profileFields.containsKey(Constants.ENTITY_TYPE)) {
                if (Objects.nonNull(profileFields.get(Constants.ENTITY_TYPE))) {
                    if (Constants.ENTITY_TYPE_HOST_AND_VULNERABILITY.equalsIgnoreCase(profileFields.get(Constants.ENTITY_TYPE).toString())) {
                        entityType = Constants.ENTITY_TYPE_HOST_AND_VULNERABILITY;
                    }
                }
            }
        }
        return entityType;
    }

    /**
     * This method decrypt the encrypted secret in AuthDetails and updates it in the Connector Execution Request
     *
     * @param request
     */
    public void updateAuthDetails(ConnectorExecutionRequest request) {
        AuthDetails authDetails = request.getConnectorConfigDetails().getAuthDetails().get(0);
        /*if (authDetails != null && !MapUtils.isEmpty(authDetails.getAuthField()) && authDetails.getAuthField().containsKey(Constants.FINGERPRINT)) {
            request.getConnectorConfigDetails().getAuthDetails().get(0).getAuthField().
                    put(Constants.FINGERPRINT, request.getEncryptionHelper().decrypt(authDetails.getAuthField().get(Constants.FINGERPRINT).toString()));
        }*/
    }


}
