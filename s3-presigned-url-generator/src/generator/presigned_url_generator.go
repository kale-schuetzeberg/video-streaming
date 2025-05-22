package generator

import (
	"context"
	"fmt"
	"s3-presigned-url-generator/models"
	"time"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

func GeneratePresignedURL(
	ctx context.Context,
	httpMethod string,
) (models.PresignResult, error) {
	cfg, err := config.LoadDefaultConfig(ctx)
	if err != nil {
		return models.PresignResult{}, fmt.Errorf("load AWS config: %w", err)
	}

	s3Client := s3.NewFromConfig(cfg)
	presignClient := s3.NewPresignClient(s3Client)

	bucket := "video-streaming-sample-videos"
	key := "SampleVideo_1280x720_1mb.mp4"
	expirySeconds := int64(600)

	expires := time.Now().Add(time.Duration(expirySeconds) * time.Second)
	var url string

	switch httpMethod {
	case "GET":
		out, err := presignClient.PresignGetObject(
			ctx,
			&s3.GetObjectInput{Bucket: &bucket, Key: &key},
			s3.WithPresignExpires(time.Duration(expirySeconds)*time.Second),
		)
		if err != nil {
			return models.PresignResult{}, err
		}
		url = out.URL

	case "PUT":
		out, err := presignClient.PresignPutObject(
			ctx,
			&s3.PutObjectInput{Bucket: &bucket, Key: &key},
			s3.WithPresignExpires(time.Duration(expirySeconds)*time.Second),
		)
		if err != nil {
			return models.PresignResult{}, err
		}
		url = out.URL

	case "DELETE":
		out, err := presignClient.PresignDeleteObject(
			ctx,
			&s3.DeleteObjectInput{Bucket: &bucket, Key: &key},
			s3.WithPresignExpires(time.Duration(expirySeconds)*time.Second),
		)
		if err != nil {
			return models.PresignResult{}, err
		}
		url = out.URL

	default:
		return models.PresignResult{}, fmt.Errorf("unsupported method %q", httpMethod)
	}

	return models.PresignResult{
		URL:     url,
		Expires: expires,
		Method:  httpMethod,
	}, nil
}
