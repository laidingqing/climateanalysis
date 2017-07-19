package com.gmos.lab.ml.redis

import org.apache.spark.ml.regression.{RandomForestRegressionModel}
import redis.clients.jedis.Protocol.Command
import redis.clients.jedis.{Jedis, _}
import org.apache.spark.ml.tree.{CategoricalSplit, ContinuousSplit, InternalNode, Node}

class RandomForest(model: RandomForestRegressionModel) {

  val trees = model.trees

  private def subtreeToRedisString(n: Node, path: String = "."): String = {
    val prefix: String = s" ${path} "
    n.getClass.getSimpleName match {
      case "InternalNode" => {
        val in = n.asInstanceOf[InternalNode]
        val splitStr = in.split match {
          case contSplit: ContinuousSplit => s"numeric ${in.split.featureIndex} ${contSplit.threshold}"
          case catSplit: CategoricalSplit => s"categoric,${in.split.featureIndex}," +
            catSplit.leftCategories.mkString(":")
        }
        prefix + splitStr + subtreeToRedisString(in.leftChild, path + "l") +
          subtreeToRedisString(in.rightChild, path + "r")
      }
      case "LeafNode" => {
        prefix + s"leaf ${n.prediction + 100}" // it seems redis ml server has the issue to deal with the leaf with negative double, so add 100 to work around it for the time being
      }
    }
  }

  private def toRedisString: String = {
    trees.zipWithIndex.map { case (tree, treeIndex) =>
      s"${treeIndex}" + subtreeToRedisString(tree.rootNode, ".")
    }.fold("") { (a, b) => a + "\n" + b }
  }

  def toDebugArray: Array[String] = {
    toRedisString.split("\n").drop(1)
  }

  def loadToRedis(forestId: String = "rfregression", host: String = "localhost") {
    val jedis = new Jedis(host)
    //val pipelined = jedis.pipelined()
    val commands = toRedisString.split("\n").drop(1)
    println(toRedisString)
    jedis.getClient().sendCommand(Command.MULTI)
    jedis.getClient().getStatusCodeReply
    for (cmd <- commands) {
      val cmdArray = forestId +: cmd.split(" ")
      jedis.getClient().sendCommand(Utils.ModuleCommand.FOREST_ADD, cmdArray: _*)
      println("status code reply: " + jedis.getClient().getStatusCodeReply)
    }
    try {
      //pipelined.sync()
      jedis.getClient.sendCommand(Command.EXEC)
      jedis.getClient.getMultiBulkReply
    }catch{
      case e: Exception => e.printStackTrace()
    }
  }

  def predict(forestId: String = "rfregression", host: String = "localhost", input: String) {
    val jedis = new Jedis(host)
    jedis.getClient.sendCommand(Utils.ModuleCommand.FOREST_RUN, forestId, input, "REGRESSION")
    val redisRes = jedis.getClient().getStatusCodeReply
    println("Predict Score: " + (redisRes.toDouble - 100))
  }
}
