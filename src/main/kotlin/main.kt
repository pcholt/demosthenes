import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> = javaClass.enclosingClass?.takeIf { it.kotlin.java == javaClass } ?: javaClass
private inline fun Logger.i(function: () -> String)=if (this.isInfoEnabled) this.info(function()) else Unit
private inline fun Logger.d(function: () -> String)=if (this.isDebugEnabled) this.debug(function()) else Unit
class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
    override fun getValue(thisRef: R, property: KProperty<*>): Logger = getLogger(getClassForLogging(thisRef.javaClass))
}

inline fun <S : Any, E: Any> stateMachine(init: StateMachine<S,E>.() -> Unit) = StateMachine<S, E>().apply{ init() }

class StateMachine<S : Any, E : Any> {
    var states = hashMapOf<S,State<S,E>>()
    private val logger by LoggerDelegate()

    fun state(state: S, init: State<S,E>.() -> Unit): State<S, E> {
        logger.d{"Create state"}
        return State<S,E>().also(init).also {
            states[state] = it
        }
    }

    fun build(newState: S)
        = StateMachineInstance(newState, this)

    override fun toString() = states.toString()
}

class StateMachineInstance<S:Any, E:Any>(val currentState: S? = null, val machine:StateMachine<S,E>) {
    private val logger by LoggerDelegate()
    fun fireEvent(event: E) : StateMachineInstance<S,E> {
        var newState : S? = null
        machine.states[currentState]?.events?.get(event)?.let {
            logger.d { "Fire $it" }
            newState = it.state
            val effect = it.sideEffect
            if (effect != null) {
                effect(it.state)
            }
            logger.d { "Fire $it" }
        }
        return StateMachineInstance(newState, this.machine)
    }

    override fun toString() =
        "${super.toString()} : $machine : $currentState"

}

class State<S : Any, E : Any> {
    var events = hashMapOf<E,Event<S,E>>()

    fun event(event: E, state: S, optionalInitialiser: ((S) -> Unit)? = null)
            = Event(event, state, optionalInitialiser).also {
                events[event] = it
            }

    override fun toString() = events.toString()

}

data class Event<S: Any, E: Any>(val event: E, val state: S, val sideEffect: ((S) -> Unit)? = null)


