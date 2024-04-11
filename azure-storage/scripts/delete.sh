set -u
: "$CONTAINER_REGISTRY"
: "$VERSION"

envsubst < ./azure-storage/scripts/azure-storage-prod.yaml | kubectl delete -f -