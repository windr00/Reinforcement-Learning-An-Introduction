package lab.mars.rl.algo.func_approx.on_policy_control

import lab.mars.rl.algo.func_approx.FunctionApprox
import lab.mars.rl.model.*
import lab.mars.rl.util.matrix.times

fun <E> FunctionApprox.`Differential semi-gradient Sarsa`(q: ApproximateFunction<E>, trans: (State, Action<State>) -> E, β: Double) {
    var average_reward = 0.0
    var s = started()
    var a = π(s)
    while (true) {
        val (s_next, reward) = a.sample()
        val a_next = π(s_next)
        val δ = reward - average_reward + q(trans(s_next, a_next)) - q(trans(s, a))
        average_reward += β * δ
        q.w += α * δ * q.`▽`(trans(s, a))
        s = s_next
        a = a_next
    }
}