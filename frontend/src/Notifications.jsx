import { useEffect, useState } from "react";
import "./Notifications.css";

function Notifications() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  // =========================================================
  // FETCH NOTIFICATIONS
  // =========================================================

  const fetchNotifications = async () => {
    setLoading(true);

    try {
      const token = localStorage.getItem("token");
      const userId = localStorage.getItem("userId");

      if (!token || !userId) {
        alert("Please login again.");
        return;
      }

      const response = await fetch(
        `https://field-service-management-production-86f5.up.railway.app/api/notifications/user/${userId}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      // =====================================================
      // UNAUTHORIZED / FORBIDDEN
      // =====================================================

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
        alert(data.message || "Unable to fetch notifications");
        return;
      }

      setNotifications(data);

    } catch (error) {
      console.error("Notification Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {
      setLoading(false);
    }
  };

  // =========================================================
  // LOAD NOTIFICATIONS
  // =========================================================

  useEffect(() => {
    fetchNotifications();
  }, []);

  // =========================================================
  // MARK AS READ
  // =========================================================

  const markAsRead = async (notificationId) => {
    try {
      const token = localStorage.getItem("token");

      const response = await fetch(
        `https://field-service-management-production-86f5.up.railway.app/api/notifications/${notificationId}/read`,
        {
          method: "PUT",
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

      if (!response.ok) {
        const data = await response.json();
        alert(data.message || "Unable to mark notification as read");
        return;
      }

      // =====================================================
      // UPDATE UI WITHOUT REFRESHING PAGE
      // =====================================================

      setNotifications((previousNotifications) =>
        previousNotifications.map((notification) =>
          notification.id === notificationId
            ? {
                ...notification,
                readStatus: true,
              }
            : notification
        )
      );

    } catch (error) {
      console.error("Mark Read Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );
    }
  };

  // =========================================================
  // FORMAT DATE
  // =========================================================

  const formatDate = (date) => {
    if (!date) {
      return "Unknown date";
    }

    return new Date(date).toLocaleString();
  };

  // =========================================================
  // UNREAD COUNT
  // =========================================================

  const unreadCount = notifications.filter(
    (notification) => !notification.readStatus
  ).length;

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="notifications-page">

      {/* =====================================================
          HEADER
      ===================================================== */}

      <header className="notifications-header">

        <div>
          <h1>Notifications</h1>

          <p>
            Stay updated with important work order events
          </p>
        </div>

        <button
          className="notification-refresh-button"
          onClick={fetchNotifications}
          disabled={loading}
        >
          ↻ Refresh
        </button>

      </header>

      {/* =====================================================
          CONTENT
      ===================================================== */}

      <main className="notifications-content">

        {/* ===================================================
            SUMMARY
        =================================================== */}

        <div className="notification-summary">

          <div className="notification-summary-card">

            <div className="notification-summary-icon">
              🔔
            </div>

            <div>
              <span>Total Notifications</span>

              <strong>
                {notifications.length}
              </strong>
            </div>

          </div>

          <div className="notification-summary-card">

            <div className="notification-summary-icon unread-summary-icon">
              📩
            </div>

            <div>
              <span>Unread Notifications</span>

              <strong>
                {unreadCount}
              </strong>
            </div>

          </div>

        </div>

        {/* ===================================================
            LOADING
        =================================================== */}

        {loading ? (

          <div className="notifications-loading">

            <div className="notification-loader"></div>

            <p>
              Loading notifications...
            </p>

          </div>

        ) : notifications.length === 0 ? (

          /* =================================================
             EMPTY STATE
          ================================================= */

          <div className="notifications-empty">

            <div className="notifications-empty-icon">
              🔔
            </div>

            <h3>
              No notifications
            </h3>

            <p>
              You don't have any notifications yet.
            </p>

          </div>

        ) : (

          /* =================================================
             NOTIFICATION LIST
          ================================================= */

          <div className="notification-list">

            {notifications.map((notification) => (

              <div
                key={notification.id}
                className={`notification-card ${
                  notification.readStatus
                    ? "notification-read"
                    : "notification-unread"
                }`}
              >

                <div className="notification-icon">

                  {notification.readStatus
                    ? "✓"
                    : "🔔"}

                </div>

                <div className="notification-details">

                  <div className="notification-top">

                    <div>

                      <span className="notification-label">
                        WORK ORDER
                      </span>

                      <h3>
                        Work Order #{notification.workOrderId}
                      </h3>

                    </div>

                    {!notification.readStatus && (
                      <span className="unread-badge">
                        UNREAD
                      </span>
                    )}

                  </div>

                  <p className="notification-message">
                    {notification.message}
                  </p>

                  <div className="notification-footer">

                    <span>
                      🕒 {formatDate(notification.createdAt)}
                    </span>

                    {!notification.readStatus && (

                      <button
                        className="mark-read-button"
                        onClick={() =>
                          markAsRead(notification.id)
                        }
                      >
                        Mark as Read
                      </button>

                    )}

                  </div>

                </div>

              </div>

            ))}

          </div>

        )}

      </main>

    </div>
  );
}

export default Notifications;