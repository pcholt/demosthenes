# demosthenes
not even slightly production ready code. I just wanted to get some sleep, after looking at
https://github.com/Tinder/StateMachine and getting some inspiration from 
https://kotlinlang.org/docs/reference/type-safe-builders.html I thought I might make a more DSL-like state machine implementation.

State machine gets constructed from a DSL like this. First create an enum class of all the possible states and events
that can occur to those states:

    enum class States {
        Solid, Liquid, Gas
    }
    enum class Events {
        Melt, Freeze, Boil, Condense
    }

Now create a state machine which will hold those states and receive events

    val machine = stateMachine<States, Events> {
        state(Solid) {
            event(Events.Melt) {
                transition(States.Liquid) {
                    melted = true
                }
            }
        }
        state(States.Liquid) {
            event(Freeze) {
                transition(Solid)
            }
            event(Boil) {
                transition(Gas)
            }
        }
        state(Gas) {
            event(Condense) {
                transition(Liquid)
            }
        }
    }

Not sure that's the best option. I'm leaning towards this format of DSL:

    val machine = stateMachine<States, Events> {
        state(Solid) {
            beforeTransitionFrom(States.Liquid) {
                
            }
            transition(Events.Melt, States.Liquid) {
                melted = true
            }
        }
        state(States.Liquid) {
            transition(Freeze, Solid)
            transition(Boil, Gas)
        }
        state(Gas) {
            transition(Condense, Liquid)
        }
    }

Set the initial state for the state machine with an invocation:

    machine(Solid)

Send the machine events, to trigger the actions listed in the transitions.

    machine.fireEvent(Melt)
