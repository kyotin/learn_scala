package test3

object Main extends App {
  import cats.Semigroup
  import cats.data.{ NonEmptyList, OneAnd, Validated, ValidatedNel }
  import cats.implicits._

  val right: Either[String, Int] = Right(41)
  println(right.map(_ + 1))

  val left: Either[String, Int] = Left("Hello")
  println(left.map(_ + 1))

  println(left.leftMap(_.reverse))

}
