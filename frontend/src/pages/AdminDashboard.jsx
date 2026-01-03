import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import { adminService } from '../services/adminService';

function AdminDashboard() {
  const [stats, setStats] = useState({
    facultyCount: 0,
    journals: 0,
    conferences: 0,
    patents: 0,
    bookChapters: 0
  });

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const [profilesRes, journalsRes, conferencesRes, patentsRes, bookChaptersRes] = await Promise.all([
        adminService.getAllProfiles(),
        adminService.getAllJournals(),
        adminService.getAllConferences(),
        adminService.getAllPatents(),
        adminService.getAllBookChapters()
      ]);

      setStats({
        facultyCount: profilesRes.data.length,
        journals: journalsRes.data.length,
        conferences: conferencesRes.data.length,
        patents: patentsRes.data.length,
        bookChapters: bookChaptersRes.data.length
      });
    } catch (error) {
      console.error('Error loading stats:', error);
    }
  };

  return (
    <Layout title="Admin Dashboard">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Total Faculty</h3>
          <p className="text-3xl font-bold text-blue-600">{stats.facultyCount}</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Journals</h3>
          <p className="text-3xl font-bold text-green-600">{stats.journals}</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Conferences</h3>
          <p className="text-3xl font-bold text-purple-600">{stats.conferences}</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Patents</h3>
          <p className="text-3xl font-bold text-orange-600">{stats.patents}</p>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-gray-500 text-sm font-medium mb-2">Book Chapters</h3>
          <p className="text-3xl font-bold text-indigo-600">{stats.bookChapters}</p>
        </div>
      </div>
    </Layout>
  );
}

export default AdminDashboard;

