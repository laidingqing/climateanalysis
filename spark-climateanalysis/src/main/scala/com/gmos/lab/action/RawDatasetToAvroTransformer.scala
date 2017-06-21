package com.gmos.lab.action

import org.apache.spark.sql.SQLContext

object RawDatasetToAvroTransformer {

  def transform(sqlContext: SQLContext) = {
    val df = sqlContext.read.parquet("/root/gmos/etl/gmos_raw.parquet")
    df.write.format("com.databricks.spark.avro").save("/root/gmos/etl/gmos_raw.avro")
  }
}
