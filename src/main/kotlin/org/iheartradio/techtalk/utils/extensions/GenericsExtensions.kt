package org.iheartradio.techtalk.utils.extensions

fun <T> T?.orElse(default: T?): T? = this ?: default

fun <T> T?.ifNullThen(doSomething: () -> Unit): T? = apply {
    if(this == null) { doSomething() }
}

fun <T> T.execute(action: () -> Unit) : T = action().let { this }