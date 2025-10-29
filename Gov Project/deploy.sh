#!/bin/bash

# Janmanrega Dashboard Deployment Script
# This script sets up the application on a Linux VM

set -e

echo "🚀 Starting Janmanrega Dashboard Deployment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if running as root
if [[ $EUID -eq 0 ]]; then
   print_error "This script should not be run as root"
   exit 1
fi

# Update system packages
print_status "Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install Java 17
print_status "Installing Java 17..."
sudo apt install -y openjdk-17-jdk

# Verify Java installation
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" != "17" ]; then
    print_error "Java 17 installation failed"
    exit 1
fi
print_status "Java 17 installed successfully"

# Install PostgreSQL
print_status "Installing PostgreSQL..."
sudo apt install -y postgresql postgresql-contrib

# Start and enable PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database and user
print_status "Setting up PostgreSQL database..."
sudo -u postgres psql << EOF
CREATE DATABASE janmanrega;
CREATE USER janmanrega_user WITH PASSWORD 'janmanrega123';
GRANT ALL PRIVILEGES ON DATABASE janmanrega TO janmanrega_user;
\q
EOF

# Create application directories
print_status "Creating application directories..."
sudo mkdir -p /opt/janmanrega/{app,data,logs}
sudo chown -R $USER:$USER /opt/janmanrega

# Copy application files
print_status "Copying application files..."
if [ -f "target/dashboard-0.0.1-SNAPSHOT.jar" ]; then
    cp target/dashboard-0.0.1-SNAPSHOT.jar /opt/janmanrega/app/
else
    print_error "JAR file not found. Please build the application first with 'mvn clean package'"
    exit 1
fi

# Copy CSV data files
if [ -d "Gov Data CSV's" ]; then
    cp -r "Gov Data CSV's"/* /opt/janmanrega/data/
    print_status "CSV data files copied successfully"
else
    print_warning "CSV data directory not found. Please ensure CSV files are available."
fi

# Create systemd service file
print_status "Creating systemd service..."
sudo tee /etc/systemd/system/janmanrega.service > /dev/null << EOF
[Unit]
Description=Janmanrega Dashboard
After=network.target postgresql.service

[Service]
Type=simple
User=$USER
WorkingDirectory=/opt/janmanrega/app
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod -Xmx2g dashboard-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
Environment=DB_HOST=localhost
Environment=DB_PORT=5432
Environment=DB_NAME=janmanrega
Environment=DB_USERNAME=janmanrega_user
Environment=DB_PASSWORD=janmanrega123
Environment=CSV_DIRECTORY=/opt/janmanrega/data
Environment=PORT=8080

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd and start service
print_status "Starting Janmanrega service..."
sudo systemctl daemon-reload
sudo systemctl enable janmanrega
sudo systemctl start janmanrega

# Wait for service to start
sleep 10

# Check service status
if sudo systemctl is-active --quiet janmanrega; then
    print_status "✅ Janmanrega service is running successfully!"
    print_status "🌐 Application is available at: http://localhost:8080"
    print_status "📊 Health check: http://localhost:8080/actuator/health"
else
    print_error "❌ Service failed to start. Check logs with: sudo journalctl -u janmanrega -f"
    exit 1
fi

# Display useful commands
echo ""
print_status "📋 Useful commands:"
echo "  • Check service status: sudo systemctl status janmanrega"
echo "  • View logs: sudo journalctl -u janmanrega -f"
echo "  • Restart service: sudo systemctl restart janmanrega"
echo "  • Stop service: sudo systemctl stop janmanrega"
echo ""

# Optional: Install Nginx for reverse proxy
read -p "Do you want to install Nginx for reverse proxy? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_status "Installing Nginx..."
    sudo apt install -y nginx
    
    # Create Nginx configuration
    sudo tee /etc/nginx/sites-available/janmanrega > /dev/null << EOF
server {
    listen 80;
    server_name _;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF
    
    # Enable site
    sudo ln -sf /etc/nginx/sites-available/janmanrega /etc/nginx/sites-enabled/
    sudo rm -f /etc/nginx/sites-enabled/default
    sudo nginx -t
    sudo systemctl restart nginx
    sudo systemctl enable nginx
    
    print_status "✅ Nginx configured successfully!"
    print_status "🌐 Application is now available at: http://$(hostname -I | awk '{print $1}')"
fi

print_status "🎉 Deployment completed successfully!"
print_status "📖 Check README.md for more information and troubleshooting tips."
