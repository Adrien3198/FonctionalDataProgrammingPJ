import com.amazonaws.regions.Regions
import jp.co.bizreach.kinesis.{AmazonKinesis, PutRecordRequest}

import scala.io.Source.fromFile
import scala.util.Random

object MyProducer {

  def sendToStream(msg: String, client: AmazonKinesis): Unit = {
    val record = PutRecordRequest(
      streamName = stream,
      partitionKey = "1",
      data = msg.getBytes("UTF-8")
    )
    client.putRecord(record)
  }

  implicit val region: Regions = Regions.EU_WEST_1
  val client: AmazonKinesis = AmazonKinesis()
  val stream = "Kinesistream"

  val infractionMsgPeriod: Int = 1
  val infoMsgPeriod: Int = infractionMsgPeriod * 1000
  val alertMsgPeriod: Int = infractionMsgPeriod * 4000

  var infractionCount = 0
  var alertCount = 0
  var infosCount = 0

  val rand = new Random()

  def printInfos(): Unit = println("\n\nEND\n\nINFRACTIONS: " + infractionCount + " sent\nINFOS: " + infosCount + " sent\nALERTS: " + alertCount + " sent")

  def sendMsgs(drones: List[Drone]): Unit = {

    val violations: Iterator[Array[String]] = fromFile("data/Parking_Violations_Issued_-_Fiscal_Year_2017-12-31.csv")
      .getLines()
      .map(_.split(","))

    val nbDrones = drones.size

    val t = new java.util.Timer()

    val task1 = new java.util.TimerTask {
      override def run(): Unit = {
        if (violations.hasNext) {
          drones.foreach(drone => {
            val msg = drone.infosMsg()
            sendToStream(msg, client)
            infosCount += 1
            //println("\nINFOS: \n\t" + msg)
            drone.changeInfos()
          })
        }
        else {
          printInfos()
          System.exit(1)
        }
      }
    }
    t.schedule(task1, infoMsgPeriod, infoMsgPeriod)

    val task2 = new java.util.TimerTask {
      override def run(): Unit = {
        if (violations.hasNext) {
          val msg = drones(rand.nextInt(nbDrones)).infractionMsg(violations.next())
          sendToStream(msg, client)
          infractionCount += 1
          //println("\nINFRACTION: \n\t" + msg)
        }
        else {
          printInfos()
          System.exit(2)
        }
      }
    }
    t.schedule(task2, infractionMsgPeriod, infractionMsgPeriod)

    val task3 = new java.util.TimerTask {
      override def run(): Unit = {
        if (violations.hasNext) {
          val msg = drones(rand.nextInt(nbDrones)).alertMsg(violations.next())
          sendToStream(msg, client)
          alertCount += 1
          println("\nALERT: \n\t" + msg)
        }
        else {
          printInfos()
          System.exit(3)
        }
      }
    }
    t.schedule(task3, alertMsgPeriod, alertMsgPeriod)
  }
}