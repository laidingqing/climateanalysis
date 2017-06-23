package com.gmos.lab.elk

import org.apache.spark.{SparkConf, SparkContext}
import com.redislabs.provider.redis._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode
import org.elasticsearch.spark.sql._

object SparkETLToELK {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark GMOS ETL To ELK")
    conf.set("redis.host", "localhost")
    conf.set("redis.port", "6379")
    val esConfig = Map("pushdown" -> "true", "es.nodes" -> "sandbox.hortonworks.com", "es.port" -> "9200")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._
    val stationRDD:RDD[(String,String)] = sc.fromRedisKV("*")
    val dfredis = stationRDD.toDF("station","country")
    dfredis.registerTempTable("gmos_lookup")
    val dfparquet = sqlContext.read.parquet("/root/gmos/etl/gmos_enrich.parquet")
    dfparquet.filter(dfparquet("gtavg").isNotNull).registerTempTable("gmos_enrich")
    sqlContext.udf.register("toString", (d: Double) => String.valueOf(d))
    sqlContext.udf.register("toDouble", (s: String) => s.toDouble)
    val df = sqlContext.sql("select a.gstation as station, b.country, a.gdate as date, a.year, a.season, toString(a.glatitude) as latitude, toString(a.glongitude) as longitude, toDouble(a.gtavg) as tavg  from gmos_enrich a left join gmos_lookup b on a.gstation = b.station where length(a.gtavg) > 0 and a.glatitude is not null and a.glongitude is not null")
    df.write.format("org.elasticsearch.spark.sql").mode(SaveMode.Overwrite).options(esConfig).save("sparkgmos/gmos_metrics")
  }
}
