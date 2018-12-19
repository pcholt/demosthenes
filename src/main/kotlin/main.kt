inline fun <S : Any, E: Any> stateMachine(init: StateMachine<S,E>.() -> Unit) = StateMachine<S, E>().apply{ init() }

class StateMachine<S : Any, E : Any> {
    var states = hashMapOf<S,State<S,E>>()

    fun state(state: S, init: State<S,E>.() -> Unit)
            = State<S,E>().also(init).also {
                states[state] = it
            }

    fun build(newState: S)
        = StateMachineInstance(newState, this)

    override fun toString() = states.toString()
}

class StateMachineInstance<S:Any, E:Any>(var currentState: S? = null, val machine:StateMachine<S,E>) {
    fun fireEvent(event: E) {
        val state = machine.states[currentState]
        state?.events?.get(event)?.let {
            currentState = it.transition?.to
        }
    }

    override fun toString() =
        "${super.toString()} : $machine : $currentState"

}

class State<S : Any, E : Any> {
    var events = hashMapOf<E,Event<S,E>>()

    fun event(event: E, state: S? = null, optionalInitialiser: (Event<S,E>.() -> Unit)? = null)
            = Event(event, state).also { optionalInitialiser?.let { initialiser -> initialiser(it) } }.also {
                events[event] = it
            }

    override fun toString() = events.toString()

}

class Event<S: Any, E: Any>(val event: E, val state: S? = null) {
    var transition: Transition<S>? = null
    fun transition(v: S, init: (Transition<S>.(S) -> Unit)? = null)
            = Transition(to= v, sideEffect = init ).also { transition1 ->
        this@Event.transition = transition1
    }

    override fun toString() =
            transition?.toString() ?: "**"
}

class Transition<S>(val to: S, val sideEffect : (Transition<S>.(S) -> Unit)? ) {
    override fun toString() =
            sideEffect.toString()

}

