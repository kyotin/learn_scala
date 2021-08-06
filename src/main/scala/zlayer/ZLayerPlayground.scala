package zlayer

import zio.{ExitCode, Has, Task, ULayer, URIO, ZIO, ZLayer}
import zio.console._
import zlayer.ZLayerPlayground.UserEmailer.UserEmailerEnv

object ZLayerPlayground extends zio.App {

  val meaningOfLife = ZIO.succeed(42)
  val aFailure = ZIO.fail("Something went wrong")

  val greeting = for {
    _ <- putStrLn("Hi, what's your name")
    name <- getStrLn
    _ <- putStr(s"Hello, ${name}, welcome to Rock the JVM")
  } yield ()

  //  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
  //    greeting.exitCode
  //  }

  case class User(name: String, email: String)

  object UserEmailer {
    type UserEmailerEnv = Has[UserEmailer.Service]

    // service definition
    trait Service {
      def notify(user: User, message: String): Task[Unit] //ZIO[Any, Throwable, Unit]
    }

    // service impl
    val live: ZLayer[Any, Nothing, UserEmailerEnv] = ZLayer.succeed(new Service {
      override def notify(user: User, message: String): Task[Unit] = Task {
        println(s"Sending $message to ${user.email}")
      }
    })

    // front-facing API
    def notify(user: User, message: String): ZIO[UserEmailerEnv, Throwable, Unit] =
      ZIO.accessM(hasService => hasService.get.notify(user, message))
  }

  object UserDb {
    type UserDbEnv = Has[UserDb.Service]

    trait Service {
      def insert(user: User): Task[Unit]
    }

    val live: ZLayer[Any, Nothing, UserDbEnv] = ZLayer.succeed(new Service {
      override def insert(user: User): Task[Unit] = Task {
        println(s"[Database] insert into public.user value (${user.email})")
      }
    })

    def insert(user: User): ZIO[UserDbEnv, Throwable, Unit] =
      ZIO.accessM(hasService => hasService.get.insert(user))
  }

  // HORIZONTAL composition
  // ZLayer[In1, Er1, Out1] ++ ZLayer[In2, Er2, Out2] => ZLayer[In1 with In2, super(E1, E2), Out1 with Out2]

  import UserDb._
  import UserEmailer._

  val userBackendLayer: ZLayer[Any, Nothing, UserDbEnv with UserEmailerEnv] = UserDb.live ++ UserEmailer.live

  // VERTICAL composition
  object UserSubscription {
    type UserSubscriptionEnv = Has[UserSubscription.Service]

    class Service(notifier: UserEmailer.Service, userDb: UserDb.Service) {
      def subscribe(user: User): Task[User] =
        for {
          _ <- userDb.insert(user)
          _ <- notifier.notify(user, s"Welcome ${user.name}")
        } yield user
    }

    val live = ZLayer.fromServices[UserEmailer.Service, UserDb.Service, UserSubscription.Service] {
      (userEmailer, userDb) => new Service(userEmailer, userDb)
    }

    def subscribe(user: User): ZIO[UserSubscriptionEnv, Throwable, User] = ZIO.accessM(_.get.subscribe(user))
  }

  import UserSubscription._

  val userSubscriptionLayer: ZLayer[Any, Nothing, UserSubscriptionEnv] = userBackendLayer >>> UserSubscription.live

  val tin = User("tinnguyen", "tinnguyen@axon.com")
  val message = "Welcome to Rock the JVM"

  //  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
  //    UserEmailer.notify(tin, message) // the kind of effect
  //      .provideLayer(userBackendLayer) // provide the input for that effect ... DI
  //      .exitCode

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    UserSubscription.subscribe(tin)
      .provideLayer(userSubscriptionLayer)
      .exitCode
}
