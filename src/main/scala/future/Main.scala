package future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends App {
  println(s"i'm - ${Thread.currentThread().getName}")

  val start = System.currentTimeMillis()
  val f1 = Future {
    println(s"${Thread.currentThread().getName}")
    val r = 1 + 1
    println(s"${Thread.currentThread().getName} done")
    r
  }

  val x = for {
    r1 <- f1
    r2 <- f1
  } yield r1 + r2

  x.foreach(v => println(v))
  val end = System.currentTimeMillis()
  printf("Running time: %d \n", end - start)
}
