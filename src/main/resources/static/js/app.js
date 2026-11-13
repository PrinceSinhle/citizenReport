window.pageViewsReady.then(loaded => {
  if (!loaded) return;
  const state = { token: null, user: null, complaints: [], adminComplaints: [], allAdminComplaints: [], adminUsers: [], settledComplaints: [], adminFilters: {}, settledFilters: {}, selectedSettledIds: new Set() };
  const views = [...document.querySelectorAll("[data-view]")];
  const navSignin = document.querySelector(".nav-signin");
  const navCta = document.querySelector(".nav-actions .button-small");
  const navSocials = document.querySelector(".nav-contact-socials");
  const navActions = document.querySelector(".nav-actions");
  const mainNav = document.querySelector(".main-nav");
  const menuButton = document.getElementById("mobile-menu");

  function setMessage(id, message = "", type = "success") {
    const box = document.getElementById(id);
    if (!box) return;
    box.textContent = message;
    box.classList.toggle("visible", Boolean(message));
    box.classList.toggle("error", type === "error");
  }

  function updateNavigation() {
    navSocials.hidden = Boolean(state.token);
    navCta.hidden = !state.token;
    if (state.token) {
      if (state.user?.role === "ADMIN") {
        navSignin.href = "#admin";
        navSignin.textContent = "Admin dashboard";
        navCta.href = "#settled";
        navCta.textContent = "Settled cases";
        delete navCta.dataset.signout;
      } else {
        navSignin.href = "#dashboard";
        navSignin.textContent = "My dashboard";
        navCta.href = "#home";
        navCta.textContent = "Sign out";
        navCta.dataset.signout = "true";
      }
    } else {
      navSignin.href = "#login";
      navSignin.textContent = "Sign in";
      navCta.href = "#register";
      navCta.textContent = "Get started";
      delete navCta.dataset.signout;
    }
    const currentView = location.hash.slice(1);
    navActions.hidden = Boolean(state.token && ["dashboard", "admin", "settled"].includes(currentView));
  }

  function showView(name) {
    const allowed = ["home", "login", "register", "dashboard", "admin", "settled"];
    const next = allowed.includes(name) ? name : "home";
    if (["dashboard", "admin", "settled"].includes(next) && !state.token) {
      location.hash = "#login";
      return;
    }
    if (["admin", "settled"].includes(next) && state.user?.role !== "ADMIN") {
      location.hash = "#dashboard";
      return;
    }
    if (next === "dashboard" && state.user?.role === "ADMIN") {
      location.hash = "#admin";
      return;
    }
    views.forEach(view => { view.hidden = view.dataset.view !== next; });
    updateNavigation();
    document.querySelectorAll("[data-nav]").forEach(link => {
      link.classList.toggle("active", link.dataset.nav === next);
    });
    mainNav.classList.remove("open");
    menuButton.setAttribute("aria-expanded", "false");
    if (next === "dashboard") loadDashboard();
    if (next === "admin") loadAdminDashboard();
    if (next === "settled") loadSettledDashboard();
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function navigate(name) {
    if (location.hash !== `#${name}`) location.hash = `#${name}`;
    else showView(name);
  }

  function api(path, options = {}) {
    const headers = new Headers(options.headers || {});
    if (options.body && !headers.has("Content-Type")) headers.set("Content-Type", "application/json");
    if (state.token) headers.set("Authorization", `Bearer ${state.token}`);
    return fetch(path, { ...options, headers }).then(async response => {
      const raw = await response.text();
      let body = {};
      try { body = raw ? JSON.parse(raw) : {}; } catch { body = {}; }
      if (!response.ok) {
        if (response.status === 401 && state.token) {
          state.token = null;
          state.user = null;
          updateNavigation();
          navigate("login");
          setMessage("login-message", "Your sign-in has expired. Please sign in again.", "error");
        }
        const validation = body.details ? Object.values(body.details).join(" ") : "";
        throw new Error(validation || body.message || "Something went wrong. Please try again.");
      }
      return body;
    });
  }

  function renderComplaints() {
    const list = document.getElementById("reports-list");
    list.replaceChildren();
    const reports = state.complaints;
    document.getElementById("count-total").textContent = reports.length;
    document.getElementById("count-progress").textContent = reports.filter(item => item.status === "IN_PROGRESS").length;
    document.getElementById("count-resolved").textContent = reports.filter(item => item.status === "RESOLVED").length;
    document.getElementById("report-count").textContent = `${reports.length} ${reports.length === 1 ? "report" : "reports"}`;

    if (!reports.length) {
      const empty = document.createElement("div");
      empty.className = "empty-state";
      empty.innerHTML = '<h3>Your reports will show up here.</h3><p>When you submit a local issue, you can follow its status from this dashboard.</p>';
      list.append(empty);
      return;
    }

    reports.forEach(report => {
      const row = document.createElement("article");
      row.className = "report-item";
      const title = document.createElement("h3");
      title.textContent = report.issueType;
      const status = document.createElement("span");
      status.className = `status-pill ${String(report.status).toLowerCase().replaceAll("_", "-")}`;
      status.textContent = String(report.status).replaceAll("_", " ");
      const description = document.createElement("p");
      description.textContent = report.description;
      const meta = document.createElement("span");
      meta.className = "report-meta";
      const date = report.createdAt ? new Date(report.createdAt).toLocaleDateString(undefined, { day: "numeric", month: "short", year: "numeric" }) : "Date unavailable";
      meta.textContent = `${report.location} · ${date}`;
      row.append(title, status, description, meta);
      list.append(row);
    });
  }

  async function loadDashboard() {
    if (!state.token) return;
    try {
      const [user, complaints] = await Promise.all([
        api("/api/auth/me"),
        api("/api/complaints")
      ]);
      state.user = user;
      state.complaints = complaints;
      const firstName = user.fullName.trim().split(/\s+/)[0] || "neighbour";
      document.getElementById("welcome-name").textContent = `${firstName}.`;
      renderComplaints();
    } catch (error) {
      if (state.token) setMessage("complaint-message", error.message, "error");
    }
  }

  function filterQuery(filters) {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value) params.set(key, value);
    });
    const query = params.toString();
    return query ? `?${query}` : "";
  }

  function statusPill(status) {
    const pill = document.createElement("span");
    pill.className = `status-pill ${String(status).toLowerCase().replaceAll("_", "-")}`;
    pill.textContent = String(status).replaceAll("_", " ");
    return pill;
  }

  function appendEmptyMessage(container, title, message) {
    const empty = document.createElement("div");
    empty.className = "empty-state admin-empty-state";
    const heading = document.createElement("h3");
    heading.textContent = title;
    const copy = document.createElement("p");
    copy.textContent = message;
    empty.append(heading, copy);
    container.append(empty);
  }

  function renderAdminComplaints() {
    const container = document.getElementById("admin-complaints");
    container.replaceChildren();
    document.getElementById("admin-case-count").textContent = `${state.adminComplaints.length} matching reports`;
    if (!state.adminComplaints.length) {
      appendEmptyMessage(container, "No complaints match these filters.", "Try another status, issue type, or location.");
      return;
    }

    state.adminComplaints.forEach(report => {
      const row = document.createElement("article");
      row.className = "admin-record";
      const content = document.createElement("div");
      content.className = "admin-record-content";
      const eyebrow = document.createElement("span");
      eyebrow.className = "admin-record-id";
      eyebrow.textContent = `REPORT #${report.id} · ${report.createdAt ? new Date(report.createdAt).toLocaleDateString() : "DATE N/A"}`;
      const title = document.createElement("h3");
      title.textContent = report.issueType;
      const description = document.createElement("p");
      description.textContent = report.description;
      const location = document.createElement("span");
      location.className = "admin-record-location";
      location.textContent = report.location;
      content.append(eyebrow, title, description, location);

      const actions = document.createElement("div");
      actions.className = "admin-record-actions";
      actions.append(statusPill(report.status));
      const select = document.createElement("select");
      select.className = "status-select";
      select.setAttribute("aria-label", `Change status for report ${report.id}`);
      ["SUBMITTED", "IN_PROGRESS", "RESOLVED", "REJECTED"].forEach(status => {
        const option = document.createElement("option");
        option.value = status;
        option.textContent = status.replaceAll("_", " ");
        option.selected = status === report.status;
        select.append(option);
      });
      const update = document.createElement("button");
      update.type = "button";
      update.className = "button button-dark button-small";
      update.dataset.updateStatus = String(report.id);
      update.textContent = "Update";
      actions.append(select, update);
      row.append(content, actions);
      container.append(row);
    });
  }

  function renderAdminUsers() {
    const container = document.getElementById("admin-users");
    container.replaceChildren();
    document.getElementById("admin-users-count").textContent = state.adminUsers.length;
    document.getElementById("admin-users-toggle-count").textContent = state.adminUsers.length;
    if (!state.adminUsers.length) {
      appendEmptyMessage(container, "No resident accounts found.", "Registered accounts will appear here.");
      return;
    }
    state.adminUsers.forEach(user => {
      const row = document.createElement("article");
      row.className = "admin-user-row";
      const monogram = document.createElement("span");
      monogram.className = "user-monogram";
      monogram.textContent = (user.fullName || "U").trim().slice(0, 1).toUpperCase();
      const identity = document.createElement("div");
      identity.className = "admin-user-identity";
      const name = document.createElement("strong");
      name.textContent = user.fullName;
      const email = document.createElement("span");
      email.textContent = user.email;
      identity.append(name, email);
      const role = document.createElement("span");
      role.className = "user-role-tag";
      role.textContent = user.role;
      const remove = document.createElement("button");
      remove.type = "button";
      remove.className = "delete-user-button";
      remove.dataset.deleteUser = String(user.id);
      remove.textContent = "Delete";
      const isSelf = String(user.id) === String(state.user?.id);
      remove.disabled = isSelf;
      if (isSelf) remove.title = "You cannot delete the account you are currently using.";
      row.append(monogram, identity, role, remove);
      container.append(row);
    });
  }

  async function loadAdminDashboard() {
    if (!state.token || state.user?.role !== "ADMIN") return;
    setMessage("admin-message");
    try {
      const [allReports, filteredReports, users] = await Promise.all([
        api("/api/admin/complaints"),
        api(`/api/admin/complaints${filterQuery(state.adminFilters)}`),
        api("/api/users")
      ]);
      state.allAdminComplaints = allReports;
      state.adminComplaints = filteredReports.filter(report =>
        report.status === "SUBMITTED" || report.status === "IN_PROGRESS");
      state.adminUsers = users;
      const settled = allReports.filter(report => ["RESOLVED", "REJECTED"].includes(report.status)).length;
      document.getElementById("admin-total").textContent = allReports.length;
      document.getElementById("admin-open").textContent = allReports.length - settled;
      document.getElementById("admin-settled").textContent = settled;
      renderAdminComplaints();
      renderAdminUsers();
    } catch (error) {
      setMessage("admin-message", error.message, "error");
    }
  }

  function renderSettledComplaints() {
    const container = document.getElementById("settled-complaints");
    container.replaceChildren();
    const reports = state.settledComplaints;
    document.getElementById("settled-resolved-count").textContent = reports.filter(report => report.status === "RESOLVED").length;
    document.getElementById("settled-rejected-count").textContent = reports.filter(report => report.status === "REJECTED").length;
    document.getElementById("settled-total-count").textContent = reports.length;
    if (!reports.length) {
      appendEmptyMessage(container, "No settled complaints match these filters.", "Try broadening your search or choosing all settled statuses.");
      updateSettledBulkActions();
      return;
    }
    reports.forEach(report => {
      const row = document.createElement("article");
      row.className = "admin-record settled-record";
      const select = document.createElement("input");
      select.type = "checkbox";
      select.className = "settled-row-select";
      select.value = String(report.id);
      select.checked = state.selectedSettledIds.has(String(report.id));
      select.setAttribute("aria-label", `Select settled complaint ${report.id}`);
      const content = document.createElement("div");
      content.className = "admin-record-content";
      const eyebrow = document.createElement("span");
      eyebrow.className = "admin-record-id";
      eyebrow.textContent = `REPORT #${report.id} · ${report.createdAt ? new Date(report.createdAt).toLocaleDateString() : "DATE N/A"}`;
      const title = document.createElement("h3");
      title.textContent = report.issueType;
      const description = document.createElement("p");
      description.textContent = report.description;
      const location = document.createElement("span");
      location.className = "admin-record-location";
      location.textContent = report.location;
      content.append(eyebrow, title, description, location);
      row.append(select, content, statusPill(report.status));
      container.append(row);
    });
    updateSettledBulkActions();
  }

  function updateSettledBulkActions() {
    const visibleIds = [...document.querySelectorAll(".settled-row-select")].map(input => input.value);
    const selectedVisible = visibleIds.filter(id => state.selectedSettledIds.has(id));
    document.getElementById("settled-selected-count").textContent = `${selectedVisible.length} selected`;
    document.getElementById("delete-settled-selected").disabled = selectedVisible.length === 0;
    const selectAll = document.getElementById("settled-select-all");
    selectAll.checked = visibleIds.length > 0 && selectedVisible.length === visibleIds.length;
    selectAll.indeterminate = selectedVisible.length > 0 && selectedVisible.length < visibleIds.length;
  }

  async function loadSettledDashboard() {
    if (!state.token || state.user?.role !== "ADMIN") return;
    setMessage("settled-message");
    try {
      state.settledComplaints = await api(`/api/admin/complaints/settled${filterQuery(state.settledFilters)}`);
      const visibleIds = new Set(state.settledComplaints.map(report => String(report.id)));
      state.selectedSettledIds = new Set([...state.selectedSettledIds].filter(id => visibleIds.has(id)));
      renderSettledComplaints();
    } catch (error) {
      setMessage("settled-message", error.message, "error");
    }
  }

  document.getElementById("login-form").addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) return;
    const button = form.querySelector("button[type=submit]");
    button.disabled = true;
    button.textContent = "Signing in...";
    setMessage("login-message");
    try {
      const result = await api("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email: form.elements.email.value.trim(),
          password: form.elements.password.value
        })
      });
      state.token = result.accessToken;
      state.user = result.user;
      updateNavigation();
      form.reset();
      navigate(result.user.role === "ADMIN" ? "admin" : "dashboard");
    } catch (error) {
      setMessage("login-message", error.message, "error");
    } finally {
      button.disabled = false;
      button.textContent = "Sign in";
    }
  });

  document.getElementById("register-form").addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) return;
    const button = form.querySelector("button[type=submit]");
    button.disabled = true;
    button.textContent = "Creating account...";
    setMessage("register-message");
    const email = form.elements.email.value.trim();
    try {
      await api("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({
          fullName: form.elements.fullName.value.trim(),
          email,
          phoneNumber: form.elements.phoneNumber.value.trim() || null,
          password: form.elements.password.value
        })
      });
      const loginEmail = document.getElementById("login-email");
      loginEmail.value = email;
      form.reset();
      navigate("login");
      setMessage("login-message", "Your account is ready. Sign in to continue.");
    } catch (error) {
      setMessage("register-message", error.message, "error");
    } finally {
      button.disabled = false;
      button.textContent = "Create account";
    }
  });

  document.getElementById("complaint-form").addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) return;
    const button = form.querySelector("button[type=submit]");
    button.disabled = true;
    button.textContent = "Sending report...";
    setMessage("complaint-message");
    try {
      await api("/api/complaints", {
        method: "POST",
        body: JSON.stringify({
          issueType: form.elements.issueType.value.trim(),
          location: form.elements.location.value.trim(),
          description: form.elements.description.value.trim()
        })
      });
      form.reset();
      setMessage("complaint-message", "Your report has been submitted.");
      state.complaints = await api("/api/complaints");
      renderComplaints();
    } catch (error) {
      setMessage("complaint-message", error.message, "error");
    } finally {
      button.disabled = false;
      button.textContent = "Submit report";
    }
  });

  document.getElementById("admin-filter-form").addEventListener("submit", event => {
    event.preventDefault();
    state.adminFilters = Object.fromEntries(new FormData(event.currentTarget).entries());
    loadAdminDashboard();
  });
  document.getElementById("admin-filter-reset").addEventListener("click", () => {
    document.getElementById("admin-filter-form").reset();
    state.adminFilters = {};
    loadAdminDashboard();
  });
  document.getElementById("admin-users-toggle").addEventListener("click", event => {
    const button = event.currentTarget;
    const expanded = button.getAttribute("aria-expanded") === "true";
    button.setAttribute("aria-expanded", String(!expanded));
    document.getElementById("admin-users-panel").hidden = expanded;
  });
  document.getElementById("settled-filter-form").addEventListener("submit", event => {
    event.preventDefault();
    state.settledFilters = Object.fromEntries(new FormData(event.currentTarget).entries());
    state.selectedSettledIds.clear();
    loadSettledDashboard();
  });
  document.getElementById("settled-filter-reset").addEventListener("click", () => {
    document.getElementById("settled-filter-form").reset();
    state.settledFilters = {};
    state.selectedSettledIds.clear();
    loadSettledDashboard();
  });

  document.getElementById("admin-complaints").addEventListener("click", async event => {
    const button = event.target.closest("[data-update-status]");
    if (!button) return;
    const row = button.closest(".admin-record");
    const status = row.querySelector(".status-select").value;
    button.disabled = true;
    try {
      await api(`/api/admin/complaints/${button.dataset.updateStatus}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status })
      });
      await loadAdminDashboard();
      setMessage("admin-message", "Complaint status updated.");
    } catch (error) {
      setMessage("admin-message", error.message, "error");
      button.disabled = false;
    }
  });

  document.getElementById("admin-users").addEventListener("click", async event => {
    const button = event.target.closest("[data-delete-user]");
    if (!button || button.disabled) return;
    const user = state.adminUsers.find(item => String(item.id) === button.dataset.deleteUser);
    if (!user) return;
    const confirmed = window.confirm(`Permanently delete ${user.fullName} and all complaints linked to this account? This cannot be undone.`);
    if (!confirmed) return;
    button.disabled = true;
    try {
      await api(`/api/users/${button.dataset.deleteUser}`, { method: "DELETE" });
      await loadAdminDashboard();
      setMessage("admin-message", `Deleted ${user.fullName} and their linked complaints.`);
    } catch (error) {
      setMessage("admin-message", error.message, "error");
      button.disabled = false;
    }
  });

  document.getElementById("settled-complaints").addEventListener("change", event => {
    const checkbox = event.target.closest(".settled-row-select");
    if (!checkbox) return;
    if (checkbox.checked) state.selectedSettledIds.add(checkbox.value);
    else state.selectedSettledIds.delete(checkbox.value);
    updateSettledBulkActions();
  });
  document.getElementById("settled-select-all").addEventListener("change", event => {
    document.querySelectorAll(".settled-row-select").forEach(checkbox => {
      checkbox.checked = event.currentTarget.checked;
      if (checkbox.checked) state.selectedSettledIds.add(checkbox.value);
      else state.selectedSettledIds.delete(checkbox.value);
    });
    updateSettledBulkActions();
  });
  document.getElementById("delete-settled-selected").addEventListener("click", async event => {
    const button = event.currentTarget;
    const selectedIds = [...state.selectedSettledIds];
    if (!selectedIds.length) return;
    const confirmed = window.confirm(`Permanently delete ${selectedIds.length} selected settled complaint${selectedIds.length === 1 ? "" : "s"}? This cannot be undone.`);
    if (!confirmed) return;
    button.disabled = true;
    let deleted = 0;
    try {
      for (const id of selectedIds) {
        await api(`/api/admin/complaints/${id}`, { method: "DELETE" });
        deleted += 1;
      }
      state.selectedSettledIds.clear();
      await loadSettledDashboard();
      setMessage("settled-message", `Deleted ${deleted} settled complaint${deleted === 1 ? "" : "s"}.`);
    } catch (error) {
      await loadSettledDashboard();
      setMessage("settled-message", deleted
        ? `${deleted} complaint${deleted === 1 ? " was" : "s were"} deleted. ${error.message}`
        : error.message, "error");
    } finally {
      updateSettledBulkActions();
    }
  });

  function signOut() {
    state.token = null;
    state.user = null;
    state.complaints = [];
    updateNavigation();
    navigate("home");
  }

  navCta.addEventListener("click", event => {
    if (navCta.dataset.signout === "true") {
      event.preventDefault();
      signOut();
    }
  });
  document.getElementById("logout-button").addEventListener("click", signOut);
  document.getElementById("admin-logout").addEventListener("click", signOut);
  document.getElementById("settled-logout").addEventListener("click", signOut);
  menuButton.addEventListener("click", () => {
    const expanded = menuButton.getAttribute("aria-expanded") === "true";
    menuButton.setAttribute("aria-expanded", String(!expanded));
    mainNav.classList.toggle("open", !expanded);
  });
  mainNav.querySelectorAll("a").forEach(link => link.addEventListener("click", () => {
    mainNav.classList.remove("open");
    menuButton.setAttribute("aria-expanded", "false");
  }));
  window.addEventListener("hashchange", () => {
    const route = location.hash.slice(1) || "home";
    if (route === "how-it-works") {
      showView("home");
      requestAnimationFrame(() => document.getElementById("how-it-works").scrollIntoView({ behavior: "smooth" }));
      return;
    }
    showView(route);
  });

  updateNavigation();
  showView(location.hash.slice(1) || "home");
});
