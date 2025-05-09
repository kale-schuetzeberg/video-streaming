# Hello, and welcome to my *ongoing* microservices exploration project!

## Dataflow description
### aws-simple-storage-service
- Retrieves videos stored in s3 buckets from aws.
- Uploads local videos to aws s3.
### video-streaming
- Streams the video
- Retrieves list of all videos from MongoDB 'video' database
- Forwards request for videos to aws-simple-storage-service 
- Publishes messages to RabbitMQ 'queue_history'
### Kafka (video)
- The video queue receives a message each time a video is played.
- ```
  key = <video-streaming>
  value = <video-title>
  ```
### history
- Consumes RabbitMQ queue_history messages keeping an ArrayList of all the videos viewed.
- Prints the total number of videos viewed
### MongoDB (videos)
- Stores a reference to the videos stored in s3
### database-fixture
- Loads data representing the videos available on s3 into MongoDB videos 

## 10,000 Feet Overview

This is a video streaming application developed with the microservice architecture inspired by Bootstrapping
Microservices by Ashley Davis.

## Project Goals

- Develop understanding of microservice architecture.
- Familiarize myself with workflows and tools used to develop highly scalable software.
- Improve knowledge cloud computing services (Azure, GCS, AWS, Oracle).
- Deploy infrastructure as software (Terraform).
- Automatically test and deploy application updates to production though GitHub Actions (CI/CD).
- Stream videos.

## Motivation

"We are what we repeatedly do. Excellence, then, is not an act, but a habit." - Aristotle <br>
I have a strong desire to architect scalable cloud software that that maximizes efficiency with minimal resources.
Driven by the wisdom of Aristotle if I aim to design exceptional software I must practice.

## Background

This project is based on the contents of Bootstrapping Microservices by Ashley Davis.

However, where Davis uses Node.js, I've opted to use Spring Boot.
Thus, I want to stress that this project isn't a simple fork of the project files provided by Davis but rather a
complete port of the project.

The decision was made to use Spring Boot for a few reasons:
- Increase challenge and therefore knowledge gained.
- Prevent myself from using copy-pasta thus forcing me to understand everything.
- I want to know how microservices work in a Spring Boot setting.

## Current Application State

- [X] Deploy cloud resources required for a single microservice using Terraform.
- [X] Deploy microservice when merged into 'main'.
- [X] Access application through a public IP Address.
- [X] Pass messages between microservices using a message broker (RabbitMQ).
- [X] Containerize single microservice.
- [X] Write kubernetes deploy.yaml for single microservice.
- [X] Develop 3 microservices
    - stream video
    - viewing history
    - video retrieval from storage
- [X] Implement MongoDB to storage available videos

## Future Milestones

- [ ] Kubernetes deploy.yaml file for all microservice projects.
- [ ] Implement database fixtures 
- [ ] Provision all infrastructure required for project with Terraform.
- [ ] Add unit and integration tests to all microservice projects.
- [ ] Integrate unit and integration tests into GitHub Actions CI/CD pipeline.
- [ ] Expand scope of GitHub Actions to all microservice projects.
- [ ] Integrate with UI.
- [ ] Compute resource tuning.
- [ ] Architectural diagrams.
- [ ] Set GitHub Secrets when terraform procures infrastructure
- [ ] Manage secrets with HashiCorp vault
- [ ] Cloud agnosticity (Azure, GCS, AWS, Oracle).

## GitHub Actions Repository Secrets

| Name                  | Description                                                                                                                 |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------------|
| AZURE_STORAGE_VERSION | azure-storage image tag                                                                                                     |
| CONTAINER_NAME        | Azure Storage Blob Container Name                                                                                           |
| CONTAINER_REGISTRY    | Container Registry URL                                                                                                      |
| KUBE_CONFIG           | Base64 encoded configuration that authenticates Kubectl with your cluster (cat ~/.kube/config \| base64 > ~/kubeconfig.txt) |
| PORT                  | Port Application Listens on Inside the Container                                                                            |
| REGISTRY_PW           | Container Registry Password                                                                                                 |
| REGISTRY_UN           | Container Registry Username                                                                                                 |
| STORAGE_ACCESS_KEY    | Azure Storage Account Key                                                                                                   |
| STORAGE_ACCOUNT_NAME  | Azure Storage Account Name                                                                                                  |

Sudo User,  
baddog
