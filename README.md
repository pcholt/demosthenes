# demosthenes
not even slightly production ready code. I just wanted to get some sleep, after looking at
https://github.com/Tinder/StateMachine and getting some inspiration from 
https://kotlinlang.org/docs/reference/type-safe-builders.html I thought I might make a more DSL-like state machine implementation.

State machine gets constructed from a DSL like this:

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

    // Set the initial state for the state machine with an invocation:
    machine(Solid)

    // Send the machine events, to trigger the actions listed in the transitions.
    machine.fireEvent(Melt)
