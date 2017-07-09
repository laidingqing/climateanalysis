package com.gmos.lab.ml.evaluation

import org.apache.spark.Logging
import org.apache.spark.rdd.RDD

class RegressionMetrics(valuesAndPreds: RDD[(Double, Double)]) extends Logging{
  def meanSquaredError: Double = {
    valuesAndPreds.map{case(v, p) => math.pow((v - p), 2)}.mean()
  }
}
