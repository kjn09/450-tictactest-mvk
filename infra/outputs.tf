output "public_ip" {
  value = aws_instance.tictactest.public_ip
}

output "public_dns" {
  value = aws_instance.tictactest.public_dns
}

output "ssh_command" {
  value = "ssh -i ~/.ssh/tictactest-aws ec2-user@${aws_instance.tictactest.public_dns}"
}