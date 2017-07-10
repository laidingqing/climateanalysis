package com.gmos.lab.ml

import com.gmos.lab.ml.regression.{DecisionTreePL, LinearRegressionPL, LinearRegressionWithSGD}
import com.gmos.lab.ml.util.MLUtils
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object SparkMLTrainer {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark Machine Learning Batch Trainer")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    /* Linear Regression With SGD
    val rdd:RDD[LabeledPoint] = MLUtils.loadGmosParquetAsRDD(sqlContext, "/root/gmos/etl/gmos_enrich.parquet")
    rdd.cache()
    rdd.count()
    val lrWithSGD = new LinearRegressionWithSGD(sc, rdd, rdd)
    lrWithSGD.execute()
    */

    /* Linear Regression Using ML Pipeline
    val rdd = MLUtils.loadGmosParquetAsDF(sqlContext, "/root/gmos/etl/gmos_enrich.parquet")
    rdd.cache()
    val lrpl = new LinearRegressionPL(sqlContext, rdd, rdd)
    lrpl.execute()
    */

    // Decision Tree Regression Using ML Pipeline
    val rdd = MLUtils.loadGmosParquetAsDF(sqlContext, "/root/gmos/etl/gmos_enrich.parquet")
    rdd.cache()
    val dtpl = new DecisionTreePL(sqlContext, rdd, rdd)
    dtpl.execute()

  }

}
