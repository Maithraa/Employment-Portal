
let currentUser = null;
let toastTimer = null;

async function api(url, options = {}) {
  const response = await fetch(url, {
    credentials: 'same-origin',
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
  });

  let data;
  try {
    data = await response.json();
  } catch {
    data = {};
  }

  if (!response.ok) {
    throw new Error(data.error || 'Something went wrong. Please try again.');
  }

  return data;
}

function escapeHTML(value) {
  return String(value ?? '').replace(/[&<>"']/g, c => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[c]));
}

function notify(message) {
  const toast = document.getElementById('toast');
  if (!toast) return;

  toast.textContent = message;
  toast.classList.add('show');

  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => {
    toast.classList.remove('show');
  }, 3000);
}

function showPage(name) {
  document.querySelectorAll('.page').forEach(page => {
    page.classList.remove('active');
  });

  const page = document.getElementById(name + 'Page');

  if (!page) {
    console.error('Page not found:', name);
    return;
  }

  page.classList.add('active');
  updateNavigation();

  if (name === 'jobs') renderJobs();
  if (name === 'applications') renderApplications();

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function updateNavigation() {
  const actions = document.getElementById('navActions');
  if (!actions) return;

  if (!currentUser) {
    actions.innerHTML = `
      <button class="btn btn-outline" onclick="showPage('login')">Login</button>
      <button class="btn btn-primary" onclick="showPage('register')">Register</button>
    `;
  } else {
    actions.innerHTML = `
      <span class="nav-link">Hi, ${escapeHTML(currentUser.name.split(' ')[0])}</span>
      <button class="btn btn-outline" onclick="logout()">Logout</button>
    `;
  }
}

async function loadSession() {
  try {
    const data = await api('api/session.php');
    currentUser = data.user;
  } catch {
    currentUser = null;
  }

  updateNavigation();
}

async function renderJobs() {
  const list = document.getElementById('jobsList');
  if (!list) return;

  const keyword =
    document.getElementById('searchKeyword')?.value.trim() || '';

  const location =
    document.getElementById('searchLocation')?.value.trim() || '';

  list.innerHTML = `
    <div class="empty-state">Loading jobs from database...</div>
  `;

  try {
    const params = new URLSearchParams();

    if (keyword) params.set('q', keyword);
    if (location) params.set('location', location);

    const query = params.toString();
    const url = 'api/jobs.php' + (query ? '?' + query : '');

    const jobs = await api(url);

    if (!Array.isArray(jobs)) {
      throw new Error('Unexpected jobs API response.');
    }

    const jobCount = document.getElementById('jobCount');
    const resultCount = document.getElementById('resultCount');

    if (jobCount) jobCount.textContent = jobs.length;

    if (resultCount) {
      resultCount.textContent =
        `${jobs.length} job${jobs.length === 1 ? '' : 's'} found`;
    }

    if (jobs.length === 0) {
      list.innerHTML = `
        <div class="empty-state">
          <h3>No jobs found</h3>
          <p>Try a different title or location.</p>
        </div>
      `;
      return;
    }

    list.innerHTML = jobs.map(job => `
      <article class="job-card">
        <div class="company-mark">
          ${escapeHTML((job.company || 'C').charAt(0).toUpperCase())}
        </div>

        <h3>${escapeHTML(job.title)}</h3>

        <p class="company">
          ${escapeHTML(job.company)} · ${escapeHTML(job.location)}
        </p>

        <div class="job-meta">
          <span>${escapeHTML(job.type)}</span>
          <span>${escapeHTML(job.experience)}</span>
        </div>

        <p class="job-description">
          ${escapeHTML(job.description)}
        </p>

        <div class="job-bottom">
          <span class="salary">${escapeHTML(job.salary)}</span>
          <button
            class="btn btn-primary"
            onclick="applyForJob(${Number(job.id)})">
            Apply Now
          </button>
        </div>
      </article>
    `).join('');

  } catch (error) {
    console.error('Jobs loading error:', error);

    list.innerHTML = `
      <div class="empty-state">
        <h3>Could not load jobs</h3>
        <p>${escapeHTML(error.message)}</p>
        <p>Please check Apache, MySQL and the jobs API.</p>
        <button class="btn btn-outline" onclick="renderJobs()">
          Try Again
        </button>
      </div>
    `;
  }
}

function clearSearch() {
  document.getElementById('searchKeyword').value = '';
  document.getElementById('searchLocation').value = '';
  renderJobs();
}

const registerForm = document.getElementById('registerForm');

if (registerForm) {
  registerForm.addEventListener('submit', async event => {
    event.preventDefault();

    const payload = {
      name: document.getElementById('regName').value.trim(),
      email: document.getElementById('regEmail').value.trim(),
      password: document.getElementById('regPassword').value,
      role: document.getElementById('regRole').value
    };

    try {
      const data = await api('api/register.php', {
        method: 'POST',
        body: JSON.stringify(payload)
      });

      currentUser = data.user;
      event.target.reset();
      updateNavigation();
      notify('Account created successfully!');

      showPage(currentUser.role === 'employer' ? 'post' : 'jobs');
    } catch (error) {
      notify(error.message);
    }
  });
}

const loginForm = document.getElementById('loginForm');

if (loginForm) {
  loginForm.addEventListener('submit', async event => {
    event.preventDefault();

    const payload = {
      email: document.getElementById('loginEmail').value.trim(),
      password: document.getElementById('loginPassword').value
    };

    try {
      const data = await api('api/login.php', {
        method: 'POST',
        body: JSON.stringify(payload)
      });

      currentUser = data.user;
      event.target.reset();
      updateNavigation();
      notify('Login successful!');

      showPage(currentUser.role === 'employer' ? 'post' : 'jobs');
    } catch (error) {
      notify(error.message);
    }
  });
}

const jobForm = document.getElementById('jobForm');

if (jobForm) {
  jobForm.addEventListener('submit', async event => {
    event.preventDefault();

    const payload = {
      title: document.getElementById('jobTitle').value.trim(),
      company: document.getElementById('companyName').value.trim(),
      location: document.getElementById('jobLocation').value.trim(),
      salary: document.getElementById('jobSalary').value.trim(),
      type: document.getElementById('jobType').value,
      experience: document.getElementById('jobExperience').value,
      description: document.getElementById('jobDescription').value.trim()
    };

    try {
      await api('api/post_job.php', {
        method: 'POST',
        body: JSON.stringify(payload)
      });

      event.target.reset();
      notify('Job published to MySQL database!');
      showPage('jobs');
    } catch (error) {
      notify(error.message);

      if (!currentUser) showPage('login');
    }
  });
}

async function applyForJob(jobId) {
  try {
    await api('api/apply.php', {
      method: 'POST',
      body: JSON.stringify({ job_id: jobId })
    });

    notify('Application saved to database!');
  } catch (error) {
    notify(error.message);

    if (!currentUser) showPage('login');
  }
}

async function renderApplications() {
  const list = document.getElementById('applicationsList');
  if (!list) return;

  list.innerHTML = `
    <div class="empty-state">Loading applications...</div>
  `;

  try {
    const apps = await api('api/applications.php');

    if (!Array.isArray(apps)) {
      throw new Error('Unexpected applications API response.');
    }

    if (apps.length === 0) {
      list.innerHTML = `
        <div class="empty-state">
          <h3>No applications yet</h3>
          <p>Find a job and select Apply Now.</p>
          <button class="btn btn-primary" onclick="showPage('jobs')">
            Find Jobs
          </button>
        </div>
      `;
      return;
    }

    list.innerHTML = apps.map(application => `
      <article class="job-card">
        <h3>${escapeHTML(application.title)}</h3>

        <p class="company">
          ${escapeHTML(application.company)} ·
          ${escapeHTML(application.location)}
        </p>

        <div class="job-meta">
          <span>Applied on ${escapeHTML(application.applied_at)}</span>
          <span>Saved in database</span>
        </div>

        <p class="salary">${escapeHTML(application.salary)}</p>
      </article>
    `).join('');

  } catch (error) {
    console.error('Applications loading error:', error);

    list.innerHTML = `
      <div class="empty-state">
        <h3>Unable to load applications</h3>
        <p>${escapeHTML(error.message)}</p>
        <button class="btn btn-primary" onclick="showPage('login')">
          Login
        </button>
      </div>
    `;
  }
}

async function logout() {
  try {
    await api('api/logout.php', {
      method: 'POST',
      body: '{}'
    });
  } catch (error) {
    console.error('Logout error:', error);
  }

  currentUser = null;
  updateNavigation();
  notify('Logged out.');
  showPage('home');
}

(async function init() {
  await loadSession();
  await renderJobs();
})();
