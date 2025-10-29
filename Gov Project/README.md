# जनमनरेगा - MGNREGA District Performance Dashboard

A production-ready Spring Boot web application for displaying MGNREGA district performance data with a low-literacy friendly Hindi interface.

## 🎯 Features

- **Low-literacy friendly UI** with Hindi text and large, colorful buttons
- **District-wise performance dashboards** with year-over-year trends
- **Visual charts** showing employment trends over 5 years
- **Caching** for improved performance
- **Production-ready** configuration for Linux VM deployment
- **Offline CSV data** processing (no external API dependencies)

## 🛠 Tech Stack

- **Backend**: Spring Boot 3.2.0 (Java 17)
- **Database**: PostgreSQL
- **Frontend**: Thymeleaf + PicoCSS
- **Charts**: XChart for static image generation
- **Caching**: Spring Cache with TTL
- **CSV Processing**: OpenCSV

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

## 🚀 Quick Start

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE janmanrega;

-- Create user (optional)
CREATE USER janmanrega_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE janmanrega TO janmanrega_user;
```

### 2. Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/janmanrega
    username: your_username
    password: your_password

app:
  csv:
    directory: /path/to/your/csv/files
```

### 3. Build and Run

```bash
# Build the application
mvn clean package

# Run the application
java -jar target/dashboard-0.0.1-SNAPSHOT.jar
```

The application will be available at `http://localhost:8080`

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/gov/dashboard/
│   │   ├── JanmanregaApplication.java          # Main Spring Boot application
│   │   ├── config/
│   │   │   └── CacheConfig.java               # Cache configuration
│   │   ├── controller/
│   │   │   └── DashboardController.java       # REST endpoints
│   │   ├── entity/
│   │   │   └── DistrictPerformance.java       # JPA entity
│   │   ├── repository/
│   │   │   └── DistrictPerformanceRepository.java # Data access layer
│   │   └── service/
│   │       ├── CsvDataService.java            # CSV processing
│   │       ├── DataInitializer.java          # Startup data loading
│   │       └── DashboardService.java         # Business logic
│   └── resources/
│       ├── application.yml                   # Development config
│       ├── application-prod.yml              # Production config
│       └── templates/
│           ├── index.html                    # Homepage
│           └── district.html                 # District dashboard
└── test/                                     # Test files
```

## 📊 CSV Data Format

The application expects CSV files with the following structure:

```csv
fin_year,month,state_code,state_name,district_code,district_name,
Approved_Labour_Budget,Average_Wage_rate_per_day_per_person,
Average_days_of_employment_provided_per_Household,Differently_abled_persons_worked,
Material_and_skilled_Wages,Number_of_Completed_Works,Number_of_GPs_with_NIL_exp,
Number_of_Ongoing_Works,Persondays_of_Central_Liability_so_far,SC_persondays,
SC_workers_against_active_workers,ST_persondays,ST_workers_against_active_workers,
Total_Adm_Expenditure,Total_Exp,Total_Households_Worked,Total_Individuals_Worked,
Total_No_of_Active_Job_Cards,Total_No_of_Active_Workers,
Total_No_of_HHs_completed_100_Days_of_Wage_Employment,Total_No_of_JobCards_issued,
Total_No_of_Workers,Total_No_of_Works_Takenup,Wages,Women_Persondays,
percent_of_Category_B_Works,percent_of_Expenditure_on_Agriculture_Allied_Works,
percent_of_NRM_Expenditure,percentage_payments_gererated_within_15_days,Remarks
```

## 🌐 API Endpoints

- `GET /` - Homepage with district list
- `GET /district/{districtName}` - District dashboard
- `GET /chart/{districtName}` - Chart image for district
- `GET /api/locate-district?lat={lat}&lon={lon}` - Location-based district detection
- `GET /actuator/health` - Health check endpoint

## 🚀 Production Deployment

### Linux VM Setup

1. **Install Java 17**:
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

2. **Install PostgreSQL**:
```bash
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

3. **Create application user**:
```bash
sudo adduser janmanrega
sudo mkdir -p /opt/janmanrega/{app,data,logs}
sudo chown -R janmanrega:janmanrega /opt/janmanrega
```

4. **Deploy application**:
```bash
# Copy JAR file
sudo cp target/dashboard-0.0.1-SNAPSHOT.jar /opt/janmanrega/app/

# Copy CSV files
sudo cp -r "Gov Data CSV's"/* /opt/janmanrega/data/

# Create systemd service
sudo tee /etc/systemd/system/janmanrega.service > /dev/null <<EOF
[Unit]
Description=Janmanrega Dashboard
After=network.target

[Service]
Type=simple
User=janmanrega
WorkingDirectory=/opt/janmanrega/app
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod dashboard-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# Start service
sudo systemctl daemon-reload
sudo systemctl enable janmanrega
sudo systemctl start janmanrega
```

5. **Configure Nginx** (optional):
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Environment Variables

Set these environment variables for production:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=janmanrega
export DB_USERNAME=janmanrega_user
export DB_PASSWORD=your_secure_password
export CSV_DIRECTORY=/opt/janmanrega/data
export PORT=8080
```

## 🔧 Configuration Options

### Application Properties

| Property | Description | Default |
|----------|-------------|---------|
| `app.csv.directory` | Path to CSV files directory | `C:\Users\...\Gov Data CSV's` |
| `app.cache.ttl` | Cache TTL in seconds | `3600` |
| `spring.datasource.url` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/janmanrega` |
| `server.port` | Application port | `8080` |

### Cache Configuration

The application uses Spring Cache with the following cache names:
- `districts` - List of all districts (TTL: 1 hour)
- `district-performance` - District performance data (TTL: 1 hour)
- `dashboard-summary` - Dashboard summary data (TTL: 1 hour)

## 📈 Monitoring

### Health Checks

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Logging

Logs are written to:
- Console (development)
- `/var/log/janmanrega/application.log` (production)

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Check PostgreSQL is running
   - Verify database credentials
   - Ensure database exists

2. **CSV Loading Issues**:
   - Check CSV file format matches expected structure
   - Verify file permissions
   - Check CSV directory path

3. **Memory Issues**:
   - Increase JVM heap size: `-Xmx2g`
   - Monitor memory usage with `jstat`

### Logs

Check application logs:
```bash
sudo journalctl -u janmanrega -f
tail -f /var/log/janmanrega/application.log
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the application logs

---

**जनमनरेगा - पारदर्शिता के लिए डिजिटल डैशबोर्ड**
