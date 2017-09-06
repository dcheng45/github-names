package github.util.profile.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class FileUtil {

    private static final Logger LOG = LogManager.getLogger(FileUtil.class);

    public static String getTxtFileName(String filename) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date curr = new Date();
        String currDate = df.format(curr);
        return filename + "-" + currDate + ".txt";
    }

    public static void writeListToFile(File file, Collection<String> list) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            Iterator<String> iter = list.iterator();
            while(iter.hasNext()) {
                String line = iter.next();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            LOG.error("IOException while writing to file: " + file.getName(), e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                LOG.error("IOException while close file: " + file.getName(), ex);
            }
        }
    }

    public static Boolean createDir(File dir) {
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdir();
        }
    }
}
