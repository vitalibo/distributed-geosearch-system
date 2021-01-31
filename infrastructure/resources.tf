variable "bootstrap_servers" {
  type        = list(string)
  description = "A list of host:port addresses that will be used to discover the full set of alive brokers"
  default     = [
    "broker:29092"
  ]
}

variable "replication_factor" {
  type        = number
  description = "The number of replicas the topic should have"
  default     = 1
}

locals {
  number_servers = length(var.bootstrap_servers)
}

terraform {
  required_providers {
    kafka = {
      source  = "Mongey/kafka"
      version = "0.2.11"
    }
  }
}

provider "kafka" {
  bootstrap_servers = var.bootstrap_servers
  sasl_mechanism    = "plain"
  skip_tls_verify   = true
  tls_enabled       = false
}

resource "kafka_topic" "geo_event" {
  name               = "geo-event"
  replication_factor = var.replication_factor
  partitions         = 2 * local.number_servers

  config = {
    "cleanup.policy" = "delete"
  }
}

resource "kafka_topic" "geo_search_command" {
  name               = "geo-search-command"
  replication_factor = var.replication_factor
  partitions         = 1 * local.number_servers

  config = {
    "cleanup.policy" = "compact"
  }
}

resource "kafka_topic" "geo_search_result" {
  name               = "geo-search-result"
  replication_factor = var.replication_factor
  partitions         = 3 * local.number_servers

  config = {
    "cleanup.policy" = "delete"
  }
}
