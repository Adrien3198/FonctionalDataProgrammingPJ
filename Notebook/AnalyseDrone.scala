// Databricks notebook source
dbutils.fs.unmount(s"/mnt/s3datawork")
val AccessKey = "XXXXXXXXXXXXXXX"
val SecretKey = "XXXXXXXXXXXXXX"
val EncodedSecretKey = SecretKey.replace("/", "%2F")
val AwsBucketName = "ices3bucket"
val MountName = "s3datawork"

dbutils.fs.mount(s"s3a://$AccessKey:$EncodedSecretKey@$AwsBucketName", s"/mnt/$MountName")
display(dbutils.fs.ls(s"/mnt/$MountName"))

// COMMAND ----------

// COMMAND ----------

object ViolationUtils {

  case class ViolMessage(
                          Summons_Number: String,
                          Plate_ID: String,
                          Issue_Date: String,
                          Violation_Code: String,
                          Vehicle_Make: String,
                          Street_Name: String,
                          Violation_County: String
                        )

  def parseFromLine(line: Array[String]): ViolMessage = {
    ViolMessage(
      Summons_Number = line(0), Plate_ID = line(1), Issue_Date = line(2), Violation_Code = line(3), Vehicle_Make = line(4), Street_Name = line(5), Violation_County = line(6)
    )
  }
}


// COMMAND ----------

// f=>{f.split(",")}
def loadData(): RDD[ViolationUtils.ViolMessage] = {
  val rdd = spark.sparkContext.textFile(s"/mnt/$MountName/ViolationData/*.csv")
  rdd.map(_.split(",")).map(ViolationUtils.parseFromLine(_))
}

// COMMAND ----------

// Top 5 of Infraction by County
loadData.map(msg => (msg.Violation_County, 1)).reduceByKey(_ + _).top(5)(Ordering.by[(String, Int), Int](t => t._2)).foreach(println)

// COMMAND ----------

// Top 10 of Infraction by Brand Car
loadData.map(msg => (msg.Vehicle_Make, 1)).reduceByKey(_ + _).top(10)(Ordering.by[(String, Int), Int](t => t._2)).foreach(println)


// COMMAND ----------

// Top 10 of Infraction by Plate ID
loadData.map(msg => (msg.Plate_ID, 1)).reduceByKey(_ + _).top(10)(Ordering.by[(String, Int), Int](t => t._2)).foreach(println)

// COMMAND ----------

// Number of Infraction by Month
loadData.map(msg => msg.Issue_Date.split("/")(0)).map(month => (month, 1)).reduceByKey(_ + _).top(12)(Ordering.by[(String, Int), Int](t => t._2)).foreach(println)

// COMMAND ----------

// Most Violation Code 
loadData.map(msg => (msg.Violation_Code, 1)).reduceByKey(_ + _).top(10)(Ordering.by[(String, Int), Int](t => t._2)).foreach(println)

// COMMAND ----------

// Most assigned violation code per month 
loadData.map(msg => ((msg.Issue_Date.split("/")(0), msg.Violation_Code), 1)).reduceByKey(_ + _).top(10)(Ordering.by[((String, String), Int), Int](t => t._2)).foreach(println)

// COMMAND ----------


