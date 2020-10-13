package com.ftp.transform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Alexey Druzik on 13.10.2020
 */
public class FileToStringTransfoemer {

    public static String transform(String path) {
        String line = null;
        try {
        BufferedReader br = new BufferedReader(new FileReader(path));
            for (String readedLine = br.readLine(); readedLine != null; readedLine = br.readLine()) {
                String modifiedReadedLine = readedLine;
                if (readedLine.charAt(0) == '\uFEFF') {
                    modifiedReadedLine = readedLine.substring(1);
                }
                if (line != null) {
                    line = line + modifiedReadedLine;
                } else {
                    line = modifiedReadedLine;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

}
