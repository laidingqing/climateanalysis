package com.gmos.lab.action

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._

object FullSchemaGenerator {

  // Full schema will be collected at driver side and return as schema as struct, schema as array and schema as map
  // About 180 data type in full schema
  def createSchema(rdd: RDD[String]) = {
    // Tuple as (schema data type, number of times)
    // (AWND,1148), (AWND_ATTRIBUTES,1148), (CDSD,29760), (CDSD_ATTRIBUTES,29760), (CLDD,32293)
    val csvHeaders:Array[(String, Int)] = rdd.filter(_ contains("\"STATION\",\"DATE\"")).flatMap(x => x.substring(x.indexOf("\"STATION\",\"DATE\"")+1,x.length-1).split("\",\"")).map((_,1)).reduceByKey(_ + _).sortBy(_._1, true).collect

    // Full Schema as StructType
    //  StructType(StructField(AWND,StringType,true), StructField(AWND_ATTRIBUTES,StringType,true)
    val schemaAsStruct: StructType = StructType(csvHeaders.map(x => StructField(x._1, StringType, true)))

    // Full Schema as Array
    // Array(AWND, AWND_ATTRIBUTES, CDSD, CDSD_ATTRIBUTES, CLDD, CLDD_ATTRIBUTES, DATE, DP01, DP01_ATTRIBUTES
    val schemaAsArray:Array[String] = csvHeaders.map(_._1)

    // Full Schema as Map
    // Map(ELEVATION -> 27, MX07 -> 138, MN07 -> 122, DT00 -> 17, DX90_ATTRIBUTES -> 26
    // Schema Data Type and Index in the Full Schema (alphabetical order)
    val schemaAsMap:scala.collection.immutable.Map[String,Int] = schemaAsArray.zipWithIndex.map(t => (t._1, t._2)).toMap

    (schemaAsStruct, schemaAsArray, schemaAsMap)
  }

}
