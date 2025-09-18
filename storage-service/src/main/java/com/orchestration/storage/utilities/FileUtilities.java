package com.orchestration.storage.utilities;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HexFormat;

public class FileUtilities {


    public static String sha256Generator(File tmp) throws Exception {
        // create hash engine
        MessageDigest hash=MessageDigest.getInstance("SHA-256");

        try(InputStream is=new BufferedInputStream(new FileInputStream(tmp))) {
            // we need to read the tmp file 8kb everytime
            byte[] buf = new byte[8192];
            int r;
            while((r=is.read(buf)) !=-1){
                hash.update(buf,0,r);
            }

        }

        return HexFormat.of().formatHex(hash.digest());
    }

    public static String getContentType(File tmp, String contentType) throws IOException {
        // get the contentType from the server using propContentType
        String actualContentType= Files.probeContentType(tmp.toPath());
        // trust the server if it returns a value
        if(actualContentType != null && !actualContentType.isBlank())
            return actualContentType;
        if(actualContentType == null || actualContentType.isBlank())
            return contentType;

        return "application/octet-stream";
    }

}
