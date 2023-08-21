package io.rabobank.ret.jni;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.word.PointerBase;

@CContext(JNIHeaderDirectives.class)
@CStruct(value = "JNIEnv_", addStructKeyword = true)
public interface JNIEnvironment extends PointerBase {
    @CField("functions")
    JNINativeInterface getFunctions();
}
