import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [loginType, setLoginType] = useState('faculty'); // 'admin' or 'faculty'
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

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 flex flex-col">
      {/* Compact Header */}
      <header className="bg-gradient-to-r from-blue-800 via-blue-700 to-indigo-800 border-b-2 border-blue-900 shadow-lg">
        <div className="container mx-auto px-4 py-2">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <img 
                src="/logo.png" 
                alt="DRIMS Logo" 
                className="h-10 w-auto object-contain drop-shadow-lg"
                onError={(e) => {
                  e.target.style.display = 'none';
                }}
              />
              <div>
                <h1 className="text-lg font-bold text-white">DRIMS</h1>
                <p className="text-xs text-blue-100">Department Research Information Management System</p>
              </div>
            </div>
            <div className="text-right">
              <div className="text-xs text-blue-100 font-medium">CSE Department</div>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 flex items-center justify-center px-4 py-8">
        <div className="w-full max-w-6xl">
          <div className="text-center mb-8">
            <h2 className="text-3xl font-bold text-gray-800 mb-2">Welcome to DRIMS</h2>
            <p className="text-gray-600">Please select your login type and enter your credentials</p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            {/* Faculty Login Card */}
            <div className={`bg-white rounded-xl shadow-lg border-2 transition-all ${
              loginType === 'faculty' 
                ? 'border-blue-600 shadow-xl scale-[1.02]' 
                : 'border-gray-200 hover:border-blue-300'
            }`}>
              <div className={`bg-gradient-to-r from-blue-600 to-indigo-600 text-white p-5 rounded-t-xl ${
                loginType === 'faculty' ? '' : 'opacity-90'
              }`}>
                <div className="flex items-center gap-3">
                  <div className="bg-white/20 p-2.5 rounded-lg">
                    <svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                    </svg>
                  </div>
                  <div>
                    <h3 className="text-xl font-bold">Faculty Portal</h3>
                    <p className="text-blue-100 text-xs mt-0.5">Research & Publication Management</p>
                  </div>
                </div>
              </div>

              <div className="p-6">
                {loginType === 'faculty' ? (
                  <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-2.5 rounded-lg text-sm">
                        {error}
                      </div>
                    )}

                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">
                        Email Address
                      </label>
                      <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                          </svg>
                        </div>
                        <input
                          type="email"
                          value={email}
                          onChange={(e) => setEmail(e.target.value)}
                          required
                          className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                          placeholder="faculty@drims.edu"
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">
                        Password
                      </label>
                      <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                          </svg>
                        </div>
                        <input
                          type="password"
                          value={password}
                          onChange={(e) => setPassword(e.target.value)}
                          required
                          className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                          placeholder="Enter your password"
                        />
                      </div>
                    </div>

                    <div className="flex items-center justify-between">
                      <label className="flex items-center text-sm text-gray-700">
                        <input
                          type="checkbox"
                          checked={rememberMe}
                          onChange={(e) => setRememberMe(e.target.checked)}
                          className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                        />
                        <span className="ml-2">Remember me</span>
                      </label>
                      <a href="#" className="text-sm text-blue-600 hover:text-blue-800">
                        Forgot password?
                      </a>
                    </div>

                    <button
                      type="submit"
                      disabled={loading}
                      className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-bold py-2.5 px-6 rounded-lg hover:from-blue-700 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-md"
                    >
                      {loading ? 'Signing in...' : 'Sign In as Faculty'}
                    </button>

                    <button
                      type="button"
                      onClick={fillSampleCredentials}
                      className="w-full text-sm text-blue-600 hover:text-blue-800 underline"
                    >
                      Use Sample Credentials
                    </button>
                  </form>
                ) : (
                  <div className="text-center py-6">
                    <button
                      onClick={() => setLoginType('faculty')}
                      className="bg-blue-600 text-white px-6 py-2.5 rounded-lg font-semibold hover:bg-blue-700 transition-colors shadow-md"
                    >
                      Select Faculty Login
                    </button>
                  </div>
                )}
              </div>
            </div>

            {/* Admin Login Card */}
            <div className={`bg-white rounded-xl shadow-lg border-2 transition-all ${
              loginType === 'admin' 
                ? 'border-indigo-600 shadow-xl scale-[1.02]' 
                : 'border-gray-200 hover:border-indigo-300'
            }`}>
              <div className={`bg-gradient-to-r from-indigo-600 to-purple-600 text-white p-5 rounded-t-xl ${
                loginType === 'admin' ? '' : 'opacity-90'
              }`}>
                <div className="flex items-center gap-3">
                  <div className="bg-white/20 p-2.5 rounded-lg">
                    <svg className="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                    </svg>
                  </div>
                  <div>
                    <h3 className="text-xl font-bold">Administrator Portal</h3>
                    <p className="text-indigo-100 text-xs mt-0.5">System Administration & Analytics</p>
                  </div>
                </div>
              </div>

              <div className="p-6">
                {loginType === 'admin' ? (
                  <form onSubmit={handleSubmit} className="space-y-4">
                    {error && (
                      <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-2.5 rounded-lg text-sm">
                        {error}
                      </div>
                    )}

                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">
                        Email Address
                      </label>
                      <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                          </svg>
                        </div>
                        <input
                          type="email"
                          value={email}
                          onChange={(e) => setEmail(e.target.value)}
                          required
                          className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                          placeholder="admin@drims.edu"
                        />
                      </div>
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">
                        Password
                      </label>
                      <div className="relative">
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                          </svg>
                        </div>
                        <input
                          type="password"
                          value={password}
                          onChange={(e) => setPassword(e.target.value)}
                          required
                          className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
                          placeholder="Enter your password"
                        />
                      </div>
                    </div>

                    <div className="flex items-center justify-between">
                      <label className="flex items-center text-sm text-gray-700">
                        <input
                          type="checkbox"
                          checked={rememberMe}
                          onChange={(e) => setRememberMe(e.target.checked)}
                          className="w-4 h-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                        />
                        <span className="ml-2">Remember me</span>
                      </label>
                      <a href="#" className="text-sm text-indigo-600 hover:text-indigo-800">
                        Forgot password?
                      </a>
                    </div>

                    <button
                      type="submit"
                      disabled={loading}
                      className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white font-bold py-2.5 px-6 rounded-lg hover:from-indigo-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-md"
                    >
                      {loading ? 'Signing in...' : 'Sign In as Administrator'}
                    </button>

                    <button
                      type="button"
                      onClick={fillSampleCredentials}
                      className="w-full text-sm text-indigo-600 hover:text-indigo-800 underline"
                    >
                      Use Sample Credentials
                    </button>
                  </form>
                ) : (
                  <div className="text-center py-6">
                    <button
                      onClick={() => setLoginType('admin')}
                      className="bg-indigo-600 text-white px-6 py-2.5 rounded-lg font-semibold hover:bg-indigo-700 transition-colors shadow-md"
                    >
                      Select Admin Login
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Quick Info Section */}
          <div className="mt-6 bg-white rounded-lg shadow-md p-4 border border-gray-200">
            <h3 className="text-sm font-semibold text-gray-800 mb-3">Quick Access</h3>
            <div className="grid md:grid-cols-2 gap-3 text-xs">
              <div>
                <p className="font-semibold text-gray-700 mb-1">Faculty:</p>
                <p className="text-gray-600 font-mono">renugadevi.r@drims.edu / faculty123</p>
              </div>
              <div>
                <p className="font-semibold text-gray-700 mb-1">Admin:</p>
                <p className="text-gray-600 font-mono">admin@drims.edu / admin123</p>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Compact Footer */}
      <footer className="bg-gray-800 text-white border-t-2 border-blue-600">
        <div className="container mx-auto px-6 py-4">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <div className="flex items-center gap-6 text-sm">
              <a href="#" className="text-gray-300 hover:text-blue-400 transition-colors">Research Database</a>
              <a href="#" className="text-gray-300 hover:text-blue-400 transition-colors">Project Tracking</a>
              <a href="#" className="text-gray-300 hover:text-blue-400 transition-colors">Analytics & Reports</a>
            </div>
            <div className="flex items-center gap-4 text-xs text-gray-400">
              <span>© 2025 DRIMS</span>
              <span>•</span>
              <a href="#" className="hover:text-blue-400 transition-colors">Privacy Policy</a>
              <span>•</span>
              <a href="#" className="hover:text-blue-400 transition-colors">Support</a>
              <span className="flex items-center gap-1 text-green-400">
                <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                </svg>
                Secure
              </span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default Login;
