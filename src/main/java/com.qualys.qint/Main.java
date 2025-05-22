package com.qualys.qint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.core.BlockstorageClient;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.VirtualNetworkClient;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.requests.*;
import com.oracle.bmc.core.responses.*;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.resourcesearch.ResourceSearchClient;
import com.oracle.bmc.resourcesearch.model.FreeTextSearchDetails;
import com.oracle.bmc.resourcesearch.model.SearchDetails;
import com.oracle.bmc.resourcesearch.model.StructuredSearchDetails;
import com.oracle.bmc.resourcesearch.requests.SearchResourcesRequest;
import com.oracle.bmc.resourcesearch.responses.SearchResourcesResponse;
import com.oracle.bmc.vulnerabilityscanning.VulnerabilityScanningClient;
import com.oracle.bmc.vulnerabilityscanning.model.HostScanTargetSummary;
import com.oracle.bmc.vulnerabilityscanning.model.HostVulnerabilitySummary;
import com.oracle.bmc.vulnerabilityscanning.requests.*;
import com.oracle.bmc.vulnerabilityscanning.responses.ListHostAgentScanResultsResponse;
import com.oracle.bmc.vulnerabilityscanning.responses.ListHostScanTargetsResponse;
import com.oracle.bmc.vulnerabilityscanning.responses.ListHostVulnerabilitiesResponse;
import com.oracle.bmc.vulnerabilityscanning.responses.ListHostVulnerabilityImpactedHostsResponse;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.*;
import java.util.Base64;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author jyadav@qualys.com
 */
public class Main {

    private static final String FILE_SUFFIX_PEM = ".pem";
    private static final String fingerprint = "6a:0f:61:fd:b4:c9:59:fe:b3:d6:71:3e:51:34:6d:d9";
    private static final String compartmentId = "ocid1.tenancy.oc1..aaaaaaaax2gwhq3hszjqhte5pgzijgyge6gvlsrqar6kxn7itwhk7keokamq";
    private static final String tenantID = "ocid1.tenancy.oc1..aaaaaaaax2gwhq3hszjqhte5pgzijgyge6gvlsrqar6kxn7itwhk7keokamq";
    private static final String user = "ocid1.user.oc1..aaaaaaaasmuskwtpw5c5ucviudosyo5mdr7zajzvg2piwfizlwb752dr6qeq";
    private static final String OCI_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDGSSN4mYsIO9Av\n" +
            "Vy0I6mshJg2Du3Lpeh5C4BROQ2dBCs9573E4V3yj7CC9U2J9zNgD58InZfGAk+Cu\n" +
            "WDe41MjWZi1/2zxWL0pUETxwukveHfRTgbfhq4yc+ofDR5EK6T+PTSozOM3+FZsc\n" +
            "txy1JOdefi+pJyniTULf10j0Yl39W1hr0jhj/3KZn4do+YfKasW8ya0lkoIN4lCW\n" +
            "EoG7z+yefVzsesu5dvhrCJ83m5UcStT2JIEmXblAu2Dndo5xWGBstZxmXgUcaWDG\n" +
            "UfCaaJyUg4iD901KWXEqgeGxOZ9bwHQj/aH8TI4F4uJwSx/YPtmSrT84vhgh8GBn\n" +
            "BmNs0RcPAgMBAAECggEAALpoB5tYvVuq32Gh6AnPAZIoM+GjrGPRv0vKB0zhZjBp\n" +
            "+U1TIcfVzKxN+nlXP+sD/WUD0NfjqSXh31dc7QWAWfk0wDwdM/EnLrku0E3UKtP6\n" +
            "qmEsPFyVlEX/QEz/pVtHQSQhZuAV3DxWviZAqWb3SouKv+5FjcUxh6znYa5ddf62\n" +
            "4vWEuAgRIPpKhMJcDGu9AZxvp1ngVmNRsFJXH6rmVcnV0b+W0fcuB3TBtUxOQ8H/\n" +
            "YFpJjWpe2RpBbnCSWFngozMBhSASdLFR/6b22F7PsI45N/Hr+PUO3mcunRW9HjJ4\n" +
            "R+XqoIDmnTTLh+0xVVXB5LK7KTQZepKHAFoqOWls8QKBgQDyrcVd0Y0cGVvf9Haq\n" +
            "fmYn5c6IR/QWZmeedp6aFeiT5mPT14rVhT/1FIVGbd9qRujdTzltox6tFPwPAY+h\n" +
            "dy9F1k5ZZwWmufa6mhZIertTt1aHKqX1qaSmqjZ/Gv3fkkQjz31BUGKj/GigZGJZ\n" +
            "enZasn1Si69CudnbfMxpShzdMwKBgQDRK4lLzeNGf3aJ+OVI9yXYrnGkvMoCa5FO\n" +
            "hB6wU55NWZpOqnx53miomcE40YVRxMsh0/Ov1wHH8deSZkDcZQEMrXmkuCfiLjWj\n" +
            "AsmqP/QiiOFeETDPPyqxi5YuiRgTiK7dDsuyfDHEVrF++Wk1I+RDiExwpVW7Ka1P\n" +
            "gL35KuCGtQKBgDCq5rTc7HahI0qzhQTGGoiPzq6H94xydv9RfJccI6tdfdGjHUWa\n" +
            "1mu49M1eBXsTw2l2lObG2sUNO0trHbnc5A8WXloRjhGUo8ViV2urqVutnd23kNbG\n" +
            "ca223vz4lcc7VwzzdIQfsdh/KyBXYV3ecFPaDcX7G2e/S9B3o6rN4SH/AoGAKLc1\n" +
            "yYixRebH10GYuCRLvKrwOiX5mJEBvuve4WnVda8MZ2WuRzaw3qjXQM0gC3OoKtSM\n" +
            "Kd1LXnVIBhpbY8tRcftOo26pbNCb5jhDsx0x04sbe2kTPDRKhjbqq/JJunuvth4M\n" +
            "LpkYQuSxNkssXIi5BSBB6Od2EbGiJ8iish7Th8UCgYACcIhZCAzgWgZZd4gavABl\n" +
            "JsCL+4I2FaU++denfpdDwqnnR3JTIb/xRxvnqBp9Wk9g2c9fq+gGql3ixhYJPSdE\n" +
            "kQWyI+eFROsJR2ATEScBoWxbquALyMOHCXPq/K6GyJaVdjbWy370Rr5KmOIPzWiZ\n" +
            "yS/cCk2/liDzNvp6UrPsZw==\n" +
            "-----END PRIVATE KEY-----";

    private static String testJson = "{\"authDetails\":[{\"authType\":\"BASIC_AUTH\",\"url\":\"https://<domain>\",\"username\":\"<userid>\",\"password\":\"<password>\"},{\"authType\":\"BASIC_AUTH\",\"url\":\"https://<domain>\",\"username\":\"<userid>\",\"password\":\"<password>\"}]}";

    public static void main(String[] args) throws Exception {

        String testStr = "abcd";
        String subString = testStr.substring(0, 2);


        JsonNode jsonNode = new ObjectMapper().readTree(testJson).at("/authDetails");
        if (jsonNode.isArray()) {
            for (JsonNode node : jsonNode) {
                String s = node.at("/userdfhsdvf").textValue();
            }
        }



        System.out.println("Hello world!");

        String test = "Basic "+ Base64.getEncoder().encodeToString("connector-config-service:connector-config-service".getBytes());

        Map<String, String> vmMap = Map.of("API_GATEWAY_BASE_URL", "https://gateway.qg2.apps.qualys.com", "SECRET_KEY", "<secretKey>");
        Map<String, String> confMap = Map.of("API_GATEWAY_BASE_URL", "https://gateway.qg2.apps.qualys.com", "SECRET_KEY", "<secretKey>");

        File tempFile = null;
        tempFile = writeTempOCICertFile(OCI_KEY);
        Supplier<InputStream> privateKeySupplier = new SimplePrivateKeySupplier(tempFile.getAbsolutePath());
        AuthenticationDetailsProvider authProvider = SimpleAuthenticationDetailsProvider.builder()
                .tenantId(tenantID)
                .userId(user)
                .fingerprint(fingerprint)
                .privateKeySupplier(privateKeySupplier)
                .region(Region.valueOf("us-ashburn-1"))
                .build();






        // Load the OCI configuration
        /*ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse("/Users/jyadav/.oci/config");
        ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);*/


        //Create a instance client and fetch instances list and it's VNIC info
        try {
            /*ComputeClient client = ComputeClient.builder()
                    .region(Region.valueOf("us-ashburn-1"))
                    .build(authProvider);

            //1. Instance list fetch operation
            ListInstancesRequest listInstancesRequest = ListInstancesRequest.builder()
                    .compartmentId(compartmentId)
                    .limit(100)
                    .build();
            ListInstancesResponse listInstancesResponse = client.listInstances(listInstancesRequest);
            System.out.println("Successfully fetched Asset Data Details...");
            *//*ObjectMapper mapper1 = new ObjectMapper();
            FilterProvider filterProvider = new SimpleFilterProvider().addFilter("explicitlySetFilter",   SimpleBeanPropertyFilter.serializeAll());
            String jsonString = mapper1.writer(filterProvider).writeValueAsString(listInstancesResponse);
            System.out.println(jsonString);*//*

            org.json.JSONObject jsonObjectInstance = new JSONObject(new ObjectMapper()
                    .writer(new SimpleFilterProvider().addFilter("explicitlySetFilter", SimpleBeanPropertyFilter.serializeAll()))
                    .writeValueAsString(listInstancesResponse));
            System.out.println(jsonObjectInstance);


            //2. ListVnicAttachments operation
            ListVnicAttachmentsRequest listVnicAttachmentsRequest = ListVnicAttachmentsRequest.builder()
                            .compartmentId(compartmentId).build();

            ListVnicAttachmentsResponse listVnicAttachmentsResponse = client.listVnicAttachments(listVnicAttachmentsRequest);
            System.out.println("Successfully fetched VNIC Attachments Data Details...");
            org.json.JSONObject jsonObjectVnic = new JSONObject(new ObjectMapper()
                    .writer(new SimpleFilterProvider().addFilter("explicitlySetFilter", SimpleBeanPropertyFilter.serializeAll()))
                    .writeValueAsString(listVnicAttachmentsResponse));
            System.out.println(jsonObjectVnic);*/


            //3. Fetch Virtual Network Card Details
            VirtualNetworkClient virtualNetworkClient = VirtualNetworkClient.builder().build(authProvider);
            GetVnicRequest getVnicRequest = GetVnicRequest.builder()
                            .vnicId("ocid1.vnic.oc1.iad.abuwcljtweaxme7r2ripgfijqfzpzrm4mjbrqs33pntcc7rsbs5uecqtw4ga").build();

            GetVnicResponse getVnicResponse =  virtualNetworkClient.getVnic(getVnicRequest);
            System.out.println("Successfully fetched VNIC Data Details:");
            org.json.JSONObject jsonObjectVnicData = new JSONObject(new ObjectMapper()
                    .writer(new SimpleFilterProvider().addFilter("explicitlySetFilter", SimpleBeanPropertyFilter.serializeAll()))
                    .writeValueAsString(getVnicResponse));
            System.out.println(jsonObjectVnicData);


            /*
            //4. ListVolumeAttachments operation
            ListVolumeAttachmentsRequest listVolumeAttachmentsRequest = ListVolumeAttachmentsRequest.builder()
                            .compartmentId(compartmentId).build();
            ListVolumeAttachmentsResponse listVolumeAttachmentsResponse = client.listVolumeAttachments(listVolumeAttachmentsRequest);
            System.out.println("Successfully fetched Volumes Attachments Data Details...");
            org.json.JSONObject jsonObjectVolumesAttachments = new JSONObject(new ObjectMapper()
                    .writer(new SimpleFilterProvider().addFilter("explicitlySetFilter", SimpleBeanPropertyFilter.serializeAll()))
                    .writeValueAsString(listVolumeAttachmentsResponse));
            System.out.println(jsonObjectVolumesAttachments);

            //5. List Volumes
            BlockstorageClient blockstorageClient = BlockstorageClient.builder().build(authProvider);
            ListVolumesRequest request = ListVolumesRequest.builder()
                            .compartmentId(compartmentId).build();
            ListVolumesResponse listVolumesResponse = blockstorageClient.listVolumes(request);
            System.out.println("Successfully fetched list of Volumes Data Details...");
            org.json.JSONObject jsonObjectVolumesList = new JSONObject(new ObjectMapper()
                    .writer(new SimpleFilterProvider().addFilter("explicitlySetFilter", SimpleBeanPropertyFilter.serializeAll()))
                    .writeValueAsString(listVolumesResponse));
            System.out.println(jsonObjectVolumesList);*/

            ResourceSearchClient client = ResourceSearchClient.builder().build(authProvider);

            SearchDetails searchDetails = StructuredSearchDetails.builder()
                    .matchingContextType(SearchDetails.MatchingContextType.Highlights)
                    .query("query Instance resources return allAdditionalFields sorted by timeCreated desc")
                    .build();

            SearchResourcesRequest searchResourcesRequest = SearchResourcesRequest.builder()
                    .searchDetails(searchDetails)
                    .tenantId(tenantID)
                    .build();

            SearchResourcesResponse searchResourcesResponse = client.searchResources(searchResourcesRequest);
            System.out.println("Search operation completed successfully....");
            org.json.JSONObject jsonObjectSearchResult = new JSONObject(new ObjectMapper()
                    .writer(new SimpleFilterProvider().addFilter("explicitlySetFilter", SimpleBeanPropertyFilter.serializeAll()))
                    .writeValueAsString(searchResourcesResponse));
            System.out.println(jsonObjectSearchResult);

            client.close();

        } catch (BmcException e) {
            System.out.println("OCI Exception is occurred while fetching compute instances. Cause by: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception is occurred while fetching compute instances. Cause by: " + e.getMessage());
        }

        //

        // Create a VulnerabilityScanningClient
        /*try {
            VulnerabilityScanningClient vulnerabilityScanningClient = new VulnerabilityScanningClient(authProvider);

            ListHostAgentScanResultsRequest listHostAgentScanResultsRequest = ListHostAgentScanResultsRequest.builder()
                    .compartmentId(compartmentId)
                    .build();

            ListHostAgentScanResultsResponse listHostAgentScanResultsResponse = vulnerabilityScanningClient.listHostAgentScanResults(listHostAgentScanResultsRequest);


            // Fetch vulnerabilities
            ListHostVulnerabilitiesRequest listHostVulnerabilitiesRequest = ListHostVulnerabilitiesRequest.builder()
                    .compartmentId(compartmentId)
                    .build();

            ListHostVulnerabilitiesResponse response = vulnerabilityScanningClient.listHostVulnerabilities(listHostVulnerabilitiesRequest);
            System.out.println("ListHostVulnerabilitiesResponse: " + response);
        } catch (BmcException e) {
            System.out.println("Exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception in main: " + e.getMessage());
        }*/

        /*IdentityClient identityClient = new IdentityClient(provider);

        try {
            // Specify the OCID of the root compartment (typically the tenancy OCID)
            String rootCompartmentId = "your_tenancy_ocid"; // Replace with your tenancy OCID

            // Create the request to list compartments
            ListCompartmentsRequest request = ListCompartmentsRequest.builder()
                    .compartmentId("ocid1.tenancy.oc1..aaaaaaaax2gwhq3hszjqhte5pgzijgyge6gvlsrqar6kxn7itwhk7keokamq")  // Specify the root compartment
                    .compartmentIdInSubtree(true)      // To list compartments in the entire subtree
                    .accessLevel(ListCompartmentsRequest.AccessLevel.Accessible) // To list only accessible compartments
                    .lifecycleState(Compartment.LifecycleState.Active)  // Optionally filter by active compartments
                    .build();

            // Execute the request
            ListCompartmentsResponse response = identityClient.listCompartments(request);
            List<Compartment> compartments = response.getItems();

            // Print details of each compartment
            for (Compartment compartment : compartments) {
                System.out.println("Compartment ID: " + compartment.getId());
                System.out.println("Compartment Name: " + compartment.getName());
                System.out.println("Compartment Description: " + compartment.getDescription());
                System.out.println("Compartment State: " + compartment.getLifecycleState());
                System.out.println("===================================");
            }
        } finally {
            // Close the IdentityClient
            identityClient.close();
        }*/

    }


    private static File writeTempOCICertFile(String data) {
        File tmpFile = null;
        FileWriter writer = null;
        try {
            tmpFile = File.createTempFile("OCI_API_KEY_FILE", FILE_SUFFIX_PEM);
            writer = new FileWriter(tmpFile);
            writer.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return tmpFile;
    }
}
