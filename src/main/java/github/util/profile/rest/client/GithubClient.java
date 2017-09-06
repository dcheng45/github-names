package github.util.profile.rest.client;

import github.util.profile.rest.model.GitHubOrgMember;
import github.util.profile.rest.model.GitHubOrganization;
import github.util.profile.rest.model.GitHubUser;

import java.util.List;

public interface GithubClient {

    List<GitHubOrganization> getUserOrganizations();

    List<GitHubOrgMember> getOrganizationMembers(String organization);

    GitHubUser getUser(String user);

}
