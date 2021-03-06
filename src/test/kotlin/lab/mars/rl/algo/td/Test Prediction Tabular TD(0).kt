package lab.mars.rl.algo.td

import lab.mars.rl.problem.*
import lab.mars.rl.util.format
import lab.mars.rl.util.printBlackjack
import org.junit.Test

class `Test Prediction Tabular TD(0)` {
  @Test
  fun `Blackjack`() {
    val (prob, π) = Blackjack.make()
    val V = prob.`Tabular TD(0)`(π = π, α = 0.1, episodes = 500000)
    printBlackjack(prob, π, V)
  }
  
  @Test
  fun `RandomWalk`() {
    val (prob, π) = RandomWalk.make()
    val V = prob.`Tabular TD(0)`(π = π, α = 0.1, episodes = 1000)
    prob.apply {
      for (s in states) {
        println("${V[s].format(2)} ")
      }
    }
  }
  
  @Test
  fun `1000-state RandomWalk`() {
    val (prob, π) = `1000-state RandomWalk`.make()
    val V = prob.`Tabular TD(0)`(π = π, α = 0.1, episodes = 10000)
    prob.apply {
      for (s in states) {
        println("${V[s].format(2)} ")
      }
    }
  }
}