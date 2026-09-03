import { useState } from "react";

import "./App.css";

import Sites from "./Sites";
import WorkOrders from "./WorkOrders";
import CreateWorkOrder from "./CreateWorkOrder";
import Parts from "./Parts";
import TechnicianKanban from "./TechnicianKanban";
import Notifications from "./Notifications";

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
  // USER INFORMATION
  // =========================================================

  const userEmail = localStorage.getItem("email");
  const userRole = localStorage.getItem("role");
  const userId = localStorage.getItem("userId");

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

      const responseText = await response.text();

      console.log("Login Status:", response.status);
      console.log("Login Response:", responseText);

      // =====================================================
      // LOGIN ERROR
      // =====================================================

      if (!response.ok) {

        let errorMessage = "Login failed";

        try {

          const errorData = JSON.parse(responseText);

          errorMessage =
            errorData.message ||
            errorData.error ||
            "Login failed";

        } catch {

          errorMessage =
            responseText || "Login failed";
        }

        alert(errorMessage);

        return;
      }

      // =====================================================
      // LOGIN SUCCESS
      // =====================================================

      const data = JSON.parse(responseText);

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
        "Unable to connect to server. Please try again."
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
  // NOTIFICATIONS PAGE
  // =========================================================

  if (
    isLoggedIn &&
    currentPage === "notifications"
  ) {

    return (

      <div>

        <DashboardHeader />

        <BackToDashboard />

        <Notifications />

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
              WELCOME SECTION
          ================================================= */}

          <div className="welcome-section">

            <h2>
              Welcome back 👋
            </h2>

            <p>
              Manage your field service activities
              from your dashboard.
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
              ADMIN DASHBOARD
          ================================================= */}

          {userRole === "ADMIN" && (

            <>

              <div className="dashboard-section-title">

                <h2>
                  Admin Dashboard
                </h2>

                <p>
                  Manage the complete field service system.
                </p>

              </div>

              <div className="dashboard-grid">


                {/* SITES */}

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


                {/* WORK ORDERS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📋
                  </div>

                  <h3>
                    Work Orders
                  </h3>

                  <p>
                    View and manage all work orders
                    in the system.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View Work Orders
                  </button>

                </div>


                {/* CREATE WORK ORDER */}

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


                {/* PARTS */}

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


                {/* TECHNICIAN KANBAN */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🧑‍🔧
                  </div>

                  <h3>
                    Technician Kanban
                  </h3>

                  <p>
                    Monitor technician work orders
                    using the Kanban board.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("technician-kanban")
                    }
                  >
                    Open Kanban
                  </button>

                </div>


                {/* STATUS & HISTORY */}

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


                {/* NOTIFICATIONS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🔔
                  </div>

                  <h3>
                    Notifications
                  </h3>

                  <p>
                    View SLA breach alerts and
                    important notifications.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("notifications")
                    }
                  >
                    View Notifications
                  </button>

                </div>

              </div>

            </>
          )}


          {/* =================================================
              MANAGER DASHBOARD
          ================================================= */}

          {userRole === "MANAGER" && (

            <>

              <div className="dashboard-section-title">

                <h2>
                  Manager Dashboard
                </h2>

                <p>
                  Manage service operations and
                  monitor field activities.
                </p>

              </div>

              <div className="dashboard-grid">


                {/* SITES */}

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


                {/* WORK ORDERS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📋
                  </div>

                  <h3>
                    Work Orders
                  </h3>

                  <p>
                    Monitor and manage service
                    work orders.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View Work Orders
                  </button>

                </div>


                {/* CREATE WORK ORDER */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    ➕
                  </div>

                  <h3>
                    Create Work Order
                  </h3>

                  <p>
                    Create work orders and assign
                    technicians.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("create-workorder")
                    }
                  >
                    Create Work Order
                  </button>

                </div>


                {/* PARTS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🔧
                  </div>

                  <h3>
                    Parts
                  </h3>

                  <p>
                    Manage parts and track inventory
                    usage.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("parts")
                    }
                  >
                    Manage Parts
                  </button>

                </div>


                {/* TECHNICIAN KANBAN */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🧑‍🔧
                  </div>

                  <h3>
                    Technician Kanban
                  </h3>

                  <p>
                    Monitor technician workload and
                    assigned jobs.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("technician-kanban")
                    }
                  >
                    Open Kanban
                  </button>

                </div>


                {/* STATUS & HISTORY */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📊
                  </div>

                  <h3>
                    Status & History
                  </h3>

                  <p>
                    Track work order progress and
                    service history.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View History
                  </button>

                </div>


                {/* NOTIFICATIONS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🔔
                  </div>

                  <h3>
                    Notifications
                  </h3>

                  <p>
                    View important alerts and
                    notifications.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("notifications")
                    }
                  >
                    View Notifications
                  </button>

                </div>

              </div>

            </>
          )}


          {/* =================================================
              DISPATCHER DASHBOARD
          ================================================= */}

          {userRole === "DISPATCHER" && (

            <>

              <div className="dashboard-section-title">

                <h2>
                  Dispatcher Dashboard
                </h2>

                <p>
                  Coordinate work orders and
                  technician activities.
                </p>

              </div>

              <div className="dashboard-grid">


                {/* WORK ORDERS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📋
                  </div>

                  <h3>
                    Work Orders
                  </h3>

                  <p>
                    View and coordinate work orders
                    across the service team.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View Work Orders
                  </button>

                </div>


                {/* CREATE WORK ORDER */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    ➕
                  </div>

                  <h3>
                    Create Work Order
                  </h3>

                  <p>
                    Create new work orders and
                    assign them to technicians.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("create-workorder")
                    }
                  >
                    Create Work Order
                  </button>

                </div>


                {/* TECHNICIAN KANBAN */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🧑‍🔧
                  </div>

                  <h3>
                    Technician Kanban
                  </h3>

                  <p>
                    Monitor technician workloads
                    and assigned jobs.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("technician-kanban")
                    }
                  >
                    Open Kanban
                  </button>

                </div>


                {/* SITES */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📍
                  </div>

                  <h3>
                    Sites
                  </h3>

                  <p>
                    View customer service locations
                    and site information.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("sites")
                    }
                  >
                    View Sites
                  </button>

                </div>


                {/* STATUS & HISTORY */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📊
                  </div>

                  <h3>
                    Status & History
                  </h3>

                  <p>
                    Track work order status and
                    service history.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View Status
                  </button>

                </div>


                {/* NOTIFICATIONS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🔔
                  </div>

                  <h3>
                    Notifications
                  </h3>

                  <p>
                    View important work order
                    and SLA notifications.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("notifications")
                    }
                  >
                    View Notifications
                  </button>

                </div>

              </div>

            </>
          )}


          {/* =================================================
              TECHNICIAN DASHBOARD
          ================================================= */}

          {userRole === "TECHNICIAN" && (

            <>

              <div className="dashboard-section-title">

                <h2>
                  Technician Dashboard
                </h2>

                <p>
                  View and manage your assigned field work.
                </p>

              </div>

              <div className="dashboard-grid">


                {/* MY WORK ORDERS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📋
                  </div>

                  <h3>
                    My Work Orders
                  </h3>

                  <p>
                    View work orders assigned
                    to you.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View My Orders
                  </button>

                </div>


                {/* MY KANBAN */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🧑‍🔧
                  </div>

                  <h3>
                    My Kanban
                  </h3>

                  <p>
                    Manage your assigned work
                    using the Kanban board.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("technician-kanban")
                    }
                  >
                    Open Kanban
                  </button>

                </div>


                {/* STATUS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📊
                  </div>

                  <h3>
                    My Status & History
                  </h3>

                  <p>
                    Track the status and history
                    of your work orders.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View Status
                  </button>

                </div>


                {/* NOTIFICATIONS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🔔
                  </div>

                  <h3>
                    My Notifications
                  </h3>

                  <p>
                    View your SLA alerts and
                    work order notifications.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("notifications")
                    }
                  >
                    View Notifications
                  </button>

                </div>

              </div>

            </>
          )}


          {/* =================================================
              CUSTOMER DASHBOARD
          ================================================= */}

          {userRole === "CUSTOMER" && (

            <>

              <div className="dashboard-section-title">

                <h2>
                  Customer Dashboard
                </h2>

                <p>
                  View your service requests and
                  work order information.
                </p>

              </div>

              <div className="dashboard-grid">


                {/* MY SITES */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📍
                  </div>

                  <h3>
                    My Sites
                  </h3>

                  <p>
                    View your registered service
                    locations.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("sites")
                    }
                  >
                    View My Sites
                  </button>

                </div>


                {/* MY WORK ORDERS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📋
                  </div>

                  <h3>
                    My Work Orders
                  </h3>

                  <p>
                    View work orders related
                    to your service requests.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View My Orders
                  </button>

                </div>


                {/* STATUS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    📊
                  </div>

                  <h3>
                    Status & History
                  </h3>

                  <p>
                    Track the current status and
                    history of your work orders.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("workorders")
                    }
                  >
                    View Status
                  </button>

                </div>


                {/* NOTIFICATIONS */}

                <div className="dashboard-card">

                  <div className="card-icon">
                    🔔
                  </div>

                  <h3>
                    Notifications
                  </h3>

                  <p>
                    View important updates about
                    your service requests.
                  </p>

                  <button
                    onClick={() =>
                      setCurrentPage("notifications")
                    }
                  >
                    View Notifications
                  </button>

                </div>

              </div>

            </>
          )}

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