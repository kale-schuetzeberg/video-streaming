set -u
: "$CONTAINER_REGISTRY"
: "$VERSION"

docker build -t $CONTAINER_REGISTRY/video-storage:$VERSION --file ./Dockerfile-prod ./azure-storage