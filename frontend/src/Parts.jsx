import { useEffect, useMemo, useState } from "react";
import "./Parts.css";

function Parts() {

  const [parts, setParts] = useState([]);
  const [loading, setLoading] = useState(true);

  const [search, setSearch] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [editingPart, setEditingPart] = useState(null);

  const [formData, setFormData] = useState({
    partNumber: "",
    name: "",
    description: "",
    unitPrice: "",
    stockQuantity: ""
  });

  const token = localStorage.getItem("token");


  // =========================================================
  // FETCH ALL PARTS
  // =========================================================

  const fetchParts = async () => {

    setLoading(true);

    try {

      const response = await fetch(
        "https://confident-ambition-production-7bdb.up.railway.app/api/parts",
        {
          method: "GET",

          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );


      if (response.status === 401 || response.status === 403) {

        alert(
          "Your session has expired. Please login again."
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
          data.message || "Unable to fetch parts"
        );

        return;
      }


      setParts(data);

    } catch (error) {

      console.error("Parts Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {

      setLoading(false);
    }
  };


  // =========================================================
  // LOAD PARTS
  // =========================================================

  useEffect(() => {

    fetchParts();

  }, []);


  // =========================================================
  // SEARCH
  // =========================================================

  const filteredParts = useMemo(() => {

    const searchText = search.toLowerCase();

    return parts.filter((part) => {

      return (
        String(part.id)
          .toLowerCase()
          .includes(searchText)
        ||

        (part.partNumber || "")
          .toLowerCase()
          .includes(searchText)
        ||

        (part.name || "")
          .toLowerCase()
          .includes(searchText)
        ||

        (part.description || "")
          .toLowerCase()
          .includes(searchText)
      );

    });

  }, [parts, search]);


  // =========================================================
  // FORM INPUT CHANGE
  // =========================================================

  const handleChange = (event) => {

    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value
    }));

  };


  // =========================================================
  // OPEN ADD FORM
  // =========================================================

  const openAddForm = () => {

    setEditingPart(null);

    setFormData({
      partNumber: "",
      name: "",
      description: "",
      unitPrice: "",
      stockQuantity: ""
    });

    setShowForm(true);
  };


  // =========================================================
  // OPEN EDIT FORM
  // =========================================================

  const openEditForm = (part) => {

    setEditingPart(part);

    setFormData({
      partNumber: part.partNumber || "",
      name: part.name || "",
      description: part.description || "",
      unitPrice: part.unitPrice ?? "",
      stockQuantity: part.stockQuantity ?? ""
    });

    setShowForm(true);
  };


  // =========================================================
  // CLOSE FORM
  // =========================================================

  const closeForm = () => {

    setShowForm(false);
    setEditingPart(null);

    setFormData({
      partNumber: "",
      name: "",
      description: "",
      unitPrice: "",
      stockQuantity: ""
    });
  };


  // =========================================================
  // CREATE / UPDATE PART
  // =========================================================

  const handleSubmit = async (event) => {

    event.preventDefault();


    if (
      formData.unitPrice === "" ||
      Number(formData.unitPrice) < 0
    ) {

      alert("Unit price cannot be negative.");

      return;
    }


    if (
      formData.stockQuantity === "" ||
      Number(formData.stockQuantity) < 0
    ) {

      alert("Stock quantity cannot be negative.");

      return;
    }


    const requestBody = {

      partNumber: formData.partNumber,

      name: formData.name,

      description: formData.description,

      unitPrice: Number(formData.unitPrice),

      stockQuantity: Number(formData.stockQuantity)

    };


    try {

      const url = editingPart
        ? `https://confident-ambition-production-7bdb.up.railway.app/api/parts/${editingPart.id}`
        : "https://confident-ambition-production-7bdb.up.railway.app/api/parts";


      const method = editingPart
        ? "PUT"
        : "POST";


      const response = await fetch(
        url,
        {
          method: method,

          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          },

          body: JSON.stringify(requestBody)
        }
      );


      const data = await response.json();


      if (!response.ok) {

        alert(
          data.message ||
          "Unable to save part"
        );

        return;
      }


      alert(
        editingPart
          ? "Part updated successfully!"
          : "Part created successfully!"
      );


      closeForm();

      fetchParts();

    } catch (error) {

      console.error(
        "Save Part Error:",
        error
      );

      alert(
        "Unable to connect to Spring Boot server."
      );
    }
  };


  // =========================================================
  // DELETE PART
  // =========================================================

  const handleDelete = async (id) => {

    const confirmDelete = window.confirm(
      "Are you sure you want to delete this part?"
    );


    if (!confirmDelete) {
      return;
    }


    try {

      const response = await fetch(
        `https://confident-ambition-production-7bdb.up.railway.app/api/parts/${id}`,
        {
          method: "DELETE",

          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        }
      );


      const data = await response.text();


      if (!response.ok) {

        alert(
          data || "Unable to delete part"
        );

        return;
      }


      alert(
        "Part deleted successfully!"
      );


      fetchParts();

    } catch (error) {

      console.error(
        "Delete Part Error:",
        error
      );

      alert(
        "Unable to connect to Spring Boot server."
      );
    }
  };


  // =========================================================
  // UI
  // =========================================================

  return (

    <div className="parts-page">


      {/* =====================================================
          HEADER
      ===================================================== */}

      <div className="parts-header">

        <div>

          <h1>
            Parts
          </h1>

          <p>
            Manage spare parts and inventory
          </p>

        </div>


        <button
          className="add-part-button"
          onClick={openAddForm}
        >
          + Add Part
        </button>

      </div>


      {/* =====================================================
          CONTENT
      ===================================================== */}

      <main className="parts-content">


        {/* ===================================================
            STATISTICS
        =================================================== */}

        <div className="parts-stats">


          <div className="part-stat-card">

            <span>
              Total Parts
            </span>

            <strong>
              {parts.length}
            </strong>

          </div>


          <div className="part-stat-card">

            <span>
              Total Stock
            </span>

            <strong>
              {parts.reduce(
                (total, part) =>
                  total + (part.stockQuantity || 0),
                0
              )}
            </strong>

          </div>


          <div className="part-stat-card">

            <span>
              Low Stock
            </span>

            <strong>
              {
                parts.filter(
                  (part) =>
                    part.stockQuantity <= 5
                ).length
              }
            </strong>

          </div>


          <div className="part-stat-card">

            <span>
              Total Inventory Value
            </span>

            <strong>
              ₹
              {parts
                .reduce(
                  (total, part) =>
                    total +
                    (part.unitPrice || 0) *
                    (part.stockQuantity || 0),
                  0
                )
                .toFixed(2)}
            </strong>

          </div>

        </div>


        {/* ===================================================
            SEARCH
        =================================================== */}

        <div className="parts-filter-card">

          <div className="parts-search">

            <span>
              🔍
            </span>

            <input
              type="text"
              placeholder="Search by part number, name or description..."
              value={search}
              onChange={(event) =>
                setSearch(event.target.value)
              }
            />

          </div>


          <button
            className="refresh-parts-button"
            onClick={fetchParts}
            disabled={loading}
          >
            ↻ Refresh
          </button>

        </div>


        {/* ===================================================
            RESULTS
        =================================================== */}

        <div className="parts-results-header">

          <div>

            <h2>
              All Parts
            </h2>

            <p>
              Showing {filteredParts.length} of{" "}
              {parts.length} parts
            </p>

          </div>

        </div>


        {/* ===================================================
            LOADING
        =================================================== */}

        {loading ? (

          <div className="parts-loading">

            <div className="parts-loader">
            </div>

            <p>
              Loading parts...
            </p>

          </div>

        ) : filteredParts.length === 0 ? (

          <div className="parts-empty">

            <div className="parts-empty-icon">
              🔧
            </div>

            <h3>
              No parts found
            </h3>

            <p>
              Add a new part or change your search.
            </p>

          </div>

        ) : (

          /* =================================================
             TABLE
          ================================================= */

          <div className="parts-table-card">

            <div className="parts-table-wrapper">

              <table className="parts-table">

                <thead>

                  <tr>

                    <th>
                      ID
                    </th>

                    <th>
                      Part Number
                    </th>

                    <th>
                      Name
                    </th>

                    <th>
                      Description
                    </th>

                    <th>
                      Unit Price
                    </th>

                    <th>
                      Stock
                    </th>

                    <th>
                      Action
                    </th>

                  </tr>

                </thead>


                <tbody>

                  {filteredParts.map(
                    (part) => (

                      <tr key={part.id}>


                        <td>

                          <span className="part-id">
                            #{part.id}
                          </span>

                        </td>


                        <td>

                          <strong className="part-number">
                            {part.partNumber}
                          </strong>

                        </td>


                        <td>

                          <strong className="part-name">
                            {part.name}
                          </strong>

                        </td>


                        <td>

                          <span className="part-description">

                            {part.description ||
                              "No description"}

                          </span>

                        </td>


                        <td>

                          <span className="part-price">

                            ₹
                            {Number(
                              part.unitPrice || 0
                            ).toFixed(2)}

                          </span>

                        </td>


                        <td>

                          <span
                            className={
                              part.stockQuantity <= 5
                                ? "stock-low"
                                : "stock-good"
                            }
                          >

                            {part.stockQuantity}

                          </span>

                        </td>


                        <td>

                          <div className="part-actions">

                            <button
                              className="edit-part-button"
                              onClick={() =>
                                openEditForm(part)
                              }
                            >
                              Edit
                            </button>


                            <button
                              className="delete-part-button"
                              onClick={() =>
                                handleDelete(part.id)
                              }
                            >
                              Delete
                            </button>

                          </div>

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
          ADD / EDIT MODAL
      ===================================================== */}

      {showForm && (

        <div
          className="part-modal-overlay"
          onClick={closeForm}
        >

          <div
            className="part-modal"
            onClick={(event) =>
              event.stopPropagation()
            }
          >


            <div className="part-modal-header">

              <div>

                <span>
                  PART MANAGEMENT
                </span>

                <h2>
                  {editingPart
                    ? "Edit Part"
                    : "Add New Part"}
                </h2>

              </div>


              <button
                className="part-modal-close"
                onClick={closeForm}
              >
                ×
              </button>

            </div>


            <form
              className="part-form"
              onSubmit={handleSubmit}
            >


              {/* PART NUMBER */}

              <div className="part-form-group">

                <label>
                  Part Number
                </label>

                <input
                  type="text"
                  name="partNumber"
                  placeholder="e.g. P-1001"
                  value={formData.partNumber}
                  onChange={handleChange}
                  required
                />

              </div>


              {/* NAME */}

              <div className="part-form-group">

                <label>
                  Part Name
                </label>

                <input
                  type="text"
                  name="name"
                  placeholder="e.g. Motor"
                  value={formData.name}
                  onChange={handleChange}
                  required
                />

              </div>


              {/* DESCRIPTION */}

              <div className="part-form-group">

                <label>
                  Description
                </label>

                <textarea
                  name="description"
                  placeholder="Enter part description..."
                  value={formData.description}
                  onChange={handleChange}
                  rows="3"
                />

              </div>


              <div className="part-form-row">


                {/* UNIT PRICE */}

                <div className="part-form-group">

                  <label>
                    Unit Price
                  </label>

                  <input
                    type="number"
                    name="unitPrice"
                    placeholder="0.00"
                    min="0"
                    step="0.01"
                    value={formData.unitPrice}
                    onChange={handleChange}
                    required
                  />

                </div>


                {/* STOCK */}

                <div className="part-form-group">

                  <label>
                    Stock Quantity
                  </label>

                  <input
                    type="number"
                    name="stockQuantity"
                    placeholder="0"
                    min="0"
                    value={formData.stockQuantity}
                    onChange={handleChange}
                    required
                  />

                </div>


              </div>


              {/* BUTTONS */}

              <div className="part-form-actions">

                <button
                  type="button"
                  className="cancel-part-button"
                  onClick={closeForm}
                >
                  Cancel
                </button>


                <button
                  type="submit"
                  className="save-part-button"
                >
                  {editingPart
                    ? "Update Part"
                    : "Create Part"}
                </button>

              </div>


            </form>

          </div>

        </div>

      )}

    </div>

  );
}

export default Parts;