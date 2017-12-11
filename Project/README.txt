PREREQUISITES

Prior to executing this program, you need to configure your AWS-CLI. If you have not already done so please follow the instructions mentioned in below document (Author of the document is Joe) - 

https://docs.google.com/document/d/1-UjNVFasTSzhAaqLtKSmeie6JZMinhtVEqCEZwUkxeE/edit




STEPS
To execute the various jobs onto the AWS, you would need to modify the top parameters of the included makefile.

For Local Execution
hadoop.root  => Modify this to the path where hadoop is installed on your machine.
local.input  => Path where training file(s) are to be placed.
local.test   => Path where model is validated
local.output => Path where output model file(s) will be generated.

For AWS Execution
aws.training      => Path where training file(s) are to be placed.
aws.validation    => Path where model is validated
aws.output        => Path where output model file(s) will be generated.
aws.num.nodes     => Number of child machines the program should be executed on.
aws.instance.type => Instance type of the AWS machines

For both of the executions above you would need to change the 'job.name' parameter to the program that you want to execute. Below are the valid options for this parameter

#To execute the program
job.name=edu.neu.adevgun.RandomForestTrain
job.name=edu.neu.adevgun.SVMTrain
job.name=edu.neu.adevgun.ModelTest




