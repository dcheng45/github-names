package github.util.profile.rest.client.impl;

import github.util.profile.rest.client.BaseClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

public class BaseClientImpl implements BaseClient {

    private static final Logger LOG = LogManager.getLogger(BaseClientImpl.class);

    protected String baseUrl;
    protected Client client;

    public BaseClientImpl(String baseUrl) {
        this.baseUrl = baseUrl;
        client = ClientBuilder.newClient();
    }

    public void errorResponse(Response response) {
        LOG.error("Client Response Error: Status Code - " + response.getStatus() + "; Message: " + response.readEntity(String.class));
    }
}
