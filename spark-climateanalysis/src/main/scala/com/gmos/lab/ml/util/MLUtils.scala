package com.gmos.lab.ml.util

import com.gmos.lab.ml.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{Logging}
import org.apache.spark.mllib.linalg.Vector

object MLUtils extends Logging {

  def loadGmosParquetAsDF(sqlContext: SQLContext, path: String): RDD[(Double, Double, Double, Double, Double)] = {
    val parsed = parseParquetFile(sqlContext, path)
    //parsed.repartition(200).map { case (label, values) => (label, Vectors.dense(values)) }
    parsed.repartition(200).map { case (label, values) => (label, values(0), values(1), values(2), values(3)) }
  }

  def loadGmosParquetAsRDD(sqlContext: SQLContext, path: String): RDD[LabeledPoint] = {
    val parsed = parseParquetFile(sqlContext, path)
    val rdd: RDD[LabeledPoint] = parsed.map { case (label, values) => LabeledPoint(label, Vectors.dense(values)) }
    val scalesRdd = StandardScaler.transform(rdd)
    scalesRdd.repartition(200)
  }

  private[gmos] def parseParquetFile(sqlContext: SQLContext, path: String): RDD[(Double, Array[Double])] = {
    val df = sqlContext.read.parquet(path)
    val dfparquet = df.filter(df("gtavg").isNotNull.and(df("glatitude").isNotNull).and(df("glongitude").isNotNull))
    dfparquet.registerTempTable("gmos_enrich")
    sqlContext.udf.register("toDouble", (s: String) => s.toDouble)
//    val dfgroup = sqlContext.sql("select year, sum(toDouble(gtavg)) / count(*) as tavg from gmos_enrich where season = 'summer' and length(gtavg) > 0 and glatitude is not null and glongitude is not null group by year")
    val dfquery = sqlContext.sql("select glatitude as latitude, glongitude as longitude, year, gdate as date, toDouble(gtavg) as tavg from gmos_enrich where length(gtavg) > 0 and glatitude is not null and glongitude is not null")
    dfquery.rdd.map(parseRowRecord)
  }

  private[gmos] def filterRowRecord(row: Row): Boolean = {
    row.getString(row.fieldIndex("gtavg")).trim.length > 0 && row.getDouble(row.fieldIndex("glatitude")) != null && row.getDouble(row.fieldIndex("glongitude")) != null && row.getDouble(row.fieldIndex("glatitude")) == 17.11667 && row.getDouble(row.fieldIndex("glongitude")) == -61.78333
  }

  private[gmos] def parseRowRecord(row: Row): (Double, Array[Double]) = {
    val year = row.getString(row.fieldIndex("year")).toDouble
    val month = row.getString(row.fieldIndex("date")).substring(5, 7).toDouble
    val tavg = row.getDouble(row.fieldIndex("tavg"))
    val latitude = row.getDouble(row.fieldIndex("latitude"))
    val longitude = row.getDouble(row.fieldIndex("longitude"))
    (tavg, Array(latitude, longitude, year, month))
  }

}
