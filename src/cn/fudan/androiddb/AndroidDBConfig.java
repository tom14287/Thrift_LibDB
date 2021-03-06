//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.fudan.androiddb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AndroidDBConfig {
    public static final String CONFIG_PROPERTIES = "androiddb.properties/";
    private static Properties pps = null;
    public static final String PROP_KEY_SERVER_BIND_IP = "RPCServerBindIP";
    public static final String PROP_KEY_SERVER_BIND_PORT = "RPCServerBindPort";
    public static final String PROP_KEY_CLIENT_BIND_IP = "RPCClientBindIP";
    public static final String PROP_KEY_LIBDATABASE_USERNAME = "LibDBUsername";
    public static final String PROP_KEY_LIBDATABASE_PASSWORD = "LibDBPassword";
    public static final String PROP_KEY_LIBDATABASE_URL = "LibDBUrl";
    public static final String PROP_KEY_APKDATABASE_USERNAME = "ApkDBUsername";
    public static final String PROP_KEY_APKDATABASE_PASSWORD = "ApkDBPassword";
    public static final String PROP_KEY_APKDATABASE_URL = "ApkDBUrl";
    public static final String PROP_KEY_DEX_ROOT_DIR = "LibDBDexRootDir";
    public static final String PROP_KEY_JAR_ROOT_DIR = "LibDBJarRootDir";
    public static final String PROP_KEY_SOURCE_ROOT_DIR = "LibDBSourceRootDir";
    public static final String PROP_KEY_AAR_ROOT_DIR = "LibDBAarRootDir";
    public static final String PROP_KEY_APKLIB_ROOT_DIR = "LibDBApklibRootDir";
    public static final String PROP_KEY_APK_ROOT_DIR = "ApkDBApkRootDir";
    public static final String PROP_KEY_APK_SOURCE_ROOT_DIR = "ApkDBApkSourceRootDir";

    public AndroidDBConfig() {
    }

    private static synchronized void init() {
        try {
            if (pps == null) {
                File configFile = new File(CONFIG_PROPERTIES);
                if (!configFile.exists()) {
                    configFile.createNewFile();
                }

                pps = new Properties();
                pps.load(new FileInputStream(CONFIG_PROPERTIES));
            }
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public static String getConfigByDefault(String propKey, String defaultVal) {
        if (pps == null) {
            init();
        }

        return pps.getProperty(propKey, defaultVal);
    }

    public static String getConfig(String propKey){
        if(pps == null){
            init();
        }

        return pps.getProperty(propKey);
    }

}
