package com.fn.reunion.app.controller;

public class BadConnectionException extends Exception {
    public BadConnectionException() {
        super("Connection error.");
    }
}
