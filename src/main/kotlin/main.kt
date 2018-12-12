import Events.*
import States.*
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable

var temperature = 5;

enum class States {
    solid, liquid, gas
}
enum class Events {
    melt, freeze, boil, condense
}
fun <S> transition(v: S, init: (Transition<S>.(S) -> Unit)? = null) = Transition(to= v, sideEffect = init )

inline fun <S, E> stateMachine(init: StateMachine<S,E>.() -> Unit) = StateMachine<S, E>().apply{ init() }

class Transition<S>(val to: S, val sideEffect : (Transition<S>.(S) -> Unit)? ) {

    fun afterEnter(function: () -> Unit): Transition<S> {
        return this
    }

    fun beforeEnter(function: () -> Unit): Transition<S> {
        return this
    }

    fun afterExit(function: () -> Unit): Transition<S> {
        return this
    }

    fun beforeExit(function: () -> Unit): Transition<S> {
        sideEffect?.invoke(this, to)
        return this
    }
}

class StateMachine<S, E> {
    var currentState: S? = null
    var states = MultiHashtable<S,State<S,E>>() // ? did I get that right? It's too late

    fun state(state: S, init: State<S,E>.() -> Unit) = State<S,E>().also(init)

    fun event(event: E, action: State<S,E>.() -> Unit) {

    }

    operator fun invoke(newState: S) {
        currentState = newState
    }

    fun fireEvent(event: E) {

    }
}

class State<T : Any?, E: Any?> {
    var first = false
    fun enter(x: () -> Unit) {}
    fun exit(x: () -> Unit) {}
}
