package test1

import scala.util.Try

object Main extends App {

  def parseDouble(s: String): Either[String, Double] =
    Try(s.toDouble).map(Right(_)).getOrElse(Left(s"$s is not a number"))

  def divide(a: Double, b: Double): Either[String, Double] =
    Either.cond(b != 0, a / b, "Can't divide by zero")

  def divisionProgram(inputA: String, inputB: String): Either[String, Double] =
    for {
      a <- parseDouble(inputA)
      b <- parseDouble(inputB)
      result <- divide(a, b)
    } yield result

  println(divisionProgram("4", "2"))
  println(divisionProgram("A", "b"))

}
