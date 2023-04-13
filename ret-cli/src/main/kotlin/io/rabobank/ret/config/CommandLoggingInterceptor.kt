package io.rabobank.ret.config

import io.quarkus.logging.Log
import java.lang.annotation.Inherited
import javax.interceptor.AroundInvoke
import javax.interceptor.Interceptor
import javax.interceptor.InterceptorBinding
import javax.interceptor.InvocationContext

@InterceptorBinding
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Inherited
annotation class Logged

@Interceptor
@Logged
class CommandLoggingInterceptor {

    @AroundInvoke()
    @Throws(Exception::class)
    fun intercept(context: InvocationContext): Any? {
        val targetClass = context.method.declaringClass.name
        val targetMethod = context.method.name
        val args = context.parameters.joinToString(", ") { it?.toString() ?: "<null>" }
        Log.debug("Invoking $targetClass::$targetMethod with args $args")
        return context.proceed()
    }
}
