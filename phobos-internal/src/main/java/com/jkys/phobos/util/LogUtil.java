package com.jkys.phobos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lo on 2/28/17.
 */
public class LogUtil {
    private static Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public static void info(String message, Object ...objs) {
        logger.info(message, objs);
    }

    public static void error(String message, Throwable t) {
        logger.error(message, t);
    }
}
