package io.rabobank.ret.plugins;

public final class RetPlugin {

    private RetPlugin() {

    }

    public static native long createIsolate();

    public static native void invoke(long isolateId, String retContext);

    public static native void initialize(long isolateId, String plugin);
}
