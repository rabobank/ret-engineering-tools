package io.rabobank.ret.jni;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.word.PointerBase;

@CContext(JNIHeaderDirectives.class)
@CStruct(value = "JNINativeInterface_", addStructKeyword = true)
public
interface JNINativeInterface extends PointerBase {
    @CField
    NewStringUTF getNewStringUTF();

    @CField
    GetStringUTFChars getGetStringUTFChars();

    @CField
    NewLocalRef NewLocalRef();

    interface NewStringUTF extends CFunctionPointer {
        @InvokeCFunctionPointer
        JString call(JNIEnvironment env, CCharPointer cCharPointer);
    }

    interface GetStringUTFChars extends CFunctionPointer {
        @InvokeCFunctionPointer
        CCharPointer call(JNIEnvironment env, JString str, byte isCopy);
    }

    interface NewLocalRef extends CFunctionPointer {
        @InvokeCFunctionPointer
        JObject call(JNIEnvironment env, JObject jObject);
    }
}
