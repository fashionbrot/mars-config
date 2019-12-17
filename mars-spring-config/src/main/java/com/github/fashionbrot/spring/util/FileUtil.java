package com.github.fashionbrot.spring.util;

import com.github.fashionbrot.ribbon.constants.GlobalConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/16 0:20
 */

@Slf4j
public class FileUtil {


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


            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            Charset charset = Charset.forName("UTF-8");
//            CharsetDecoder decoder = charset.newDecoder();
            String content =  charset.decode(byteBuffer.asReadOnlyBuffer()).toString();
//            byte[] str = content.getBytes("UTF-8");
//            return new String(str,"ISO-8859-1");
            return content;
        } catch (Exception e) {
            log.error("getFileContent error", e);
        } finally {
            close(fileLock,randomAccessFile,null);
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

            ByteBuffer sendBuffer = ByteBuffer.wrap(content.getBytes(Charset.forName("UTF-8")));
            while (sendBuffer.hasRemaining()) {
                fileChannel.write(sendBuffer);
            }
            fileChannel.truncate(content.length());
        } catch (Exception e) {
            log.error("writeFile error",e);
        } finally {
            close(fileLock,randomAccessFile,fileChannel);
        }
    }

    private static void close(FileLock fileLock,RandomAccessFile randomAccessFile,FileChannel fileChannel){
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
                fileChannel=null;
            } catch (IOException e) {
                log.error("fileChannel close error");
            }
        }
    }


}
