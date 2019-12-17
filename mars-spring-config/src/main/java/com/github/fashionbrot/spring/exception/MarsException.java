package com.github.fashionbrot.spring.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author guoran.sun
 * @date 2019/12/17 13:51
 */
@Getter
@Setter
public class MarsException extends RuntimeException {

    private String message ;

    public MarsException(String message) {
        super(message);
        this.message = message;
    }
}
