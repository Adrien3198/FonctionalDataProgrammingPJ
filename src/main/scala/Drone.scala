import java.util.{Calendar, Random}

import play.api.libs.json.{Json, _}

class Drone(id: Int) {
  val id_drone: Int = id
  var timestamp: Long = Calendar.getInstance.getTimeInMillis
  var battery: Int = 100
  var altitude: Int = 0
  var temperature: Int = 30
  var speed: Int = 0
  var disk_space: Int = 1000
  var lat: Double = 48.8534
  var long: Double = 2.3488

  def changeInfos(): Unit = {
    val r = new Random()
    temperature = 31
    timestamp = Calendar.getInstance.getTimeInMillis
    battery = battery - 1
    altitude = altitude + 1 + r.nextInt(3)
    speed = 10 + r.nextInt(50)
    disk_space = disk_space - r.nextInt(20 + r.nextInt(10))
    lat = lat + r.nextDouble
    long = long + r.nextDouble
  }

  private def infosToJson: JsObject = {
    Json.obj(
      "type" -> 0,
      "id_drone" -> id_drone,
      "battery" -> battery,
      "timestamp" -> timestamp,
      "altitude" -> altitude,
      "temperature" -> temperature,
      "speed" -> speed,
      "disk_space" -> disk_space,
      "Location" -> Json.obj(
        "lat" -> lat,
        "long" -> long
      ))
  }

  private def infractionToJson(s: Array[String]): JsObject = {
    Json.obj(
      "Summons_Number" -> s(0),
      "Plate_ID" -> s(1),
      "Issue_Date" -> s(2),
      "Violation_Code" -> s(3),
      "Vehicle_Make" -> s(4),
      "Street_Name" -> s(5),
      "Violation_County" -> s(6),
      "Violation_Time" -> s(7))
  }

  def infosMsg(): String = Json.stringify(this.infosToJson)

  def infractionMsg(array: Array[String]): String = {
    val json = this.infractionToJson(array)
    Json.stringify(Json.obj("type" -> 1) ++ json.as[JsObject])
  }

  def alertMsg(array: Array[String]): String = {
    val json = this.infractionToJson(array)
    Json.stringify(Json.obj("type" -> -1) ++ json.as[JsObject])
  }
}