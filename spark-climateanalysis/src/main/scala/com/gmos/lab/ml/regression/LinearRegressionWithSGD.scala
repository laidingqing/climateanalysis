package com.gmos.lab.ml.regression

import com.gmos.lab.hbase.Utils
import com.gmos.lab.ml.evaluation.RegressionMetrics
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.{LabeledPoint}
import org.apache.spark.rdd.RDD

class LinearRegressionWithSGD (val sc: SparkContext, val trainRdd: RDD[LabeledPoint],
                               val testRdd: RDD[LabeledPoint]){

  def execute() = {
    val numIterations = 1000
    var regression = new org.apache.spark.mllib.regression.LinearRegressionWithSGD().setIntercept(true)
    regression.optimizer.setStepSize(1.5)
    regression.optimizer.setNumIterations(numIterations)
    val model = regression.run(trainRdd)
    val valuesAndPreds = testRdd.map { point =>
      val prediction = model.predict(point.features)
      (point.label, BigDecimal(prediction).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)
    }
    val fs = FileSystem.newInstance(new Configuration())
    Utils.delete(new Path("/root/gmos/model/lrwithsgd"), fs)
    model.save(sc, "/root/gmos/model/lrwithsgd")
    val rm = new RegressionMetrics(valuesAndPreds)
    println("training Mean Squared Error = " + rm.meanSquaredError)
  }

}
