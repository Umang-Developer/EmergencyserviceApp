# Emergency Service App

A feature-rich JavaFX-based desktop application for managing emergency service calls with real-time user-admin interaction.

## 📌 Key Features

- ✅ Register emergency calls with:
  - Caller name, contact number, description
  - Selection of one or more services: Fire, Police, Ambulance
- 💬 **Chatbox** system:
  - Real-time user-to-admin messaging using Java Sockets
  - Toggle views (admin/user) with message history
- 🧠 AI Assistant:
  - Suggests help options and responses in the chatbox
- 📊 View all calls:
  - Filter by service type (e.g., only Police or Fire calls)
  - Display calls in a JavaFX TableView
- 🖥️ JavaFX GUI:
  - Clean, modern interface with alerts, placeholder texts, and styled layout
- 📝 Input Validation:
  - UK-style phone numbers (`+44`)
  - Only digits for contact number
  - Only letters for caller name
- 💾 Data Persistence:
  - Stores call data in `.dat` files
  - Automatically updates UI and state after each operation

## 🛠️ Technologies Used

- Java 8
- JavaFX
- Java Socket Programming (TCP Chat)
- File I/O
- IntelliJ IDEA

## 📦 Future Enhancements

- Integration with cloud databases (MongoDB/Firebase)
- Admin dashboard analytics
- Multi-admin broadcast chat
