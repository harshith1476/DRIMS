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
  const isFaculty = user?.role === 'FACULTY';
  const isStudent = user?.role === 'STUDENT';

  const isActive = (path) => {
    if (path === '/admin' && location.pathname === '/admin') return true;
    if (path === '/faculty' && location.pathname === '/faculty') return true;
    if (path === '/student' && location.pathname === '/student') return true;
    if (path !== '/admin' && path !== '/faculty' && path !== '/student' && location.pathname.startsWith(path)) return true;
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
                  to="/admin/approvals" 
                  className={`nav-link ${isActive('/admin/approvals') ? 'active' : ''}`}
                >
                  Approvals
                </Link>
                <Link 
                  to="/admin/reports" 
                  className={`nav-link ${isActive('/admin/reports') ? 'active' : ''}`}
                >
                  Reports
                </Link>
                <Link 
                  to="/admin/analytics" 
                  className={`nav-link ${isActive('/admin/analytics') ? 'active' : ''}`}
                >
                  Analytics
                </Link>
              </>
            ) : isStudent ? (
              <>
                <Link 
                  to="/student" 
                  className={`nav-link ${isActive('/student') ? 'active' : ''}`}
                >
                  Dashboard
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
              <span className="header-user-email">{user?.email || user?.registerNumber || user?.role}</span>
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
              to="/admin/approvals" 
              className={`mobile-nav-link ${isActive('/admin/approvals') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Approvals
            </Link>
            <Link 
              to="/admin/reports" 
              className={`mobile-nav-link ${isActive('/admin/reports') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Reports
            </Link>
            <Link 
              to="/admin/analytics" 
              className={`mobile-nav-link ${isActive('/admin/analytics') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Analytics
            </Link>
          </>
        ) : isStudent ? (
          <>
            <Link 
              to="/student" 
              className={`mobile-nav-link ${isActive('/student') ? 'active' : ''}`}
              onClick={() => setMobileMenuOpen(false)}
            >
              Dashboard
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
      <main className={`admin-main ${isStudent ? 'has-bottom-nav' : ''}`}>
        <div className="main-container">
          {title && <h2 className="page-title">{title}</h2>}
          {children}
        </div>
      </main>

      {/* Bottom Navigation for Students (Mobile Only) */}
      {isStudent && (
        <nav className="bottom-nav">
          <Link
            to="/student"
            className={`bottom-nav-item ${isActive('/student') ? 'active' : ''}`}
          >
            <svg className="bottom-nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
            <span>Dashboard</span>
          </Link>
        </nav>
      )}
    </div>
  );
}

export default Layout;

