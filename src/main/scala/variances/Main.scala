package variances

abstract class Animal {
  def name: String
}

case class Cat(name: String) extends Animal
case class Dog(name: String) extends Animal

object Main extends App {
  def printAnimal(animals: List[Animal]): Unit =
    animals.foreach {
      animals => println(animals.name)
    }

  val cats: List[Cat] = List(Cat("AAA"), Cat("BBB"))
  val dogs: List[Dog] = List(Dog("CCC"), Dog("DDD"))

  printAnimal(cats)
  printAnimal(dogs)
}
