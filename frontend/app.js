const ROLE_OPTIONS = ["TOP", "JUNGLE", "MID", "ADC", "SUPPORT"];
const STORAGE = {
  baseUrl: "lolapi.baseUrl",
  apiKey: "lolapi.apiKey",
  username: "lolapi.username"
};

const resources = {
  overview: {
    label: "Visao geral",
    icon: "layout-dashboard"
  },
  champions: {
    label: "Campeoes",
    singular: "Campeao",
    icon: "sword",
    path: "/api/v1/champions",
    searchParam: "nome",
    searchLabel: "Nome",
    embeddedHints: ["champion", "campe"],
    columns: [
      ["id", "ID"],
      ["nome", "Nome"],
      ["role", "Rota"],
      ["players", "Jogadores"]
    ],
    fields: [
      { name: "id", label: "ID", type: "number", defaultValue: 0, min: 0 },
      { name: "nome", label: "Nome", type: "text", required: true, placeholder: "Aatrox" },
      { name: "role", label: "Role", type: "select", required: true, options: ROLE_OPTIONS }
    ]
  },
  players: {
    label: "Jogadores",
    singular: "Jogador",
    icon: "user-round",
    path: "/api/v1/players",
    searchParam: "nome",
    searchLabel: "Nome",
    embeddedHints: ["player", "jogador"],
    columns: [
      ["id", "ID"],
      ["nome", "Nome"],
      ["nick", "Nick"],
      ["role", "Role"],
      ["team", "Time"],
      ["champions", "Campeoes"]
    ],
    fields: [
      { name: "id", label: "ID", type: "number", defaultValue: 0, min: 0 },
      { name: "nome", label: "Nome", type: "text", required: true, placeholder: "Lee Sang-hyeok" },
      { name: "nick", label: "Nick", type: "text", required: true, placeholder: "Faker" },
      { name: "role", label: "Role", type: "select", required: true, options: ROLE_OPTIONS },
      { name: "teamId", label: "Time", type: "relation", source: "teams", allowZero: true },
      { name: "championIds", label: "Campeoes", type: "relation-multiple", source: "champions" }
    ]
  },
  teams: {
    label: "Times",
    singular: "Time",
    icon: "shield",
    path: "/api/v1/teams",
    searchParam: "nome",
    searchLabel: "Nome",
    embeddedHints: ["team", "time"],
    columns: [
      ["id", "ID"],
      ["nome", "Nome"],
      ["regiao", "Regiao"],
      ["coach", "Coach"],
      ["players", "Jogadores"]
    ],
    fields: [
      { name: "id", label: "ID", type: "number", defaultValue: 0, min: 0 },
      { name: "nome", label: "Nome", type: "text", required: true, placeholder: "T1" },
      { name: "regiao", label: "Regiao", type: "text", required: true, placeholder: "KR" },
      { name: "coachId", label: "Coach", type: "relation", source: "coaches", allowZero: true }
    ]
  },
  coaches: {
    label: "Coaches",
    singular: "Coach",
    icon: "badge-check",
    path: "/api/v1/coaches",
    searchParam: "nome",
    searchLabel: "Nome",
    embeddedHints: ["coach"],
    columns: [
      ["id", "ID"],
      ["nome", "Nome"],
      ["experiencia", "Experiencia"],
      ["team", "Time"]
    ],
    fields: [
      { name: "id", label: "ID", type: "number", defaultValue: 0, min: 0 },
      { name: "nome", label: "Nome", type: "text", required: true, placeholder: "kkOma" },
      { name: "experiencia", label: "Experiencia", type: "number", required: true, min: 0, max: 60, defaultValue: 0 }
    ]
  },
  matchgames: {
    label: "Partidas",
    singular: "Partida",
    icon: "swords",
    path: "/api/v1/matchgames",
    searchParam: "duracao",
    searchLabel: "Duracao",
    embeddedHints: ["match", "partida"],
    columns: [
      ["id", "ID"],
      ["duracao", "Duracao"],
      ["timeA", "Time A"],
      ["timeB", "Time B"],
      ["vencedor", "Vencedor"],
      ["players", "Jogadores"],
      ["champions", "Campeoes"]
    ],
    fields: [
      { name: "id", label: "ID", type: "number", defaultValue: 0, min: 0 },
      { name: "duracao", label: "Duracao", type: "text", required: true, placeholder: "35:00" },
      { name: "timeAId", label: "Time A", type: "relation", source: "teams", required: true },
      { name: "timeBId", label: "Time B", type: "relation", source: "teams", required: true },
      { name: "vencedorId", label: "Vencedor", type: "relation", source: "teams", allowZero: true },
      { name: "playerIds", label: "Jogadores", type: "relation-multiple", source: "players" },
      { name: "championIds", label: "Campeoes", type: "relation-multiple", source: "champions" }
    ]
  },
  version: {
    label: "API Version",
    icon: "git-branch"
  }
};

const state = {
  active: "overview",
  mode: "list",
  page: 0,
  size: 8,
  search: "",
  editItem: null,
  data: {},
  pageMeta: {},
  loading: false
};

const dom = {
  navList: document.querySelector("#navList"),
  content: document.querySelector("#content"),
  pageTitle: document.querySelector("#pageTitle"),
  apiBaseUrl: document.querySelector("#apiBaseUrl"),
  apiUsername: document.querySelector("#apiUsername"),
  apiKeyInput: document.querySelector("#apiKeyInput"),
  authStatus: document.querySelector("#authStatus"),
  generateKeyButton: document.querySelector("#generateKeyButton"),
  saveKeyButton: document.querySelector("#saveKeyButton"),
  revokeKeyButton: document.querySelector("#revokeKeyButton"),
  refreshButton: document.querySelector("#refreshButton"),
  swaggerLink: document.querySelector("#swaggerLink"),
  connectionDot: document.querySelector("#connectionDot"),
  connectionText: document.querySelector("#connectionText"),
  rateLimitText: document.querySelector("#rateLimitText"),
  lastActionText: document.querySelector("#lastActionText"),
  toastPanel: document.querySelector("#toastPanel")
};

function init() {
  dom.apiBaseUrl.value = localStorage.getItem(STORAGE.baseUrl) || "http://localhost:8080";
  dom.apiUsername.value = localStorage.getItem(STORAGE.username) || "guilherme";
  dom.apiKeyInput.value = localStorage.getItem(STORAGE.apiKey) || "";
  syncAuthStatus();
  renderNavigation();
  bindGlobalEvents();
  updateSwaggerLink();
  render();
  if (hasApiKey()) {
    loadOverview();
  }
}

function bindGlobalEvents() {
  dom.generateKeyButton.addEventListener("click", generateApiKey);
  dom.saveKeyButton.addEventListener("click", saveApiKey);
  dom.revokeKeyButton.addEventListener("click", revokeApiKey);
  dom.refreshButton.addEventListener("click", refreshCurrent);
  dom.apiBaseUrl.addEventListener("change", () => {
    localStorage.setItem(STORAGE.baseUrl, cleanBaseUrl());
    updateSwaggerLink();
  });
  dom.apiUsername.addEventListener("change", () => {
    localStorage.setItem(STORAGE.username, dom.apiUsername.value.trim());
  });
}

function renderNavigation() {
  dom.navList.innerHTML = Object.entries(resources).map(([key, resource]) => `
    <button class="nav-button ${state.active === key ? "active" : ""}" type="button" data-nav="${key}">
      <i data-lucide="${resource.icon}" aria-hidden="true"></i>
      <span>${resource.label}</span>
    </button>
  `).join("");

  dom.navList.querySelectorAll("[data-nav]").forEach((button) => {
    button.addEventListener("click", () => {
      state.active = button.dataset.nav;
      state.mode = "list";
      state.page = 0;
      state.search = "";
      state.editItem = null;
      renderNavigation();
      render();
      refreshCurrent();
    });
  });
  refreshIcons();
}

function render() {
  const resource = resources[state.active];
  dom.pageTitle.textContent = resource.label;
  dom.content.innerHTML = "";

  if (state.active === "overview") {
    renderOverview();
  } else if (state.active === "version") {
    renderVersion();
  } else {
    renderResource(state.active);
  }
  refreshIcons();
}

function renderOverview() {
  const cards = ["champions", "players", "teams", "coaches", "matchgames"].map((key) => {
    const resource = resources[key];
    const total = state.pageMeta[key]?.totalElements ?? state.data[key]?.length ?? 0;
    return `
      <article class="metric-card">
        <div class="metric-icon"><i data-lucide="${resource.icon}" aria-hidden="true"></i></div>
        <div>
          <p>${resource.label}</p>
          <strong>${total}</strong>
        </div>
      </article>
    `;
  }).join("");

  dom.content.innerHTML = `
    <section class="metric-grid">${cards}</section>
    <section class="panel">
      <div class="panel-header">
        <div>
          <h3>Controle da API</h3>
          <p>${hasApiKey() ? "Chave carregada. O painel esta pronto para operar." : "Gere ou cole uma chave para liberar as operacoes protegidas."}</p>
        </div>
        <button class="btn btn-primary" type="button" data-action="load-overview">
          <i data-lucide="activity" aria-hidden="true"></i>
          Sincronizar
        </button>
      </div>
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Recurso</th>
              <th>Endpoint</th>
              <th>Busca</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            ${["champions", "players", "teams", "coaches", "matchgames"].map((key) => `
              <tr>
                <td><span class="row-title">${resources[key].label}</span></td>
                <td><span class="tag">${resources[key].path}</span></td>
                <td>${resources[key].searchParam}</td>
                <td>${state.data[key] ? "Carregado" : "Aguardando"}</td>
              </tr>
            `).join("")}
          </tbody>
        </table>
      </div>
    </section>
  `;

  dom.content.querySelector("[data-action='load-overview']").addEventListener("click", loadOverview);
}

function renderResource(key) {
  const resource = resources[key];
  const rows = state.data[key] || [];
  const page = state.pageMeta[key] || { number: 0, totalPages: 0, totalElements: rows.length };

  dom.content.innerHTML = `
    <section class="panel">
      <div class="panel-header">
        <div>
          <h3>${resource.label}</h3>
          <p>${resource.path}</p>
        </div>
        <div class="segmented" role="tablist" aria-label="Modo de ${resource.label}">
          <button type="button" class="${state.mode === "list" ? "active" : ""}" data-mode="list">Listar</button>
          <button type="button" class="${state.mode === "create" ? "active" : ""}" data-mode="create">Adicionar</button>
          <button type="button" class="${state.mode === "edit" ? "active" : ""}" data-mode="edit">Editar</button>
        </div>
      </div>

      ${state.mode === "list" ? renderListToolbar(resource) : ""}
      ${state.mode === "list" ? renderTable(key, rows, page) : ""}
      ${state.mode === "create" ? renderForm(key, "create") : ""}
      ${state.mode === "edit" ? renderEditArea(key) : ""}
    </section>
  `;

  bindResourceEvents(key);
}

function renderListToolbar(resource) {
  return `
    <form class="toolbar" data-search-form>
      <label class="field">
        <span>Buscar por ${resource.searchLabel}</span>
        <input name="search" type="text" value="${escapeAttribute(state.search)}" placeholder="Digite para filtrar">
      </label>
      <label class="field">
        <span>Tamanho</span>
        <select name="size">
          ${[5, 8, 10, 20].map((value) => `<option value="${value}" ${state.size === value ? "selected" : ""}>${value}</option>`).join("")}
        </select>
      </label>
      <button class="btn btn-primary" type="submit">
        <i data-lucide="search" aria-hidden="true"></i>
        Buscar
      </button>
    </form>
  `;
}

function renderTable(key, rows, page) {
  const resource = resources[key];
  if (!rows.length) {
    return `<div class="empty-state">Nenhum registro carregado.</div>`;
  }

  return `
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            ${resource.columns.map(([, label]) => `<th>${label}</th>`).join("")}
            <th>Acoes</th>
          </tr>
        </thead>
        <tbody>
          ${rows.map((row) => `
            <tr>
              ${resource.columns.map(([column]) => `<td>${formatColumn(row, column)}</td>`).join("")}
              <td>
                <div class="mini-actions">
                  <button class="mini-action" type="button" title="Editar" aria-label="Editar" data-edit-id="${row.id}">
                    <i data-lucide="pencil" aria-hidden="true"></i>
                  </button>
                  <button class="mini-action" type="button" title="Ver JSON" aria-label="Ver JSON" data-json-id="${row.id}">
                    <i data-lucide="braces" aria-hidden="true"></i>
                  </button>
                  <button class="mini-action" type="button" title="Excluir" aria-label="Excluir" data-delete-id="${row.id}">
                    <i data-lucide="trash-2" aria-hidden="true"></i>
                  </button>
                </div>
              </td>
            </tr>
          `).join("")}
        </tbody>
      </table>
    </div>
    <div class="pagination">
      <button class="btn btn-muted" type="button" data-page-prev ${page.number <= 0 ? "disabled" : ""}>
        <i data-lucide="chevron-left" aria-hidden="true"></i>
        Anterior
      </button>
      <span>Pagina ${(page.number ?? 0) + 1} de ${Math.max(page.totalPages ?? 1, 1)}</span>
      <button class="btn btn-muted" type="button" data-page-next ${page.totalPages && page.number + 1 < page.totalPages ? "" : "disabled"}>
        Proxima
        <i data-lucide="chevron-right" aria-hidden="true"></i>
      </button>
    </div>
  `;
}

function renderEditArea(key) {
  const rows = state.data[key] || [];
  const selectedId = state.editItem?.id ?? "";
  return `
    <div class="form-panel">
      <label class="field">
        <span>Registro para editar</span>
        <select data-edit-picker>
          <option value="">Selecione</option>
          ${rows.map((row) => `<option value="${row.id}" ${String(selectedId) === String(row.id) ? "selected" : ""}>#${row.id} - ${escapeHtml(displayName(row))}</option>`).join("")}
        </select>
      </label>
    </div>
    ${state.editItem ? renderForm(key, "edit", state.editItem) : `<div class="empty-state">Selecione um registro carregado ou use o botao editar na tabela.</div>`}
  `;
}

function renderForm(key, mode, item = {}) {
  const resource = resources[key];
  return `
    <form class="form-panel" data-resource-form="${mode}">
      <div class="section-heading">
        <i data-lucide="${mode === "create" ? "plus" : "pencil"}" aria-hidden="true"></i>
        <h3>${mode === "create" ? "Adicionar" : "Editar"} ${resource.singular}</h3>
      </div>
      <div class="form-grid">
        ${resource.fields.map((field) => renderField(field, item, mode)).join("")}
      </div>
      <div class="form-actions">
        <button class="btn btn-muted" type="button" data-cancel-form>
          <i data-lucide="x" aria-hidden="true"></i>
          Cancelar
        </button>
        <button class="btn btn-primary" type="submit">
          <i data-lucide="${mode === "create" ? "plus" : "save"}" aria-hidden="true"></i>
          ${mode === "create" ? "Criar" : "Salvar"}
        </button>
      </div>
    </form>
  `;
}

function renderField(field, item, mode) {
  const rawValue = getFieldValue(field, item, mode);
  const required = field.required ? "required" : "";
  const min = field.min !== undefined ? `min="${field.min}"` : "";
  const max = field.max !== undefined ? `max="${field.max}"` : "";
  const placeholder = field.placeholder ? `placeholder="${escapeAttribute(field.placeholder)}"` : "";
  const classes = field.type === "relation-multiple" ? "field wide" : "field";

  if (field.type === "select") {
    return `
      <label class="${classes}">
        <span>${field.label}</span>
        <select name="${field.name}" ${required}>
          <option value="">Selecione</option>
          ${field.options.map((option) => `<option value="${option}" ${rawValue === option ? "selected" : ""}>${option}</option>`).join("")}
        </select>
      </label>
    `;
  }

  if (field.type === "relation") {
    return `
      <label class="${classes}">
        <span>${field.label}</span>
        <select name="${field.name}" ${required}>
          ${field.allowZero ? `<option value="0">Sem vinculo</option>` : `<option value="">Selecione</option>`}
          ${relationOptions(field.source, rawValue)}
        </select>
      </label>
    `;
  }

  if (field.type === "relation-multiple") {
    const selected = new Set(Array.isArray(rawValue) ? rawValue.map(String) : []);
    const options = (state.data[field.source] || []).map((row) => `
      <option value="${row.id}" ${selected.has(String(row.id)) ? "selected" : ""}>#${row.id} - ${escapeHtml(displayName(row))}</option>
    `).join("");
    return `
      <label class="${classes}">
        <span>${field.label}</span>
        <select name="${field.name}" multiple>
          ${options}
        </select>
      </label>
    `;
  }

  return `
    <label class="${classes}">
      <span>${field.label}</span>
      <input name="${field.name}" type="${field.type}" value="${escapeAttribute(rawValue)}" ${required} ${min} ${max} ${placeholder}>
    </label>
  `;
}

function bindResourceEvents(key) {
  dom.content.querySelectorAll("[data-mode]").forEach((button) => {
    button.addEventListener("click", () => {
      state.mode = button.dataset.mode;
      if (state.mode !== "edit") {
        state.editItem = null;
      }
      render();
      if (state.mode !== "create") {
        ensureListLoaded(key);
      }
    });
  });

  const searchForm = dom.content.querySelector("[data-search-form]");
  if (searchForm) {
    searchForm.addEventListener("submit", (event) => {
      event.preventDefault();
      state.search = searchForm.elements.search.value.trim();
      state.size = Number(searchForm.elements.size.value);
      state.page = 0;
      loadResource(key);
    });
  }

  const form = dom.content.querySelector("[data-resource-form]");
  if (form) {
    form.addEventListener("submit", (event) => submitResourceForm(event, key, form.dataset.resourceForm));
  }

  dom.content.querySelectorAll("[data-cancel-form]").forEach((button) => {
    button.addEventListener("click", () => {
      state.mode = "list";
      state.editItem = null;
      render();
    });
  });

  dom.content.querySelectorAll("[data-edit-id]").forEach((button) => {
    button.addEventListener("click", () => {
      const item = findLoadedItem(key, button.dataset.editId);
      state.editItem = item;
      state.mode = "edit";
      render();
    });
  });

  dom.content.querySelectorAll("[data-json-id]").forEach((button) => {
    button.addEventListener("click", () => {
      const item = findLoadedItem(key, button.dataset.jsonId);
      showJson(item);
    });
  });

  dom.content.querySelectorAll("[data-delete-id]").forEach((button) => {
    button.addEventListener("click", () => deleteResource(key, button.dataset.deleteId));
  });

  const picker = dom.content.querySelector("[data-edit-picker]");
  if (picker) {
    picker.addEventListener("change", () => {
      state.editItem = picker.value ? findLoadedItem(key, picker.value) : null;
      render();
    });
  }

  const prev = dom.content.querySelector("[data-page-prev]");
  const next = dom.content.querySelector("[data-page-next]");
  if (prev) {
    prev.addEventListener("click", () => {
      state.page = Math.max(0, state.page - 1);
      loadResource(key);
    });
  }
  if (next) {
    next.addEventListener("click", () => {
      state.page += 1;
      loadResource(key);
    });
  }
}

function renderVersion() {
  dom.content.innerHTML = `
    <section class="version-grid">
      <form class="form-panel" data-version-form>
        <div class="section-heading">
          <i data-lucide="git-compare" aria-hidden="true"></i>
          <h3>Jogador v1 e v2</h3>
        </div>
        <label class="field">
          <span>ID do jogador</span>
          <input name="playerId" type="number" min="1" value="1" required>
        </label>
        <div class="button-row">
          <button class="btn btn-muted" type="submit" value="v1" name="version">
            <i data-lucide="corner-down-right" aria-hidden="true"></i>
            Buscar v1
          </button>
          <button class="btn btn-primary" type="submit" value="v2" name="version">
            <i data-lucide="corner-down-right" aria-hidden="true"></i>
            Buscar v2
          </button>
        </div>
      </form>
      <pre class="json-box" id="versionOutput">Aguardando consulta.</pre>
    </section>
  `;

  const form = dom.content.querySelector("[data-version-form]");
  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const submitter = event.submitter;
    const version = submitter?.value || "v1";
    const id = form.elements.playerId.value;
    try {
      const response = await apiFetch(`/api-version/${version}/players/${id}`);
      dom.content.querySelector("#versionOutput").textContent = JSON.stringify(response.data, null, 2);
      updateConnection(true, response);
      notify("Consulta realizada", `Jogador ${version} carregado.`, "success");
    } catch (error) {
      handleError(error);
    }
  });
}

async function generateApiKey() {
  const username = dom.apiUsername.value.trim() || "guilherme";
  localStorage.setItem(STORAGE.username, username);
  try {
    const response = await rawFetch(`/api-keys/generate?username=${encodeURIComponent(username)}`, {
      method: "POST"
    }, { auth: false });
    const keyValue = response.data.keyValue;
    dom.apiKeyInput.value = keyValue;
    localStorage.setItem(STORAGE.apiKey, keyValue);
    syncAuthStatus();
    updateConnection(true, response);
    notify("Chave gerada", "Cole ou mantenha a chave salva para operar o painel.", "success");
    await loadOverview();
  } catch (error) {
    handleError(error);
  }
}

function saveApiKey() {
  const key = dom.apiKeyInput.value.trim();
  if (!key) {
    notify("Chave vazia", "Cole uma X-API-Key antes de salvar.", "error");
    return;
  }
  localStorage.setItem(STORAGE.apiKey, key);
  localStorage.setItem(STORAGE.baseUrl, cleanBaseUrl());
  syncAuthStatus();
  notify("Chave salva", "As proximas chamadas usarao essa X-API-Key.", "success");
  refreshCurrent();
}

async function revokeApiKey() {
  const key = getApiKey();
  if (!key) {
    notify("Sem chave", "Nao ha chave salva para revogar.", "error");
    return;
  }
  if (!window.confirm("Revogar a chave atual?")) {
    return;
  }
  try {
    const response = await apiFetch(`/api-keys/${encodeURIComponent(key)}`, { method: "DELETE" });
    dom.apiKeyInput.value = "";
    localStorage.removeItem(STORAGE.apiKey);
    syncAuthStatus();
    updateConnection(true, response);
    notify("Chave revogada", "A chave atual foi desativada.", "success");
  } catch (error) {
    handleError(error);
  }
}

async function refreshCurrent() {
  if (state.active === "overview") {
    await loadOverview();
  } else if (state.active !== "version") {
    await loadResource(state.active);
  }
}

async function loadOverview() {
  if (!hasApiKey()) {
    render();
    notify("Autorizacao pendente", "Gere ou salve uma X-API-Key para carregar os dados.", "error");
    return;
  }
  await Promise.allSettled(["champions", "players", "teams", "coaches", "matchgames"].map((key) => loadResource(key, { silent: true })));
  render();
}

async function ensureListLoaded(key) {
  if (!state.data[key]) {
    await loadResource(key);
  }
}

async function loadResource(key, options = {}) {
  if (!hasApiKey()) {
    if (!options.silent) {
      notify("Autorizacao pendente", "Gere ou salve uma X-API-Key antes de consultar.", "error");
    }
    return;
  }

  const resource = resources[key];
  const params = new URLSearchParams({
    page: String(state.active === key ? state.page : 0),
    size: String(state.size)
  });
  const useSearch = state.active === key && state.search;
  const path = useSearch ? `${resource.path}/buscar` : resource.path;
  if (useSearch) {
    params.set(resource.searchParam, state.search);
  }

  try {
    const response = await apiFetch(`${path}?${params.toString()}`);
    state.data[key] = extractRows(response.data, resource);
    state.pageMeta[key] = response.data.page || {
      number: 0,
      totalPages: state.data[key].length ? 1 : 0,
      totalElements: state.data[key].length
    };
    updateConnection(true, response);
    if (!options.silent && state.active === key) {
      render();
    }
  } catch (error) {
    if (!options.silent) {
      handleError(error);
    }
  }
}

async function submitResourceForm(event, key, mode) {
  event.preventDefault();
  if (!hasApiKey()) {
    notify("Autorizacao pendente", "Gere ou salve uma X-API-Key antes de gravar.", "error");
    return;
  }

  const form = event.currentTarget;
  const resource = resources[key];
  const payload = readFormPayload(form, resource);
  const path = mode === "create" ? resource.path : `${resource.path}/${state.editItem.id}`;
  const method = mode === "create" ? "POST" : "PUT";

  try {
    const response = await apiFetch(path, {
      method,
      body: JSON.stringify(payload)
    }, {
      idempotency: mode === "create"
    });
    updateConnection(true, response);
    notify(mode === "create" ? "Criado" : "Atualizado", `${resource.singular} salvo com sucesso.`, "success");
    state.mode = "list";
    state.editItem = null;
    await loadResource(key);
  } catch (error) {
    handleError(error);
  }
}

async function deleteResource(key, id) {
  const resource = resources[key];
  if (!window.confirm(`Excluir ${resource.singular} #${id}?`)) {
    return;
  }
  try {
    const response = await apiFetch(`${resource.path}/${id}`, { method: "DELETE" });
    updateConnection(true, response);
    notify("Excluido", `${resource.singular} removido.`, "success");
    await loadResource(key);
  } catch (error) {
    handleError(error);
  }
}

function readFormPayload(form, resource) {
  const payload = {};
  resource.fields.forEach((field) => {
    const control = form.elements[field.name];
    if (!control) {
      return;
    }
    if (field.type === "number" || field.type === "relation") {
      payload[field.name] = control.value === "" ? null : Number(control.value);
      return;
    }
    if (field.type === "relation-multiple") {
      payload[field.name] = Array.from(control.selectedOptions).map((option) => Number(option.value));
      return;
    }
    payload[field.name] = control.value.trim();
  });
  return payload;
}

function getFieldValue(field, item, mode) {
  if (mode === "create") {
    return field.defaultValue ?? "";
  }
  if (field.name in item) {
    return item[field.name] ?? "";
  }
  if (field.name.endsWith("Id")) {
    const relationName = field.name.slice(0, -2);
    return item[relationName]?.id ?? (field.allowZero ? 0 : "");
  }
  if (field.name.endsWith("Ids")) {
    const relationName = field.name.slice(0, -3);
    const listName = relationName.endsWith("ie") ? `${relationName.slice(0, -2)}ies` : `${relationName}s`;
    return (item[listName] || []).map((entry) => entry.id);
  }
  return "";
}

function relationOptions(source, selectedValue) {
  return (state.data[source] || []).map((row) => `
    <option value="${row.id}" ${String(selectedValue) === String(row.id) ? "selected" : ""}>#${row.id} - ${escapeHtml(displayName(row))}</option>
  `).join("");
}

function formatColumn(row, column) {
  const value = row[column];
  if (column === "id") {
    return `<span class="tag">#${escapeHtml(value)}</span>`;
  }
  if (Array.isArray(value)) {
    return value.length ? value.map((item) => escapeHtml(displayName(item))).join(", ") : "Sem vinculo";
  }
  if (value && typeof value === "object") {
    return escapeHtml(displayName(value));
  }
  if (column === "role" && value) {
    return `<span class="tag">${escapeHtml(value)}</span>`;
  }
  if (column === "experiencia") {
    return `${escapeHtml(value ?? 0)} anos`;
  }
  return escapeHtml(value ?? "Sem vinculo");
}

function displayName(item) {
  if (!item) {
    return "Sem vinculo";
  }
  return item.nick || item.nome || item.duracao || item.keyValue || `ID ${item.id}`;
}

function findLoadedItem(key, id) {
  return (state.data[key] || []).find((item) => String(item.id) === String(id)) || null;
}

function showJson(item) {
  dom.toastPanel.innerHTML = `
    <pre class="json-box">${escapeHtml(JSON.stringify(item, null, 2))}</pre>
  `;
}

async function apiFetch(path, options = {}, extra = {}) {
  return rawFetch(path, options, { ...extra, auth: true });
}

async function rawFetch(path, options = {}, extra = {}) {
  const headers = new Headers(options.headers || {});
  if (options.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  if (extra.auth) {
    const key = getApiKey();
    if (!key) {
      throw new Error("X-API-Key nao informada.");
    }
    headers.set("X-API-Key", key);
  }
  if (extra.idempotency) {
    headers.set("X-Idempotency-Key", createIdempotencyKey());
  }

  const response = await fetch(`${cleanBaseUrl()}${path}`, {
    ...options,
    headers
  });

  const rateLimit = response.headers.get("X-RateLimit-Limit");
  const remaining = response.headers.get("X-RateLimit-Remaining");
  if (rateLimit) {
    dom.rateLimitText.textContent = `${remaining ?? "0"}/${rateLimit}`;
  }

  const text = await response.text();
  const data = text ? parseJson(text) : null;
  const result = { response, data };

  if (!response.ok) {
    const message = data?.mensagem || data?.erro || response.statusText || "Erro na requisicao.";
    const error = new Error(message);
    error.status = response.status;
    error.data = data;
    error.response = response;
    throw error;
  }

  dom.lastActionText.textContent = `${response.status} ${response.statusText || "OK"}`;
  return result;
}

function extractRows(payload, resource) {
  if (Array.isArray(payload)) {
    return payload;
  }
  if (!payload?._embedded) {
    return payload ? [payload].filter((item) => item.id !== undefined) : [];
  }

  const embedded = payload._embedded;
  for (const hint of resource.embeddedHints || []) {
    const match = Object.keys(embedded).find((key) => key.toLowerCase().includes(hint));
    if (match && Array.isArray(embedded[match])) {
      return embedded[match];
    }
  }

  const firstArray = Object.values(embedded).find(Array.isArray);
  return firstArray || [];
}

function parseJson(text) {
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function createIdempotencyKey() {
  if (crypto.randomUUID) {
    return crypto.randomUUID();
  }
  return `post-${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function cleanBaseUrl() {
  const raw = dom.apiBaseUrl.value.trim() || "http://localhost:8080";
  return raw.replace(/\/+$/, "");
}

function getApiKey() {
  return dom.apiKeyInput.value.trim() || localStorage.getItem(STORAGE.apiKey) || "";
}

function hasApiKey() {
  return Boolean(getApiKey());
}

function syncAuthStatus() {
  const key = getApiKey();
  dom.authStatus.textContent = key ? `Chave ativa: ${maskKey(key)}` : "Sem chave salva.";
}

function maskKey(key) {
  if (key.length <= 12) {
    return key;
  }
  return `${key.slice(0, 8)}...${key.slice(-4)}`;
}

function updateSwaggerLink() {
  dom.swaggerLink.href = `${cleanBaseUrl()}/swagger-ui.html`;
}

function updateConnection(online, result) {
  dom.connectionDot.classList.toggle("online", online);
  dom.connectionDot.classList.toggle("offline", !online);
  dom.connectionText.textContent = online ? "API conectada" : "Falha na conexao";
  if (result?.response) {
    dom.lastActionText.textContent = `${result.response.status} ${result.response.statusText || ""}`.trim();
  }
}

function notify(title, message, type = "info") {
  const toast = document.createElement("div");
  toast.className = `toast ${type}`;
  toast.innerHTML = `<strong>${escapeHtml(title)}</strong><span>${escapeHtml(message)}</span>`;
  dom.toastPanel.prepend(toast);
  while (dom.toastPanel.children.length > 3) {
    dom.toastPanel.lastElementChild.remove();
  }
}

function handleError(error) {
  updateConnection(false);
  const detail = error.data ? JSON.stringify(error.data, null, 2) : error.message;
  notify(`Erro ${error.status || ""}`.trim(), error.message || "Falha na requisicao.", "error");
  dom.toastPanel.insertAdjacentHTML("beforeend", `<pre class="json-box">${escapeHtml(detail)}</pre>`);
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
  return escapeHtml(value);
}

function refreshIcons() {
  if (window.lucide) {
    window.lucide.createIcons();
  }
}

init();
