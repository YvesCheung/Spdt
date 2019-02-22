package com.unionyy.mobile.spdt.compiler.flavor;

import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FileUtils {

    public static void copyTo(Reader in, Writer out) throws IOException {
        int bufferSize = 8 * 1024;
        char[] buffer = new char[bufferSize];
        int chars = in.read(buffer);
        while (chars >= 0) {
            out.write(buffer, 0, chars);
            chars = in.read(buffer);
        }
    }

    public static <R extends Closeable> void use(
            @Nullable R resource,
            @Nullable Action<R> action
    ) throws Exception {
        try {
            if (action != null && resource != null) {
                action.run(resource);
            }
        } finally {
            try {
                if (resource != null) {
                    resource.close();
                }
            } catch (IOException ignore) {
                //Do Nothing
            }
        }
    }

    public interface Action<T> {
        void run(T param) throws Exception;
    }
}
