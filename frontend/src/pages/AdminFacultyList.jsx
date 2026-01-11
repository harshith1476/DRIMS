import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import { adminService } from '../services/adminService';
import { useDeviceDetection } from '../hooks/useDeviceDetection';

function AdminFacultyList() {
  const { isMobile } = useDeviceDetection();
  const [profiles, setProfiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadProfiles();
  }, []);

  const loadProfiles = async () => {
    try {
      const response = await adminService.getAllProfiles();
      setProfiles(response.data);
    } catch (error) {
      console.error('Error loading profiles:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Layout title="Faculty Profiles">Loading...</Layout>;
  }

  // Mobile: Render cards
  if (isMobile) {
    return (
      <Layout title="Faculty Profiles">
        <div className="mobile-card-view">
          {profiles.length === 0 ? (
            <p className="text-gray-500 text-center py-8">No faculty profiles found.</p>
          ) : (
            profiles.map((profile) => (
              <div 
                key={profile.id} 
                className="data-card"
                onClick={() => navigate(`/admin/faculty/${profile.id}`)}
                style={{ cursor: 'pointer' }}
              >
                <div className="data-card-header">
                  <div className="data-card-title">{profile.name}</div>
                  <div className="data-card-status">
                    <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs font-medium">
                      {profile.designation || 'N/A'}
                    </span>
                  </div>
                </div>
                <div className="data-card-body">
                  <div className="data-card-field">
                    <div className="data-card-label">Employee ID</div>
                    <div className="data-card-value">{profile.employeeId || 'N/A'}</div>
                  </div>
                  <div className="data-card-field">
                    <div className="data-card-label">Department</div>
                    <div className="data-card-value">{profile.department || 'N/A'}</div>
                  </div>
                  <div className="data-card-field">
                    <div className="data-card-label">Email</div>
                    <div className="data-card-value">{profile.email || 'N/A'}</div>
                  </div>
                  {profile.researchAreas && profile.researchAreas.length > 0 && (
                    <div className="data-card-field">
                      <div className="data-card-label">Research Areas</div>
                      <div className="data-card-value">
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '4px' }}>
                          {profile.researchAreas.map((area, index) => (
                            <span key={index} className="research-pill">
                              {area}
                            </span>
                          ))}
                        </div>
                      </div>
                    </div>
                  )}
                </div>
                <div className="data-card-actions">
                  <span style={{ color: '#1e3a8a', fontWeight: 600, fontSize: '14px' }}>
                    View Details →
                  </span>
                </div>
              </div>
            ))
          )}
        </div>
      </Layout>
    );
  }

  // Desktop: Render table
  return (
    <Layout title="Faculty Profiles">
      <div className="admin-table-wrapper">
        <div className="overflow-x-auto table-container">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Employee ID</th>
                <th>Name</th>
                <th>Designation</th>
                <th>Department</th>
                <th>Email</th>
                <th>Research Areas</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {profiles.map((profile) => (
                <tr 
                  key={profile.id}
                  onClick={() => navigate(`/admin/faculty/${profile.id}`)}
                  style={{ cursor: 'pointer' }}
                >
                  <td>{profile.employeeId}</td>
                  <td style={{ fontWeight: 600, color: '#1e3a8a' }}>
                    {profile.name}
                  </td>
                  <td>{profile.designation}</td>
                  <td>{profile.department}</td>
                  <td>{profile.email}</td>
                  <td>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                      {profile.researchAreas?.map((area, index) => (
                        <span key={index} className="research-pill">
                          {area}
                        </span>
                      ))}
                    </div>
                  </td>
                  <td>
                    <span style={{ color: '#1e3a8a', fontWeight: 500, fontSize: '13px' }}>
                      View Details →
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </Layout>
  );
}

export default AdminFacultyList;

