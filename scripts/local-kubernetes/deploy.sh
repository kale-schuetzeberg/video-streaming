#!/bin/bash

function build_image() {
  local image_name=$1
  local image_tag=$2
  local image_path=$3

  echo "Deploying $image_name"
  docker build -t $image_name:$image_tag --file $image_path
  BUILD_PID=$!

  # Wait for the build process to finish
  wait $BUILD_PID

  if [ $? -ne 0 ]; then
    echo "Error: Docker build failed for $image_name"
    exit 1
  fi
}

function deploy_kubernetes_resource() {
    local kubernetes_deploymnet_yaml=$1

    echo "Deploying $1"
    kubectl apply -f $kubernetes_deploymnet_yaml
}

build_image "aws-simple-storage-service" "0.0.1" "../../aws-simple-storage-service/Dockerfile-dev ../../aws-simple-storage-service"
build_image "database-fixture" "0.0.1" "../../database-fixture/Dockerfile-dev ../../database-fixture"
build_image "history" "0.0.1" "../../history/Dockerfile-dev ../../history"
build_image "video-streaming" "0.0.1" "../../video-streaming/Dockerfile-dev ../../video-streaming"

deploy_kubernetes_resource "rabbitmq-dev.yaml"
deploy_kubernetes_resource "mongodb-dev.yaml"
deploy_kubernetes_resource "aws-simple-storage-service-dev.yaml"
deploy_kubernetes_resource "database-fixture-dev.json.yaml"
deploy_kubernetes_resource "history-dev.yaml"
deploy_kubernetes_resource "video-streaming-dev.yaml"

# docker build -t aws-simple-storage-service:0.0.1 --file ../../aws-simple-storage-service/Dockerfile-dev ../../aws-simple-storage-service
# docker build -t database-fixture:0.0.1 --file ../../database-fixture/Dockerfile-dev ../../database-fixture
# docker build -t history:0.0.1 --file ../../history/Dockerfile-dev ../../history
# docker build -t video-streaming:0.0.1 --file Dockerfile-dev ../../video-streaming/Dockerfile-dev ../../video-streaming

# kubectl apply -f rabbitmq-dev.yaml
# kubectl apply -f mongodb-dev.yaml
# kubectl apply -f aws-simple-storage-service-dev.yaml
# kubectl apply -f database-fixture-dev.json.yaml
# kubectl apply -f history-dev.yaml
# kubectl apply -f video-streaming-dev.yaml