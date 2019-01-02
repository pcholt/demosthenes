package com.overswell.demosthenes

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> =
    javaClass.enclosingClass?.takeIf { it.kotlin.java == javaClass } ?: javaClass

private inline fun Logger.d(function: () -> String) = if (this.isDebugEnabled) this.debug(function()) else Unit

class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
    override fun getValue(thisRef: R, property: KProperty<*>): Logger = getLogger(
        getClassForLogging(
            thisRef.javaClass
        )
    )
}

inline fun <S : Any, E : Any> stateMachine(init: StateMachine<S, E>.() -> Unit) = StateMachine<S, E>().apply { init() }

class StateMachine<S : Any, E : Any> {
    var states = hashMapOf<S, State<S, E>>()
    private val logger by LoggerDelegate()

    fun state(state: S, init: State<S, E>.() -> Unit): State<S, E> {
        logger.d { "Create state" }
        return State<S, E>().also(init).also {
            states[state] = it
        }
    }

    fun build(newState: S) = StateMachineInstance(newState, this)

    override fun toString() = states.toString()
}

class StateMachineInstance<S : Any, E : Any>(var currentState: S? = null, private val machine: StateMachine<S, E>) {

    fun fireEvent(event: E) = this.also{
        fireEventOrFalse(event)
    }

    fun fireEventOrFalse(event: E): Boolean {
        return machine.states[currentState]?.run {
            if (events.containsKey(event)) {
                events[event]?.let {
                    currentState = it.state
                    it.sideEffect?.invoke(it.state)
                    return true
                }
            } else {
                eventMatchers.firstOrNull { it.predicate(event) }?.let {
                    currentState = it.state
                    it.sideEffect?.invoke(it.state)
                    return true
                }
                return false
            }
        } ?: false
    }

    override fun toString() =
        "${super.toString()} : $machine : $currentState"

}

class State<S : Any, E : Any> {
    var events = hashMapOf<E, Event<S, E>>()
    var eventMatchers = arrayListOf<EventMatcher<S, E>>()

    fun event(event: E, state: S, optionalInitialiser: ((S) -> Unit)? = null) = Event(
        event = event,
        state = state,
        sideEffect = optionalInitialiser
    ).also {
        events[event] = it
    }

    fun event(eventMatcher: (E) -> Boolean, state: S, optionalInitialiser: ((S) -> Unit)? = null) {
        eventMatchers.add(
            EventMatcher(
                predicate = eventMatcher,
                state = state,
                sideEffect = optionalInitialiser
            )
        )
    }

    override fun toString() = events.toString()

}

data class EventMatcher<S : Any, E : Any>(
    val predicate: (E) -> Boolean,
    val state: S,
    val sideEffect: ((S) -> Unit)? = null
)

data class Event<S : Any, E : Any>(
    val event: E,
    val state: S,
    val sideEffect: ((S) -> Unit)? = null
)

