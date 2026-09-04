# Field Service Management Platform

A full-stack web application designed to manage customers, service sites, work orders, technicians, work-order status, parts, notifications, and role-based access in a centralized system.

---

## 📌 Project Overview

The **Field Service Management Platform** is a web-based application developed to simplify and organize field-service operations.

The system allows organizations to manage customers and their service locations, create and assign work orders to technicians, monitor job progress, maintain status history, and provide different dashboards according to user roles.

The application follows a layered architecture with a **Spring Boot REST API**, **MySQL database**, and **React frontend**.

---

## 🎯 Objectives

- Manage customers and service sites efficiently.
- Create and manage work orders.
- Assign work orders to technicians.
- Track technician work using a Kanban board.
- Maintain work-order status history.
- Manage parts and inventory information.
- Provide role-based dashboards.
- Secure APIs using JWT authentication.
- Control access using role-based authorization.
- Deploy the application on cloud platforms.

---

## 🚀 Features

### Customer Management
- Create and manage customer records.
- View customer information.
- Maintain customer-related service sites.

### Site Management
- Create service sites for customers.
- View and manage site information.
- Restrict customer access to their own sites.

### Work Order Management
- Create work orders.
- Set priority and status.
- Assign technicians.
- View work-order details.
- Track SLA due date.
- Update work-order status.

### Technician Kanban
- View technician work orders.
- Display jobs according to their current status.
- Start assigned jobs.
- Put jobs on hold.
- Resume jobs.
- Complete jobs.

### Status & History
- Track work-order status changes.
- Store previous and new status.
- Record the user who changed the status.
- Maintain a chronological status history.

### Parts & Inventory
- Manage parts information.
- Track parts used for field-service work.

### Notifications
- Dedicated notification module for system/work-order notifications.

### Role-Based Dashboard
Different users receive different dashboards and access permissions according to their roles.

---

## 👥 User Roles

| Role | Description |
|------|-------------|
| ADMIN | Full system administration |
| MANAGER | Management and monitoring operations |
| DISPATCHER | Work-order coordination and technician assignment |
| TECHNICIAN | Manage assigned work orders and update job status |
| CUSTOMER | View own sites, work orders and related information |

---

## 🛠️ Technology Stack

### Backend
- Java 17
- Spring Boot 4.1.0
- Spring Security
- JWT Authentication
- BCrypt Password Hashing
- Spring Data JPA
- Hibernate
- Maven
- REST APIs

### Frontend
- React
- Vite
- JavaScript
- HTML5
- CSS3

### Database
- MySQL 8

### API Documentation
- OpenAPI
- Swagger UI

### Deployment
- Railway
- Vercel

---

## 🏗️ Project Architecture

The project follows a layered architecture:

```text
React Frontend
       │
       ▼
REST API
       │
       ▼
Controller Layer
       │
       ▼
Service Layer
       │
       ▼
Repository Layer
       │
       ▼
MySQL Database