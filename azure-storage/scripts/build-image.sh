set -u
: "$CONTAINER_REGISTRY"
: "$VERSION"

docker build -t $CONTAINER_REGISTRY/azure-storage:$VERSION --file ./azure-storage/Dockerfile-prod ./azure-storage