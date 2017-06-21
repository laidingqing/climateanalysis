package com.gmos.lab.action

import org.apache.spark.sql.SQLContext

object StationGeoMapper {
  def action(sqlContext: SQLContext) = {
    val df = sqlContext.read.parquet("/root/gmos/etl/gmos_enrich.parquet")
    val df1 = df.select(df("gstation"), df("glatitude"), df("glongitude")).distinct()

    val prop = new java.util.Properties
    prop.setProperty("driver", "com.mysql.jdbc.Driver")
    prop.setProperty("user", "root")
    prop.setProperty("password", "87552790")

    val url = "jdbc:mysql://localhost:3306/test"
    val table = "station"

    df1.write.mode("overwrite").jdbc(url, table, prop)

  }

}
