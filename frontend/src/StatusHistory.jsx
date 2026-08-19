import { useEffect, useState } from "react";
import "./StatusHistory.css";

function StatusHistory() {

  const [workOrders, setWorkOrders] = useState([]);
  const [selectedWorkOrder, setSelectedWorkOrder] = useState("");

  const [history, setHistory] = useState([]);

  const [loadingOrders, setLoadingOrders] = useState(true);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [changingStatus, setChangingStatus] = useState(false);

  const [newStatus, setNewStatus] = useState("");
  const [note, setNote] = useState("");

  const token = localStorage.getItem("token");


  // =========================================================
  // FETCH WORK ORDERS
  // =========================================================

  const fetchWorkOrders = async () => {

    setLoadingOrders(true);

    try {

      const response = await fetch(
        "http://localhost:8080/api/work-orders",
        {
          method: "GET",

          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          }
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

        alert(
          data.message ||
          "Unable to fetch work orders"
        );

        return;
      }


      setWorkOrders(data);

    } catch (error) {

      console.error(
        "Work Orders Error:",
        error
      );

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {

      setLoadingOrders(false);
    }
  };


  // =========================================================
  // LOAD WORK ORDERS
  // =========================================================

  useEffect(() => {

    fetchWorkOrders();

  }, []);


  // =========================================================
  // FETCH HISTORY
  // =========================================================

  const fetchHistory = async (workOrderId) => {

    if (!workOrderId) {

      setHistory([]);

      return;
    }


    setLoadingHistory(true);


    try {

      const response = await fetch(
        `http://localhost:8080/api/work-orders/${workOrderId}/history`,
        {
          method: "GET",

          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );


      if (!response.ok) {

        setHistory([]);

        return;
      }


      const data = await response.json();

      setHistory(data);

    } catch (error) {

      console.error(
        "History Error:",
        error
      );

      setHistory([]);

    } finally {

      setLoadingHistory(false);
    }
  };


  // =========================================================
  // WORK ORDER SELECT
  // =========================================================

  const handleWorkOrderChange = (event) => {

    const workOrderId = event.target.value;

    setSelectedWorkOrder(workOrderId);

    setNewStatus("");

    setNote("");

    fetchHistory(workOrderId);
  };


  // =========================================================
  // CHANGE STATUS
  // =========================================================

  const handleStatusChange = async (event) => {

    event.preventDefault();


    if (!selectedWorkOrder) {

      alert("Please select a work order.");

      return;
    }


    if (!newStatus) {

      alert("Please select a new status.");

      return;
    }


    setChangingStatus(true);


    try {

      const response = await fetch(
        `http://localhost:8080/api/work-orders/${selectedWorkOrder}/status`,
        {
          method: "POST",

          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          },

          body: JSON.stringify({
            status: newStatus,
            note: note
          })
        }
      );


      const data = await response.json();


      if (!response.ok) {

        alert(
          data.message ||
          "Unable to change work order status"
        );

        return;
      }


      alert(
        "Work order status updated successfully!"
      );


      setNewStatus("");

      setNote("");


      // Refresh work orders

      await fetchWorkOrders();


      // Refresh history

      await fetchHistory(
        selectedWorkOrder
      );

    } catch (error) {

      console.error(
        "Status Change Error:",
        error
      );

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {

      setChangingStatus(false);
    }
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
  // SELECTED WORK ORDER
  // =========================================================

  const selectedOrder = workOrders.find(
    (item) =>
      String(item.id) ===
      String(selectedWorkOrder)
  );


  // =========================================================
  // UI
  // =========================================================

  return (

    <div className="status-history-page">


      {/* =====================================================
          HEADER
      ===================================================== */}

      <div className="status-history-header">

        <div>

          <h1>
            Status & History
          </h1>

          <p>
            Track and manage work order status changes
          </p>

        </div>

      </div>


      {/* =====================================================
          CONTENT
      ===================================================== */}

      <main className="status-history-content">


        {/* ===================================================
            SELECT WORK ORDER
        =================================================== */}

        <div className="selection-card">

          <h2>
            Select Work Order
          </h2>

          <p>
            Select a work order to view its current status
            and complete status history.
          </p>


          {loadingOrders ? (

            <div className="status-loading">
              Loading work orders...
            </div>

          ) : (

            <select
              className="work-order-select"
              value={selectedWorkOrder}
              onChange={handleWorkOrderChange}
            >

              <option value="">
                -- Select Work Order --
              </option>


              {workOrders.map(
                (workOrder) => (

                  <option
                    key={workOrder.id}
                    value={workOrder.id}
                  >

                    #{workOrder.id} -{" "}
                    {workOrder.code} -{" "}
                    {workOrder.title}

                  </option>

                )
              )}

            </select>

          )}

        </div>


        {/* ===================================================
            CURRENT STATUS
        =================================================== */}

        {selectedOrder && (

          <div className="current-status-card">

            <div>

              <span>
                Current Status
              </span>

              <h2>
                {selectedOrder.code}
              </h2>

              <p>
                {selectedOrder.title}
              </p>

            </div>


            <span
              className={`status-badge ${getStatusClass(
                selectedOrder.status
              )}`}
            >

              {selectedOrder.status}

            </span>

          </div>

        )}


        {/* ===================================================
            CHANGE STATUS
        =================================================== */}

        {selectedWorkOrder && (

          <div className="change-status-card">

            <h2>
              Change Status
            </h2>

            <p>
              Update the current status of this work order.
            </p>


            <form
              onSubmit={handleStatusChange}
            >


              <div className="status-form-grid">


                <div className="status-form-group">

                  <label>
                    New Status
                  </label>


                  <select
                    value={newStatus}
                    onChange={(event) =>
                      setNewStatus(
                        event.target.value
                      )
                    }
                    required
                  >

                    <option value="">
                      Select Status
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

                </div>


                <div className="status-form-group">

                  <label>
                    Note
                  </label>


                  <input
                    type="text"
                    placeholder="Optional note..."
                    value={note}
                    onChange={(event) =>
                      setNote(
                        event.target.value
                      )
                    }
                  />

                </div>


              </div>


              <button
                type="submit"
                className="update-status-button"
                disabled={changingStatus}
              >

                {changingStatus
                  ? "Updating..."
                  : "Update Status"}

              </button>


            </form>

          </div>

        )}


        {/* ===================================================
            HISTORY
        =================================================== */}

        {selectedWorkOrder && (

          <div className="history-card">

            <div className="history-header">

              <div>

                <h2>
                  Status History
                </h2>

                <p>
                  Complete timeline of status changes
                </p>

              </div>

            </div>


            {loadingHistory ? (

              <div className="history-loading">
                Loading history...
              </div>

            ) : history.length === 0 ? (

              <div className="history-empty">

                <div className="history-empty-icon">
                  📊
                </div>

                <h3>
                  No Status History
                </h3>

                <p>
                  No status changes have been recorded
                  for this work order yet.
                </p>

              </div>

            ) : (

              <div className="history-timeline">

                {history.map(
                  (item) => (

                    <div
                      className="history-item"
                      key={item.id}
                    >

                      <div className="history-dot">
                      </div>


                      <div className="history-item-content">

                        <div className="history-status-row">

                          <span
                            className={`status-badge ${getStatusClass(
                              item.fromStatus
                            )}`}
                          >

                            {item.fromStatus}

                          </span>


                          <strong>
                            →
                          </strong>


                          <span
                            className={`status-badge ${getStatusClass(
                              item.toStatus
                            )}`}
                          >

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
                            : "Date unavailable"}

                        </small>


                        {item.note && (

                          <div className="history-note">

                            <strong>
                              Note:
                            </strong>{" "}

                            {item.note}

                          </div>

                        )}

                      </div>

                    </div>

                  )
                )}

              </div>

            )}

          </div>

        )}


        {/* ===================================================
            NOTHING SELECTED
        =================================================== */}

        {!selectedWorkOrder && !loadingOrders && (

          <div className="history-placeholder">

            <div>
              📊
            </div>

            <h2>
              Select a Work Order
            </h2>

            <p>
              Choose a work order above to view its
              status and history.
            </p>

          </div>

        )}

      </main>

    </div>
  );
}

export default StatusHistory;