package com.gitee.mars.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class FileUtil {


    public static final String PROPERTIES_SUFFIX = ".properties";

    public static void findPath(String path, List<File> fileList, String suffix) {
        File f = new File(path);
        if (f != null) {
            File[] files = f.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    findPath(file.getPath(), fileList, suffix);
                } else {
                    if (file.getName().endsWith(suffix)) {
                        fileList.add(file);
                    }
                }
            }
        }
    }

    public static boolean searchFile(String path, String fileName) {
        File f = new File(path);
        if (f != null) {
            File[] files = f.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    searchFile(file.getPath(), fileName);
                } else {
                    if (file.getName().endsWith(PROPERTIES_SUFFIX)
                            && (file.getName().equals(fileName)
                            || file.getName().equals(fileName + PROPERTIES_SUFFIX))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void putProperties(File file, Properties properties) {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            properties.load(in);
        } catch (Exception e) {
            log.error("putProperties error ", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Properties getResources(java.net.URL url, String fileName) {
        File f = new File(url.getPath());
        List<File> fileList = new ArrayList<File>();
        FileUtil.findPath(f.getPath(), fileList, PROPERTIES_SUFFIX);
        Properties all = new Properties();
        if (!CollectionUtils.isEmpty(fileList)) {
            for (File properties : fileList) {
                if (StringUtils.isEmpty(fileName)) {
                    all.putAll(toProperties(properties));
                } else {
                    String fileStr = properties.getName();
                    String fileNameTemp = fileName + PROPERTIES_SUFFIX;
                    if (fileStr.equalsIgnoreCase(fileName) || fileStr.equalsIgnoreCase(fileNameTemp)) {
                        all.putAll(toProperties(properties));
                    }
                }
            }
        }
        return all;
    }

    public static Properties toProperties(File file) {
        Properties pp = new Properties();
        FileUtil.putProperties(file, pp);
        return pp;
    }

    public static String suffix(String fileName){
        if (StringUtils.isNoneEmpty(fileName)){

        }
        return null;
    }

    public static void main(String[] args) {
       /* String p="E:\\dev\\ideaProject\\config-test\\spring-mvc-config\\src\\main\\resources";
        boolean b=searchFile(p,"application");
        System.out.println(b);*/

    }

    /*public static ConfigTypeEnum match(String propertiesName){
        if (StringUtils.isNotEmpty(propertiesName)){
            if (propertiesName.endsWith(".txt")){
                return ConfigTypeEnum.TEXT;
            }
            if (propertiesName.endsWith(".properties")){
                return ConfigTypeEnum.PROPERTIES;
            }
            if (propertiesName.endsWith(".json")){
                return ConfigTypeEnum.JSON;
            }
            if (propertiesName.endsWith(".yaml")){
                return ConfigTypeEnum.YAML;
            }
            if (propertiesName.endsWith(".html")){
                return ConfigTypeEnum.HTML;
            }
        }
        return ConfigTypeEnum.PROPERTIES;
    }*/

    public static boolean  isMatch(String propertiesName){
        if (StringUtils.isNotEmpty(propertiesName)){
            if (propertiesName.endsWith(".txt")){
                return false;
            }
            if (propertiesName.endsWith(".properties")){
                return false;
            }
            if (propertiesName.endsWith(".yaml")){
                return false;
            }
        }
        return true;
    }

}