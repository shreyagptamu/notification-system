#######################################
# ECS Task Execution Role and Policy  #
#######################################

resource "aws_iam_role" "ecs_execution_role" {
  name = "ecsExecutionRole"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role_policy" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_policy" "ecs_secrets_policy" {
  name = "ecsSecretsPolicy"
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = ["secretsmanager:GetSecretValue"],
        Resource = var.dockerhub_secret_arn
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_secrets_policy_attachment" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = aws_iam_policy.ecs_secrets_policy.arn
}

#######################################
# VPC and Networking Setup            #
#######################################

resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "notification-system-vpc"
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "notification-system-igw"
  }
}

# Public subnets for ALB and NAT Gateway
resource "aws_subnet" "public1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "us-east-1a"
  map_public_ip_on_launch = true

  tags = {
    Name = "notif-sys-public-subnet-1"
  }
}

resource "aws_subnet" "public2" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "us-east-1b"
  map_public_ip_on_launch = true

  tags = {
    Name = "notif-sys-public-subnet-2"
  }
}

# Private subnets for ECS tasks
resource "aws_subnet" "private1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.101.0/24"
  availability_zone       = "us-east-1a"
  map_public_ip_on_launch = false

  tags = {
    Name = "notif-sys-private-subnet-1"
  }
}

resource "aws_subnet" "private2" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.102.0/24"
  availability_zone       = "us-east-1b"
  map_public_ip_on_launch = false

  tags = {
    Name = "notif-sys-private-subnet-2"
  }
}

# Public Route Table.
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "notif-sys-public-route-table"
  }
}

resource "aws_route_table_association" "public1_assoc" {
  subnet_id      = aws_subnet.public1.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public2_assoc" {
  subnet_id      = aws_subnet.public2.id
  route_table_id = aws_route_table.public.id
}

# Elastic IP and NAT Gateway.
resource "aws_eip" "nat_eip" {
  vpc = true
}

resource "aws_nat_gateway" "nat_gw" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.public1.id

  tags = {
    Name = "nat-gateway"
  }
}

# Private Route Table.
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat_gw.id
  }

  tags = {
    Name = "notif-sys-private-route-table"
  }
}

resource "aws_route_table_association" "private1_assoc" {
  subnet_id      = aws_subnet.private1.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route_table_association" "private2_assoc" {
  subnet_id      = aws_subnet.private2.id
  route_table_id = aws_route_table.private.id
}

####################################
# ALB Setup                        #
####################################

resource "aws_security_group" "alb_sg" {
  name        = "alb-sg"
  description = "Security group for the ALB"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "Allow HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_lb" "ecs_alb" {
  name               = "ecs-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = [aws_subnet.public1.id, aws_subnet.public2.id]

  tags = {
    Name = "ecs-alb"
  }
}

resource "aws_lb_target_group" "user_service_tg" {
  name        = "user-svc-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/health"
    protocol            = "HTTP"
    matcher             = "200-399"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
  }
}

resource "aws_lb_target_group" "notification_scheduler_tg" {
  name        = "notif-sched-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/health"
    protocol            = "HTTP"
    matcher             = "200-399"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
  }
}

resource "aws_lb_target_group" "contact_service_tg" {
  name        = "contact-svc-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/health"
    protocol            = "HTTP"
    matcher             = "200-399"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
  }
}

resource "aws_lb_listener" "http_listener" {
  load_balancer_arn = aws_lb.ecs_alb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Not Found"
      status_code  = "404"
    }
  }
}

resource "aws_lb_listener_rule" "user_service_rule" {
  listener_arn = aws_lb_listener.http_listener.arn
  priority     = 10

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.user_service_tg.arn
  }

  condition {
    path_pattern {
      values = ["/api/user*"]
    }
  }
}

resource "aws_lb_listener_rule" "notification_scheduler_rule" {
  listener_arn = aws_lb_listener.http_listener.arn
  priority     = 20

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.notification_scheduler_tg.arn
  }

  condition {
    path_pattern {
      values = ["/api/notification*"]
    }
  }
}

resource "aws_lb_listener_rule" "contact_service_rule" {
  listener_arn = aws_lb_listener.http_listener.arn
  priority     = 30

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.contact_service_tg.arn
  }

  condition {
    path_pattern {
      values = ["/api/contact*"]
    }
  }
}

####################################
# ECS Cluster & ECS Security       #
####################################

resource "aws_ecs_cluster" "ecs_cluster" {
  name = "example-ecs-cluster"
}

resource "aws_security_group" "ecs_sg" {
  name        = "ecs-sg"
  description = "Security group for ECS tasks"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "Postgres"
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "Zookeeper"
    from_port   = 2181
    to_port     = 2181
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "Kafka"
    from_port   = 9092
    to_port     = 9092
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "Application services"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

####################################
# Service Discovery Setup          #
####################################

resource "aws_service_discovery_private_dns_namespace" "service_namespace" {
  name        = "myapp.local"
  description = "Private DNS namespace for ECS service discovery"
  vpc         = aws_vpc.main.id
}

resource "aws_service_discovery_service" "postgres_sd" {
  name         = "postgres"
  namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id
    dns_records {
      ttl  = 10
      type = "A"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_service_discovery_service" "zookeeper_sd" {
  name         = "zookeeper"
  namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id
    dns_records {
      ttl  = 10
      type = "A"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_service_discovery_service" "kafka_sd" {
  name         = "kafka"
  namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id
    dns_records {
      ttl  = 10
      type = "A"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_service_discovery_service" "contact_sd" {
  name         = "contact-service"
  namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id
    dns_records {
      ttl  = 10
      type = "A"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_service_discovery_service" "user_service_sd" {
  name         = "user-service"
  namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id
    dns_records {
      ttl  = 10
      type = "A"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_service_discovery_service" "notification_scheduler_sd" {
  name         = "notification-scheduler"
  namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id
    dns_records {
      ttl  = 10
      type = "A"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_service_discovery_service" "notification_handler_sd" {
  name         = "notification-handler"
  namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.service_namespace.id
    dns_records {
      ttl  = 10
      type = "A"
    }
    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

####################################
# ECS Task Definitions             #
####################################

resource "aws_ecs_task_definition" "postgres_task" {
  family                   = "postgres-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  container_definitions    = jsonencode([
    {
      name         = "postgres"
      image        = "postgres:14"
      essential    = true
      portMappings = [
        { containerPort = 5432, hostPort = 5432, protocol = "tcp" }
      ],
      environment = [
        { name = "POSTGRES_USER", value = var.data_source_username },
        { name = "POSTGRES_PASSWORD", value = var.data_source_password },
        { name = "POSTGRES_DB", value = var.data_source_name }
      ]
    }
  ])
}

resource "aws_ecs_task_definition" "zookeeper_task" {
  family                   = "zookeeper-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  container_definitions    = jsonencode([
    {
      name         = "zookeeper"
      image        = "docker.io/bitnami/zookeeper:3.9"
      essential    = true
      portMappings = [
        { containerPort = 2181, hostPort = 2181, protocol = "tcp" }
      ],
      environment = [
        { name = "ALLOW_ANONYMOUS_LOGIN", value = "yes" }
      ]
    }
  ])
}

resource "aws_ecs_task_definition" "kafka_task" {
  family                   = "kafka-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "1024"    # Increased memory allocation to 1GB
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  container_definitions    = jsonencode([
    {
      name         = "kafka"
      image        = "docker.io/bitnami/kafka:3.4"
      essential    = true
      portMappings = [
        { containerPort = 9092, hostPort = 9092, protocol = "tcp" }
      ],
      environment = [
        { name = "KAFKA_CFG_ZOOKEEPER_CONNECT", value = "zookeeper.myapp.local:2181" },
        { name = "KAFKA_HEAP_OPTS", value = "-Xms128M -Xmx256M" }
      ],
      logConfiguration = {
        logDriver = "awslogs",
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.kafka_log_group.name,
          "awslogs-region"        = "us-east-1",
          "awslogs-stream-prefix" = "kafka"
        }
      }
    }
  ])
}

# Create CloudWatch Log Group for Kafka.
resource "aws_cloudwatch_log_group" "kafka_log_group" {
  name              = "/ecs/kafka-service"
  retention_in_days = 7
}

resource "aws_ecs_task_definition" "contact_task" {
  family                   = "contact-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  container_definitions    = jsonencode([
    {
      name         = "contact-service"
      image        = "tamulearning/contact-service:latest"
      essential    = true
      portMappings = [
        { containerPort = 8080, hostPort = 8080, protocol = "tcp" },
        { containerPort = 50051, hostPort = 50051, protocol = "tcp" }
      ],
      environment = [
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://postgres.myapp.local:5432/postgres" },
        { name = "SPRING_DATASOURCE_USERNAME", value = var.data_source_username },
        { name = "SPRING_DATASOURCE_PASSWORD", value = var.data_source_password }
      ],
      repositoryCredentials = {
        credentialsParameter = var.dockerhub_secret_arn
      }
    }
  ])
}

resource "aws_ecs_task_definition" "user_task" {
  family                   = "user-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  container_definitions    = jsonencode([
    {
      name         = "user-service"
      image        = "tamulearning/user-service:latest"
      essential    = true
      portMappings = [
        { containerPort = 8080, hostPort = 8080, protocol = "tcp" }
      ],
      environment = [
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://postgres.myapp.local:5432/postgres" },
        { name = "SPRING_DATASOURCE_USERNAME", value = var.data_source_username },
        { name = "SPRING_DATASOURCE_PASSWORD", value = var.data_source_password }
      ],
      repositoryCredentials = {
        credentialsParameter = var.dockerhub_secret_arn
      }
    }
  ])
}

resource "aws_ecs_task_definition" "notification_scheduler_task" {
  family                   = "notification-scheduler-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  container_definitions    = jsonencode([
    {
      name         = "notification-scheduler"
      image        = "tamulearning/notification-scheduler:latest"
      essential    = true
      portMappings = [
        { containerPort = 8080, hostPort = 8080, protocol = "tcp" }
      ],
      environment = [
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://postgres.myapp.local:5432/postgres" },
        { name = "SPRING_DATASOURCE_USERNAME", value = var.data_source_username },
        { name = "SPRING_DATASOURCE_PASSWORD", value = var.data_source_password },
        { name = "KAFKA_BOOTSTRAP_SERVERS", value = "kafka.myapp.local:9092" },
        { name = "CONTACT_GRPC_SERVER", value = "contact-service.myapp.local:50051" }
      ],
      repositoryCredentials = {
        credentialsParameter = var.dockerhub_secret_arn
      }
    }
  ])
}

resource "aws_ecs_task_definition" "notification_handler_task" {
  family                   = "notification-handler-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  container_definitions    = jsonencode([
    {
      name         = "notification-handler"
      image        = "tamulearning/notification-handler:latest"
      essential    = true
      portMappings = [
        { containerPort = 8080, hostPort = 8080, protocol = "tcp" }
      ],
      environment = [
        { name = "KAFKA_BOOTSTRAP_SERVERS", value = "kafka.myapp.local:9092" }
      ],
      repositoryCredentials = {
        credentialsParameter = var.dockerhub_secret_arn
      }
    }
  ])
}

resource "aws_ecs_task_definition" "kafka_config_task" {
  family                   = "kafka-config-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  container_definitions    = jsonencode([
    {
      name         = "kafka-config-service"
      image        = "tamulearning/kafka-config-service:latest"
      essential    = true
      portMappings = [
        { containerPort = 8080, hostPort = 8080, protocol = "tcp" }
      ],
      environment = [
        { name = "KAFKA_BOOTSTRAP_SERVERS", value = "kafka.myapp.local:9092" }
      ],
      repositoryCredentials = {
        credentialsParameter = var.dockerhub_secret_arn
      }
    }
  ])
}

####################################
# ECS Services                     #
####################################

resource "aws_ecs_service" "postgres_service" {
  name            = "postgres-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.postgres_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = [aws_subnet.private1.id, aws_subnet.private2.id]
    security_groups = [aws_security_group.ecs_sg.id]
    assign_public_ip = false
  }
  service_registries {
    registry_arn = aws_service_discovery_service.postgres_sd.arn
  }
}

resource "aws_ecs_service" "zookeeper_service" {
  name            = "zookeeper-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.zookeeper_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = [aws_subnet.private1.id, aws_subnet.private2.id]
    security_groups = [aws_security_group.ecs_sg.id]
    assign_public_ip = false
  }
  service_registries {
    registry_arn = aws_service_discovery_service.zookeeper_sd.arn
  }
}

resource "aws_ecs_service" "kafka_service" {
  name            = "kafka-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.kafka_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = [aws_subnet.private1.id, aws_subnet.private2.id]
    security_groups = [aws_security_group.ecs_sg.id]
    assign_public_ip = false
  }
  service_registries {
    registry_arn = aws_service_discovery_service.kafka_sd.arn
  }
  depends_on = [aws_ecs_service.zookeeper_service]
}

resource "aws_ecs_service" "notification_handler_service" {
  name            = "notification-handler-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.notification_handler_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = [aws_subnet.private1.id, aws_subnet.private2.id]
    security_groups = [aws_security_group.ecs_sg.id]
    assign_public_ip = false
  }
  service_registries {
    registry_arn = aws_service_discovery_service.notification_handler_sd.arn
  }
}

resource "aws_ecs_service" "kafka_config_service" {
  name            = "kafka-config-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.kafka_config_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = [aws_subnet.private1.id, aws_subnet.private2.id]
    security_groups = [aws_security_group.ecs_sg.id]
    assign_public_ip = false
  }
  # No service registry – internal only.
}

resource "aws_ecs_service" "user_service" {
  name            = "user-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.user_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = [aws_subnet.private1.id, aws_subnet.private2.id]
    security_groups = [aws_security_group.ecs_sg.id]
    assign_public_ip = false
  }
  load_balancer {
    target_group_arn = aws_lb_target_group.user_service_tg.arn
    container_name   = "user-service"
    container_port   = 8080
  }
  service_registries {
    registry_arn = aws_service_discovery_service.user_service_sd.arn
  }
  depends_on = [
    aws_lb_listener_rule.user_service_rule,
    aws_ecs_service.postgres_service,
    aws_ecs_service.kafka_service
  ]
}

resource "aws_ecs_service" "notification_scheduler_service" {
  name            = "notification-scheduler-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.notification_scheduler_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = [aws_subnet.private1.id, aws_subnet.private2.id]
    security_groups = [aws_security_group.ecs_sg.id]
    assign_public_ip = false
  }
  load_balancer {
    target_group_arn = aws_lb_target_group.notification_scheduler_tg.arn
    container_name   = "notification-scheduler"
    container_port   = 8080
  }
  service_registries {
    registry_arn = aws_service_discovery_service.notification_scheduler_sd.arn
  }
  depends_on = [
    aws_lb_listener_rule.notification_scheduler_rule,
    aws_ecs_service.postgres_service,
    aws_ecs_service.kafka_service
  ]
}

resource "aws_ecs_service" "contact_service" {
  name            = "contact-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.contact_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets         = [aws_subnet.private1.id, aws_subnet.private2.id]
    security_groups = [aws_security_group.ecs_sg.id]
    assign_public_ip = false
  }
  load_balancer {
    target_group_arn = aws_lb_target_group.contact_service_tg.arn
    container_name   = "contact-service"
    container_port   = 8080
  }
  service_registries {
    registry_arn = aws_service_discovery_service.contact_sd.arn
  }
  depends_on = [
    aws_lb_listener_rule.contact_service_rule,
    aws_ecs_service.postgres_service,
    aws_ecs_service.kafka_service
  ]
}