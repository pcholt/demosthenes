import States.*
import Events.*
import org.junit.Assert.*
import org.junit.Test
import com.overswell.demosthenes.*

enum class States {
    Solid, Liquid, Gas
}
enum class Events {
    Melt, Freeze, Boil, Condense, Move
}


class StateMachineKtTest {

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

        val machine = stateMachine<Int, Int> {
            state(0) {
                event({ it < 1 }, 1)
                event(1, 2)
            }
            state(1) {
                event(0, 0)
            }
        }.build(0)

        machine.fireEvent(-2).fireEvent(-2)
        assertEquals(1, machine.currentState)

    }

    @Test
    fun `lets try using a complete data class for states and events`() {
        data class X(val a: Int, val b: Int)
        data class Y(val name: String)

        val machine = stateMachine<X, Y> {
            state(X(1, 2)) {
                this.event({ it.name == "Fred" }, X(2, 1))
            }
            state(X(2, 1)) {
                this.event(Y("Jane"), X(1, 2))
            }
        }.build(X(1, 2))

        machine.fireEvent(Y("Fred"))
        assertEquals(X(2, 1), machine.currentState)
        machine.fireEvent(Y("Fred"))
        assertEquals(X(2, 1), machine.currentState)
        machine.fireEvent(Y("Jane"))
        assertEquals(X(1, 2), machine.currentState)
    }

    enum class X { A, B }
    enum class Y { C, D }

    @Test
    fun `check fireEventOrFalse`() {
        val machine = stateMachine<X, Y> {
            state(X.A) { event(Y.C, X.B) }
            state(X.B) { event(Y.D, X.A) }
        }.build(X.A)

        assertTrue(machine.fireEventOrFalse(Y.C))
        assertFalse(machine.fireEventOrFalse(Y.C))
    }


}