import States.*
import Events.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

enum class States {
    Solid, Liquid, Gas
}
enum class Events {
    Melt, Freeze, Boil, Condense, Move
}

class MainKtTest {

    var melted = false
    var wind = false

    @Test
    fun `can we create a new state machine`() {

        val machine = stateMachine<States, Events> {
            state(Solid) {
                event(Events.Melt, States.Liquid) {
                    melted = true
                }
            }
            state(States.Liquid) {
                event(Freeze, Solid)
                event(Boil, Gas)
            }
            state(Gas) {
                event(Condense, Liquid)
                event(Move) {
                    wind = true
                }
            }
        }

        val machineInstance = machine.build(Solid)
        machineInstance.fireEvent(Melt)

        print(machineInstance)

        assertEquals(Liquid, machineInstance.currentState)
        assertTrue(melted)

    }
}