// Sameer Motwani
// CS6240
// Section 01

The assignment has the following contents
--> Assignment Report (Report.pdf)
--> Deliverables for all page rank code is present inside src folder

--> Make-file : used to run code
----------
The make file in the code directory. The input data needs to be placed in
input folder. Following are the commands that can be used to run a makefile. The
make file needs to be configured according to the programming environment. Following
are the fields that need to be changed in make file

--> The programs pick the data from input folder inside the code folder. the makefile is configured to
    pick inputs from input folder
	
hadoop.root : stores the path of hadoop distribution
hdfs.user.name: <username of hdfs account>
hdfs.input: <input location in  hdfs>
hdfs.output: <output location of hdfs>
aws.bucket.name: <bucket name  in S3>
aws.subnet.id: <subnet-id in aws>
aws.input: <aws input>
aws.output: <aws output>
aws.num.nodes:<num nodes>
aws.instance.type=<type instance>

aws.log.dir: <Location of log directory>


For Example: In order to run the secondary Sort Program on local/emr, first set the job name to "pageRank" and jar.name to "prscala_2.11-0.1.jar" and set jar.path to "target/scala-2.11/${jar.name}"



Run the following command to execute the program on local:
make alone

Run the following command to execute the program on EMR.
make cloud

Each of the folder contains following contents

Page Rank
-----------
Output : contains outputs of running the program on emr
log contains controller.txt  stderr.txt files of two executions on AWS