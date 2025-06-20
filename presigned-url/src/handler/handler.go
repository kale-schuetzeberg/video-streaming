package handler

import (
	"context"
	"encoding/json"
	"github.com/aws/aws-lambda-go/events"
	"log"
	"s3-presigned-url-generator/generator"
	"s3-presigned-url-generator/models"
)

func Handler(
	ctx context.Context,
	req events.APIGatewayProxyRequest,
) (events.APIGatewayProxyResponse, error) {
	return handler(ctx, req)
}

func handler(
	ctx context.Context,
	req events.APIGatewayProxyRequest,
) (events.APIGatewayProxyResponse, error) {
	bucket := req.QueryStringParameters["bucket"]
	key := req.QueryStringParameters["key"]

	if bucket == "" || key == "" {
		return apiResponse(400, map[string]string{"error": "bucket and key are required"})
	}

	s3Object := models.S3Object{
		Bucket: bucket,
		Key:    key,
	}

	result, err := generator.GeneratePresignedURL(ctx, req.HTTPMethod, s3Object)
	if err != nil {
		log.Printf("failed to generate URL: %v", err)
		return apiResponse(500, map[string]string{"error": err.Error()})
	}
	return apiResponse(200, result)
}

func apiResponse(status int, body interface{}) (events.APIGatewayProxyResponse, error) {
	jsonBody, err := json.Marshal(body)
	if err != nil {
		log.Printf("error marshaling result: %v (body: %#v)", err, body)
		return events.APIGatewayProxyResponse{
			StatusCode: 500,
			Headers:    map[string]string{"Content-Type": "application/json"},
			Body:       `{"error":"internal server error"}`,
		}, nil
	}
	return events.APIGatewayProxyResponse{
		StatusCode: status,
		Headers:    map[string]string{"Content-Type": "application/json"},
		Body:       string(jsonBody),
	}, nil
}
