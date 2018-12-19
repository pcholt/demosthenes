import States.*
import Events.*
import org.junit.Assert.*
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
                event(Melt, Liquid) {
                    melted = true
                }
            }
            state(States.Liquid) {
                event(Freeze, Solid) {}
                event(Boil, Gas) {}
            }
            state(Gas) {
                event(Condense, Liquid) {}
                event(Move, Gas) {
                    wind = true
                }
            }
        }

        val machineInstance = machine.build(Solid)
        assertFalse("not melted", melted)
        assertFalse("no wind", wind)
        val finalInstance= machineInstance.fireEvent(Melt).fireEvent(Freeze).fireEvent(Melt).fireEvent(Boil)

        assertEquals(Gas, finalInstance.currentState)
        assertTrue(melted)
        assertFalse(wind)

        assertTrue(finalInstance.fireEvent(Move).run { wind })

    }
}