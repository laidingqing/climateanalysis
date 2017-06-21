package com.gmos.lab.action

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext

object HiveTableAsParquetCreator {

  def transform(sqlContext: SQLContext) = {
    val hiveContext = new HiveContext(sqlContext.sparkContext)
    hiveContext.sql("create external table if not exists gmos_etl_external_hive (gstation string, gdate string, season string, year string, gtavg string, glatitude double, glongitude double, latitude_left bigint, latitude_right bigint, longitude_left bigint, longitude_right bigint) stored as parquet location '/root/gmos/etl/gmos_enrich.parquet'")
  }
}
