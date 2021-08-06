package test5

object Main {
  def main(args: Array[String]): Unit = {
    def fuck(a: Int, b: Int)(c: Int) = a + b + c
    val m: Int => Int = fuck(1 ,1)

    println(m)
    println(m(1))

  }
}
