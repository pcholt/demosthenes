import States.*
import Events.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MainKtTest {

    @Test
    fun `can we create a new state machine`() {
        val machine = stateMachine<States, Events> {
            state(solid) {
                event(Events.melt) {
                    transition(States.liquid)
                }
            }
            state(States.liquid) {
                event(freeze) {
                    transition(solid)
                }
                event(boil) {
                    transition(gas)
                }
            }
            state(gas) {
                event(condense) {
                    transition(liquid)
                }
            }
        }
        machine(solid)
        machine.fireEvent(melt)
        assertEquals(liquid, machine.currentState)

    }
}