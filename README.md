# Department Research Information Management System (DRIMS)

A complete, production-ready web application for managing academic research data, replacing Excel-based data collection with a secure, modern web platform.

## 🎯 Project Overview

DRIMS is a full-stack application designed to streamline research data management for academic departments. It provides separate interfaces for faculty members and administrators (Research Coordinators) to manage research publications, targets, and analytics.

## ✨ Features

### Faculty Features
- ✅ Secure JWT-based authentication
- ✅ Profile management (Name, Employee ID, Designation, Department, Research Areas, ORCID, Scopus, Google Scholar)
- ✅ Yearly research target setting and tracking
- ✅ Publication management:
  - Journals
  - Conferences
  - Patents
  - Book Chapters
- ✅ PDF document upload for proof documents
- ✅ View own historical data

### Admin Features
- ✅ View all faculty profiles (read-only)
- ✅ View all research targets and publications
- ✅ Department-level analytics:
  - Year-wise publication totals
  - Category-wise breakdown
  - Faculty-wise contribution
  - Status-wise distribution
- ✅ Excel export functionality (filtered by year & category)

## 🛠️ Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT authentication
- **Spring Data MongoDB**
- **Lombok**
- **Maven**
- **Apache POI** (Excel export)

### Database
- **MongoDB** (NoSQL)

### Frontend
- **React 18** with Vite
- **Tailwind CSS**
- **Axios** (HTTP client)
- **React Router** (Navigation)
- **Recharts** (Data visualization)

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

1. **Java 17** or higher
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **Node.js 18+** and npm
   ```bash
   node -version
   npm -version
   ```

4. **MongoDB** (Local installation or MongoDB Atlas)
   - Local: Download from [MongoDB Download Center](https://www.mongodb.com/try/download/community)
   - Cloud: Create free account at [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)

## 🚀 Setup Instructions

### Step 1: Clone/Download the Project

Navigate to the project directory:
```bash
cd AJP-Pro
```

### Step 2: MongoDB Setup

#### Option A: Local MongoDB
1. Install MongoDB Community Edition
2. Start MongoDB service:
   ```bash
   # Windows
   net start MongoDB
   
   # Linux/Mac
   sudo systemctl start mongod
   ```
3. MongoDB will run on `mongodb://localhost:27017`

#### Option B: MongoDB Atlas (Cloud)
1. Create a free account at [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Create a new cluster
3. Get your connection string
4. Update `backend/src/main/resources/application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/drims
   ```

### Step 3: Backend Setup

1. Navigate to backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

   Or using the JAR file:
   ```bash
   mvn clean package
   java -jar target/drims-backend-1.0.0.jar
   ```

4. Backend will start on `http://localhost:8080`

### Step 4: Frontend Setup

1. Open a new terminal and navigate to frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. Frontend will start on `http://localhost:5173`

### Step 5: Access the Application

Open your browser and navigate to:
```
http://localhost:5173
```

## 🔐 Default Credentials

The system automatically creates sample users on first startup:

### Admin Account
- **Email:** `admin@drims.edu`
- **Password:** `admin123`
- **Role:** ADMIN

### Faculty Accounts
- **Default Password:** `faculty123` (for all faculty)
- **Role:** FACULTY
- **Email Format:** `firstname.lastname@drims.edu`

**Complete Faculty Credentials:**
All faculty credentials are stored in separate files for easy reference:

- **`FACULTY_CREDENTIALS_COMPLETE.md`** - Complete list of all 109+ faculty members
- **`FACULTY_CREDENTIALS_PART1.md`** - Faculty members 1-40
- **`FACULTY_CREDENTIALS_PART2.md`** - Faculty members 41-109+

**Example faculty emails:**
- `renugadevi.r@drims.edu` / `faculty123`
- `sourav.mondal@drims.edu` / `faculty123`
- `dr.md.oqail.ahmad@drims.edu` / `faculty123`

**Note:** The system automatically creates all faculty accounts on first startup based on Excel data.

## 📁 Project Structure

```
AJP-Pro/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/drims/
│   │   │   │   ├── controller/     # REST Controllers
│   │   │   │   ├── service/       # Business Logic
│   │   │   │   ├── repository/    # MongoDB Repositories
│   │   │   │   ├── entity/        # MongoDB Documents
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── security/      # JWT & Security Config
│   │   │   │   ├── config/        # Configuration Classes
│   │   │   │   └── DRIMSApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/    # Reusable Components
│   │   ├── pages/         # Page Components
│   │   ├── services/      # API Services
│   │   ├── App.jsx        # Main App Component
│   │   └── main.jsx       # Entry Point
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

## 🔧 Maven Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn clean package

# Run application
mvn spring-boot:run

# Skip tests during build
mvn clean package -DskipTests

# Install dependencies
mvn clean install
```

## 📦 NPM Commands

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## 🗄️ MongoDB Schema

### Collections

1. **users** - User authentication data
2. **faculty_profiles** - Faculty profile information
3. **targets** - Yearly research targets
4. **journals** - Journal publications
5. **conferences** - Conference publications
6. **patents** - Patent records
7. **book_chapters** - Book chapter publications

### Document Structure Example

**User:**
```json
{
  "_id": "...",
  "email": "faculty@drims.edu",
  "password": "$2a$10$...",
  "role": "FACULTY",
  "facultyId": "...",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

**FacultyProfile:**
```json
{
  "_id": "...",
  "employeeId": "EMP001",
  "name": "Dr. John Doe",
  "designation": "Professor",
  "department": "Computer Science",
  "researchAreas": ["Machine Learning", "AI"],
  "orcidId": "0000-0000-0000-0000",
  "scopusId": "1234567890",
  "googleScholarLink": "https://...",
  "email": "faculty@drims.edu",
  "userId": "..."
}
```

## 🔒 Security Features

- JWT-based authentication
- BCrypt password hashing
- Role-based access control (RBAC)
- CORS configuration
- Secure file upload validation (PDF only)
- Authorization checks on all endpoints

## 📊 API Endpoints

### Authentication
- `POST /api/auth/login` - User login

### Faculty Endpoints (Requires FACULTY or ADMIN role)
- `GET /api/faculty/profile` - Get own profile
- `PUT /api/faculty/profile` - Update own profile
- `GET /api/faculty/targets` - Get own targets
- `POST /api/faculty/targets` - Create/update target
- `GET /api/faculty/journals` - Get own journals
- `POST /api/faculty/journals` - Create journal
- `PUT /api/faculty/journals/{id}` - Update journal
- `DELETE /api/faculty/journals/{id}` - Delete journal
- Similar endpoints for conferences, patents, book-chapters
- `POST /api/faculty/upload/{category}/{id}` - Upload PDF

### Admin Endpoints (Requires ADMIN role)
- `GET /api/admin/faculty-profiles` - Get all profiles
- `GET /api/admin/targets` - Get all targets
- `GET /api/admin/journals` - Get all journals
- `GET /api/admin/conferences` - Get all conferences
- `GET /api/admin/patents` - Get all patents
- `GET /api/admin/book-chapters` - Get all book chapters
- `GET /api/admin/analytics` - Get analytics data
- `GET /api/admin/export?year={year}&category={category}` - Export to Excel

## 🚢 Deployment Guide

### Backend Deployment

#### Option 1: JAR File
```bash
cd backend
mvn clean package
java -jar target/drims-backend-1.0.0.jar
```

#### Option 2: Docker
Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/drims-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:
```bash
docker build -t drims-backend .
docker run -p 8080:8080 drims-backend
```

#### Option 3: Cloud Platforms
- **Heroku**: Use Heroku CLI and deploy JAR
- **AWS Elastic Beanstalk**: Upload JAR file
- **Google Cloud Run**: Containerize and deploy
- **Azure App Service**: Deploy JAR or container

### Frontend Deployment

#### Option 1: Build Static Files
```bash
cd frontend
npm run build
# Serve the 'dist' folder using any static file server
```

#### Option 2: Vercel/Netlify
```bash
npm run build
# Deploy 'dist' folder to Vercel or Netlify
```

#### Option 3: Nginx
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/frontend/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

### Environment Variables

Update `application.properties` for production:
```properties
# MongoDB
spring.data.mongodb.uri=${MONGODB_URI}

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# File Upload
file.upload.dir=${UPLOAD_DIR}

# CORS
cors.allowed-origins=${ALLOWED_ORIGINS}
```

## 🐛 Troubleshooting

### Backend Issues

1. **Port 8080 already in use:**
   - Change port in `application.properties`: `server.port=8081`

2. **MongoDB connection failed:**
   - Check MongoDB is running
   - Verify connection string in `application.properties`
   - Check network/firewall settings

3. **JWT token errors:**
   - Ensure JWT secret is set in `application.properties`
   - Check token expiration settings

### Frontend Issues

1. **CORS errors:**
   - Verify backend CORS configuration
   - Check `cors.allowed-origins` in `application.properties`

2. **API connection failed:**
   - Verify backend is running on port 8080
   - Check API base URL in `frontend/src/services/api.js`

3. **Build errors:**
   - Delete `node_modules` and `package-lock.json`
   - Run `npm install` again

## 📝 Notes

- File uploads are stored in the `uploads` directory (configurable)
- Only PDF files are accepted for document uploads
- JWT tokens expire after 24 hours (configurable)
- All passwords are hashed using BCrypt
- Faculty can only view/edit their own data
- Admin has read-only access to all data

## 🤝 Contributing

This is an academic project. For improvements:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📄 License

This project is created for academic purposes.

## 👨‍💻 Author

Developed as part of Advanced Java Programming (AJP) project.

## 🎓 Academic Use

This project demonstrates:
- Full-stack development with Java and React
- RESTful API design
- JWT authentication and authorization
- MongoDB NoSQL database usage
- Modern UI/UX with Tailwind CSS
- Data visualization with charts
- Excel export functionality
- File upload handling
- Role-based access control

---

**For any issues or questions, please refer to the code comments or contact the development team.**

