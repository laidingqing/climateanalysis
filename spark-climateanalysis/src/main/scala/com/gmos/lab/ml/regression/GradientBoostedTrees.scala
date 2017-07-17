package com.gmos.lab.ml.regression

import com.gmos.lab.hbase.Utils
import com.gmos.lab.ml.evaluation.RegressionMetrics
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.GradientBoostedTrees
import org.apache.spark.mllib.tree.configuration.BoostingStrategy
import org.apache.spark.mllib.tree.model.{GradientBoostedTreesModel}
import org.apache.spark.rdd.RDD

class GradientBoostedTrees (val sc: SparkContext, val trainRdd: RDD[LabeledPoint],
                        val testRdd: RDD[LabeledPoint]){
  def execute() = {

    val boostingStrategy = BoostingStrategy.defaultParams("Regression")
    boostingStrategy.setNumIterations(3)
    boostingStrategy.treeStrategy.setMaxDepth(5)
    boostingStrategy.treeStrategy.setMaxBins(32)

    val model = GradientBoostedTrees.train(trainRdd, boostingStrategy)

    val labelsAndPredictions = testRdd.map { point =>
        val prediction = model.predict(point.features)
        (point.label, prediction)
    }

    val rm = new RegressionMetrics(labelsAndPredictions)
    println("Prediction Mean Squared Error = " + rm.meanSquaredError)

    val fs = FileSystem.newInstance(new Configuration())
    Utils.delete(new Path("/root/gmos/model/gbt"), fs)
    model.save(sc, "/root/gmos/model/gbt")

    val savedGBTModel = GradientBoostedTreesModel.load(sc, "/root/gmos/model/gbt")
    val slabelsAndPredictions = testRdd.map { point =>
      val prediction = savedGBTModel.predict(point.features)
      (point.label, prediction)
    }

    val srm = new RegressionMetrics(slabelsAndPredictions)
    println("Saved Model Prediction Mean Squared Error = " + srm.meanSquaredError)

  }
}
