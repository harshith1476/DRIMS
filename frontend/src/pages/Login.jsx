import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import './Login.css';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [loginType, setLoginType] = useState('faculty'); // 'admin' or 'faculty'
  const [toggled, setToggled] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.login(email, password);
      const user = authService.getCurrentUser();
      
      if (rememberMe) {
        localStorage.setItem('rememberEmail', email);
      }
      
      if (user.role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/faculty');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid email or password');
    } finally {
      setLoading(false);
    }
  };

  const fillSampleCredentials = () => {
    if (loginType === 'admin') {
      setEmail('admin@drims.edu');
      setPassword('admin123');
    } else {
      setEmail('renugadevi.r@drims.edu');
      setPassword('faculty123');
    }
  };

  const switchLoginType = (type) => {
    setLoginType(type);
    setToggled(type === 'admin');
    setError('');
    setEmail('');
    setPassword('');
  };

  return (
    <div className="login-page">
      {/* DRIMS Header - Like VIMS Style */}
      <header className="login-header">
        <div className="header-top-bar"></div>
        <div className="header-container">
          <div className="header-left">
            <img 
              src="/pictures/image.png" 
              alt="DRIMS Logo" 
              className="header-logo"
              onError={(e) => {
                e.target.style.display = 'none';
              }}
            />
          </div>
          
          <div className="header-center">
            <div className="drims-branding">
              <div className="drims-logo">DRIMS</div>
              <div className="drims-separator"></div>
              <div className="drims-full-name">
                <div className="drims-line">DEPARTMENT</div>
                <div className="drims-line">RESEARCH</div>
                <div className="drims-line">INFORMATION</div>
                <div className="drims-line">MANAGEMENT</div>
                <div className="drims-line">SYSTEM</div>
              </div>
            </div>
          </div>

          <div className="header-right">
            <div className="cse-label">CSE</div>
          </div>
        </div>
        <div className="header-bottom-bar"></div>
      </header>

      <div className={`auth-wrapper ${toggled ? 'toggled' : ''}`}>
        {/* Animated Background Shapes */}
        <div className="background-shape"></div>
        <div className="secondary-shape"></div>

        {/* Faculty Login Panel */}
        <div className="credentials-panel faculty">
          <h2 className="slide-element">Faculty Login</h2>
          <form onSubmit={handleSubmit}>
            {error && (
              <div className="field-wrapper slide-element error-message">
                <div className="error-text">{error}</div>
              </div>
            )}

            <div className="field-wrapper slide-element">
              <input 
                type="email" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required 
              />
              <label>Email Address</label>
              <i className="fa-solid fa-envelope"></i>
            </div>

            <div className="field-wrapper slide-element">
              <input 
                type="password" 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required 
              />
              <label>Password</label>
              <i className="fa-solid fa-lock"></i>
            </div>

            <div className="field-wrapper slide-element remember-forgot-container">
              <div className="remember-section">
                <label className="remember-label">
                  <input
                    type="checkbox"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                  />
                  <span>Remember me</span>
                </label>
              </div>
              <div className="forgot-section">
                <a href="#" className="forgot-link">Forgot password?</a>
              </div>
            </div>

            <div className="field-wrapper slide-element">
              <button className="submit-button" type="submit" disabled={loading}>
                {loading ? 'Signing in...' : 'Login as Faculty'}
              </button>
            </div>

            <div className="field-wrapper slide-element">
              <button
                type="button"
                onClick={fillSampleCredentials}
                className="sample-credentials-btn"
              >
                Use Sample Credentials
              </button>
            </div>

            <div className="switch-link slide-element">
              <p>Administrator? <br /> <a href="#" className="admin-trigger" onClick={(e) => { e.preventDefault(); switchLoginType('admin'); }}>Admin Login</a></p>
            </div>
          </form>
        </div>

        {/* Welcome Section for Faculty */}
        <div className="welcome-section faculty">
          <h2 className="slide-element">WELCOME BACK!</h2>
          <p className="slide-element">Faculty Portal</p>
          <p className="slide-element">Research & Publication Management</p>
        </div>

        {/* Admin Login Panel */}
        <div className="credentials-panel admin">
          <h2 className="slide-element">Admin Login</h2>
          <form onSubmit={handleSubmit}>
            {error && (
              <div className="field-wrapper slide-element error-message">
                <div className="error-text">{error}</div>
              </div>
            )}

            <div className="field-wrapper slide-element">
              <input 
                type="email" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required 
              />
              <label>Email Address</label>
              <i className="fa-solid fa-envelope"></i>
            </div>

            <div className="field-wrapper slide-element">
              <input 
                type="password" 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required 
              />
              <label>Password</label>
              <i className="fa-solid fa-lock"></i>
            </div>

            <div className="field-wrapper slide-element remember-forgot-container">
              <div className="remember-section">
                <label className="remember-label">
                  <input
                    type="checkbox"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                  />
                  <span>Remember me</span>
                </label>
              </div>
              <div className="forgot-section">
                <a href="#" className="forgot-link">Forgot password?</a>
              </div>
            </div>

            <div className="field-wrapper slide-element">
              <button className="submit-button" type="submit" disabled={loading}>
                {loading ? 'Signing in...' : 'Login as Administrator'}
              </button>
            </div>

            <div className="field-wrapper slide-element">
              <button
                type="button"
                onClick={fillSampleCredentials}
                className="sample-credentials-btn"
              >
                Use Sample Credentials
              </button>
            </div>

            <div className="switch-link slide-element">
              <p>Faculty Member? <br /> <a href="#" className="faculty-trigger" onClick={(e) => { e.preventDefault(); switchLoginType('faculty'); }}>Faculty Login</a></p>
            </div>
          </form>
        </div>

        {/* Welcome Section for Admin */}
        <div className="welcome-section admin">
          <h2 className="slide-element">WELCOME!</h2>
          <p className="slide-element">Administrator Portal</p>
          <p className="slide-element">System Administration & Analytics</p>
        </div>
      </div>

      {/* Simple Footer */}
      <footer className="login-footer">
        <div className="footer-container">
          <div className="footer-bottom">
            <div className="footer-copyright-section">
              <p className="footer-copyright">
                © <strong>2025</strong> Department Research Information Management System (<strong>DRIMS</strong>). All rights reserved.
              </p>
              <p className="footer-institution">
                VIGNAN'S Foundation for Science, Technology & Research (Deemed to be University)
              </p>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default Login;
