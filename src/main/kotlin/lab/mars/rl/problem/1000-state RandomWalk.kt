package lab.mars.rl.problem

import lab.mars.rl.model.MDP
import lab.mars.rl.model.NonDeterminedPolicy
import lab.mars.rl.model.Possible
import lab.mars.rl.model.impl.CNSetMDP
import lab.mars.rl.util.Rand
import lab.mars.rl.util.emptyNSet

/**
 * <p>
 * Created on 2017-10-10.
 * </p>
 *
 * @author wumo
 */
object `1000-state RandomWalk` {
    val num_states = 1000
    val step_range = 100
    fun make(): Pair<MDP, NonDeterminedPolicy> {
        val mdp = CNSetMDP(1.0, num_states + 2, 1)
        mdp.apply {
            val last = num_states + 1
            states[0].actions = emptyNSet()
            states[last].actions = emptyNSet()
            started = states(num_states / 2)
            for (a in 1 until last)
                states[a].actions[0].sample = {
                    val move = Rand().nextInt(1, step_range + 1) *
                               (if (Rand().nextBoolean()) 1 else -1)
                    val next = (a + move).coerceIn(0, last)
                    Possible(states[next],
                             when (next) {
                                 0 -> -1.0
                                 last -> 1.0
                                 else -> 0.0
                             }, 1.0)
                }
        }
        val policy = mdp.QFunc { 1.0 }
        return Pair(mdp, policy)
    }
}