
object Main extends App {
  val drone1 = new Drone(1)
  val drone2 = new Drone( 2)
  val drone3= new Drone(3)

  var drones = List(drone1, drone2, drone3)
  val producer = MyProducer
  producer.sendMsgs(drones)

}