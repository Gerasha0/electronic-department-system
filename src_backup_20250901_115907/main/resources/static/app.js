// Simple client-side behavior for demo
(function(){
  const logout = document.getElementById('logout-link');
  if (logout) {
    logout.addEventListener('click', () => {
      localStorage.removeItem('jwt');
      fetch('/api/auth/logout', { method: 'POST' });
      window.location.href = '/';
    });
  }

  // Check if user is authenticated and redirect to dashboard
  const token = localStorage.getItem('jwt');
  if (token && window.location.pathname === '/') {
    window.location.href = '/dashboard.html';
    return;
  }

  // show welcome with role if authenticated
  async function showUser() {
    if (!token) return;
    const res = await fetch('/api/auth/current-user', { headers: { 'Authorization': 'Bearer ' + token } });
    if (res.ok) {
      const user = await res.json();
      const w = document.getElementById('welcome');
      if (w) {
        w.innerHTML = `Вітаємо, ${user.firstName} ${user.lastName} (${user.role})! 
          <br><a href="/dashboard.html" class="btn btn-primary" style="margin-top: 1rem;">Перейти до панелі управління</a>`;
      }
    }
  }
  showUser();
})();
