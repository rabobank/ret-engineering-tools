package io.rabobank.ret.jni;

import org.apache.commons.lang3.NotImplementedException;
import org.graalvm.nativeimage.c.CContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
import static org.apache.commons.lang3.SystemUtils.JAVA_HOME;

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
        final File jreHome = new File(JAVA_HOME);
        return new File[]{
            new File(jreHome.getAbsolutePath(), "include/jni.h"),
            new File(jreHome.getAbsolutePath(), getJniMdDirectory())
        };
    }

    private static String getJniMdDirectory() {
        if(IS_OS_MAC) {
            return "include/darwin/jni_md.h";
        }

        if(IS_OS_LINUX) {
            return "include/linux/jni_md.h";
        }

        if(IS_OS_WINDOWS) {
            return "include/windows/jni_md.h";
        }

        throw new NotImplementedException("Unsupported operation system");
    }
}
