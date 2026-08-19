import { useEffect, useMemo, useState } from "react";
import "./WorkOrders.css";

function WorkOrders() {
  const [workOrders, setWorkOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [priorityFilter, setPriorityFilter] = useState("ALL");

  const [selectedWorkOrder, setSelectedWorkOrder] = useState(null);
  const [history, setHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(false);

  // =========================================================
  // FETCH WORK ORDERS
  // =========================================================

  const fetchWorkOrders = async () => {
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
            "Content-Type": "application/json",
          },
        }
      );

      if (response.status === 401 || response.status === 403) {
        alert("Your session has expired. Please login again.");

        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        localStorage.removeItem("email");
        localStorage.removeItem("role");

        window.location.href = "/";
        return;
      }

      const data = await response.json();

      if (!response.ok) {
        alert(data.message || "Unable to fetch work orders");
        return;
      }

      setWorkOrders(data);
    } catch (error) {
      console.error("Work Orders Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );
    } finally {
      setLoading(false);
    }
  };

  // =========================================================
  // LOAD WORK ORDERS
  // =========================================================

  useEffect(() => {
    fetchWorkOrders();
  }, []);

  // =========================================================
  // FILTER WORK ORDERS
  // =========================================================

  const filteredWorkOrders = useMemo(() => {
    return workOrders.filter((workOrder) => {
      const searchText = search.toLowerCase();

      const matchesSearch =
        String(workOrder.id)
          .toLowerCase()
          .includes(searchText) ||
        (workOrder.code || "")
          .toLowerCase()
          .includes(searchText) ||
        (workOrder.title || "")
          .toLowerCase()
          .includes(searchText);

      const matchesStatus =
        statusFilter === "ALL" ||
        workOrder.status === statusFilter;

      const matchesPriority =
        priorityFilter === "ALL" ||
        workOrder.priority === priorityFilter;

      return (
        matchesSearch &&
        matchesStatus &&
        matchesPriority
      );
    });
  }, [
    workOrders,
    search,
    statusFilter,
    priorityFilter,
  ]);

  // =========================================================
  // OPEN DETAILS + HISTORY
  // =========================================================

  const openDetails = async (workOrder) => {
    setSelectedWorkOrder(workOrder);
    setHistory([]);
    setHistoryLoading(true);

    try {
      const token = localStorage.getItem("token");

      const response = await fetch(
        `https://confident-ambition-production-7bdb.up.railway.app/api/work-orders/${workOrder.id}/history`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (response.status === 401 || response.status === 403) {
        alert("Your session has expired. Please login again.");

        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        localStorage.removeItem("email");
        localStorage.removeItem("role");

        window.location.href = "/";
        return;
      }

      if (response.ok) {
        const data = await response.json();
        setHistory(data);
      } else {
        console.error("Unable to load status history.");
      }
    } catch (error) {
      console.error("History Error:", error);
    } finally {
      setHistoryLoading(false);
    }
  };

  // =========================================================
  // CLOSE DETAILS
  // =========================================================

  const closeDetails = () => {
    setSelectedWorkOrder(null);
    setHistory([]);
  };

  // =========================================================
  // BACK TO DASHBOARD
  // =========================================================

  const goBack = () => {
    window.history.back();
  };

  // =========================================================
  // STATUS CLASS
  // =========================================================

  const getStatusClass = (status) => {
    switch (status) {
      case "NEW":
        return "status-new";

      case "ASSIGNED":
        return "status-assigned";

      case "IN_PROGRESS":
        return "status-progress";

      case "ON_HOLD":
        return "status-hold";

      case "COMPLETED":
        return "status-completed";

      case "CLOSED":
        return "status-closed";

      case "CANCELLED":
        return "status-cancelled";

      default:
        return "status-default";
    }
  };

  // =========================================================
  // PRIORITY CLASS
  // =========================================================

  const getPriorityClass = (priority) => {
    switch (priority) {
      case "HIGH":
        return "priority-high";

      case "MEDIUM":
        return "priority-medium";

      case "LOW":
        return "priority-low";

      default:
        return "priority-default";
    }
  };

  // =========================================================
  // DASHBOARD STATS
  // =========================================================

  const totalOrders = workOrders.length;

  const newOrders = workOrders.filter(
    (item) => item.status === "NEW"
  ).length;

  const assignedOrders = workOrders.filter(
    (item) => item.status === "ASSIGNED"
  ).length;

  const progressOrders = workOrders.filter(
    (item) => item.status === "IN_PROGRESS"
  ).length;

  const completedOrders = workOrders.filter(
    (item) => item.status === "COMPLETED"
  ).length;

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="work-orders-page">

      {/* =====================================================
          HEADER
      ===================================================== */}

      <header className="work-orders-header-main">

        <div className="work-orders-title">

          <button
            className="back-button"
            onClick={goBack}
          >
            ←
          </button>

          <div>
            <h1>Work Orders</h1>

            <p>
              Manage and track field service work orders
            </p>
          </div>

        </div>

        <button
          className="refresh-button"
          onClick={fetchWorkOrders}
          disabled={loading}
        >
          ↻ Refresh
        </button>

      </header>

      {/* =====================================================
          CONTENT
      ===================================================== */}

      <main className="work-orders-content">

        {/* ===================================================
            STATISTICS
        =================================================== */}

        <div className="work-order-stats">

          <div className="stat-card">
            <div className="stat-icon total-icon">
              📋
            </div>

            <div>
              <span>Total Orders</span>
              <strong>{totalOrders}</strong>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon new-icon">
              🆕
            </div>

            <div>
              <span>New</span>
              <strong>{newOrders}</strong>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon assigned-icon">
              👤
            </div>

            <div>
              <span>Assigned</span>
              <strong>{assignedOrders}</strong>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon progress-icon">
              ⚙️
            </div>

            <div>
              <span>In Progress</span>
              <strong>{progressOrders}</strong>
            </div>
          </div>

          <div className="stat-card">
            <div className="stat-icon completed-icon">
              ✓
            </div>

            <div>
              <span>Completed</span>
              <strong>{completedOrders}</strong>
            </div>
          </div>

        </div>

        {/* ===================================================
            FILTERS
        =================================================== */}

        <div className="filters-card">

          <div className="search-box">

            <span>🔍</span>

            <input
              type="text"
              placeholder="Search by ID, code or title..."
              value={search}
              onChange={(event) =>
                setSearch(event.target.value)
              }
            />

          </div>

          <select
            value={statusFilter}
            onChange={(event) =>
              setStatusFilter(event.target.value)
            }
          >
            <option value="ALL">
              All Status
            </option>

            <option value="NEW">
              New
            </option>

            <option value="ASSIGNED">
              Assigned
            </option>

            <option value="IN_PROGRESS">
              In Progress
            </option>

            <option value="ON_HOLD">
              On Hold
            </option>

            <option value="COMPLETED">
              Completed
            </option>

            <option value="CLOSED">
              Closed
            </option>

            <option value="CANCELLED">
              Cancelled
            </option>
          </select>

          <select
            value={priorityFilter}
            onChange={(event) =>
              setPriorityFilter(event.target.value)
            }
          >
            <option value="ALL">
              All Priority
            </option>

            <option value="HIGH">
              High
            </option>

            <option value="MEDIUM">
              Medium
            </option>

            <option value="LOW">
              Low
            </option>
          </select>

        </div>

        {/* ===================================================
            RESULTS HEADER
        =================================================== */}

        <div className="results-header">

          <div>
            <h2>All Work Orders</h2>

            <p>
              Showing {filteredWorkOrders.length} of{" "}
              {workOrders.length} work orders
            </p>
          </div>

        </div>

        {/* ===================================================
            LOADING / EMPTY / TABLE
        =================================================== */}

        {loading ? (

          <div className="loading-container">

            <div className="loader"></div>

            <p>
              Loading work orders...
            </p>

          </div>

        ) : filteredWorkOrders.length === 0 ? (

          <div className="empty-state">

            <div className="empty-icon">
              📋
            </div>

            <h3>
              No work orders found
            </h3>

            <p>
              Try changing your search or filters.
            </p>

          </div>

        ) : (

          <div className="table-card">

            <div className="table-wrapper">

              <table className="professional-table">

                <thead>

                  <tr>
                    <th>ID</th>
                    <th>Work Order</th>
                    <th>Title</th>
                    <th>Priority</th>
                    <th>Status</th>
                    <th>Customer</th>
                    <th>Technician</th>
                    <th>Action</th>
                  </tr>

                </thead>

                <tbody>

                  {filteredWorkOrders.map(
                    (workOrder) => (

                      <tr key={workOrder.id}>

                        <td>
                          <span className="order-id">
                            #{workOrder.id}
                          </span>
                        </td>

                        <td>
                          <strong className="order-code">
                            {workOrder.code}
                          </strong>
                        </td>

                        <td>

                          <div className="title-cell">

                            <strong>
                              {workOrder.title}
                            </strong>

                            <span>
                              {workOrder.description ||
                                "No description"}
                            </span>

                          </div>

                        </td>

                        <td>

                          <span
                            className={`priority-badge ${getPriorityClass(
                              workOrder.priority
                            )}`}
                          >
                            {workOrder.priority || "N/A"}
                          </span>

                        </td>

                        <td>

                          <span
                            className={`status-badge ${getStatusClass(
                              workOrder.status
                            )}`}
                          >
                            {workOrder.status || "N/A"}
                          </span>

                        </td>

                        <td>

                          <span className="id-value">
                            {workOrder.customerId
                              ? `Customer #${workOrder.customerId}`
                              : "N/A"}
                          </span>

                        </td>

                        <td>

                          {workOrder.assignedToId ? (

                            <span className="technician-value">
                              👨‍🔧 Technician #
                              {workOrder.assignedToId}
                            </span>

                          ) : (

                            <span className="not-assigned">
                              Not Assigned
                            </span>

                          )}

                        </td>

                        <td>

                          <button
                            className="view-button"
                            onClick={() =>
                              openDetails(workOrder)
                            }
                          >
                            View
                          </button>

                        </td>

                      </tr>

                    )
                  )}

                </tbody>

              </table>

            </div>

          </div>

        )}

      </main>

      {/* =====================================================
          DETAILS MODAL
      ===================================================== */}

      {selectedWorkOrder && (

        <div
          className="modal-overlay"
          onClick={closeDetails}
        >

          <div
            className="details-modal"
            onClick={(event) =>
              event.stopPropagation()
            }
          >

            <div className="modal-header">

              <div>

                <span className="modal-label">
                  WORK ORDER
                </span>

                <h2>
                  {selectedWorkOrder.code}
                </h2>

              </div>

              <button
                className="modal-close"
                onClick={closeDetails}
              >
                ×
              </button>

            </div>

            <div className="modal-body">

              {/* BASIC INFORMATION */}

              <section className="detail-section">

                <h3>
                  Work Order Information
                </h3>

                <div className="details-grid">

                  <div className="detail-item">
                    <span>ID</span>

                    <strong>
                      #{selectedWorkOrder.id}
                    </strong>
                  </div>

                  <div className="detail-item">
                    <span>Title</span>

                    <strong>
                      {selectedWorkOrder.title}
                    </strong>
                  </div>

                  <div className="detail-item">
                    <span>Priority</span>

                    <span
                      className={`priority-badge ${getPriorityClass(
                        selectedWorkOrder.priority
                      )}`}
                    >
                      {selectedWorkOrder.priority}
                    </span>
                  </div>

                  <div className="detail-item">
                    <span>Status</span>

                    <span
                      className={`status-badge ${getStatusClass(
                        selectedWorkOrder.status
                      )}`}
                    >
                      {selectedWorkOrder.status}
                    </span>
                  </div>

                  <div className="detail-item">
                    <span>Customer ID</span>

                    <strong>
                      {selectedWorkOrder.customerId ||
                        "N/A"}
                    </strong>
                  </div>

                  <div className="detail-item">
                    <span>Site ID</span>

                    <strong>
                      {selectedWorkOrder.siteId ||
                        "N/A"}
                    </strong>
                  </div>

                  <div className="detail-item">
                    <span>Assigned Technician</span>

                    <strong>
                      {selectedWorkOrder.assignedToId
                        ? `Technician #${selectedWorkOrder.assignedToId}`
                        : "Not Assigned"}
                    </strong>
                  </div>

                  <div className="detail-item">
                    <span>SLA Due At</span>

                    <strong>
                      {selectedWorkOrder.slaDueAt ||
                        "Not specified"}
                    </strong>
                  </div>

                </div>

              </section>

              {/* DESCRIPTION */}

              <section className="detail-section">

                <h3>
                  Description
                </h3>

                <div className="description-box">

                  {selectedWorkOrder.description ||
                    "No description available."}

                </div>

              </section>

              {/* STATUS HISTORY */}

              <section className="detail-section">

                <h3>
                  Status History
                </h3>

                {historyLoading ? (

                  <div className="history-loading">
                    Loading history...
                  </div>

                ) : history.length === 0 ? (

                  <div className="history-empty">
                    No status history available.
                  </div>

                ) : (

                  <div className="timeline">

                    {history.map((item) => (

                      <div
                        className="timeline-item"
                        key={item.id}
                      >

                        <div className="timeline-dot"></div>

                        <div className="timeline-content">

                          <div className="timeline-status">

                            <span>
                              {item.fromStatus}
                            </span>

                            <strong>
                              →
                            </strong>

                            <span className="timeline-new-status">
                              {item.toStatus}
                            </span>

                          </div>

                          <p>
                            Changed by:{" "}
                            <strong>
                              {item.changedByEmail ||
                                `User #${item.changedById}`}
                            </strong>
                          </p>

                          <small>
                            {item.changedAt
                              ? new Date(
                                  item.changedAt
                                ).toLocaleString()
                              : ""}
                          </small>

                          {item.note && (
                            <div className="history-note">
                              {item.note}
                            </div>
                          )}

                        </div>

                      </div>

                    ))}

                  </div>

                )}

              </section>

            </div>

          </div>

        </div>

      )}

    </div>
  );
}

export default WorkOrders;