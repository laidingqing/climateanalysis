package com.gmos.lab.streaming

import org.apache.spark.{SparkConf, SparkContext}
import com.redislabs.provider.redis._
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.StreamingContext;
import kafka.serializer.StringDecoder;
import org.apache.spark.rdd.RDD;
import org.apache.spark.streaming.Time;
import org.json.JSONObject;

object SparkStreamingETLStationGeo {
  def main(args: Array[String]): Unit ={
    val conf = new SparkConf().setAppName("Spark Straming GMOS Station GEO ETL ")
    conf.set("redis.host", "localhost")
    conf.set("redis.port", "6379")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(60))
    val brokers = "sandbox.hortonworks.com:6667"
    val topics = Set("gmos_stations")
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
    val stationStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)

    stationStream.foreachRDD {
      (message: RDD[(String, String)], batchTime: Time) => {
        message.cache()
        val counties:RDD[(String,String)] = message.map {
          x => {
            val json = new JSONObject(x._2)
            (json.getString("id"),json.getString("country"))
          }
        }
        sc.toRedisKV(counties)
        message.unpersist()
      }
    }
    ssc.start()
    ssc.awaitTermination()

  }

}
