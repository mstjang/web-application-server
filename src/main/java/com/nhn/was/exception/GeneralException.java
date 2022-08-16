package com.nhn.was.exception;

import com.nhn.was.constant.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class GeneralException extends RuntimeException{
    private static final Logger log = LoggerFactory.getLogger(GeneralException.class);
    private final HttpStatusCode httpStatusCode;

    public GeneralException(String message, IOException e, HttpStatusCode httpStatusCode) {
        super(httpStatusCode.getMessage());
        logWrite(Arrays.toString(e.getStackTrace()));
        this.httpStatusCode = httpStatusCode;
        log.error(Arrays.toString(e.getStackTrace()));
    }

    public GeneralException(String message, Exception e, HttpStatusCode httpStatusCode) {
        super(httpStatusCode.getMessage());
        logWrite(Arrays.toString(e.getStackTrace()));
        this.httpStatusCode = httpStatusCode;
        log.error(Arrays.toString(e.getStackTrace()));
    }

    public GeneralException(HttpStatusCode httpStatusCode) {
        super(httpStatusCode.getMessage());
        logWrite(httpStatusCode.getMessage());
        this.httpStatusCode = httpStatusCode;
        log.error(httpStatusCode.getMessage());
    }

    public GeneralException(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode.getMessage(message));
        logWrite(httpStatusCode.getMessage() + "\n" + message);
        this.httpStatusCode = httpStatusCode;
        log.error(httpStatusCode.getMessage() + "\n" + message);
    }

    public static synchronized void logWrite(String log)
    {
        String LogTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); //for log-time
        String logFileName = "Log_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
        log = "[" + LogTime + "]:" + log;

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFileName, true));
            bw.write(log);
            bw.newLine();
            bw.close();
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }
}
