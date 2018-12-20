# Demosthenes

## State machine

Not production ready code. 

After looking at
https://github.com/Tinder/StateMachine and getting some inspiration from 
https://kotlinlang.org/docs/reference/type-safe-builders.html I thought I might make a more 
DSL-like state machine implementation.

State machine gets constructed from a DSL like this. 
__First__ create an enum class of all the possible states and events
that can occur to those states:

    enum class States {
        Solid, Liquid, Gas
    }
    enum class Events {
        Melt, Freeze, Boil, Condense
    }
    
The state and event classes don't actually have to be enum classes.
Any class that returns a viable `hashcode`
and `equals` implementation can be used. Kotlin data classes come with their own
pre-built implementations of `hashcode` and `equals` so they are also good candidates. 

__Second__ create a state machine which will hold state machine rules 
and the events to be fired when states change:

    val machine = stateMachine<States, Events> {
        state(Solid) {
            event(Melt, Liquid) {
                melted = true
            }
        }
        state(States.Liquid) {
            event(Freeze, Solid)
            event(Boil, Gas)
        }
        state(Gas) {
            // You can match events with a predicate
            event({it==Condense}, Liquid)
        }
    }

Create a new state machine instance from the state machine definition:

    val machineInstance = machine.build(Solid)

All side effects are triggered when you fire an event:

    var melted = false
    ...
    machine.fireEvent(Melt)
    assertTrue(melted)
