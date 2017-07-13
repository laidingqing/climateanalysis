package com.gmos.lab.ml.regression

import com.gmos.lab.hbase.Utils
import com.gmos.lab.ml.evaluation.RegressionMetrics
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD

class DecisionTree (val sc: SparkContext, val trainRdd: RDD[LabeledPoint],
                        val testRdd: RDD[LabeledPoint]){
  def execute() = {

    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "variance"
    val maxDepth = 10
    val maxBins = 32

    val model = DecisionTree.trainRegressor(trainRdd, categoricalFeaturesInfo, impurity, maxDepth, maxBins)
    val labelsAndPredictions = testRdd.map { point =>
        val prediction = model.predict(point.features)
        (point.label, prediction)
    }
    val rm = new RegressionMetrics(labelsAndPredictions)
    println("Prediction Mean Squared Error = " + rm.meanSquaredError)

    val fs = FileSystem.newInstance(new Configuration())
    Utils.delete(new Path("/root/gmos/model/dt"), fs)
    model.save(sc, "/root/gmos/model/dt")

    val savedDTModel = DecisionTreeModel.load(sc, "/root/gmos/model/dt")
    val slabelsAndPredictions = testRdd.map { point =>
      val prediction = savedDTModel.predict(point.features)
      (point.label, prediction)
    }

    val srm = new RegressionMetrics(slabelsAndPredictions)
    println("Saved Model Prediction Mean Squared Error = " + srm.meanSquaredError)

  }
}
