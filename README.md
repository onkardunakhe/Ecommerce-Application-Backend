# Ecommerce Backend with AI Assistant

A robust and scalable backend system for an e-commerce platform that handles **user management, authentication/authorization, product catalog, order processing, payment integration, and refund handling** — similar to a backend for a private online store.
This project also integrates an AI-powered product assistant that allows users to search and explore products using natural language queries.

---

## 🧩 Problem Statement
E-commerce platforms often struggle with secure user management, role-based access, and seamless payment handling when relying on generic third-party backends. This project provides a customizable, secure, and fully managed backend that gives merchants full control over users, orders, and payments while supporting modern authentication like JWT and Google OAuth2.
This project solves both problems by providing:
A secure backend system for e-commerce management
An AI-powered assistant that understands natural language queries to search products

---

## 💡 Solution Architecture
The **Ecommerce Application Backend** provides:

- **User Authentication:** JWT-secured registration, login, and role-based access control.  
- **Product Management:** Full CRUD operations for products with images.
- **Cart Management:**Add Update,Delete Products from cart with quantity 
- **Order Management:** Create, update, and track order status.  
- **Payment Integration:** Process payments and handle refunds for cancelled orders.  
- **Access Control:** Users can access only their own orders and data.  
- **RESTful APIs:** Clean and secure endpoints for frontend integration.  
- **Database Management:** Configurable for MySQL or PostgreSQL with JPA/Hibernate.  
🤖 AI Assistant
The backend also includes an AI-powered product assistant built using Spring AI and Ollama.
The assistant uses LLM Tool Calling to retrieve real-time product data directly from the database.
Capabilities include:
Natural language product search
Price-based product filtering
Category-based product discovery
Real-time database queries through AI tools
---

## 🔧 Tech Stack

### 🖥️ Backend
- Java 21
- Spring Boot
- Spring Security (JWT)
- SpringAI
- Ollama
- Hibernate & JPA
- MySQL / PostgreSQL
- Maven
- REST APIs

### 📦 Others
- JUnit & Mockito for testing
- Spring DevTools for development
- Spring Security
- Spring starter Oauth2
- Spring OpenApi Swaggwer
- Environment variables for sensitive data  

---

## 🚀 Features

- 🔐 **Secure user registration and login** using JWT  
- 👤 **Role-based authorization** (`ROLE_USER`, `ROLE_ADMIN`)  
- 🌐 **Google OAuth2 login integration**  
- 📦 **CRUD operations for products**  
- 🛒 **Order processing and status updates**  
- 💳 **Payment integration with automated refund handling**  
- 📜 **View all orders** with **user-specific data access**  
- ⚙️ **Environment-based configuration**  
- 🔧 **Testing with JUnit & Mockito**
- AI Features
- AI-powered product search using natural language
- Tool calling to fetch real-time product data from database
- Search products by name, category, and price range
- Optimized AI responses using DTO-based data retrieval 


## ⚙️ Installation & Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/onkardunakhe/Ecommerce-Application-Backend.git
   cd Ecommerce-Application-Backend
2. Configure the application:
    Copy the example properties file and update it according to your system:
    cp src/main/resources/application-example.properties src/main/resources/application.properties
3. Build and run the application:
   mvn clean install
   mvn spring-boot:run
4. Test APIs using **Swagger UI** or integrate with your frontend or test with Postman:  
     After running the application, open your browser and go to(for swagger):  
     http://localhost:8080/swagger-ui.html
---

## ✨ Future Improvements
  - 🔐 **OTP-based Login / Two-Factor Authentication (2FA)**  
    Enhance security by allowing users to log in with one-time passwords via email or SMS
  - 📈 **Analytics Dashboard**  
    Provide insights on orders, revenue, and user activity.
   - 💾 **Backup & Restore Functionality**  
     Allow database backups and restoration to prevent data loss.
   - ☁️ **Cloud Deployment / Dockerization**  
     Make deployment easier using Docker or cloud platforms like AWS or Azure.

## 🙋‍♂️ Author
Onkar Dunakhe  
📧 onkardunakhe1@gmail.com  
🔗www.linkedin.com/in/onkar-dunakhe
