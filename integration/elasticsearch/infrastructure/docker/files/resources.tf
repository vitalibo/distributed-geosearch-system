terraform {
  required_providers {
    kafka-connect = {
      source  = "Mongey/kafka-connect"
      version = "0.2.3"
    }

    elasticsearch = {
      source  = "phillbaker/elasticsearch"
      version = "1.5.2-beta1"
    }
  }
}

provider "kafka-connect" {
  url = "http://connect:8083"
}

resource "kafka-connect_connector" "geo-event-connector" {
  provider = kafka-connect
  name     = "geo-event-connector"

  config = {
    "name"                            = "geo-event-connector"
    "connector.class"                 = "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector"
    "tasks.max"                       = "1"
    "connection.url"                  = "http://elasticsearch:9200",
    "topics"                          = "geo-event"
    "key.ignore"                      = "true"
    "max.retries"                     = "5"
    "retry.backoff.ms"                = "1000"
    "behavior.on.null.values"         = "FAIL"
    "behavior.on.malformed.documents" = "WARN"
    "write.method"                    = "UPSERT"
    "errors.tolerance"                = "all"

    "transforms"                          = "GeoPoint, DropFields"
    "transforms.GeoPoint.type"            = "com.github.vitalibo.connect.elasticsearch.GeoPoint"
    "transforms.GeoPoint.field.latitude"  = "latitude"
    "transforms.GeoPoint.field.longitude" = "longitude"
    "transforms.GeoPoint.field.new"       = "location"
    "transforms.DropFields.type"          = "org.apache.kafka.connect.transforms.ReplaceField$Value",
    "transforms.DropFields.blacklist"     = "latitude, longitude"
  }
}

provider "elasticsearch" {
  url   = "http://elasticsearch:9200"
  sniff = false
}

resource "elasticsearch_index_template" "geo_search" {
  provider = elasticsearch
  name     = "geo-search"
  body     = file("${path.module}/index_template.json")
}
