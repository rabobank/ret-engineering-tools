package io.rabobank.ret;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.picocli.runtime.PicocliRunner;
import io.quarkus.runtime.Quarkus;
import io.rabobank.ret.jni.JClass;
import io.rabobank.ret.jni.JNIEnvironment;
import io.rabobank.ret.jni.JNINativeInterface;
import io.rabobank.ret.jni.JString;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public final class RetPlugin {
    private RetPlugin() {

    }

    @CEntryPoint(name = "Java_io_rabobank_ret_plugins_RetPlugin_invoke")
    public static void invoke(JNIEnvironment jniEnv, JClass clazz, @CEntryPoint.IsolateThreadContext long isolateId, JString dataCharStr) {
        JNINativeInterface fn = jniEnv.getFunctions();
        CCharPointer charptr = fn.getGetStringUTFChars().call(jniEnv, dataCharStr, (byte) 0);
        final String resultString = CTypeConversion.toJavaString(charptr);

        try {
            RetContext retContext = new ObjectMapper().readValue(resultString, RetContext.class);
            PluginConfiguration.RET_CONTEXT_INSTANCE = retContext;
            Quarkus.run(PicocliRunner.class, retContext.getCommand().split(" "));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @CEntryPoint(name = "Java_io_rabobank_ret_plugins_RetPlugin_initialize")
    public static void initialize(JNIEnvironment jniEnv, JClass clazz, @CEntryPoint.IsolateThreadContext long isolateId, JString dataCharStr) {
        JNINativeInterface fn = jniEnv.getFunctions();
        CCharPointer charptr = fn.getGetStringUTFChars().call(jniEnv, dataCharStr, (byte) 0);
        final String resultString = CTypeConversion.toJavaString(charptr);

        Quarkus.run(PicocliRunner.class, "initialize", resultString);
    }

    @CEntryPoint(name = "Java_io_rabobank_ret_plugins_RetPlugin_createIsolate", builtin = CEntryPoint.Builtin.CREATE_ISOLATE)
    public static native IsolateThread createIsolate();
}
