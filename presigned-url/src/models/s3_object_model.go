package models

type S3Object struct {
	Bucket string `json:"bucket"`
	Key    string `json:"key"`
}
