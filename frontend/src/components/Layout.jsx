import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';

function Layout({ children, title }) {
  const user = authService.getCurrentUser();
  const navigate = useNavigate();

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const isAdmin = user?.role === 'ADMIN';

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-gradient-to-r from-blue-800 via-blue-700 to-indigo-800 shadow-lg">
        <div className="max-w-7xl mx-auto px-3 sm:px-4 lg:px-6">
          <div className="flex justify-between h-12">
            <div className="flex">
              <div className="flex-shrink-0 flex items-center gap-2">
                <img 
                  src="/logo.png" 
                  alt="DRIMS Logo" 
                  className="h-8 w-auto object-contain drop-shadow-lg"
                  onError={(e) => {
                    e.target.style.display = 'none';
                  }}
                />
                <h1 className="text-lg font-bold text-white">DRIMS</h1>
              </div>
              <div className="hidden sm:ml-4 sm:flex sm:space-x-6">
                {isAdmin ? (
                  <>
                    <Link to="/admin" className="border-blue-300 text-white inline-flex items-center px-1 pt-1 border-b-2 text-xs font-medium">
                      Dashboard
                    </Link>
                    <Link to="/admin/faculty" className="border-transparent text-blue-100 hover:border-blue-300 hover:text-white inline-flex items-center px-1 pt-1 border-b-2 text-xs font-medium">
                      Faculty
                    </Link>
                    <Link to="/admin/publications" className="border-transparent text-blue-100 hover:border-blue-300 hover:text-white inline-flex items-center px-1 pt-1 border-b-2 text-xs font-medium">
                      Publications
                    </Link>
                    <Link to="/admin/analytics" className="border-transparent text-blue-100 hover:border-blue-300 hover:text-white inline-flex items-center px-1 pt-1 border-b-2 text-xs font-medium">
                      Analytics
                    </Link>
                  </>
                ) : (
                  <>
                    <Link to="/faculty" className="border-blue-300 text-white inline-flex items-center px-1 pt-1 border-b-2 text-xs font-medium">
                      Dashboard
                    </Link>
                    <Link to="/faculty/profile" className="border-transparent text-blue-100 hover:border-blue-300 hover:text-white inline-flex items-center px-1 pt-1 border-b-2 text-xs font-medium">
                      Profile
                    </Link>
                    <Link to="/faculty/targets" className="border-transparent text-blue-100 hover:border-blue-300 hover:text-white inline-flex items-center px-1 pt-1 border-b-2 text-xs font-medium">
                      Targets
                    </Link>
                    <Link to="/faculty/publications" className="border-transparent text-blue-100 hover:border-blue-300 hover:text-white inline-flex items-center px-1 pt-1 border-b-2 text-xs font-medium">
                      Publications
                    </Link>
                  </>
                )}
              </div>
            </div>
            <div className="flex items-center">
              <span className="text-blue-100 mr-3 text-xs">{user?.email}</span>
              <button
                onClick={handleLogout}
                className="bg-red-600 text-white px-3 py-1.5 rounded-lg hover:bg-red-700 transition shadow-md text-xs"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        {title && <h2 className="text-2xl font-bold text-gray-900 mb-6">{title}</h2>}
        {children}
      </main>
    </div>
  );
}

export default Layout;

