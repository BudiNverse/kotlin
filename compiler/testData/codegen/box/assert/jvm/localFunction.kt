// IGNORE_BACKEND: JVM_IR
// IGNORE_BACKEND: JS_IR
// IGNORE_BACKEND: JS
// KOTLIN_CONFIGURATION_FLAGS: ASSERTIONS_MODE=jvm
// WITH_RUNTIME

interface Checker {
    fun checkTrue(): Boolean
    fun checkFalse(): Boolean
    fun checkTrueWithMessage(): Boolean
    fun checkFalseWithMessage(): Boolean
}

class ShouldBeDisabled : Checker {
    override fun checkTrue(): Boolean {
        var hit = false
        val l = { hit = true; true }
        fun local() {
            assert(l())
        }
        local()
        return hit
    }

    override fun checkFalse(): Boolean {
        var hit = false
        val l = { hit = true; false }
        fun local() {
            assert(l())
        }
        local()
        return hit
    }

    override fun checkTrueWithMessage(): Boolean {
        var hit = false
        val l = { hit = true; true }
        fun local() {
            assert(l()) { "BOOYA" }
        }
        local()
        return hit
    }

    override fun checkFalseWithMessage(): Boolean {
        var hit = false
        val l = { hit = true; false }
        fun local() {
            assert(l()) { "BOOYA" }
        }
        local()
        return hit
    }
}

class ShouldBeEnabled : Checker {
    override fun checkTrue(): Boolean {
        var hit = false
        val l = { hit = true; true }
        fun local() {
            assert(l())
        }
        local()
        return hit
    }

    override fun checkFalse(): Boolean {
        var hit = false
        val l = { hit = true; false }
        fun local() {
            assert(l())
        }
        local()
        return hit
    }

    override fun checkTrueWithMessage(): Boolean {
        var hit = false
        val l = { hit = true; true }
        fun local() {
            assert(l()) { "BOOYA" }
        }
        local()
        return hit
    }

    override fun checkFalseWithMessage(): Boolean {
        var hit = false
        val l = { hit = true; false }
        fun local() {
            assert(l()) { "BOOYA" }
        }
        local()
        return hit
    }
}

fun setDesiredAssertionStatus(v: Boolean): Checker {
    val loader = Checker::class.java.classLoader
    loader.setDefaultAssertionStatus(v)
    val c = loader.loadClass(if (v) "ShouldBeEnabled" else "ShouldBeDisabled")
    return c.newInstance() as Checker
}

fun box(): String {
    var c = setDesiredAssertionStatus(false)
    if (c.checkTrue()) return "FAIL 0"
    if (c.checkTrueWithMessage()) return "FAIL 1"
    if (c.checkFalse()) return "FAIL 2"
    if (c.checkFalseWithMessage()) return "FAIL 3"
    c = setDesiredAssertionStatus(true)
    if (!c.checkTrue()) return "FAIL 4"
    if (!c.checkTrueWithMessage()) return "FAIL 5"
    try {
        c.checkFalse()
        return "FAIL 6"
    } catch (ignore: AssertionError) {
    }
    try {
        c.checkFalseWithMessage()
        return "FAIL 7"
    } catch (ignore: AssertionError) {
    }

    Checker::class.java.classLoader.setDefaultAssertionStatus(true)

    return "OK"
}