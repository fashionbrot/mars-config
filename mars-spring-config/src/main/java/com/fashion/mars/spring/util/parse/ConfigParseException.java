
package com.fashion.mars.spring.util.parse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConfigParseException extends RuntimeException {

    ConfigParseException(Throwable cause) {
        super(cause);
        log.error("ConfigParseException cause:{}",cause);
    }
}
