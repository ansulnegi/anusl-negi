/**
 * Portfolio Interactive Scripts
 * Author: Ansul (Full Stack Java Developer)
 * Integrates directly with PortfolioServer.java REST endpoints
 */

document.addEventListener("DOMContentLoaded", () => {
  initYear();
  initMobileNav();
  initScrollSpy();
  initProjectFilters();
  checkJavaServerStatus();
});

// 1. Set current year dynamically
function initYear() {
  const yearEl = document.getElementById("currentYear");
  if (yearEl) {
    yearEl.textContent = new Date().getFullYear();
  }
}

// 2. Mobile Navigation Toggle
function initMobileNav() {
  const toggleBtn = document.getElementById("navToggle");
  const navMenu = document.getElementById("navMenu");
  const navLinks = document.querySelectorAll(".nav-link");

  if (toggleBtn && navMenu) {
    toggleBtn.addEventListener("click", () => {
      navMenu.classList.toggle("active");
    });

    navLinks.forEach((link) => {
      link.addEventListener("click", () => {
        navMenu.classList.remove("active");
      });
    });
  }
}

// 3. Active Link Highlight on Scroll
function initScrollSpy() {
  const sections = document.querySelectorAll("section[id]");
  const navLinks = document.querySelectorAll(".nav-link");

  window.addEventListener("scroll", () => {
    let current = "";
    const scrollPosition = window.pageYOffset + 120;

    sections.forEach((section) => {
      const sectionTop = section.offsetTop;
      const sectionHeight = section.offsetHeight;
      if (scrollPosition >= sectionTop && scrollPosition < sectionTop + sectionHeight) {
        current = section.getAttribute("id");
      }
    });

    navLinks.forEach((link) => {
      link.classList.remove("active");
      if (link.getAttribute("href") === `#${current}`) {
        link.classList.add("active");
      }
    });
  });
}

// 4. Project Filtering by Category
function initProjectFilters() {
  const filterBtns = document.querySelectorAll(".filter-btn");
  const projectCards = document.querySelectorAll(".project-card");

  filterBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      filterBtns.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");

      const filter = btn.getAttribute("data-filter");

      projectCards.forEach((card) => {
        const categories = card.getAttribute("data-category") || "";
        if (filter === "all" || categories.includes(filter)) {
          card.style.display = "flex";
        } else {
          card.style.display = "none";
        }
      });
    });
  });
}

// 5. Check Java Backend Status
async function checkJavaServerStatus() {
  const badge = document.getElementById("serverBadge");
  if (!badge) return;

  const statusText = badge.querySelector(".status-text");

  try {
    const response = await fetch("/api/status", { method: "GET" });
    if (response.ok) {
      const data = await response.json();
      badge.className = "server-badge online";
      statusText.textContent = `Java Backend: Online (Port 8080)`;
      return true;
    }
  } catch (err) {
    // If running from file:// or server not started
    badge.className = "server-badge offline";
    statusText.textContent = "Java Backend: Offline (Run PortfolioServer.java)";
    return false;
  }
}

// 6. Interactive API Tester in the Live Terminal Section
async function queryApi(endpoint) {
  const outputEl = document.getElementById("apiOutput");
  if (!outputEl) return;

  outputEl.textContent = `// Sending GET request to ${endpoint}...\n`;

  try {
    const startTime = performance.now();
    const res = await fetch(endpoint);
    const duration = Math.round(performance.now() - startTime);

    if (res.ok) {
      const data = await res.json();
      outputEl.textContent = `// HTTP 200 OK (${duration}ms)\n// Endpoint: ${endpoint}\n\n` + JSON.stringify(data, null, 2);
    } else {
      outputEl.textContent = `// HTTP Error: ${res.status} ${res.statusText}\n// Ensure Java server is handling ${endpoint}`;
    }
  } catch (err) {
    // Fallback simulation when running directly via file://
    outputEl.textContent = 
      `// [Notice] Server not detected on current host.\n` +
      `// To run the live Java API server:\n` +
      `// 1. Open terminal in this folder\n` +
      `// 2. Run: javac PortfolioServer.java\n` +
      `// 3. Run: java PortfolioServer\n` +
      `// 4. Open: http://localhost:8080\n\n` +
      `// Simulated Response for ${endpoint}:\n` +
      getSimulatedResponse(endpoint);
  }
}

function getSimulatedResponse(endpoint) {
  if (endpoint === "/api/status") {
    return JSON.stringify({
      status: "UP",
      server: "PortfolioServer (Java 21)",
      uptimeSeconds: 142,
      freeMemoryMB: 284,
      totalMemoryMB: 512,
      activeThreads: 10
    }, null, 2);
  } else if (endpoint === "/api/profile") {
    return JSON.stringify({
      name: "Ansul",
      education: "B.Tech Computer Science & Engineering (2029)",
      role: "Full Stack Java Developer",
      primaryStack: ["Java 21", "Spring Boot", "HTML5", "CSS3", "JavaScript", "MySQL", "REST APIs"],
      interviewReady: true
    }, null, 2);
  } else if (endpoint === "/api/projects") {
    return JSON.stringify([
      { id: 1, title: "Custom Java Portfolio HTTP Server", tech: "Java 21, HttpServer" },
      { id: 2, title: "Enterprise E-Commerce REST API", tech: "Spring Boot, MySQL, JWT" },
      { id: 3, title: "Smart Task & Workflow Portal", tech: "Java, HTML5, CSS3, JS" },
      { id: 4, title: "Student Placement Record System", tech: "Java, JDBC, MySQL" }
    ], null, 2);
  }
  return '{"message": "Endpoint simulated"}';
}

function clearOutput() {
  const outputEl = document.getElementById("apiOutput");
  if (outputEl) {
    outputEl.textContent = "// Terminal cleared. Click an API button to test.";
  }
}

function testBackendApi() {
  const liveDemoSection = document.getElementById("live-demo");
  if (liveDemoSection) {
    liveDemoSection.scrollIntoView({ behavior: "smooth" });
  }
  queryApi("/api/status");
}

// 7. Contact Form Handler (POST to Java backend with fallback)
async function handleContactSubmit(event) {
  event.preventDefault();

  const submitBtn = document.getElementById("submitBtn");
  const feedback = document.getElementById("formFeedback");

  const name = document.getElementById("name").value.trim();
  const email = document.getElementById("email").value.trim();
  const subject = document.getElementById("subject").value.trim();
  const message = document.getElementById("message").value.trim();

  if (!name || !email || !subject || !message) {
    showFeedback("Please fill in all fields.", "error");
    return;
  }

  submitBtn.disabled = true;
  submitBtn.textContent = "Sending...";

  const payload = { name, email, subject, message, timestamp: new Date().toISOString() };

  try {
    const res = await fetch("/api/contact", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      const data = await res.json();
      showFeedback(`✓ ${data.message || "Message delivered successfully to Java Backend!"}`, "success");
      document.getElementById("contactForm").reset();
    } else {
      throw new Error("Server responded with error");
    }
  } catch (err) {
    // If server is not running, provide graceful fallback simulation
    setTimeout(() => {
      showFeedback("✓ Message received! (Standalone Demo Mode: Java server simulated response)", "success");
      document.getElementById("contactForm").reset();
    }, 600);
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = `<span>Send Message</span> <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="22" y1="2" x2="11" y2="13"></line><polygon points="22 2 15 22 11 13 2 9 22 2"></polygon></svg>`;
  }
}

function showFeedback(msg, type) {
  const feedback = document.getElementById("formFeedback");
  if (!feedback) return;
  feedback.textContent = msg;
  feedback.className = `form-feedback ${type}`;
  feedback.style.display = "block";

  setTimeout(() => {
    feedback.style.display = "none";
  }, 6000);
}

// 8. Copy Email Helper & Toast
function copyEmail() {
  const email = document.getElementById("emailVal").textContent;
  navigator.clipboard.writeText(email).then(() => {
    showToast(`Copied ${email} to clipboard!`);
  }).catch(() => {
    showToast("Email: " + email);
  });
}

function showToast(message) {
  const toast = document.getElementById("toast");
  if (!toast) return;
  toast.textContent = message;
  toast.classList.add("show");

  setTimeout(() => {
    toast.classList.remove("show");
  }, 3000);
}

