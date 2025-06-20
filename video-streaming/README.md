# video-streaming

## Purpose
The video streaming service keeps a record of the videos stored in s3.
The video streaming service sends request for individual videos to the presigned url generator.
The video streaming service streams videos returned by the presigned url generator. 

## Endpoints
### /videos
Returns a list of all the videos in the database
### /video
Returns a stream of s3 presigned url

## Dataflow
1. A user makes a request to /videos to get a list of the videos from a database detailing the videos stored in the S3 bucket.
2. A user makes a request to /video which retreives the requied info from the databse about what video to retrieve from s3 and then a presigned-url with info stored in the to retrieve a presigend S3 READ url.
3. The url is streamed

## 
