package test2

import cats.data.EitherT

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.Try

object Main extends App {

  def runLongTask(inputStr: String): EitherT[Future, String, Int] = {
    EitherT {
      Future {
        Try(inputStr.toInt).map(Right(_)).getOrElse(Left("Error"))
      }
    }
  }

  def runAnotherLongTask(input: Number): EitherT[Future, String, Int] = {
    EitherT {
      Future {
        Try(input.intValue()).map(Right(_)).getOrElse(Left("Error"))
      }
    }
  }

  def multiply(input1: String, input2: String): EitherT[Future, String, Int] = {
    for {
      num1 <- runLongTask(input1)
      num2 <- runAnotherLongTask(num1 * num1)
      num3 <- runAnotherLongTask(num2 * num2)
    } yield num3
  }

  val f = multiply("4", "1").value
  f.map {
    case Right(v) => println(v)
    case Left(err) => println(err)
  }

  Await.result(f, 100 seconds)

}
