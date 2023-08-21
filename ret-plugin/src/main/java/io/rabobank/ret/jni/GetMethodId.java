package io.rabobank.ret.jni;

import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.type.CCharPointer;

interface GetMethodId extends CFunctionPointer {
    @InvokeCFunctionPointer
    JMethodID find(JNIEnvironment env, JClass clazz, CCharPointer name, CCharPointer sig);
}
