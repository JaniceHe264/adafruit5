
package com.mz.jarboot.exception;

import com.mz.jarboot.api.exception.JarbootRunException;
import com.mz.jarboot.common.JarbootException;
import com.mz.jarboot.common.pojo.ResponseSimple;
import com.mz.jarboot.api.constant.CommonConst;
import com.mz.jarboot.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author majianzheng
 */
@ControllerAdvice
public class JarbootExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JarbootExceptionHandler.class);
    
    @ExceptionHandler(AccessException.class)
    public ResponseEntity<String> handleAccessException(AccessException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getAllExceptionMsg(e));
    }

    @ExceptionHandler(JarbootException.class)
    public ResponseEntity<ResponseSimple> handleJarbootException(JarbootException e) {
        LOGGER.error(e.getMessage(), e);
        ResponseSimple resp = new ResponseSimple();
        resp.setResultCode(e.getErrorCode());
        resp.setResultMsg(e.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @ExceptionHandler(JarbootRunException.class)
    public ResponseEntity<ResponseSimple> handleJarbootRunException(JarbootRunException e) {
        LOGGER.error(e.getMessage(), e);
        ResponseSimple resp = new ResponseSimple();
        resp.setResultCode(-1);
        resp.setResultMsg(e.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        LOGGER.error(CommonConst.JARBOOT_NAME, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getAllExceptionMsg(e));
    }

    private static String getAllExceptionMsg(Throwable e) {
        Throwable cause = e;
        StringBuilder strBuilder = new StringBuilder();

        while (cause != null && !StringUtils.isEmpty(cause.getMessage())) {
            strBuilder.append("caused: ").append(cause.getMessage()).append(";");
            cause = cause.getCause();
        }

        return strBuilder.toString();
    }
}
