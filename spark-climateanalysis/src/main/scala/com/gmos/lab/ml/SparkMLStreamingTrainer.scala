package com.gmos.lab.ml

import org.apache.spark.{SparkConf, SparkContext}

object SparkMLStreamingTrainer {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark Machine Learning Streaming Trainer")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
  }

}
