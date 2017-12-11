import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

object pageRank {
  def main(args: Array[String]) {
    val alpha = 0.15
    val remaining = 1 - alpha
    val conf = new SparkConf().setAppName("pageRank").setMaster("local[*]")
      //setMaster("yarn")
    val sc = new SparkContext(conf)


    // reading path of input file through command line
    val adjList = sc.textFile(args(0)).
      map(line => Bz2WikiParser.lineGenerator(line)) //line by line reading from input
      .filter(line => line != null)
      .map(line => line.split("@@@")) //splitting based on @@@
      .map(line => (line(0),
      if (line.size > 1) line(1).split(", ").toList else List[String]()))
      .map(line => List((line._1, line._2)) ++ line._2
        .map(danglingNode => (danglingNode, List[String]()))) // Adding missing dangling nodes
      .flatMap(line => line)
      .reduceByKey((x, y) => (x ++ y))
      .persist()


    //calculating total page count
    val pc = adjList.count()
    //initializing default page rank for each node . i.e 1/pageCount
    val pageRanksInit = adjList.map(node => (node._1, 1.0 / pc))

    var allInput = adjList.join(pageRanksInit)

    for (itr <- 1 to 10)
    {
      //logic to compute dangling factor
      val dfactor = allInput.filter(line => line._2._1.length == 0)
        .reduce((x, y) => (x._1, (x._2._1, x._2._2 + y._2._2)))._2._2

      // calculating page rank contribution to each node
      val pr = allInput.values
        .map(nodes => (nodes._1.map(line => (line, nodes._2 / nodes._1.size))))
        .flatMap(line => line)
        .reduceByKey((a, b) => a + b)

      //adding delta factors to the nodes using left join
      allInput = adjList.leftOuterJoin(pr)
          .map(node1 => {
          (node1._1, (node1._2._1, node1._2._2 match {
            case None => (alpha / pc) + ((remaining) * dfactor / pc)
            case Some(x: Double) => (alpha / pc) + (remaining * ((dfactor / pc) + x))
          }))
        })
    }


    //calculating top 100 pages
    val result = allInput.map(x => {(x._2._2, x._1)})
      .top(100)


    //the output is written to txt file
    sc.parallelize(result, 1).saveAsTextFile(args(1))

  }
}
