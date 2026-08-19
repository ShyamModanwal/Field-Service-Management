import { useState } from "react";

import "./App.css";

import Sites from "./Sites";
import WorkOrders from "./WorkOrders";
import CreateWorkOrder from "./CreateWorkOrder";
import Parts from "./Parts";
import TechnicianKanban from "./TechnicianKanban";


function App() {

  // =========================================================
  // LOGIN STATES
  // =========================================================

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [isLoggedIn, setIsLoggedIn] = useState(
    !!localStorage.getItem("token")
  );


  // =========================================================
  // PAGE NAVIGATION
  // =========================================================

  const [currentPage, setCurrentPage] = useState("dashboard");


  // =========================================================
  // LOGIN
  // =========================================================

  const handleLogin = async (event) => {

    event.preventDefault();

    try {

      const response = await fetch(
        "https://confident-ambition-production-7bdb.up.railway.app/api/auth/login",
        {
          method: "POST",

          headers: {
            "Content-Type": "application/json"
          },

          body: JSON.stringify({
            email: email,
            password: password
          })
        }
      );


      const data = await response.json();


      if (!response.ok) {

        alert(data.message || "Login failed");

        return;
      }


      // =====================================================
      // SAVE LOGIN INFORMATION
      // =====================================================

      localStorage.setItem("token", data.token);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("email", data.email);
      localStorage.setItem("role", data.role);


      alert("Login successful!");


      setIsLoggedIn(true);

      setCurrentPage("dashboard");


    } catch (error) {

      console.error("Login Error:", error);

      alert(
        "Unable to connect to server. Make sure Spring Boot is running."
      );

    }

  };


  // =========================================================
  // LOGOUT
  // =========================================================

  const handleLogout = () => {

    localStorage.removeItem("token");
    localStorage.removeItem("userId");
    localStorage.removeItem("email");
    localStorage.removeItem("role");

    setIsLoggedIn(false);

    setCurrentPage("dashboard");

    setEmail("");

    setPassword("");

  };


  // =========================================================
  // USER INFORMATION
  // =========================================================

  const userEmail = localStorage.getItem("email");

  const userRole = localStorage.getItem("role");

  const userId = localStorage.getItem("userId");


  // =========================================================
  // COMMON HEADER
  // =========================================================

  const DashboardHeader = () => {

    return (

      <header className="dashboard-header">

        <div>

          <h1>
            Field Service
          </h1>

          <p>
            Management System
          </p>

        </div>


        <div className="header-actions">

          <div className="logged-user">

            <span>
              {userEmail}
            </span>

            <small>
              {userRole}
            </small>

          </div>


          <button
            className="logout-button"
            onClick={handleLogout}
          >
            Logout
          </button>

        </div>

      </header>

    );

  };


  // =========================================================
  // COMMON BACK BUTTON
  // =========================================================

  const BackToDashboard = () => {

    return (

      <div className="page-navigation">

        <button
          className="back-button"
          onClick={() =>
            setCurrentPage("dashboard")
          }
        >
          ← Dashboard
        </button>

      </div>

    );

  };


  // =========================================================
  // SITES PAGE
  // =========================================================

  if (
    isLoggedIn &&
    currentPage === "sites"
  ) {

    return (

      <div>

        <DashboardHeader />

        <BackToDashboard />

        <Sites />

      </div>

    );

  }


  // =========================================================
  // WORK ORDERS PAGE
  // =========================================================

  if (
    isLoggedIn &&
    currentPage === "workorders"
  ) {

    return (

      <div>

        <DashboardHeader />

        <BackToDashboard />

        <WorkOrders />

      </div>

    );

  }


  // =========================================================
  // CREATE WORK ORDER PAGE
  // =========================================================

  if (
    isLoggedIn &&
    currentPage === "create-workorder"
  ) {

    return (

      <div>

        <DashboardHeader />

        <BackToDashboard />

        <CreateWorkOrder

          onBack={() =>
            setCurrentPage("dashboard")
          }

          onCreated={() =>
            setCurrentPage("workorders")
          }

        />

      </div>

    );

  }


  // =========================================================
  // PARTS PAGE
  // =========================================================

  if (
    isLoggedIn &&
    currentPage === "parts"
  ) {

    return (

      <div>

        <DashboardHeader />

        <BackToDashboard />

        <Parts />

      </div>

    );

  }


  // =========================================================
  // TECHNICIAN KANBAN PAGE
  // =========================================================

  if (
    isLoggedIn &&
    currentPage === "technician-kanban"
  ) {

    return (

      <div>

        <DashboardHeader />

        <BackToDashboard />

        <TechnicianKanban />

      </div>

    );

  }


  // =========================================================
  // DASHBOARD
  // =========================================================

  if (isLoggedIn) {

    return (

      <div className="dashboard-page">


        <DashboardHeader />


        <main className="dashboard-content">


          {/* =================================================
              WELCOME
          ================================================= */}

          <div className="welcome-section">

            <h2>
              Welcome back 👋
            </h2>

            <p>
              You are successfully logged in to the
              Field Service Management System.
            </p>

          </div>


          {/* =================================================
              ACCOUNT INFORMATION
          ================================================= */}

          <div className="user-info-card">

            <h3>
              Account Information
            </h3>


            <div className="user-info">


              <div>

                <span>
                  Email
                </span>

                <strong>
                  {userEmail}
                </strong>

              </div>


              <div>

                <span>
                  Role
                </span>

                <strong>
                  {userRole}
                </strong>

              </div>


              <div>

                <span>
                  User ID
                </span>

                <strong>
                  {userId}
                </strong>

              </div>


            </div>

          </div>


          {/* =================================================
              DASHBOARD CARDS
          ================================================= */}

          <div className="dashboard-grid">


            {/* =================================================
                SITES
            ================================================= */}

            <div className="dashboard-card">

              <div className="card-icon">
                📍
              </div>

              <h3>
                Sites
              </h3>

              <p>
                Manage customer locations and
                service sites.
              </p>

              <button
                onClick={() =>
                  setCurrentPage("sites")
                }
              >
                Manage Sites
              </button>

            </div>


            {/* =================================================
                WORK ORDERS
            ================================================= */}

            <div className="dashboard-card">

              <div className="card-icon">
                📋
              </div>

              <h3>
                Work Orders
              </h3>

              <p>
                Create, view, update and manage
                field service work orders.
              </p>

              <button
                onClick={() =>
                  setCurrentPage("workorders")
                }
              >
                View Work Orders
              </button>

            </div>


            {/* =================================================
                CREATE WORK ORDER
            ================================================= */}

            <div className="dashboard-card">

              <div className="card-icon">
                ➕
              </div>

              <h3>
                Create Work Order
              </h3>

              <p>
                Create a new work order and
                assign it to a technician.
              </p>

              <button
                onClick={() =>
                  setCurrentPage("create-workorder")
                }
              >
                Create Work Order
              </button>

            </div>


            {/* =================================================
                PARTS
            ================================================= */}

            <div className="dashboard-card">

              <div className="card-icon">
                🔧
              </div>

              <h3>
                Parts
              </h3>

              <p>
                Manage parts and track part
                usage for work orders.
              </p>

              <button
                onClick={() =>
                  setCurrentPage("parts")
                }
              >
                Manage Parts
              </button>

            </div>


            {/* =================================================
                TECHNICIAN KANBAN
            ================================================= */}

            <div className="dashboard-card">

              <div className="card-icon">
                🧑‍🔧
              </div>

              <h3>
                Technician Kanban
              </h3>

              <p>
                View assigned work orders in a
                technician-friendly Kanban board.
              </p>

              <button
                onClick={() =>
                  setCurrentPage("technician-kanban")
                }
              >
                Open Kanban
              </button>

            </div>


            {/* =================================================
                STATUS & HISTORY
            ================================================= */}

            <div className="dashboard-card">

              <div className="card-icon">
                📊
              </div>

              <h3>
                Status & History
              </h3>

              <p>
                Track work order status and
                status history.
              </p>

              <button
                onClick={() =>
                  setCurrentPage("workorders")
                }
              >
                View History
              </button>

            </div>


          </div>


        </main>


      </div>

    );

  }


  // =========================================================
  // LOGIN PAGE
  // =========================================================

  return (

    <div className="login-page">


      <div className="login-card">


        <div className="login-header">

          <h1>
            Field Service
          </h1>

          <p>
            Management System
          </p>

        </div>


        <form onSubmit={handleLogin}>


          {/* EMAIL */}

          <div className="form-group">

            <label>
              Email
            </label>

            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(event) =>
                setEmail(event.target.value)
              }
              required
            />

          </div>


          {/* PASSWORD */}

          <div className="form-group">

            <label>
              Password
            </label>

            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(event) =>
                setPassword(event.target.value)
              }
              required
            />

          </div>


          <button type="submit">
            Login
          </button>


        </form>


      </div>


    </div>

  );

}


export default App;