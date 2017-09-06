package github.util.profile.rest.client.impl;

import github.util.profile.rest.client.GithubClient;
import github.util.profile.rest.model.GitHubOrgMember;
import github.util.profile.rest.model.GitHubOrganization;
import github.util.profile.rest.model.GitHubUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

public class GithubClientImpl extends BaseClientImpl implements GithubClient {

    private static final Logger LOG = LogManager.getLogger(GithubClientImpl.class);

    private String username;

    public GithubClientImpl(String baseUrl) {
        super(baseUrl);
    }

    public GithubClientImpl(String baseUrl, String username, String password) {
        super(baseUrl);
        this.username = username;
        HttpAuthenticationFeature authenticationFeature = HttpAuthenticationFeature.basic(username, password);
        client.register(authenticationFeature);
    }

    public List<GitHubOrganization> getUserOrganizations() {
        final String url = baseUrl  + "/user/orgs";
        final Response response = client.target(url).request().get();
        List<GitHubOrganization> orgs = null;
        if (response.getStatus() == 200) {
            orgs = response.readEntity(new GenericType<List<GitHubOrganization>>(){});
        } else {
            errorResponse(response);
        }
        return orgs;
    }

    public List<GitHubOrgMember> getOrganizationMembers(String organization) {
        final String url = baseUrl + "/orgs/" + organization + "/members";
        final Response response = client.target(url).request().get();
        List<GitHubOrgMember> members = null;
        if (response.getStatus() == 200) {
            members = response.readEntity(new GenericType<List<GitHubOrgMember>>(){});
        } else {
            errorResponse(response);
        }
        return members;
    }

    public GitHubUser getUser(String username) {
        final String url = baseUrl + "/users/" + username;
        final Response response = client.target(url).request().get();
        GitHubUser user = null;
        if (response.getStatus() == 200) {
            user = response.readEntity(GitHubUser.class);
        } else {
            errorResponse(response);
        }
        return user;
    }
}
