import States.*
import Events.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

enum class States {
    Solid, Liquid, Gas
}
enum class Events {
    Melt, Freeze, Boil, Condense
}

class MainKtTest {

    var melted = false

    @Test
    fun `can we create a new state machine`() {

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

        machine(Solid)
        machine.fireEvent(Melt)

        print(machine)

        assertEquals(Liquid, machine.currentState)
        assertTrue(melted)

    }
}