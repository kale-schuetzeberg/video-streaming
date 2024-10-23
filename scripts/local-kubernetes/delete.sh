#!/bin/bash

function delete_kubernetes_resource() {
    local kubernetes_deploymnet_yaml=$1

    echo "Deleting $1"
    kubectl delete -f $kubernetes_deploymnet_yaml
}

delete_kubernetes_resource "rabbitmq-dev.yaml"
delete_kubernetes_resource "mongodb-dev.yaml"
delete_kubernetes_resource "aws-simple-storage-service-dev.yaml"
delete_kubernetes_resource "database-fixture-dev.json.yaml"
delete_kubernetes_resource "history-dev.yaml"
delete_kubernetes_resource "video-streaming-dev.yaml"

#kubectl delete -f rabbitmq-dev.yaml
#kubectl delete -f mongodb-dev.yaml
#kubectl delete -f aws-simple-storage-service-dev.yaml
#kubectl delete -f database-fixture-dev.json.yaml
#kubectl delete -f history-dev.yaml
#kubectl delete -f video-streaming-dev.yaml