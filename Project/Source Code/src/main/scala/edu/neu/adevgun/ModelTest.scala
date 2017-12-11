package edu.neu.adevgun

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.{SparkConf, SparkContext}

object ModelTest {

  def main(args: Array[String]) {

    // create Spark context with Spark configuration.
    val sc = new SparkContext(new SparkConf().setAppName("Model Tester"))

    val model = RandomForestModel.load(sc, args(1)+"/model")

    //Input training data
    val testInput = sc.textFile(args(0)).map(x => x.split(","))

    //Converted the input file to libsvm format
    val testData = testInput.map(row => new LabeledPoint(0,
      Vectors.dense(row.take(row.length - 1).map(str => str.toDouble))))

    // Evaluate model on test instances and compute test error
    val score = testData.map { point =>
      model.predict(point.features).toInt
    }

    score.coalesce(1).saveAsTextFile(args(2))
  }
}
