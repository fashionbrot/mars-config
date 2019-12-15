package com.github.fashionbrot.spring.util;

import com.github.fashionbrot.ribbon.constants.GlobalConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/16 0:20
 */

@Slf4j
public class FileUtil {

    private static final Charset FILE_CHARSET = Charset.forName("UTF-8");

    private static final int LOCK_COUNT = 10;

    public static List<File> searchFiles(File folder, final String keyword) {
        List<File> result = new ArrayList<File>();
        if (folder.isFile()) {
            result.add(folder);
        }
        File[] subFolders = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                if (file.getName().toLowerCase().contains(keyword)) {
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
                } else {
                    // 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                    //result.addAll(searchFiles(file, keyword));
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

            int fileSize = (int) fileChannel.size();
            ByteBuffer byteBuffer = ByteBuffer.allocate(fileSize);
            fileChannel.read(byteBuffer);
            byteBuffer.flip();

            Charset charset = Charset.forName("UTF-8");
            return charset.decode(byteBuffer).toString();
        } catch (Exception e) {
            log.error("getFileContent error", e);
        } finally {
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }


    public static void writeFile(File file, String content) {

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
                        log.error("writeFile  get lock count error filePath:{}",file.getAbsolutePath(),e);
                    }
                }
            } while (null == fileLock);

            ByteBuffer sendBuffer = ByteBuffer.wrap(content.getBytes(GlobalConstants.ENCODE));
            while (sendBuffer.hasRemaining()) {
                fileChannel.write(sendBuffer);
            }
            fileChannel.truncate(content.length());
        } catch (Exception e) {
            log.error("writeFile error",e);
        } finally {
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }



}
