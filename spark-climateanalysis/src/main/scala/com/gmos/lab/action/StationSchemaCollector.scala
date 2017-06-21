package com.gmos.lab.action

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.StructType

object StationSchemaCollector {

  def createSchema(sc:SparkContext, rdd: RDD[String], st: (StructType, Array[String], scala.collection.immutable.Map[String,Int])): scala.collection.Map[String, Map[Int,Int]] = {

    val fullschemaAsMapVar = sc.broadcast(st._3)

    // Create RDD with each schema map per station, key is station, value is schema map for station
    val schemaPerStation: RDD[(String, Map[Int,Int])]  = rdd.filter(_ contains("\"STATION\",\"DATE\"")).map {line =>
      (line.substring(0,line.indexOf(".csv")).trim, line.substring(line.indexOf("\"STATION\",\"DATE\"")+1,line.length-1).split("\",\"").zipWithIndex.map(t => (t._2, fullschemaAsMapVar.value.get(t._1).get)).toMap)
    }

    // Create schema map of map at the driver side, key is station, value is schema map for station which key is the index in
    // station schema map and value is the index in full schema map
    // Map(ASN00040762 -> Map(0 -> 149, 5 -> 142, 10 -> 11
    val schemaMapOfMap: scala.collection.Map[String, Map[Int,Int]] = schemaPerStation.collectAsMap

    schemaMapOfMap

  }
}
