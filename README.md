# Witbank Community Service System

A Spring Boot web application designed to improve communication between citizens and local authorities by allowing residents to report community service issues, track complaint progress, and provide community feedback.

---

## Features

### User Features
- User Registration and Login
- Secure Authentication with Spring Security
- Submit Community Complaints
- Track Complaint Status
- Submit Feedback
- Delete Personal Account

### Admin Features
- Admin Dashboard
- Manage Complaints
- Update Complaint Status
- Delete and Restore Complaints
- Push Selected Community Feedback to Homepage
- Analytics Dashboard
- Report Generation Feature

### Security Features
- Spring Security Authentication
- Role-Based Authorization (ADMIN / USER)
- BCrypt Password Encryption
- Protected Routes and Sessions

---

## Technologies Used

### Backend
- Java
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA

### Frontend
- HTML5
- CSS3
- Thymeleaf

### Database
- MySQL

### Tools
- Maven
- Git
- GitHub
- VS Code

---

## System Modules

### Authentication Module
Handles:
- Registration
- Login
- Logout
- Password Encryption
- User Roles

### Complaint Management Module
Allows users to:
- Submit complaints
- View complaint history
- Track status updates

Allows admins to:
- Manage complaints
- Update statuses
- Restore deleted complaints

### Feedback Module
Users can:
- Submit feedback

Admins can:
- Approve selected feedback
- Publish feedback to homepage

### Analytics & Reports Module
Provides:
- Complaint statistics
- Community issue analysis
- Report generation for administrators

---

## Project Structure

```text
src/
 ├── controller/
 ├── model/
 ├── repository/
 ├── security/
 ├── service/
 ├── templates/
 └── static/
