# 🚀 Ansul | Full Stack Java Developer Portfolio

A modern, responsive, interview-ready portfolio designed for **Full Stack Java Developer** roles. It is engineered with **HTML5**, **CSS3**, **JavaScript**, and an embedded **Java 21 HTTP Server** (`PortfolioServer.java`).

---

## 📁 Project Structure

```text
ansul_betchCSE_29/
├── index.html            # Semantic, accessible HTML5 structure
├── style.css             # Responsive modern CSS3 (Flexbox, Grid, CSS Variables)
├── script.js             # Interactive client script & REST API integration
├── PortfolioServer.java  # Lightweight multithreaded Java HTTP Server & REST API
├── start_portfolio.bat   # 1-click Windows launcher (compiles & opens browser)
└── README.md             # Interview preparation guide & talking points
```

---

## ⚡ How to Run the Portfolio

You have **two easy ways** to run and present this project:

### Option 1: 1-Click Launch (Recommended for Windows)
Double-click `start_portfolio.bat`. It will automatically:
1. Compile `PortfolioServer.java`
2. Start the multithreaded backend on `http://localhost:8080`
3. Launch your browser with the live portfolio!

### Option 2: Command Line (Manual)
```bash
# 1. Compile the Java server
javac PortfolioServer.java

# 2. Start the server
java PortfolioServer

# 3. Open your browser at:
http://localhost:8080
```

*(Note: You can also double-click `index.html` directly in any browser for standalone static viewing.)*

---

## 🎯 Why This Project Makes a Great Impression in Interviews

1. **True Full Stack Proof**: Most student portfolios are just static HTML/CSS templates. Yours contains an actual **Java Backend (`PortfolioServer.java`)** that you can open and run live in front of the interviewer.
2. **Zero-Dependency Architecture**: Built on standard `com.sun.net.httpserver` without needing 500MB of external libraries, proving deep understanding of core Java and HTTP protocols.
3. **Live Interactive Terminal**: Interviewers can click buttons in the UI to ping `/api/status`, `/api/profile`, and `/api/projects`, showing real-time client-to-server JSON communication.
4. **Clean Code & Design Principles**:
   - Encapsulation & Separation of Concerns
   - Concurrency with a fixed worker thread pool
   - Responsive CSS with variables and modern glassmorphism
   - Form submission handling with CORS and input validation

---

## 🎤 Interview Questions & Model Answers

### Q1: "Can you walk us through the architecture of your portfolio?"
> **Model Answer:**
> *"Certainly. My portfolio is architected as a lightweight full-stack application. On the frontend, I used semantic HTML5 and CSS3 with CSS Grid and custom variables to ensure full responsiveness across mobile and desktop devices.*
> *On the backend, I wrote a multi-threaded HTTP server in Java using `com.sun.net.httpserver`. It serves the frontend static assets with appropriate MIME types and security checks, and exposes RESTful endpoints like `/api/status` for health diagnostics and `/api/contact` for processing inquiries via asynchronous Fetch API calls."*

### Q2: "How did you handle concurrency in your Java server?"
> **Model Answer:**
> *"In `PortfolioServer.java`, I configured an executor using `Executors.newFixedThreadPool(10)`. By assigning a thread pool executor to the `HttpServer`, each incoming HTTP request is delegated to an available worker thread. This prevents long-running file I/O or network calls from blocking other clients, providing consistent latency and high throughput."*

### Q3: "How does the backend prevent Directory Traversal attacks?"
> **Model Answer:**
> *"In the `StaticFileHandler`, before resolving any file from the file system, I sanitize the incoming URI path by stripping out parent directory references (`..`) and ensuring the path resolves only within the designated web root. Additionally, directory browsing is blocked to avoid leaking internal file structures."*

### Q4: "Why choose Java for backend development over Node.js or Python?"
> **Model Answer:**
> *"Java provides strong compile-time type safety, exceptional multi-threading capabilities, robust garbage collection, and a mature enterprise ecosystem (like Spring Boot and Hibernate). For large enterprise systems handling transactional integrity, banking, or high-throughput microservices, the JVM's performance and stability make it an industry benchmark."*

---

## ✏️ Customizing Your Information

To personalize this portfolio with your exact details:
1. **Name & Social Links**: Open `index.html` and update:
   - Line 25: Your name & role
   - Lines 360-390: Your LinkedIn, GitHub, LeetCode profile URLs
   - Line 350: Your actual email address
2. **College Details**: In `index.html` under the `education` section, replace `"Engineering University / College"` with your university name.
3. **Resume Download**: Place your PDF resume in this folder as `resume.pdf` and link it to the Download button in the Hero section.

---

**Best of luck with your interviews! You've got this!** 🚀

