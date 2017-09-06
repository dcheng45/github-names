package github.util.profile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import github.util.profile.exception.PropertyNotFoundException;
import github.util.profile.rest.client.GithubClient;
import github.util.profile.rest.client.impl.GithubClientImpl;
import github.util.profile.rest.model.GitHubOrgMember;
import github.util.profile.rest.model.GitHubUser;
import github.util.profile.util.FileUtil;
import github.util.profile.util.MailerUtil;
import github.util.profile.util.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Hello world!
 *
 */
public class ProfileNameCheck {
    private static final Logger LOG = LogManager.getLogger(ProfileNameCheck.class);

    private static String jarPath;
    private static String jarFileLocation;
    private static String logDirLocation;

    static {
        jarPath = ProfileNameCheck.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(jarPath);
        jarFileLocation = jarFile.getParentFile().getAbsolutePath();
        logDirLocation = jarFileLocation + "/logs";
        System.setProperty("log.location", logDirLocation);
    }

    public static void main( String[] args ) {
        if (args.length == 2) {
            String propFileName = args[0];
            String awsOrganization = args[1];
            if (propFileName.contains(".properties")) {
                Properties props = PropertiesUtil.loadProperties(propFileName);

                /*
                 * Load values from properties file
                 * All properties are required so throw exception if not found
                 */
                try {
                    String filename = PropertiesUtil.getProperty(props, "file.basename");
                    String txtFilename = FileUtil.getTxtFileName(filename + "-" + awsOrganization);
                    String fileDirLocation = PropertiesUtil.getProperty(props, "file.directory");
                    String absFileName = fileDirLocation + "/" + txtFilename;

                    String githubUrl = PropertiesUtil.getProperty(props,"github.url");
                    String githubUsername = PropertiesUtil.getProperty(props,"github.username");
                    String githubPassword = PropertiesUtil.getProperty(props,"github.password");

                    String awsBucket = PropertiesUtil.getProperty(props,"aws.bucket");
                    String awsBucketDir = PropertiesUtil.getProperty(props,"aws.bucket.directory");
                    String awsRegion = PropertiesUtil.getProperty(props,"aws.region");

                    String emailUsername = PropertiesUtil.getProperty(props,"email.username");
                    String emailPassword = PropertiesUtil.getProperty(props,"email.password");
                    String emailHost = PropertiesUtil.getProperty(props,"email.smtp.host");
                    String emailPort = PropertiesUtil.getProperty(props,"email.smtp.port");
                    String emailStartTls = PropertiesUtil.getProperty(props,"email.smtp.starttls.enable");
                    String emailAuth = PropertiesUtil.getProperty(props,"email.smtp.auth");

                    String emailSubject = PropertiesUtil.getProperty(props,"email.subject");
                    String emailBody = PropertiesUtil.getProperty(props,"email.body");
                    String emailMissingSubject = PropertiesUtil.getProperty(props,"email.missing.subject");
                    String emailMissingBody = PropertiesUtil.getProperty(props,"email.missing.body");

                    File fileDir = new File(fileDirLocation);
                    File txtFile = new File(absFileName);

                    Set<String> noNameMembers = new HashSet<>();

                    GithubClient githubClient = new GithubClientImpl(githubUrl, githubUsername, githubPassword);

                    MailerUtil mailer = new MailerUtil(emailUsername, emailPassword, emailHost, emailPort, emailAuth, emailStartTls);

                    /*
                     * Get organization members
                     */
                    List<GitHubOrgMember> orgMembers = githubClient.getOrganizationMembers(awsOrganization);
                    for (GitHubOrgMember orgMember : orgMembers) {
                        /*
                         * Get user info of member
                         */
                        GitHubUser user = githubClient.getUser(orgMember.getMemberName());
                        //Check if name in profile is empty
                        if (isNull(user.getName())) {
                            //If empty add to set
                            noNameMembers.add(orgMember.getMemberName());
                            LOG.info("User - " + orgMember.getMemberName() + " is missing name value in GitHub profile");
                            //Check if user public email is empty
                            if (!isNull(user.getEmail())) {
                                LOG.info("Email sent to: " + user.getEmail());
                                mailer.sendMail(emailUsername, user.getEmail(), emailSubject, emailBody);
                                //Send email to user at their public email
                            } else {
                                //If empty then notify administator
                                LOG.info("User - " + orgMember.getMemberName() + " does not have public email. Notify site admin");
                            }
                        }
                    }

                    /*
                     * Check if set of members with no name is empty.
                     * Create txt file with the list of members with no name.
                     */
                    if (!noNameMembers.isEmpty()) {
                        Boolean madeDir = FileUtil.createDir(fileDir);
                        if (madeDir) {
                            //if files directory is made/exists write text file
                            FileUtil.writeListToFile(txtFile, noNameMembers);

                            //Connect to Amazon S3
                            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(awsRegion).build();
                            try {
                                //Check if bucket exists in S3 and create if it doesn't
                                if (!s3.doesBucketExist(awsBucket)) {
                                    s3.createBucket(awsBucket);
                                }
                                //Put text file in S3 bucket
                                s3.putObject(new PutObjectRequest(awsBucket, awsBucketDir + "/" + txtFilename, txtFile));
                            } catch (AmazonServiceException e) {
                                LOG.error("AmazonServiceException: adding " + txtFilename + " to " + awsBucket, e);
                            }
                        } else {
                            LOG.error("The file directory was not created: " + fileDir);
                        }
                    }
                } catch (PropertyNotFoundException e) {
                    LOG.error("PropertyNotFoundException", e);
                }
            } else {
                LOG.error("Properties file specified is not the correct file type");
            }
        } else {
            LOG.error("Program requires two arguments - properties file and AWS organization name");
        }
    }

    public static Boolean isNull(String value) {
        if (value != null && !value.equals("null") && !value.equals("")) {
            return false;
        } else {
            return true;
        }
    }
}
