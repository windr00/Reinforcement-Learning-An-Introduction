package lab.mars.rl.algo.dyna

import lab.mars.rl.algo.V_from_Q_ND
import lab.mars.rl.algo.`e-greedy tie random`
import lab.mars.rl.model.*
import lab.mars.rl.util.debug
import lab.mars.rl.util.max
import lab.mars.rl.util.repeat
import lab.mars.rl.util.tuples.tuple2
import lab.mars.rl.util.tuples.tuple3
import org.apache.commons.math3.util.FastMath.abs
import org.slf4j.LoggerFactory
import java.util.*

@Suppress("NAME_SHADOWING")
class PrioritizedSweeping(val mdp: MDP) {
    companion object {
        val log = LoggerFactory.getLogger(this::class.java)!!
    }

    val gamma = mdp.gamma
    val started = mdp.started
    val states = mdp.states
    var stepListener: (ActionValueFunction, State) -> Unit = { _, _ -> }
    var episodeListener: (StateValueFunction) -> Unit = {}

    var episodes = 10000
    var alpha = 0.1
    var epsilon = 0.1
    var theta = 0.0
    var n = 10
    val null_tuple2 = tuple2(null_state, Double.NaN)
    fun optimal(_alpha: (State, Action) -> Double = { _, _ -> alpha }): OptimalSolution {
        val policy = mdp.QFunc { 0.0 }
        val Q = mdp.QFunc { 0.0 }
        val PQueue = PriorityQueue(Q.size, Comparator<tuple3<Double, State, Action>> { o1, o2 ->
            var result = o2._1.compareTo(o1._1)
            if (result == 0) {

            }
            result
        })
        val Model = mdp.QFunc { null_tuple2 }
        val predecessor = mdp.VFunc { hashSetOf<tuple2<State, Action>>() }
        val V = mdp.VFunc { 0.0 }
        val result = tuple3(policy, V, Q)
        for (episode in 1..episodes) {
            log.debug { "$episode/$episodes" }
            var count = 0
            var s = started.rand()
            while (s.isNotTerminal()) {
                V_from_Q_ND(states, result)
                stepListener(V, s)
                count++
                `e-greedy tie random`(s, Q, policy, epsilon)
                val a = s.actions.rand(policy(s))
                val (s_next, reward, _) = a.sample()
                Model[s, a] = tuple2(s_next, reward)
                predecessor[s_next] += tuple2(s, a)
                val P = abs(reward + gamma * max(s_next.actions, 0.0) { Q[s_next, it] } - Q[s, a])
                if (P > theta) PQueue.add(tuple3(P, s, a))
                repeat(n, { PQueue.isNotEmpty() }) {
                    val (_, s, a) = PQueue.poll()
                    val (s_next,reward)=Model[s,a]
                    Q[s, a] += _alpha(s, a) * reward + gamma * max(s_next.actions, 0.0) { Q[s_next, it] } - Q[s, a]
                    V_from_Q_ND(states, result)
                    stepListener(V, s)
                    for ((s_pre, a_pre) in predecessor[s]) {
                        val (s_next, reward) = Model[s_pre, a_pre]
                        assert(s_next === s)
                        val P = abs( reward + gamma * max(s.actions, 0.0) { Q[s, it] } - Q[s_pre, a_pre])
                        if (P > theta) PQueue.add(tuple3(P, s_pre, a_pre))
                    }
                }
                s = s_next
            }
            episodeListener(V)
            log.debug { "steps=$count" }
        }
        return result
    }
}