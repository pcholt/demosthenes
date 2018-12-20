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
        machineInstance.fireEvent(Melt).fireEvent(Freeze).fireEvent(Melt).fireEvent(Boil)

        assertEquals(Gas, machineInstance.currentState)
        assertTrue(melted)

        assertFalse(wind)
        assertTrue(machineInstance.fireEvent(Move).run { wind })

    }

    @Test
    fun `how about a state machine based on integers?`() {

        val a= 2
        val machine = stateMachine<Int, Int> {
            state(0) {
                event ({it < 1}, 1)
                event (1, 2)
            }
            state(1) {
                event (0, 0)
            }
        }.build(0)

        machine.fireEvent(-2).fireEvent(-2)
        assertEquals(1, machine.currentState )


    }


}