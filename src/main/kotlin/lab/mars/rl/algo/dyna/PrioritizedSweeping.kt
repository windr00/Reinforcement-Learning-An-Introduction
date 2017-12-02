package lab.mars.rl.algo.dyna

import lab.mars.rl.algo.V_from_Q
import lab.mars.rl.algo.`ε-greedy (tie broken randomly)`
import lab.mars.rl.model.emptyPossibleSet
import lab.mars.rl.model.impl.mdp.*
import lab.mars.rl.model.isNotTerminal
import lab.mars.rl.util.collection.cnsetOf
import lab.mars.rl.util.log.debug
import lab.mars.rl.util.math.max
import lab.mars.rl.util.tuples.tuple2
import lab.mars.rl.util.tuples.tuple3
import org.apache.commons.math3.util.FastMath.abs
import org.slf4j.LoggerFactory
import java.util.*

@Suppress("NAME_SHADOWING")
class PrioritizedSweeping(val indexedMdp: IndexedMDP) {
    companion object {
        val log = LoggerFactory.getLogger(this::class.java)!!
    }

    val γ = indexedMdp.γ
    val started = indexedMdp.started
    val states = indexedMdp.states
    var stepListener: (StateValueFunction, IndexedState) -> Unit = { _, _ -> }
    var episodeListener: (StateValueFunction) -> Unit = {}

    var episodes = 10000
    var α = 0.1
    var ε = 0.1
    var θ = 0.0
    var n = 10
    fun optimal(α: (IndexedState, IndexedAction) -> Double = { _, _ -> this.α }): OptimalSolution {
        val π = IndexedPolicy(indexedMdp.QFunc { 0.0 })
        val Q = indexedMdp.QFunc { 0.0 }
        val PQueue = PriorityQueue(Q.size, Comparator<tuple3<Double, IndexedState, IndexedAction>> { o1, o2 ->
            o2._1.compareTo(o1._1)
        })
        val Model = indexedMdp.QFunc { emptyPossibleSet }
        val predecessor = indexedMdp.VFunc { hashSetOf<tuple2<IndexedState, IndexedAction>>() }
        val V = indexedMdp.VFunc { 0.0 }
        val result = tuple3(π, V, Q)
        for (episode in 1..episodes) {
            log.debug { "$episode/$episodes" }
            var step = 0
            var s = started()
            while (s.isNotTerminal()) {
                V_from_Q(states, result)
                stepListener(V, s)
                step++
                `ε-greedy (tie broken randomly)`(s, Q, π, ε)
                val a = π(s)
                val (s_next, reward) = a.sample()
                Model[s, a] = cnsetOf(IndexedPossible(s_next, reward, 1.0))
                predecessor[s_next] += tuple2(s, a)
                val P = abs(reward + γ * max(s_next.actions, 0.0) { Q[s_next, it] } - Q[s, a])
                if (P > θ) PQueue.add(tuple3(P, s, a))
                lab.mars.rl.util.math.repeat(n, { PQueue.isNotEmpty() }) {
                    val (_, s, a) = PQueue.poll()
                    val (s_next, reward) = Model[s, a].rand()
                    Q[s, a] += α(s, a) * (reward + γ * max(s_next.actions, 0.0) { Q[s_next, it] } - Q[s, a])
                    for ((s_pre, a_pre) in predecessor[s]) {
                        val (s_next, reward) = Model[s_pre, a_pre].rand()
                        assert(s_next === s)
                        val P = abs(reward + γ * max(s.actions, 0.0) { Q[s, it] } - Q[s_pre, a_pre])
                        if (P > θ) PQueue.add(tuple3(P, s_pre, a_pre))
                    }
                }
                s = s_next
            }
            episodeListener(V)
            log.debug { "steps=$step" }
        }
        return result
    }
}