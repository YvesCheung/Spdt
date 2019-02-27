package com.unionyy.mobile.spdt.compiler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;

public class Logger {

    private final Messager mMessager;

    public Logger(Messager messager) {
        mMessager = messager;
    }

    public void info(String message) {
        info(message, false);
    }

    public void info(String message, boolean format) {
        if (message.length() == 0) {
            return;
        }
        mMessager.printMessage(Kind.NOTE, format ? message(message) : message);
    }

    public void error(String message) {
        if (message.length() == 0) {
            return;
        }
        mMessager.printMessage(Kind.ERROR, message(message));
    }

    public void warn(String message) {
        mMessager.printMessage(Kind.WARNING, message);
    }

    private String message(String message) {
        String tmpMessage = message.replace("\n", "\n:\t");
        return "\n==========================================================================================================\n" +
                ":Spdt log:\n" +
                ":\t" + tmpMessage + "\n" +
                ":\n" +
                "==========================================================================================================\n";
    }
}
