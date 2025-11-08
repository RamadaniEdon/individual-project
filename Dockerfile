FROM mongo:latest

# Set environment variables
ENV MONGO_INITDB_ROOT_USERNAME=admin
ENV MONGO_INITDB_ROOT_PASSWORD=password

# Expose MongoDB port
EXPOSE 27017

# Start MongoDB
CMD ["mongod", "--bind_ip_all", "--port", "27017"]
