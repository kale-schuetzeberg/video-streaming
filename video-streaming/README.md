
## Docker
### Build Image
docker build -f Dockerfile-dev -t video-streaming:0.0.1 .

### Remove Image
docker rmi $(docker images | grep "0.0.1" | grep "video-streaming" | awk '{print $3}')

### Run container
docker run -t \
-e PORT='80' \
-e DBNAME='video_library' \
-e COLLECTION_NAME='videos' \
-e VIDEO_STORAGE_HOST='aws-simple-storage-service' \
-e VIDEO_STORAGE_PORT='80' \
--name aws-s3-container -p 4003:80 video-streaming:0.0.1

### Stop and Remove Container
docker stop $(docker ps | grep "video-streaming" | awk '{print $1}') && \
docker rm $(docker ps -a | grep "video-streaming" | awk '{print $1}')

## Kubernetes
