package main

import (
	"github.com/aws/aws-lambda-go/lambda"
	"s3-presigned-url-generator/handler"
)

func main() {
	lambda.Start(handler.Handler)
}
