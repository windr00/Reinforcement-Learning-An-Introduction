package lab.mars.rl.algo.dp

import lab.mars.rl.model.isNotTerminal
import lab.mars.rl.problem.*
import lab.mars.rl.problem.GridWorld.make
import lab.mars.rl.util.*
import org.junit.Assert
import org.junit.Test

val `Car Rental Result` = arrayOf(
    "554.95", "561.56", "567.59", "573.19", "578.54", "583.63", "588.50", "593.15", "597.57", "601.78", "605.83", "609.70", "613.40", "616.91", "620.25", "623.49", "626.55", "629.41", "632.20", "634.75", "636.99",
    "550.82", "557.59", "563.80", "569.59", "575.19", "580.54", "585.63", "590.50", "595.15", "599.57", "603.78", "607.83", "611.70", "615.40", "618.91", "622.25", "625.49", "628.55", "631.41", "634.02", "636.31",
    "546.45", "553.39", "559.77", "565.80", "571.59", "577.19", "582.54", "587.63", "592.50", "597.15", "601.57", "605.78", "609.83", "613.70", "617.40", "620.91", "624.25", "627.40", "630.35", "633.02", "635.37",
    "541.86", "548.95", "555.53", "561.77", "567.80", "573.59", "579.19", "584.54", "589.63", "594.50", "599.15", "603.57", "607.78", "611.79", "615.61", "619.25", "622.70", "625.96", "628.99", "631.75", "634.16",
    "537.03", "544.29", "551.05", "557.53", "563.77", "569.80", "575.59", "581.19", "586.54", "591.63", "596.45", "601.04", "605.41", "609.57", "613.53", "617.29", "620.87", "624.23", "627.37", "630.22", "632.71",
    "531.96", "539.39", "546.33", "553.05", "559.53", "565.77", "571.80", "577.59", "583.19", "588.46", "593.46", "598.22", "602.75", "607.07", "611.17", "615.07", "618.77", "622.25", "625.49", "628.43", "631.00",
    "526.64", "534.24", "541.39", "548.33", "555.05", "561.53", "567.77", "573.80", "579.58", "585.04", "590.22", "595.15", "599.85", "604.32", "608.56", "612.60", "616.42", "620.02", "623.37", "626.41", "629.06",
    "521.05", "528.82", "536.24", "543.39", "550.33", "557.05", "563.53", "569.77", "575.75", "581.39", "586.75", "591.85", "596.71", "601.33", "605.72", "609.89", "613.84", "617.55", "621.01", "624.15", "626.88",
    "515.16", "523.08", "530.82", "538.24", "545.39", "552.33", "559.05", "565.53", "571.69", "577.51", "583.05", "588.32", "593.34", "598.11", "602.64", "606.94", "611.01", "614.85", "618.41", "621.64", "624.46",
    "508.91", "517.16", "525.08", "532.82", "540.24", "547.39", "554.33", "561.05", "567.40", "573.41", "579.12", "584.56", "589.73", "594.64", "599.31", "603.75", "607.94", "611.89", "615.56", "618.89", "621.79",
    "502.36", "510.91", "519.16", "527.08", "534.82", "542.24", "549.39", "556.33", "562.87", "569.06", "574.95", "580.54", "585.87", "590.92", "595.73", "600.28", "604.60", "608.66", "612.43", "615.86", "618.84",
    "495.58", "504.36", "512.91", "521.16", "529.08", "536.82", "544.24", "551.36", "558.08", "564.45", "570.50", "576.25", "581.72", "586.91", "591.84", "596.52", "600.95", "605.12", "609.00", "612.51", "615.58",
    "488.45", "497.58", "506.36", "514.91", "523.16", "531.08", "538.82", "546.11", "553.01", "559.54", "565.74", "571.64", "577.24", "582.56", "587.61", "592.41", "596.95", "601.23", "605.21", "608.82", "611.97",
    "481.06", "490.45", "499.58", "508.36", "516.91", "525.16", "533.08", "540.54", "547.59", "554.27", "560.62", "566.65", "572.37", "577.81", "582.98", "587.89", "592.54", "596.92", "601.00", "604.71", "607.96",
    "473.49", "483.06", "492.45", "501.58", "510.36", "518.91", "526.97", "534.56", "541.75", "548.57", "555.04", "561.19", "567.03", "572.58", "577.85", "582.87", "587.62", "592.11", "596.30", "600.11", "603.45",
    "465.64", "475.49", "485.06", "494.45", "503.58", "512.22", "520.38", "528.09", "535.39", "542.32", "548.90", "555.15", "561.08", "566.73", "572.11", "577.23", "582.08", "586.68", "590.97", "594.89", "598.33",
    "457.68", "467.64", "477.49", "487.06", "496.23", "504.95", "513.19", "520.99", "528.38", "535.39", "542.05", "548.38", "554.40", "560.14", "565.61", "570.81", "575.77", "580.46", "584.86", "588.97", "592.89",
    "449.58", "459.56", "469.41", "479.00", "488.20", "496.96", "505.25", "513.11", "520.56", "527.63", "534.35", "540.74", "546.83", "552.64", "558.18", "563.61", "568.81", "573.77", "578.46", "582.86", "586.97",
    "440.76", "450.73", "460.59", "470.19", "479.41", "488.19", "496.52", "504.40", "511.89", "518.99", "525.75", "532.35", "538.74", "544.83", "550.64", "556.18", "561.61", "566.81", "571.77", "576.46", "580.86",
    "431.29", "441.26", "451.12", "460.72", "469.95", "478.74", "487.08", "494.98", "502.47", "509.89", "516.99", "523.75", "530.35", "536.74", "542.83", "548.64", "554.18", "559.61", "564.81", "569.77", "574.46",
    "421.41", "431.39", "441.24", "450.85", "460.08", "468.87", "477.21", "485.11", "492.98", "500.47", "507.89", "514.99", "521.75", "528.35", "534.74", "540.83", "546.64", "552.18", "557.61", "562.81", "567.77")

class `Test Policy Iteration` {
  @Test
  fun `Grid World`() {
    val prob = make()
    val (_, V) = prob.`Policy Iteration V`()
    for (s in prob.states) {
      println(V[s])
    }
  }
  
  @Test
  fun `Car Rental Policy Iteration Value`() {
    val prob = CarRental.make(false)
    val (_, V) = prob.`Policy Iteration V`()
    var i = 0
    for (a in CarRental.max_car downTo 0)
      for (b in 0..CarRental.max_car)
        Assert.assertEquals(`Car Rental Result`[i++], V[a, b].format(2))
  }
  
  @Test
  fun `Car Rental Policy Iteration Policy`() {
    val prob = CarRental.make(false)
    val (_, V, _) = prob.`Policy Iteration Q`()
    var i = 0
    for (a in CarRental.max_car downTo 0)
      for (b in 0..CarRental.max_car)
        Assert.assertEquals(`Car Rental Result`[i++], V[prob.states[a, b]].format(2))
  }
  
  @Test
  fun `Dyna Maze`() {
    val prob = DynaMaze.make()
    val (π) = prob.`Policy Iteration V`()
    var s = prob.started()
    var count = 0
    print(s)
    while (s.isNotTerminal) {
      val a = π(s)
      val possible = a.sample()
      s = possible.next
      count++
      print("${DynaMaze.desc_move[a[0]]}$s")
    }
    println("\nsteps=$count")//optimal=14
  }
  
  @Test
  fun `Rod maneuvering`() {
    val prob = RodManeuvering.make()
    val (π) = prob.`Policy Iteration V`()
    var s = prob.started()
    var count = 0
    print(s)
    while (s.isNotTerminal) {
      val a = π(s)
      val possible = a.sample()
      s = possible.next
      count++
      print("$a$s")
    }
    println("\nsteps=$count")//optimal=39
  }
  
  @Test
  fun `AccessControl`() {
    val prob = AccessControl.make()
    val (π) = prob.`Policy Iteration V`()
    for (pr in AccessControl.priorities) {
      for (fs in 0..AccessControl.k) {
        val s = prob.states[fs, pr]
        print("${color(1 - π.greedy(s)[0])}  ${reset()}")
      }
      println()
    }
  }
}