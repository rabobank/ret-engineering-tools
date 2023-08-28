package io.rabobank.ret;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.quarkus.picocli.runtime.PicocliRunner;
import io.quarkus.runtime.Quarkus;
import io.rabobank.ret.jni.JClass;
import io.rabobank.ret.jni.JNIEnvironment;
import io.rabobank.ret.jni.JString;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CTypeConversion;

@SuppressWarnings("unused")
public final class RetPlugin {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private RetPlugin() {

    }

    @CEntryPoint(name = "Java_io_rabobank_ret_plugins_RetPlugin_invoke")
    public static void invoke(final JNIEnvironment jniEnv,
                              final JClass clazz,
                              @CEntryPoint.IsolateThreadContext final long isolateId,
                              final JString dataCharStr) {
        final var fn = jniEnv.getFunctions();
        final var charptr = fn.getGetStringUTFChars().call(jniEnv, dataCharStr, (byte) 0);
        final var resultString = CTypeConversion.toJavaString(charptr);

        try {
            var retContext = OBJECT_MAPPER.readValue(resultString, RetContext.class);
            RetContextConfiguration.RET_CONTEXT_INSTANCE = retContext;
            Quarkus.run(PicocliRunner.class, retContext.getCommand().toArray(new String[0]));
        } catch (JsonProcessingException e) {
            Log.error(e.getMessage(), e);
        }
    }

    @CEntryPoint(name = "Java_io_rabobank_ret_plugins_RetPlugin_initialize")
    public static void initialize(final JNIEnvironment jniEnv,
                                  final JClass clazz,
                                  @CEntryPoint.IsolateThreadContext final long isolateId,
                                  final JString dataCharStr) {
        final var fn = jniEnv.getFunctions();
        final var charptr = fn.getGetStringUTFChars().call(jniEnv, dataCharStr, (byte) 0);
        final var args = CTypeConversion.toJavaString(charptr);

        Quarkus.run(PicocliRunner.class, "initialize", args);
    }

    @CEntryPoint(name = "Java_io_rabobank_ret_plugins_RetPlugin_createIsolate", builtin = CEntryPoint.Builtin.CREATE_ISOLATE)
    public static native IsolateThread createIsolate();
}
