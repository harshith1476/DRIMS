import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import { adminService } from '../services/adminService';

function AdminFacultyList() {
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

  return (
    <Layout title="Faculty Profiles">
      <div className="admin-table-wrapper">
        <div className="overflow-x-auto">
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

