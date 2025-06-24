# 📲 UPI Transaction Analyzer

**UPI Transaction Analyzer** is a mobile application that helps users manage and analyze their UPI (Unified Payments Interface) transactions effortlessly. From detailed transaction summaries to insightful analytics, this app provides a complete financial snapshot of recent UPI activity—backed with smart visualizations and a clean UI.

---

## 🚀 Key Features

### ✅ Transaction Summary
Get a quick overview of your UPI activity, including total transaction count and total amount transacted.

### 🧾 Recent Transactions
Displays your latest UPI transactions in an easy-to-read format. Each entry includes:
- Date
- Amount
- UPI ID or merchant
- Type (Credit/Debit)

### 📅 7-Day and 30-Day Transaction History
- View all transactions from the **past 7 days** or **30 days**.
- Sort and filter based on amount, date, or UPI ID.
- Get detailed insights into your weekly and monthly financial activity.

### 📊 Reports & Analytics
Visualize your UPI spending trends through **bar graphs** that display:
- Daily total spend
- Weekly comparison
- Top spenders and receivers

Built-in analytics engine processes transaction data and presents meaningful insights.

---

## 📷 Screenshots

| Home Screen | Analytics View | Report View |
|-------------|----------------|-------------|
| ![Home Screen](https://github.com/user-attachments/assets/c00a961c-3bbe-41f1-a1af-723b22fc6060) | ![Analytics View](https://github.com/user-attachments/assets/e19ff653-0845-44bd-b3f4-cc745a733482) | ![Report View](https://github.com/user-attachments/assets/6dc3edf7-6357-4608-b8f0-02b4f6affc42) |

---

## 🛠️ Tech Stack

| Layer         | Technologies Used            |
|---------------|------------------------------|
| **Frontend**  | Android (Kotlin)             |
| **Backend**   | AWS Lambda, API Gateway, Express.js, MongoDB |
| **Data Sync** | Local SMS Parsing + REST API |
| **Analytics** | MPAndroidChart (Android)     |
| **Auth**      | Firebase Authentication      |
| **Hosting**   | AWS Cloud                    |

---

## ☁️ How We Used AWS in Our Backend

Our backend is fully serverless and deployed using **AWS Lambda**, integrated with **Amazon API Gateway**, and built using **Express.js** for routing and business logic. Here's a breakdown of how each AWS service fits into the architecture:

### 🔹 AWS Lambda
- Every backend function (e.g., fetch transactions, generate reports, return analytics) is handled by individual Lambda functions.
- These functions are stateless, event-driven, and scale automatically—perfect for handling lightweight API calls from mobile apps.

### 🔹 API Gateway
- We use **Amazon API Gateway** to expose RESTful endpoints to the frontend.
- API Gateway routes requests (like `/getTransactions`, `/generateReport`) to the corresponding AWS Lambda function.
- Enables secure, scalable, and cost-efficient access to the backend from Android devices.

### 🔹 Express.js
- Our Express app is wrapped inside a Lambda function using the `aws-serverless-express` package.
- It allows us to use standard REST architecture and routing (`app.get`, `app.post`, etc.).
- Easy to structure and scale while staying familiar to developers.

### 🔹 MongoDB (Atlas)
- Used for storing all parsed and structured UPI transaction data.
- MongoDB allows fast queries, indexing, and aggregation required for analytics like bar charts and spending breakdowns.
- Connected securely using MongoDB Atlas with environment-protected credentials inside Lambda.

### ✅ Why This Architecture?
- **Cost-efficient**: Only pay when functions are called (no idle server time).
- **Scalable**: Auto-scales for any number of users without server maintenance.
- **Secure**: API Gateway with throttling and CORS, MongoDB with access control, Firebase Auth for user identity.

---

## 📚 Example Backend Flow

```text
[Mobile App]
     ↓
HTTP Request to API Gateway (e.g., /api/7days-transactions)
     ↓
API Gateway invokes AWS Lambda (Node.js + Express)
     ↓
Lambda function queries MongoDB Atlas
     ↓
Returns JSON response to mobile app
     ↓
Displayed in UI with Graph (MPAndroidChart)
