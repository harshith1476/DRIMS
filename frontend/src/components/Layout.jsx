import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../services/authService';
import './Layout.css';

function Layout({ children, title }) {
  const user = authService.getCurrentUser();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const isAdmin = user?.role === 'ADMIN';

  const isActive = (path) => {
    if (path === '/admin' && location.pathname === '/admin') return true;
    if (path !== '/admin' && location.pathname.startsWith(path)) return true;
    return false;
  };

  return (
    <div className="admin-layout">
      {/* Official University Header */}
      <header className="admin-header">
        <div className="header-top-bar"></div>
        <div className="header-container">
          <div className="header-left">
            <div className="header-logo-wrapper">
              <img 
                src="/pictures/image.png" 
                alt="DRIMS Logo" 
                className="header-logo-img"
                onError={(e) => {
                  e.target.style.display = 'none';
                }}
              />
              <div className="header-brand">
                <h1 className="header-brand-title">DRIMS</h1>
              </div>
            </div>
          </div>

          {/* Desktop Navigation */}
          <nav className="header-nav-desktop">
            {isAdmin ? (
              <>
                <Link 
                  to="/admin" 
                  className={`nav-link ${isActive('/admin') ? 'active' : ''}`}
                >
                  Dashboard
                </Link>
                <Link 
                  to="/admin/faculty" 
                  className={`nav-link ${isActive('/admin/faculty') ? 'active' : ''}`}
                >
                  Faculty
                </Link>
                <Link 
                  to="/admin/publications" 
                  className={`nav-link ${isActive('/admin/publications') ? 'active' : ''}`}
                >
                  Publications
                </Link>
                <Link 
                  to="/admin/analytics" 
                  className={`nav-link ${isActive('/admin/analytics') ? 'active' : ''}`}
                >
                  Analytics
                </Link>
              </>
            ) : (
              <>
                <Link 
                  to="/faculty" 
                  className={`nav-link ${isActive('/faculty') && location.pathname === '/faculty' ? 'active' : ''}`}
                >
                  Dashboard
                </Link>
                <Link 
                  to="/faculty/profile" 
                  className={`nav-link ${isActive('/faculty/profile') ? 'active' : ''}`}
                >
                  Profile
                </Link>
                <Link 
                  to="/faculty/targets" 
                  className={`nav-link ${isActive('/faculty/targets') ? 'active' : ''}`}
                >
                  Targets
                </Link>
                <Link 
                  to="/faculty/publications" 
                  className={`nav-link ${isActive('/faculty/publications') ? 'active' : ''}`}
                >
                  Publications
                </Link>
              </>
            )}
          </nav>

          {/* Right Side - User Info & Logout */}
          <div className="header-right">
            <div className="header-user-info">
              <span className="header-user-email">{user?.email}</span>
            </div>
            <button
              onClick={handleLogout}
              className="header-logout-btn"
            >
              Logout
            </button>
            
            {/* Mobile Menu Toggle */}
            <button
              className="mobile-menu-toggle"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              aria-label="Toggle menu"
            >
              <span className={`hamburger ${mobileMenuOpen ? 'open' : ''}`}>
                <span></span>
                <span></span>
                <span></span>
              </span>
            </button>
          </div>
        </div>
        <div className="header-bottom-bar"></div>
      </header>

      {/* Mobile Navigation */}
      <nav className={`mobile-nav ${mobileMenuOpen ? 'open' : ''}`}>
        {isAdmin ? (
          <>
            <Link 
              to="/admin" 
              className={`mobile-nav-link ${isActive('/admin') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Dashboard
            </Link>
            <Link 
              to="/admin/faculty" 
              className={`mobile-nav-link ${isActive('/admin/faculty') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Faculty
            </Link>
            <Link 
              to="/admin/publications" 
              className={`mobile-nav-link ${isActive('/admin/publications') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Publications
            </Link>
            <Link 
              to="/admin/analytics" 
              className={`mobile-nav-link ${isActive('/admin/analytics') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Analytics
            </Link>
          </>
        ) : (
          <>
            <Link 
              to="/faculty" 
              className={`mobile-nav-link ${isActive('/faculty') && location.pathname === '/faculty' ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Dashboard
            </Link>
            <Link 
              to="/faculty/profile" 
              className={`mobile-nav-link ${isActive('/faculty/profile') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Profile
            </Link>
            <Link 
              to="/faculty/targets" 
              className={`mobile-nav-link ${isActive('/faculty/targets') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Targets
            </Link>
            <Link 
              to="/faculty/publications" 
              className={`mobile-nav-link ${isActive('/faculty/publications') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Publications
            </Link>
          </>
        )}
      </nav>

      {/* Main Content */}
      <main className="admin-main">
        <div className="main-container">
          {title && <h2 className="page-title">{title}</h2>}
          {children}
        </div>
      </main>
    </div>
  );
}

export default Layout;

