import { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import { facultyService } from '../services/facultyService';

function FacultyTargets() {
  const [targets, setTargets] = useState([]);
  const [formData, setFormData] = useState({
    year: new Date().getFullYear(),
    journalTarget: 0,
    conferenceTarget: 0,
    patentTarget: 0,
    bookChapterTarget: 0
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadTargets();
  }, []);

  const loadTargets = async () => {
    try {
      const response = await facultyService.getTargets();
      setTargets(response.data);
    } catch (error) {
      console.error('Error loading targets:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    try {
      await facultyService.createOrUpdateTarget(formData);
      setMessage('Target saved successfully!');
      loadTargets();
      setFormData({
        year: new Date().getFullYear(),
        journalTarget: 0,
        conferenceTarget: 0,
        patentTarget: 0,
        bookChapterTarget: 0
      });
    } catch (error) {
      setMessage('Error saving target');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout title="Research Targets">
      <div className="space-y-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Set/Update Target</h3>
          
          {message && (
            <div className={`mb-4 p-3 rounded ${message.includes('success') ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
              {message}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Year *</label>
              <input
                type="number"
                value={formData.year}
                onChange={(e) => setFormData({ ...formData, year: parseInt(e.target.value) })}
                required
                min="2000"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Journal Target *</label>
                <input
                  type="number"
                  value={formData.journalTarget}
                  onChange={(e) => setFormData({ ...formData, journalTarget: parseInt(e.target.value) })}
                  required
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Conference Target *</label>
                <input
                  type="number"
                  value={formData.conferenceTarget}
                  onChange={(e) => setFormData({ ...formData, conferenceTarget: parseInt(e.target.value) })}
                  required
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Patent Target *</label>
                <input
                  type="number"
                  value={formData.patentTarget}
                  onChange={(e) => setFormData({ ...formData, patentTarget: parseInt(e.target.value) })}
                  required
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Book Chapter Target *</label>
                <input
                  type="number"
                  value={formData.bookChapterTarget}
                  onChange={(e) => setFormData({ ...formData, bookChapterTarget: parseInt(e.target.value) })}
                  required
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50"
            >
              {loading ? 'Saving...' : 'Save Target'}
            </button>
          </form>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Historical Targets</h3>
          {targets.length === 0 ? (
            <p className="text-gray-500">No targets set yet.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Year</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Journals</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Conferences</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Patents</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Book Chapters</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {targets.map((target) => (
                    <tr key={target.id}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">{target.year}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">{target.journalTarget}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">{target.conferenceTarget}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">{target.patentTarget}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">{target.bookChapterTarget}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}

export default FacultyTargets;

