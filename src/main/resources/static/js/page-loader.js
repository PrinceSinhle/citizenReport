(() => {
  const main = document.getElementById("page-content");
  const pageFiles = ["home", "login", "register", "dashboard", "admin", "settled"]
    .map(name => `/js/pages/${name}.html`);

  window.pageViewsReady = Promise.all(pageFiles.map(async path => {
    const response = await fetch(path);
    if (!response.ok) {
      throw new Error(`Could not load ${path} (${response.status}).`);
    }
    return response.text();
  })).then(fragments => {
    main.innerHTML = fragments.join("\n");
    return true;
  }).catch(error => {
    console.error("Could not load the application pages.", error);
    const message = document.createElement("p");
    message.className = "page-load-error";
    message.setAttribute("role", "alert");
    message.textContent = "The pages could not be loaded. Please refresh to try again.";
    main.replaceChildren(message);
    return false;
  });
})();
