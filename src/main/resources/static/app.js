// Modern client-side behavior for Electronic Department System
(function(){
  // Initialize theme before anything else
  initializeTheme();
  
  // Main initialization
  document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
  });
  
  function initializeTheme() {
    // Check for saved theme preference or default to system preference
    const savedTheme = localStorage.getItem('theme');
    const systemPreference = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    const currentTheme = savedTheme || systemPreference;
    
    document.documentElement.setAttribute('data-theme', currentTheme);
    
    // Listen for system theme changes if no manual preference is set
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      if (!localStorage.getItem('theme')) {
        const newTheme = e.matches ? 'dark' : 'light';
        document.documentElement.setAttribute('data-theme', newTheme);
      }
    });
  }
  
  async function initializeApp() {
    const token = localStorage.getItem('jwt');
    
    // Setup logout functionality
    const logout = document.getElementById('logout-link');
    if (logout) {
      logout.addEventListener('click', handleLogout);
      
      // Show/hide logout link based on authentication status
      logout.style.display = token ? 'block' : 'none';
    }

    // Check if user is authenticated and redirect to dashboard
    if (token && window.location.pathname === '/') {
      // Verify token is still valid before redirecting
        await verifyTokenAndRedirect();
      return;
    }

    // Show welcome message with role if authenticated
    if (token) {
      await showAuthenticatedUser();
    }
  }
  
  async function handleLogout(event) {
    event.preventDefault();
    
    try {
      // Call logout endpoint
      await fetch('/api/auth/logout', { 
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('jwt')}`
        }
      });
    } catch (error) {
      console.warn('Logout request failed:', error);
    } finally {
      // Always clear local storage and redirect
      localStorage.removeItem('jwt');
      window.location.href = '/';
    }
  }
  
  async function verifyTokenAndRedirect() {
    try {
      const response = await fetch('/api/auth/current-user', { 
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwt') } 
      });
      
      if (response.ok) {
        window.location.href = '/dashboard.html';
      } else {
        // Token is invalid, clear it
        localStorage.removeItem('jwt');
      }
    } catch (error) {
      console.warn('Token verification failed:', error);
      localStorage.removeItem('jwt');
    }
  }

  async function showAuthenticatedUser() {
    const token = localStorage.getItem('jwt');
    if (!token) return;
    
    try {
      const response = await fetch('/api/auth/current-user', { 
        headers: { 'Authorization': 'Bearer ' + token } 
      });
      
      if (response.ok) {
        const user = await response.json();
        const welcomeElement = document.getElementById('welcome');
        
        if (welcomeElement) {
          const roleIcons = {
            'ADMIN': '⚙️',
            'MANAGER': '👥',
            'TEACHER': '👨‍🏫',
            'STUDENT': '🎓',
            'GUEST': '👤'
          };
          
          const roleIcon = roleIcons[user.role] || '👤';
          const roleName = getRoleDisplayName(user.role);
          
          welcomeElement.innerHTML = `
            <div class="text-center">
              <div style="font-size: 1.5rem; margin-bottom: 1rem;">${roleIcon}</div>
              <p style="font-size: 1.125rem; color: var(--text-primary); margin-bottom: 0.5rem;">
                Вітаємо, <strong>${user.firstName} ${user.lastName}</strong>!
              </p>
              <p style="font-size: 0.9375rem; color: var(--text-secondary); margin-bottom: 2rem;">
                Роль: ${roleName}
              </p>
              <a href="/dashboard.html" class="btn btn-primary" style="width: auto; min-width: 200px;">
                🚀 Перейти до панелі управління
              </a>
            </div>
          `;
        }
        
        // Show logout link
        const logoutLink = document.getElementById('logout-link');
        if (logoutLink) {
          logoutLink.style.display = 'block';
        }
      } else {
        // Invalid token
        localStorage.removeItem('jwt');
        const logoutLink = document.getElementById('logout-link');
        if (logoutLink) {
          logoutLink.style.display = 'none';
        }
      }
    } catch (error) {
      console.warn('Failed to load user info:', error);
      localStorage.removeItem('jwt');
    }
  }
  
  function getRoleDisplayName(role) {
    const roleNames = {
      'ADMIN': 'Адміністратор',
      'MANAGER': 'Менеджер',
      'TEACHER': 'Викладач',
      'STUDENT': 'Студент',
      'GUEST': 'Гість'
    };
    return roleNames[role] || role;
  }
  
  // Add smooth scroll behavior to internal links
  document.addEventListener('click', function(e) {
    if (e.target.matches('a[href^="#"]')) {
      e.preventDefault();
      const target = document.querySelector(e.target.getAttribute('href'));
      if (target) {
        target.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });
      }
    }
  });
  
  // Add loading state management for buttons
  window.setButtonLoading = function(button, isLoading, originalText = null) {
    if (isLoading) {
      button.disabled = true;
      button.dataset.originalText = originalText || button.textContent;
      button.innerHTML = '<span style="margin-right: 0.5rem;">⏳</span>Завантаження...';
    } else {
      button.disabled = false;
      button.textContent = button.dataset.originalText || originalText || 'Готово';
      delete button.dataset.originalText;
    }
  };
  
  // Add global error handler for better UX
  window.addEventListener('unhandledrejection', function(event) {
    console.error('Unhandled promise rejection:', event.reason);
    // You could show a toast notification here
  });
  
  // Add performance monitoring
  if ('performance' in window) {
    window.addEventListener('load', function() {
      setTimeout(function() {
        const perfData = performance.getEntriesByType('navigation')[0];
        if (perfData) {
          console.log(`Page load time: ${Math.round(perfData.loadEventEnd - perfData.loadEventStart)}ms`);
        }
      }, 0);
    });
  }
  
})();
