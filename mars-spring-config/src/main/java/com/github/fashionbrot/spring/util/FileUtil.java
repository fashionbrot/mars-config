package com.github.fashionbrot.spring.util;

import com.github.fashionbrot.ribbon.constants.GlobalConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.1
 * @date 2019/12/16 0:20
 */

@Slf4j
public class FileUtil {

    private static final String USER_HOME;

    static {
        USER_HOME = System.getProperty("user.home");
    }

    public static String getUserHome(String appId) {
        return USER_HOME + File.separator + GlobalConstants.NAME + File.separator + appId + File.separator;
    }


    private static final int LOCK_COUNT = 10;

    public static List<File> searchFiles(File folder, final String keyword) {
        List<File> result = new ArrayList<File>();
        if (folder.isFile()) {
            result.add(folder);
        }
        File[] subFolders = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().contains(keyword)) {
                    return true;
                }
                return false;
            }
        });
        if (subFolders != null) {
            for (File file : subFolders) {
                if (file.isFile()) {
                    // 如果是文件则将文件添加到结果列表中
                    result.add(file);
                }
            }
        }
        return result;
    }


    /**
     * get file content
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileContent(File file) {
        RandomAccessFile randomAccessFile = null;
        FileLock fileLock = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            FileChannel fileChannel = randomAccessFile.getChannel();
            do {
                try {
                    fileLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
                } catch (Exception e) {
                    log.error("getFileContent error", e);
                }
            } while (null == fileLock);


            byte[] buf = new byte[1024];
            StringBuffer sb = new StringBuffer();
            while ((randomAccessFile.read(buf)) != -1) {
                sb.append(new String(buf, GlobalConstants.ENCODE_UTF8));
                buf = new byte[1024];
            }

            return sb.toString();
        } catch (Exception e) {
            log.error("getFileContent error", e);
        } finally {
            close(fileLock, randomAccessFile, null);
        }
        return "";
    }

    public static void deleteFile(File file){
        try {
            file.delete();
        }catch (Exception e){
            log.error("deleteFile error ",e);
        }
    }

    public static void writeFile(File file, String content) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
        } catch (Exception e) {
            log.error("writeFile error", e);
            return;
        }


        FileChannel fileChannel = null;
        FileLock fileLock = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            fileChannel = randomAccessFile.getChannel();
            int i = 0;
            do {
                try {
                    fileLock = fileChannel.tryLock();
                } catch (Exception e) {
                    ++i;
                    if (i > LOCK_COUNT) {
                        log.error("writeFile  get lock count error filePath:{}", file.getAbsolutePath(), e);
                    }
                }
            } while (null == fileLock);

            randomAccessFile.write(content.getBytes(GlobalConstants.ENCODE_UTF8));
        } catch (Exception e) {
            log.error("writeFile error", e);
        } finally {
            close(fileLock, randomAccessFile, fileChannel);
        }
    }


    private static void close(FileLock fileLock, RandomAccessFile randomAccessFile, FileChannel fileChannel) {
        if (fileLock != null) {
            try {
                fileLock.release();
                fileLock = null;
            } catch (IOException e) {
                log.error("fileLock release error");
            }
        }
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
                randomAccessFile = null;
            } catch (IOException e) {
                log.error("randomAccessFile close error");
            }
        }
        if (fileChannel != null) {
            try {
                fileChannel.close();
                fileChannel = null;
            } catch (IOException e) {
                log.error("fileChannel close error");
            }
        }
    }


}
