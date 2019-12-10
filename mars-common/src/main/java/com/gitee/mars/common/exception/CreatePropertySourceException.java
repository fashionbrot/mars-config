
package com.gitee.mars.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreatePropertySourceException extends RuntimeException {

    public CreatePropertySourceException(Throwable cause) {
        super(cause);
        log.error("PropertyCreateException cause:{}",cause);
    }
}
