import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { authService } from './services/authService';
import Login from './pages/Login';
import FacultyDashboard from './pages/FacultyDashboard';
import AdminDashboard from './pages/AdminDashboard';
import FacultyProfile from './pages/FacultyProfile';
import FacultyTargets from './pages/FacultyTargets';
import FacultyPublications from './pages/FacultyPublications';
import AdminAnalytics from './pages/AdminAnalytics';
import AdminFacultyList from './pages/AdminFacultyList';
import AdminFacultyDetail from './pages/AdminFacultyDetail';
import AdminPublications from './pages/AdminPublications';

const PrivateRoute = ({ children, allowedRoles }) => {
  const user = authService.getCurrentUser();
  const isAuthenticated = authService.isAuthenticated();

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/" />;
  }

  return children;
};

function App() {
  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route path="/faculty" element={
          <PrivateRoute allowedRoles={['FACULTY', 'ADMIN']}>
            <FacultyDashboard />
          </PrivateRoute>
        } />

        <Route path="/faculty/profile" element={
          <PrivateRoute allowedRoles={['FACULTY', 'ADMIN']}>
            <FacultyProfile />
          </PrivateRoute>
        } />

        <Route path="/faculty/targets" element={
          <PrivateRoute allowedRoles={['FACULTY', 'ADMIN']}>
            <FacultyTargets />
          </PrivateRoute>
        } />

        <Route path="/faculty/publications" element={
          <PrivateRoute allowedRoles={['FACULTY', 'ADMIN']}>
            <FacultyPublications />
          </PrivateRoute>
        } />

        <Route path="/admin" element={
          <PrivateRoute allowedRoles={['ADMIN']}>
            <AdminDashboard />
          </PrivateRoute>
        } />

        <Route path="/admin/faculty" element={
          <PrivateRoute allowedRoles={['ADMIN']}>
            <AdminFacultyList />
          </PrivateRoute>
        } />

        <Route path="/admin/faculty/:id" element={
          <PrivateRoute allowedRoles={['ADMIN']}>
            <AdminFacultyDetail />
          </PrivateRoute>
        } />

        <Route path="/admin/publications" element={
          <PrivateRoute allowedRoles={['ADMIN']}>
            <AdminPublications />
          </PrivateRoute>
        } />

        <Route path="/admin/analytics" element={
          <PrivateRoute allowedRoles={['ADMIN']}>
            <AdminAnalytics />
          </PrivateRoute>
        } />

        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}

export default App;

