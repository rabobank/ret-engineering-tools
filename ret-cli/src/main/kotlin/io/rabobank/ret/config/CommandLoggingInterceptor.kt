package io.rabobank.ret.config

import io.quarkus.logging.Log
import jakarta.interceptor.AroundInvoke
import jakarta.interceptor.Interceptor
import jakarta.interceptor.InterceptorBinding
import jakarta.interceptor.InvocationContext
import java.lang.annotation.Inherited

@InterceptorBinding
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Inherited
annotation class Logged

@Interceptor
@Logged
class CommandLoggingInterceptor {

    @AroundInvoke
    fun intercept(context: InvocationContext): Any? {
        val targetClass = context.method.declaringClass.name
        val targetMethod = context.method.name
        val args = context.parameters.joinToString(", ") { it?.toString() ?: "<null>" }
        Log.debug("Invoking $targetClass::$targetMethod with args $args")
        return context.proceed()
    }
}
