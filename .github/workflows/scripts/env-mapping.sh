#!/bin/bash
# Environment to cluster mapping configuration
# Centralized to avoid duplication across workflows

get_cluster_config() {
  local env=$1
  
  case "$env" in
    dev)
      export CLUSTER_NAME="opera-dev"
      export REGION="eu-west-1"
      ;;
    dit)
      export CLUSTER_NAME="opera-dit"
      export REGION="eu-west-1"
      ;;
    sit)
      export CLUSTER_NAME="opera-sit"
      export REGION="eu-west-1"
      ;;
    uat)
      export CLUSTER_NAME="opera-uat"
      export REGION="eu-west-1"
      ;;
    hulk)
      export CLUSTER_NAME="opera-hulk"
      export REGION="eu-central-1"
      ;;
    wanda)
      export CLUSTER_NAME="opera-wanda"
      export REGION="eu-central-1"
      ;;
    *)
      echo "Unknown environment: $env"
      exit 1
      ;;
  esac
  
  echo "Environment: $env → Cluster: $CLUSTER_NAME ($REGION)"
}
