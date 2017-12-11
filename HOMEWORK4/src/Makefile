# Makefile for Spark Page Rank project.

# Customize these paths for your environment.
# -----------------------------------------------------------
hadoop.root=/home/sameer/spark
jar.name=prscala_2.11-0.1.jar
jar.path=target/scala-2.11/${jar.name}
job.name=pageRank
local.input=input
local.output=op
# Pseudo-Cluster Execution
hdfs.user.name=root
hdfs.input=input1
hdfs.output=output3
# AWS EMR Execution
aws.emr.release=emr-5.8.0
aws.region=us-east-1
aws.bucket.name=sameermapreduce
aws.subnet.id=subnet-4d55ae72
aws.input=input
aws.output=output
aws.log.dir=log
aws.num.nodes=10
aws.instance.type=m4.large
# -----------------------------------------------------------

# Compiles code and builds jar (with dependencies).
jar:
	sbt package

# Removes local output directory.
clean-local-output:
	rm -rf ${local.output}*

# Runs standalone

alone: jar
	${spark.root}/bin/spark-submit --class ${job.name} --master local[*] ${pagerank.jar.path} ${local.input} ${local.output}


# Create S3 bucket.
make-bucket:
	aws s3 mb s3://${aws.bucket.name}

# Upload data to S3 input dir.
upload-input-aws: make-bucket
	aws s3 sync ${local.input} s3://${aws.bucket.name}/${aws.input}
	
# Delete S3 output dir.
delete-output-aws:
	aws s3 rm s3://${aws.bucket.name}/ --recursive --exclude "" --include "${aws.output}"

# Upload application to S3 bucket.
upload-app-aws:
	aws s3 cp ${jar.path} s3://${aws.bucket.name}

# Main EMR launch.
cloud: 
	aws emr create-cluster \
		--name "Page Rank Spark-11" \
		--release-label ${aws.emr.release} \
		--instance-groups '[{"InstanceCount":${aws.num.nodes},"InstanceGroupType":"CORE","InstanceType":"${aws.instance.type}"},{"InstanceCount":1,"InstanceGroupType":"MASTER","InstanceType":"${aws.instance.type}"}]' \
	    --applications Name=Spark \
	    --steps '[{"Name":"Spark Program","Args":["--class", "${job.name}", "--master", "yarn", "--deploy-mode", "cluster", "s3://${aws.bucket.name}/pagerank/${jar.name}", "s3://${aws.bucket.name}/${aws.input}","s3://${aws.bucket.name}/${aws.output}"],"Type":"Spark","Jar":"s3://${aws.bucket.name}/pagerank/${jar.name}","ActionOnFailure":"TERMINATE_CLUSTER"}]' \
		--log-uri s3://${aws.bucket.name}/${aws.log.dir} \
		--service-role EMR_DefaultRole \
		--ec2-attributes InstanceProfile=EMR_EC2_DefaultRole,SubnetId=${aws.subnet.id} \
		--configurations '[{"Classification":"spark", "Properties":{"maximizeResourceAllocation": "true"}}]' \
		--region ${aws.region} \
		--enable-debugging \
		--auto-terminate


# Download output from S3.
download-output-aws: clean-local-output
	mkdir ${local.output}
	aws s3 sync s3://${aws.bucket.name}/${aws.output} ${local.output}
