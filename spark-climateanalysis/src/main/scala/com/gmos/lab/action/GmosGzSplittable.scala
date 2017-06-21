package com.gmos.lab.action

import org.apache.spark.SparkContext

object GmosGzSplittable {
  def transform(sc: SparkContext) = {
    val rdd = sc.textFile("/root/gsom-latest.tar.gz")
    rdd.saveAsTextFile("/root/gmos/income")
  }
}
