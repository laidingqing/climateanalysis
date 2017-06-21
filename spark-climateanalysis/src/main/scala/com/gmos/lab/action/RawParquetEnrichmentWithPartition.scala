package com.gmos.lab.action

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DoubleType

object RawParquetEnrichmentWithPartition {
  def getSeason(date: String) : String = {
    val month = date.substring(5, 7);
    month match {
      case "03"|"04"|"05" => "spring"
      case "06"|"07"|"08" => "summer"
      case "09"|"10"|"11" => "fall"
      case "12"|"01"|"02" => "winter"
      case _:String => "unknown"
    }
  }

  def transform(sqlContext: SQLContext) = {
    val convertToSeason = udf(getSeason(_:String))
    val df = sqlContext.read.parquet("/root/gmos/etl/gmos_raw.parquet")

    // Convert column station, latitude and longitude to lowercase in order for mapping hive metadata
    // Create new columns year from date, season from date
    val df1 = df.withColumn("gstation", df("STATION")).withColumn("year", substring(df("DATE"),1,4)).withColumn("season", convertToSeason(df("DATE"))).withColumn("glatitude", df("LATITUDE").cast(DoubleType)).withColumn("glongitude", df("LONGITUDE").cast(DoubleType))

    // Convert column tavg (average temperature) to lowercase in order for mapping hive metadata
    // Rename column date to gdate for hive metadata
    // Create new columns latitude_left, latitude_right, longitude_left, logitude_right from longitude and latitude (grid 1 X 1)
    val df2 = df1.withColumn("gtavg", df1("TAVG")).withColumn("gdate", df1("DATE")).withColumn("latitude_left", floor(df1("glatitude"))).withColumn("latitude_right", floor(df1("glatitude"))+1).withColumn("longitude_left", floor(df1("glongitude"))).withColumn("longitude_right", floor(df1("glongitude"))+1)

    df2.write.partitionBy("year", "season").format("parquet").mode(org.apache.spark.sql.SaveMode.Append).save("/root/gmos/etl/gmos_enrich_partition.parquet")
  }

}
