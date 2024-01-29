FROM mysql:latest

# Set environment variables
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=sql_test_orders
ENV MYSQL_USER=db_user
ENV MYSQL_PASSWORD=db_user_pass

# Copy the SQL script into the Docker image
COPY sql_test_orders.sql /docker-entrypoint-initdb.d/