import { useEffect, useState } from "react";
import "./CreateWorkOrder.css";

function CreateWorkOrder({ onBack, onCreated }) {

  const [customers, setCustomers] = useState([]);
  const [sites, setSites] = useState([]);
  const [technicians, setTechnicians] = useState([]);

  const [formData, setFormData] = useState({
    code: "",
    title: "",
    description: "",
    priority: "MEDIUM",
    customerId: "",
    siteId: "",
    assignedToId: "",
    slaDueAt: ""
  });

  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);

  const token = localStorage.getItem("token");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {

    try {

      const headers = {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      };

      const [customerResponse, siteResponse, userResponse] =
        await Promise.all([
          fetch("https://confident-ambition-production-7bdb.up.railway.app/api/customers", {
            headers
          }),

          fetch("https://confident-ambition-production-7bdb.up.railway.app/api/customers/sites", {
            headers
          }),

          fetch("https://confident-ambition-production-7bdb.up.railway.app/api/users", {
            headers
          })
        ]);

      if (!customerResponse.ok ||
          !siteResponse.ok ||
          !userResponse.ok) {

        alert("Unable to load customers, sites or users.");
        return;
      }

      const customerData = await customerResponse.json();
      const siteData = await siteResponse.json();
      const userData = await userResponse.json();

      setCustomers(customerData);
      setSites(siteData);

      setTechnicians(
        userData.filter(
          (user) =>
            user.role &&
            user.role.toUpperCase() === "TECHNICIAN"
        )
      );

    } catch (error) {

      console.error("Load Data Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {

      setPageLoading(false);
    }
  };

  const handleChange = (event) => {

    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value
    }));

    if (name === "customerId") {

      setFormData((previous) => ({
        ...previous,
        customerId: value,
        siteId: ""
      }));
    }
  };

  const handleSubmit = async (event) => {

    event.preventDefault();

    if (!formData.customerId) {
      alert("Please select a customer.");
      return;
    }

    if (!formData.siteId) {
      alert("Please select a site.");
      return;
    }

    setLoading(true);

    try {

      const requestBody = {
        code: formData.code,
        title: formData.title,
        description: formData.description,
        priority: formData.priority,
        status: "NEW",
        slaDueAt: formData.slaDueAt || null,
        customerId: Number(formData.customerId),
        siteId: Number(formData.siteId),
        assignedToId: formData.assignedToId
          ? Number(formData.assignedToId)
          : null
      };

      const response = await fetch(
        "https://confident-ambition-production-7bdb.up.railway.app/api/work-orders",
        {
          method: "POST",

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
          "Unable to create work order."
        );

        return;
      }

      alert("Work Order created successfully!");

      setFormData({
        code: "",
        title: "",
        description: "",
        priority: "MEDIUM",
        customerId: "",
        siteId: "",
        assignedToId: "",
        slaDueAt: ""
      });

      if (onCreated) {
        onCreated(data);
      }

    } catch (error) {

      console.error("Create Work Order Error:", error);

      alert(
        "Unable to connect to Spring Boot server."
      );

    } finally {

      setLoading(false);
    }
  };

  if (pageLoading) {

    return (
      <div className="create-work-order-page">

        <div className="create-loading">
          Loading form data...
        </div>

      </div>
    );
  }

  const selectedCustomerSites = sites.filter(
    (site) =>
      site.customerId &&
      Number(site.customerId) ===
      Number(formData.customerId)
  );

  return (
    <div className="create-work-order-page">

      <div className="create-work-order-header">

        <div>

          <h1>Create Work Order</h1>

          <p>
            Create and assign a new service work order
          </p>

        </div>

        <button
          className="back-button"
          onClick={onBack}
          type="button"
        >
          ← Back
        </button>

      </div>

      <div className="create-work-order-card">

        <form onSubmit={handleSubmit}>

          <div className="form-section">

            <h2>Basic Information</h2>

            <div className="form-grid">

              <div className="form-field">

                <label>
                  Work Order Code
                </label>

                <input
                  type="text"
                  name="code"
                  placeholder="Example: WO-006"
                  value={formData.code}
                  onChange={handleChange}
                  required
                />

              </div>

              <div className="form-field">

                <label>
                  Priority
                </label>

                <select
                  name="priority"
                  value={formData.priority}
                  onChange={handleChange}
                >

                  <option value="LOW">
                    LOW
                  </option>

                  <option value="MEDIUM">
                    MEDIUM
                  </option>

                  <option value="HIGH">
                    HIGH
                  </option>

                  <option value="CRITICAL">
                    CRITICAL
                  </option>

                </select>

              </div>

            </div>

            <div className="form-field">

              <label>
                Title
              </label>

              <input
                type="text"
                name="title"
                placeholder="Example: AC Maintenance"
                value={formData.title}
                onChange={handleChange}
                required
              />

            </div>

            <div className="form-field">

              <label>
                Description
              </label>

              <textarea
                name="description"
                placeholder="Describe the work that needs to be completed..."
                value={formData.description}
                onChange={handleChange}
                rows="5"
                required
              />

            </div>

          </div>

          <div className="form-section">

            <h2>Customer & Site</h2>

            <div className="form-grid">

              <div className="form-field">

                <label>
                  Customer
                </label>

                <select
                  name="customerId"
                  value={formData.customerId}
                  onChange={handleChange}
                  required
                >

                  <option value="">
                    Select Customer
                  </option>

                  {customers.map((customer) => (

                    <option
                      key={customer.id}
                      value={customer.id}
                    >
                      {customer.customerName}
                      {customer.companyName
                        ? ` - ${customer.companyName}`
                        : ""}
                    </option>

                  ))}

                </select>

              </div>

              <div className="form-field">

                <label>
                  Site
                </label>

                <select
                  name="siteId"
                  value={formData.siteId}
                  onChange={handleChange}
                  disabled={!formData.customerId}
                  required
                >

                  <option value="">
                    {formData.customerId
                      ? "Select Site"
                      : "Select Customer First"}
                  </option>

                  {selectedCustomerSites.map((site) => (

                    <option
                      key={site.id}
                      value={site.id}
                    >
                      {site.siteName}
                    </option>

                  ))}

                </select>

              </div>

            </div>

          </div>

          <div className="form-section">

            <h2>Assignment & SLA</h2>

            <div className="form-grid">

              <div className="form-field">

                <label>
                  Assign Technician
                </label>

                <select
                  name="assignedToId"
                  value={formData.assignedToId}
                  onChange={handleChange}
                >

                  <option value="">
                    Not Assigned
                  </option>

                  {technicians.map((technician) => (

                    <option
                      key={technician.id}
                      value={technician.id}
                    >
                      {technician.name}
                      {" - "}
                      {technician.email}
                    </option>

                  ))}

                </select>

              </div>

              <div className="form-field">

                <label>
                  SLA Due Date & Time
                </label>

                <input
                  type="datetime-local"
                  name="slaDueAt"
                  value={formData.slaDueAt}
                  onChange={handleChange}
                />

              </div>

            </div>

          </div>

          <div className="form-actions">

            <button
              type="button"
              className="cancel-button"
              onClick={onBack}
            >
              Cancel
            </button>

            <button
              type="submit"
              className="create-button"
              disabled={loading}
            >
              {loading
                ? "Creating..."
                : "Create Work Order"}
            </button>

          </div>

        </form>

      </div>

    </div>
  );
}

export default CreateWorkOrder;