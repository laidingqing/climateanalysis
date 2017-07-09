package com.gmos.lab.ml.regression

import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

class LinearRegression (val sc: SparkContext, val trainRdd: RDD[LabeledPoint],
                        val testRdd: RDD[LabeledPoint]){
  def execute() = {
    val lr = new org.apache.spark.ml.regression.LinearRegression().setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)
/*
    val lrModel = lr.fit(trainRdd)

    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

    val trainingSummary = lrModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: ${trainingSummary.objectiveHistory.toList}")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")
*/
  }
}
