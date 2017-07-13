package com.gmos.lab.ml.regression

import com.gmos.lab.hbase.Utils
import com.gmos.lab.ml.evaluation.RegressionMetrics
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.{RandomForestModel}
import org.apache.spark.rdd.RDD

class RandomForest (val sc: SparkContext, val trainRdd: RDD[LabeledPoint],
                        val testRdd: RDD[LabeledPoint]){
  def execute() = {

    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 5
    val featureSubsetStrategy = "auto"
    val impurity = "variance"
    val maxDepth = 10
    val maxBins = 32

    val model = RandomForest.trainRegressor(trainRdd, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

    val labelsAndPredictions = testRdd.map { point =>
        val prediction = model.predict(point.features)
        (point.label, prediction)
    }
    val rm = new RegressionMetrics(labelsAndPredictions)
    println("Prediction Mean Squared Error = " + rm.meanSquaredError)

    val fs = FileSystem.newInstance(new Configuration())
    Utils.delete(new Path("/root/gmos/model/rf"), fs)
    model.save(sc, "/root/gmos/model/rf")

    val savedRFModel = RandomForestModel.load(sc, "/root/gmos/model/rf")
    val slabelsAndPredictions = testRdd.map { point =>
      val prediction = savedRFModel.predict(point.features)
      (point.label, prediction)
    }

    val srm = new RegressionMetrics(slabelsAndPredictions)
    println("Saved Model Prediction Mean Squared Error = " + srm.meanSquaredError)

  }
}
