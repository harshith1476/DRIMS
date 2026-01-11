import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import { facultyService } from '../services/facultyService';

function FacultyDashboard() {
  const [profile, setProfile] = useState(null);
  const [stats, setStats] = useState({
    journals: 0,
    conferences: 0,
    patents: 0,
    bookChapters: 0
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [profileRes, journalsRes, conferencesRes, patentsRes, bookChaptersRes] = await Promise.all([
        facultyService.getProfile(),
        facultyService.getJournals(),
        facultyService.getConferences(),
        facultyService.getPatents(),
        facultyService.getBookChapters()
      ]);

      setProfile(profileRes.data);
      setStats({
        journals: journalsRes.data.length,
        conferences: conferencesRes.data.length,
        patents: patentsRes.data.length,
        bookChapters: bookChaptersRes.data.length
      });
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    }
  };

  return (
    <Layout title="Faculty Dashboard">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Journals</h3>
          <p className="text-3xl font-bold text-blue-600">{stats.journals}</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Conferences</h3>
          <p className="text-3xl font-bold text-green-600">{stats.conferences}</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Patents</h3>
          <p className="text-3xl font-bold text-purple-600">{stats.patents}</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Book Chapters</h3>
          <p className="text-3xl font-bold text-orange-600">{stats.bookChapters}</p>
        </div>
      </div>

      {profile && (
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Profile Information</h3>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-gray-500">Name</p>
              <p className="font-medium">{profile.name}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Employee ID</p>
              <p className="font-medium">{profile.employeeId}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Designation</p>
              <p className="font-medium">{profile.designation}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Department</p>
              <p className="font-medium">{profile.department}</p>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}

export default FacultyDashboard;

