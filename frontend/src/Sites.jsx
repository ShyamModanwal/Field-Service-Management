import { useEffect, useState } from "react";
import "./Sites.css";

function Sites() {

  const [sites, setSites] = useState([]);
  const [loading, setLoading] = useState(true);

  const [showForm, setShowForm] = useState(false);
  const [editingSite, setEditingSite] = useState(null);

  const [siteName, setSiteName] = useState("");
  const [address, setAddress] = useState("");
  const [customerId, setCustomerId] = useState("");

  const token = localStorage.getItem("token");

  // =========================================================
  // FETCH ALL SITES
  // =========================================================

  const fetchSites = async () => {

    setLoading(true);

    try {

      const response = await fetch(
        "https://confident-ambition-production-7bdb.up.railway.app/api/customers/sites",
        {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );

      if (response.status === 401 || response.status === 403) {

        alert("Session expired. Please login again.");

        localStorage.clear();

        window.location.href = "/";

        return;
      }

      const data = await response.json();

      if (!response.ok) {

        alert(
          data.message || "Unable to fetch sites."
        );

        return;
      }

      setSites(data);

    } catch (error) {

      console.error("Sites Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {

      setLoading(false);
    }
  };

  // =========================================================
  // LOAD SITES
  // =========================================================

  useEffect(() => {

    fetchSites();

  }, []);

  // =========================================================
  // OPEN CREATE FORM
  // =========================================================

  const openCreateForm = () => {

    setEditingSite(null);

    setSiteName("");
    setAddress("");
    setCustomerId("");

    setShowForm(true);
  };

  // =========================================================
  // OPEN EDIT FORM
  // =========================================================

  const openEditForm = (site) => {

    setEditingSite(site);

    setSiteName(site.siteName || "");
    setAddress(site.address || "");

    setCustomerId(
      site.customerId ||
      site.customer?.id ||
      ""
    );

    setShowForm(true);
  };

  // =========================================================
  // CLOSE FORM
  // =========================================================

  const closeForm = () => {

    setShowForm(false);

    setEditingSite(null);

    setSiteName("");
    setAddress("");
    setCustomerId("");
  };

  // =========================================================
  // SAVE SITE
  // =========================================================

  const handleSubmit = async (event) => {

    event.preventDefault();

    if (!siteName.trim()) {

      alert("Please enter site name.");

      return;
    }

    if (!address.trim()) {

      alert("Please enter site address.");

      return;
    }

    if (!customerId) {

      alert("Please enter customer ID.");

      return;
    }

    try {

      let url;
      let method;

      if (editingSite) {

        url =
          `https://confident-ambition-production-7bdb.up.railway.app/api/customers/sites/${editingSite.id}`;

        method = "PUT";

      } else {

        url =
          `https://confident-ambition-production-7bdb.up.railway.app/api/customers/${customerId}/sites`;

        method = "POST";
      }

      const response = await fetch(
        url,
        {
          method: method,

          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          },

          body: JSON.stringify({
            siteName: siteName,
            address: address
          })
        }
      );

      const data = await response.json();

      if (!response.ok) {

        alert(
          data.message ||
          "Unable to save site."
        );

        return;
      }

      alert(
        editingSite
          ? "Site updated successfully!"
          : "Site created successfully!"
      );

      closeForm();

      fetchSites();

    } catch (error) {

      console.error("Save Site Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );
    }
  };

  // =========================================================
  // DELETE SITE
  // =========================================================

  const deleteSite = async (id) => {

    const confirmDelete = window.confirm(
      "Are you sure you want to delete this site?"
    );

    if (!confirmDelete) {
      return;
    }

    try {

      const response = await fetch(
        `https://confident-ambition-production-7bdb.up.railway.app/api/customers/sites/${id}`,
        {
          method: "DELETE",

          headers: {
            "Authorization": `Bearer ${token}`
          }
        }
      );

      if (!response.ok) {

        const data = await response.json();

        alert(
          data.message ||
          "Unable to delete site."
        );

        return;
      }

      alert("Site deleted successfully!");

      fetchSites();

    } catch (error) {

      console.error("Delete Site Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );
    }
  };

  // =========================================================
  // UI
  // =========================================================

  return (

    <div className="sites-page">

      {/* HEADER */}

      <header className="sites-header">

        <div>

          <h1>Sites</h1>

          <p>
            Manage customer service locations
          </p>

        </div>

        <div className="sites-header-actions">

          <button
            className="sites-refresh-button"
            onClick={fetchSites}
            disabled={loading}
          >
            ↻ Refresh
          </button>

          <button
            className="sites-add-button"
            onClick={openCreateForm}
          >
            + Add Site
          </button>

        </div>

      </header>


      {/* CONTENT */}

      <main className="sites-content">

        {/* STAT */}

        <div className="sites-stat-card">

          <div className="sites-stat-icon">
            📍
          </div>

          <div>

            <span>Total Sites</span>

            <strong>
              {sites.length}
            </strong>

          </div>

        </div>


        {/* TABLE */}

        {loading ? (

          <div className="sites-loading">

            <div className="sites-loader"></div>

            <p>
              Loading sites...
            </p>

          </div>

        ) : sites.length === 0 ? (

          <div className="sites-empty">

            <div className="sites-empty-icon">
              📍
            </div>

            <h3>
              No sites found
            </h3>

            <p>
              Create your first customer site.
            </p>

            <button
              onClick={openCreateForm}
            >
              + Add First Site
            </button>

          </div>

        ) : (

          <div className="sites-table-card">

            <div className="sites-table-wrapper">

              <table className="sites-table">

                <thead>

                  <tr>

                    <th>ID</th>

                    <th>Site Name</th>

                    <th>Address</th>

                    <th>Customer</th>

                    <th>Actions</th>

                  </tr>

                </thead>

                <tbody>

                  {sites.map((site) => (

                    <tr key={site.id}>

                      <td>
                        <span className="site-id">
                          #{site.id}
                        </span>
                      </td>

                      <td>

                        <strong className="site-name">
                          {site.siteName}
                        </strong>

                      </td>

                      <td>

                        <span className="site-address">
                          {site.address}
                        </span>

                      </td>

                      <td>

                        <span className="customer-badge">
                          Customer #
                          {site.customerId ||
                            site.customer?.id ||
                            "N/A"}
                        </span>

                      </td>

                      <td>

                        <div className="site-actions">

                          <button
                            className="edit-site-button"
                            onClick={() =>
                              openEditForm(site)
                            }
                          >
                            Edit
                          </button>

                          <button
                            className="delete-site-button"
                            onClick={() =>
                              deleteSite(site.id)
                            }
                          >
                            Delete
                          </button>

                        </div>

                      </td>

                    </tr>

                  ))}

                </tbody>

              </table>

            </div>

          </div>

        )}

      </main>


      {/* CREATE / EDIT MODAL */}

      {showForm && (

        <div
          className="site-modal-overlay"
          onClick={closeForm}
        >

          <div
            className="site-modal"
            onClick={(event) =>
              event.stopPropagation()
            }
          >

            <div className="site-modal-header">

              <div>

                <span>
                  SITE MANAGEMENT
                </span>

                <h2>
                  {editingSite
                    ? "Edit Site"
                    : "Add New Site"}
                </h2>

              </div>

              <button
                className="site-modal-close"
                onClick={closeForm}
              >
                ×
              </button>

            </div>


            <form
              className="site-form"
              onSubmit={handleSubmit}
            >

              <div className="site-form-group">

                <label>
                  Site Name
                </label>

                <input
                  type="text"
                  placeholder="Enter site name"
                  value={siteName}
                  onChange={(event) =>
                    setSiteName(event.target.value)
                  }
                  required
                />

              </div>


              <div className="site-form-group">

                <label>
                  Address
                </label>

                <textarea
                  placeholder="Enter site address"
                  value={address}
                  onChange={(event) =>
                    setAddress(event.target.value)
                  }
                  rows="4"
                  required
                />

              </div>


              <div className="site-form-group">

                <label>
                  Customer ID
                </label>

                <input
                  type="number"
                  placeholder="Enter customer ID"
                  value={customerId}
                  onChange={(event) =>
                    setCustomerId(event.target.value)
                  }
                  disabled={!!editingSite}
                  required
                />

                {editingSite && (

                  <small>
                    Customer cannot be changed while editing.
                  </small>

                )}

              </div>


              <div className="site-form-actions">

                <button
                  type="button"
                  className="site-cancel-button"
                  onClick={closeForm}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="site-save-button"
                >
                  {editingSite
                    ? "Update Site"
                    : "Create Site"}
                </button>

              </div>

            </form>

          </div>

        </div>

      )}

    </div>
  );
}

export default Sites;