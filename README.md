
# Role-Based Discussion & Review Platform

A JavaFX-based, role-driven discussion and moderation platform demonstrating real-world software engineering practices, including MVC architecture, role-based access control, service and repository layers, and formal input validation.

Built to emphasize **clean architecture, maintainability, and scalability**, rather than a single-feature prototype.

---

## Why This Project Was Made

This project was designed to simulate a real application backend and GUI flow similar to a lightweight learning management or forum system. It focuses on how production software is structured: clear separation of concerns, role-based permissions, and testable business logic.

---

## Key Features

- **Role-Based Access Control**
  - Users may hold multiple roles and select which role to act as
  - Role-specific home views and permissions

- **User Lifecycle Management**
  - First-run administrator bootstrapping
  - Account creation with FSM-based validation
  - Profile updates and role-specific constraints

- **Discussion & Moderation System**
  - Student discussion boards
  - Staff review and moderation workflows
  - Post creation, storage, and search

- **Requests & Approvals**
  - User-submitted requests (e.g., role changes)
  - Staff/Admin approval pipelines

---

## Engineering & Architecture

- **Architecture:** MVC (Model–View–Controller)
- **Backend Design:** Service layer and repository pattern
- **Data Storage:** In-memory repositories (designed for easy persistence upgrades)
- **Validation:** Finite State Machines (FSMs) for usernames, emails, and passwords
- **Testing:** Unit tests for repository and service logic

---

## Technologies

- **Language:** Java  
- **UI:** JavaFX, CSS  
- **Testing:** JUnit  
- **Tools:** Git, GitHub  


---

## Notes

This project uses in-memory storage for instructional purposes. The architecture was intentionally designed so persistent storage (e.g., SQL or NoSQL databases) could be integrated with minimal refactoring.

---
