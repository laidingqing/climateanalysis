package com.gmos.lab.action

import org.apache.spark.rdd.RDD
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext

object RawDatasetToParquetTransformer {

  def transform(sqlContext: SQLContext, rdd: RDD[String], broadcastVar: Broadcast[scala.collection.Map[String,Map[Int,Int]]],
                fullschemaVar:Broadcast[Array[String]], fullschema: StructType) = {

    val rdd1 = rdd.filter(x => x.nonEmpty && x.contains("\",\"") && !(x contains("\"STATION\",\"DATE\""))).map {line =>
      val extracted = line.trim.substring(1,line.length-1).split("\",\"");
      val schema = broadcastVar.value.get(extracted(0).trim).get;
      val valuesmap = extracted.zipWithIndex.map(t => (schema.get(t._2).get, t._1)).toMap;
      val values = fullschemaVar.value.zipWithIndex.map { t =>
        valuesmap.get(t._2) match {
          case Some(i) => i
          case None => ""
        }
      };
      Row.fromSeq(values.toSeq)
    }

    val df = sqlContext.createDataFrame(rdd1, fullschema)
    df.write.parquet("/root/gmos/etl/gmos_raw.parquet")

  }

}
