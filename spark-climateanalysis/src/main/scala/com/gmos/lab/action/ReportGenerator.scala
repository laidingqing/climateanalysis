package com.gmos.lab.action

import org.apache.spark.sql.SQLContext

object ReportGenerator {
  def action(sqlContext: SQLContext) = {
    sqlContext.udf.register("toDouble", (s: String) => s.toDouble)
    val df = sqlContext.read.parquet("/root/gmos/etl/gmos_enrich.parquet")
    df.registerTempTable("gmos_parquet")
    // Some data of dataset is bad, will be skipped
    // Such as "NOE00105477","1937-01",,,,,"","","","","15",
    // Latitude is missing
    val results = sqlContext.sql("SELECT year, latitude_left, latitude_right, longitude_left, longitude_right, season, sum(case when length(gtavg) = 0 then 0.0 else toDouble(gtavg) end)/sum(case when length(gtavg) = 0 then 0 else 1 end) as average_tempeture, sum(case when length(gtavg) = 0 then 0 else 1 end) as available_tavg FROM gmos_parquet WHERE glatitude is not null group by year, latitude_left, latitude_right, longitude_left, longitude_right, season")
    results.write.parquet("/root/gmos/etl/report.parquet")
  }
}
