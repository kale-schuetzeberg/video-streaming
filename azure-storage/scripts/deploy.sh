set -u
: "$CONTAINER_REGISTRY"
: "$VERSION"

envsubst < ./azure-storage/scripts/azure-storage-deploy-prod.yaml | kubectl apply -f -