import { useEffect, useMemo, useState } from "react";
import "./TechnicianKanban.css";

function TechnicianKanban() {

  // =========================================================
  // STATES
  // =========================================================

  const [workOrders, setWorkOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState(null);

  // =========================================================
  // FETCH MY WORK ORDERS
  // =========================================================

  const fetchMyWorkOrders = async () => {

    setLoading(true);

    try {

      const token = localStorage.getItem("token");

      if (!token) {

        alert("Please login again.");

        window.location.href = "/";

        return;
      }

      const response = await fetch(
        "https://confident-ambition-production-7bdb.up.railway.app/api/work-orders",
        {
          method: "GET",

          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );

      // =====================================================
      // UNAUTHORIZED
      // =====================================================

      if (
        response.status === 401 ||
        response.status === 403
      ) {

        alert(
          "Your session has expired or you are not authorized."
        );

        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        localStorage.removeItem("email");
        localStorage.removeItem("role");

        window.location.href = "/";

        return;
      }

      const data = await response.json();

      if (!response.ok) {

        alert(
          data.message ||
          "Unable to fetch work orders"
        );

        return;
      }

      setWorkOrders(data);

    } catch (error) {

      console.error(
        "Technician Work Orders Error:",
        error
      );

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {

      setLoading(false);
    }
  };

  // =========================================================
  // INITIAL LOAD
  // =========================================================

  useEffect(() => {

    fetchMyWorkOrders();

  }, []);

  // =========================================================
  // UPDATE STATUS
  // =========================================================

  const updateStatus = async (
    workOrder,
    newStatus
  ) => {

    const token = localStorage.getItem("token");

    if (!token) {

      alert("Please login again.");

      return;
    }

    let note = "";

    // =======================================================
    // NOTES
    // =======================================================

    if (newStatus === "IN_PROGRESS") {

      note =
        workOrder.status === "ON_HOLD"
          ? "Technician resumed the work"
          : "Technician started the work";

    }

    else if (newStatus === "ON_HOLD") {

      note = "Technician put the work on hold";

    }

    else if (newStatus === "COMPLETED") {

      note = "Technician completed the work";

    }

    try {

      setUpdatingId(workOrder.id);

      const response = await fetch(
        `https://confident-ambition-production-7bdb.up.railway.app/api/work-orders/${workOrder.id}/status`,
        {
          method: "POST",

          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          },

          body: JSON.stringify({
            status: newStatus,
            note: note
          })
        }
      );

      // =====================================================
      // AUTH ERROR
      // =====================================================

      if (
        response.status === 401 ||
        response.status === 403
      ) {

        alert(
          "You are not authorized for this action."
        );

        return;
      }

      const data = await response.json();

      if (!response.ok) {

        alert(
          data.message ||
          "Unable to update work order status"
        );

        return;
      }

      alert(
        `Work order status changed to ${newStatus}`
      );

      // =====================================================
      // REFRESH DATA
      // =====================================================

      await fetchMyWorkOrders();

    } catch (error) {

      console.error(
        "Status Update Error:",
        error
      );

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {

      setUpdatingId(null);
    }
  };

  // =========================================================
  // GROUP WORK ORDERS
  // =========================================================

  const columns = useMemo(() => {

    return {

      ASSIGNED: workOrders.filter(
        (item) => item.status === "ASSIGNED"
      ),

      IN_PROGRESS: workOrders.filter(
        (item) => item.status === "IN_PROGRESS"
      ),

      ON_HOLD: workOrders.filter(
        (item) => item.status === "ON_HOLD"
      ),

      COMPLETED: workOrders.filter(
        (item) => item.status === "COMPLETED"
      )

    };

  }, [workOrders]);

  // =========================================================
  // STATUS LABEL
  // =========================================================

  const statusLabel = (status) => {

    switch (status) {

      case "ASSIGNED":
        return "Assigned";

      case "IN_PROGRESS":
        return "In Progress";

      case "ON_HOLD":
        return "On Hold";

      case "COMPLETED":
        return "Completed";

      default:
        return status;
    }
  };

  // =========================================================
  // PRIORITY CLASS
  // =========================================================

  const priorityClass = (priority) => {

    switch (priority) {

      case "HIGH":
        return "tech-priority-high";

      case "MEDIUM":
        return "tech-priority-medium";

      case "LOW":
        return "tech-priority-low";

      default:
        return "tech-priority-default";
    }
  };

  // =========================================================
  // STATUS CLASS
  // =========================================================

  const statusClass = (status) => {

    switch (status) {

      case "ASSIGNED":
        return "tech-status-assigned";

      case "IN_PROGRESS":
        return "tech-status-progress";

      case "ON_HOLD":
        return "tech-status-hold";

      case "COMPLETED":
        return "tech-status-completed";

      default:
        return "";
    }
  };

  // =========================================================
  // RENDER CARD
  // =========================================================

  const renderCard = (workOrder) => {

    return (

      <div
        className="technician-job-card"
        key={workOrder.id}
      >

        {/* =================================================
            CARD HEADER
        ================================================= */}

        <div className="tech-card-header">

          <span className="tech-order-code">
            {workOrder.code}
          </span>

          <span
            className={`tech-priority-badge ${priorityClass(
              workOrder.priority
            )}`}
          >
            {workOrder.priority || "N/A"}
          </span>

        </div>

        {/* =================================================
            TITLE
        ================================================= */}

        <h3>
          {workOrder.title}
        </h3>

        {/* =================================================
            DESCRIPTION
        ================================================= */}

        <p className="tech-description">

          {workOrder.description ||
            "No description available."}

        </p>

        {/* =================================================
            DETAILS
        ================================================= */}

        <div className="tech-job-details">

          <div>
            <span>Customer</span>

            <strong>
              {workOrder.customerId
                ? `#${workOrder.customerId}`
                : "N/A"}
            </strong>
          </div>

          <div>
            <span>Site</span>

            <strong>
              {workOrder.siteId
                ? `#${workOrder.siteId}`
                : "N/A"}
            </strong>
          </div>

          <div>
            <span>SLA</span>

            <strong>
              {workOrder.slaDueAt ||
                "Not specified"}
            </strong>
          </div>

        </div>

        {/* =================================================
            STATUS
        ================================================= */}

        <div className="tech-card-status">

          <span
            className={`tech-status-badge ${statusClass(
              workOrder.status
            )}`}
          >
            {statusLabel(workOrder.status)}
          </span>

        </div>

        {/* =================================================
            ACTIONS
        ================================================= */}

        <div className="technician-actions">

          {/* ASSIGNED → START */}

          {workOrder.status === "ASSIGNED" && (

            <button
              className="tech-action-start"
              disabled={updatingId === workOrder.id}
              onClick={() =>
                updateStatus(
                  workOrder,
                  "IN_PROGRESS"
                )
              }
            >

              {updatingId === workOrder.id
                ? "Updating..."
                : "▶ Start Job"}

            </button>

          )}

          {/* IN PROGRESS → HOLD */}

          {workOrder.status === "IN_PROGRESS" && (

            <button
              className="tech-action-hold"
              disabled={updatingId === workOrder.id}
              onClick={() =>
                updateStatus(
                  workOrder,
                  "ON_HOLD"
                )
              }
            >

              {updatingId === workOrder.id
                ? "Updating..."
                : "⏸ Put On Hold"}

            </button>

          )}

          {/* IN PROGRESS → COMPLETE */}

          {workOrder.status === "IN_PROGRESS" && (

            <button
              className="tech-action-complete"
              disabled={updatingId === workOrder.id}
              onClick={() =>
                updateStatus(
                  workOrder,
                  "COMPLETED"
                )
              }
            >

              {updatingId === workOrder.id
                ? "Updating..."
                : "✓ Complete"}

            </button>

          )}

          {/* ON HOLD → RESUME */}

          {workOrder.status === "ON_HOLD" && (

            <button
              className="tech-action-start"
              disabled={updatingId === workOrder.id}
              onClick={() =>
                updateStatus(
                  workOrder,
                  "IN_PROGRESS"
                )
              }
            >

              {updatingId === workOrder.id
                ? "Updating..."
                : "▶ Resume Job"}

            </button>

          )}

        </div>

      </div>

    );
  };

  // =========================================================
  // UI
  // =========================================================

  return (

    <div className="technician-kanban-page">

      {/* =====================================================
          HEADER
      ===================================================== */}

      <header className="technician-kanban-header">

        <div>

          <span className="technician-label">
            TECHNICIAN PORTAL
          </span>

          <h1>
            My Assigned Jobs
          </h1>

          <p>
            View and manage work orders assigned to you.
          </p>

        </div>

        <button
          className="technician-refresh"
          onClick={fetchMyWorkOrders}
          disabled={loading}
        >
          ↻ Refresh
        </button>

      </header>

      {/* =====================================================
          SUMMARY
      ===================================================== */}

      <div className="technician-summary">

        <div className="tech-summary-card">

          <span>
            Total Jobs
          </span>

          <strong>
            {workOrders.length}
          </strong>

        </div>

        <div className="tech-summary-card">

          <span>
            Assigned
          </span>

          <strong>
            {columns.ASSIGNED.length}
          </strong>

        </div>

        <div className="tech-summary-card">

          <span>
            In Progress
          </span>

          <strong>
            {columns.IN_PROGRESS.length}
          </strong>

        </div>

        <div className="tech-summary-card">

          <span>
            On Hold
          </span>

          <strong>
            {columns.ON_HOLD.length}
          </strong>

        </div>

        <div className="tech-summary-card">

          <span>
            Completed
          </span>

          <strong>
            {columns.COMPLETED.length}
          </strong>

        </div>

      </div>

      {/* =====================================================
          LOADING
      ===================================================== */}

      {loading ? (

        <div className="technician-loading">

          <div className="technician-loader"></div>

          <p>
            Loading your assigned jobs...
          </p>

        </div>

      ) : (

        /* ===================================================
           KANBAN
        =================================================== */

        <div className="technician-kanban">

          {/* ASSIGNED */}

          <div className="kanban-column">

            <div className="kanban-column-header assigned-header">

              <div>

                <h2>
                  Assigned
                </h2>

                <span>
                  Ready to start
                </span>

              </div>

              <strong>
                {columns.ASSIGNED.length}
              </strong>

            </div>

            <div className="kanban-column-body">

              {columns.ASSIGNED.length === 0 ? (

                <div className="kanban-empty">
                  No assigned jobs
                </div>

              ) : (

                columns.ASSIGNED.map(renderCard)

              )}

            </div>

          </div>

          {/* IN PROGRESS */}

          <div className="kanban-column">

            <div className="kanban-column-header progress-header">

              <div>

                <h2>
                  In Progress
                </h2>

                <span>
                  Currently working
                </span>

              </div>

              <strong>
                {columns.IN_PROGRESS.length}
              </strong>

            </div>

            <div className="kanban-column-body">

              {columns.IN_PROGRESS.length === 0 ? (

                <div className="kanban-empty">
                  No jobs in progress
                </div>

              ) : (

                columns.IN_PROGRESS.map(renderCard)

              )}

            </div>

          </div>

          {/* ON HOLD */}

          <div className="kanban-column">

            <div className="kanban-column-header hold-header">

              <div>

                <h2>
                  On Hold
                </h2>

                <span>
                  Waiting / paused
                </span>

              </div>

              <strong>
                {columns.ON_HOLD.length}
              </strong>

            </div>

            <div className="kanban-column-body">

              {columns.ON_HOLD.length === 0 ? (

                <div className="kanban-empty">
                  No jobs on hold
                </div>

              ) : (

                columns.ON_HOLD.map(renderCard)

              )}

            </div>

          </div>

          {/* COMPLETED */}

          <div className="kanban-column">

            <div className="kanban-column-header completed-header">

              <div>

                <h2>
                  Completed
                </h2>

                <span>
                  Finished jobs
                </span>

              </div>

              <strong>
                {columns.COMPLETED.length}
              </strong>

            </div>

            <div className="kanban-column-body">

              {columns.COMPLETED.length === 0 ? (

                <div className="kanban-empty">
                  No completed jobs
                </div>

              ) : (

                columns.COMPLETED.map(renderCard)

              )}

            </div>

          </div>

        </div>

      )}

    </div>

  );
}

export default TechnicianKanban;