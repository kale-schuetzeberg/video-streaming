set -u
: "$CONTAINER_REGISTRY"
: "$VERSION"

docker build -t $CONTAINER_REGISTRY/azure-storage:$VERSION --file ./Dockerfile-prod ./azure-storage