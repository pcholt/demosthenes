inline fun <S : Any, E: Any> stateMachine(init: StateMachine<S,E>.() -> Unit) = StateMachine<S, E>().apply{ init() }

class StateMachine<S : Any, E : Any> {
    var currentState: S? = null
    var states = hashSetOf<Pair<S,State<S,E>>>()

    fun state(state: S, init: State<S,E>.() -> Unit)
            = State<S,E>().also(init).also {
                states.add(Pair(state, it))
            }

    operator fun invoke(newState: S) {
        currentState = newState
    }

    fun fireEvent(event: E) {
        // find ever

    }

    override fun toString() = states.toString()
}

class State<S : Any, E : Any> {
    var events = hashSetOf<Pair<E,Event<S,E>>>()

    fun event(event: E, init: Event<S,E>.() -> Unit)
            = Event<S,E>(event).also(init).also {
                events.add(Pair(event, it))
            }

    override fun toString() = events.toString()

}

class Event<S: Any, E: Any>(val event: E) {
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

