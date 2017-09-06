package github.util.profile.util;

import github.util.profile.exception.PropertyNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    private static final Logger LOG = LogManager.getLogger(PropertiesUtil.class);

    public static Properties loadProperties(String propFilePath) {
        Properties props = new Properties();
        try {
            InputStream resourceStream = new FileInputStream(propFilePath);
            props.load(resourceStream);
        } catch (FileNotFoundException e) {
            LOG.error("FileNotFoundException: " + propFilePath, e);
        } catch (IOException e) {
            LOG.error("IOException: " + propFilePath, e);
        }
        return props;
    }

    public static String getProperty(Properties props, String property) throws PropertyNotFoundException {
        if (props.getProperty(property) != null) {
            return props.getProperty(property);
        } else {
            throw new PropertyNotFoundException("Property not found - " + property);
        }
    }
}
