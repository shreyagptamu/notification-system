variable "data_source_username" {
    type = string
}


variable "data_source_password" {
    type = string
}


variable "data_source_name" {
    type = string
    default = "postgres"
}

variable "dockerhub_secret_arn" {
    type = string
    default = "arn:aws:secretsmanager:us-east-1:597088049951:secret:docker-creds-1KKMr3"
}