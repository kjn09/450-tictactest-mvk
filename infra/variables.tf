variable "region" {
  type    = string
  default = "us-east-1"
}

variable "instance_type" {
  type    = string
  default = "t2.micro"
}

variable "public_key_path" {
  type    = string
  default = "~/.ssh/tictactest-aws.pub"
}

variable "ssh_allowed_cidr" {
  type        = string
  description = "IP range allowed to connect via SSH, for example 203.0.113.7/32"
}