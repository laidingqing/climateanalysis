package com.gmos.lab.action

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object ReadGmosRaw {
  def action(sc: SparkContext):RDD[String] = {
    sc.textFile("/root/gmos/income")
  }
}
