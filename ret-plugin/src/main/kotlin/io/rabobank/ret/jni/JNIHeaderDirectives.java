package io.rabobank.ret.jni;

import org.graalvm.nativeimage.c.CContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;

final class JNIHeaderDirectives implements CContext.Directives {
    @Override
    public List<String> getOptions() {
        File[] jnis = findJNIHeaders();
        return Arrays.asList("-I" + jnis[0].getParent(), "-I" + jnis[1].getParent());
    }

    @Override
    public List<String> getHeaderFiles() {
        File[] jnis = findJNIHeaders();
        return Arrays.asList("<" + jnis[0] + ">", "<" + jnis[1] + ">");
    }

    private static File[] findJNIHeaders() throws IllegalStateException {
        final File jreHome = new File(System.getProperty("java.home"));
        return new File[]{
            new File(jreHome.getAbsolutePath(), "include/jni.h"),
            new File(jreHome.getAbsolutePath(), "include/darwin/jni_md.h")
        };
    }
}
