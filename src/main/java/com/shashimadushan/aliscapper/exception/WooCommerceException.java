package com.shashimadushan.aliscapper.exception;

public class WooCommerceException extends Exception {
    public WooCommerceException(String message) {
        super(message);
    }

    public WooCommerceException(String message, Throwable cause) {
        super(message, cause);
    }
}