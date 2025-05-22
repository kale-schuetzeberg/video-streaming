package main

import (
	"github.com/aws/aws-lambda-go/lambda"
	"s3-presigned-url-generator/controller"
)

func main() {
	lambda.Start(controller.Handler)
}
