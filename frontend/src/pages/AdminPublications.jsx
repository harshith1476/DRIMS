import { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import { adminService } from '../services/adminService';

function AdminPublications() {
  const [activeTab, setActiveTab] = useState('journals');
  const [journals, setJournals] = useState([]);
  const [conferences, setConferences] = useState([]);
  const [patents, setPatents] = useState([]);
  const [bookChapters, setBookChapters] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [journalsRes, conferencesRes, patentsRes, bookChaptersRes] = await Promise.all([
        adminService.getAllJournals(),
        adminService.getAllConferences(),
        adminService.getAllPatents(),
        adminService.getAllBookChapters()
      ]);
      setJournals(journalsRes.data);
      setConferences(conferencesRes.data);
      setPatents(patentsRes.data);
      setBookChapters(bookChaptersRes.data);
    } catch (error) {
      console.error('Error loading publications:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async () => {
    try {
      const response = await adminService.exportToExcel(null, null);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'research_data.xlsx');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error exporting data:', error);
    }
  };

  const renderTable = (data, type) => {
    if (loading) return <p>Loading...</p>;
    if (data.length === 0) return <p className="text-gray-500">No {type} found.</p>;

    const headers = getHeaders(type);
    return (
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              {headers.map((header) => (
                <th key={header} className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                  {header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {data.map((item) => (
              <tr key={item.id}>
                {renderRow(item, type)}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  const getHeaders = (type) => {
    switch (type) {
      case 'journals':
        return ['Title', 'Journal', 'Authors', 'Year', 'Status'];
      case 'conferences':
        return ['Title', 'Conference', 'Authors', 'Year', 'Status'];
      case 'patents':
        return ['Title', 'Patent Number', 'Inventors', 'Year', 'Status'];
      case 'bookChapters':
        return ['Title', 'Book', 'Authors', 'Year', 'Status'];
      default:
        return [];
    }
  };

  const renderRow = (item, type) => {
    switch (type) {
      case 'journals':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.title}</td>
            <td className="px-6 py-4 text-sm">{item.journalName}</td>
            <td className="px-6 py-4 text-sm">{item.authors}</td>
            <td className="px-6 py-4 text-sm">{item.year}</td>
            <td className="px-6 py-4 text-sm">{item.status}</td>
          </>
        );
      case 'conferences':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.title}</td>
            <td className="px-6 py-4 text-sm">{item.conferenceName}</td>
            <td className="px-6 py-4 text-sm">{item.authors}</td>
            <td className="px-6 py-4 text-sm">{item.year}</td>
            <td className="px-6 py-4 text-sm">{item.status}</td>
          </>
        );
      case 'patents':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.title}</td>
            <td className="px-6 py-4 text-sm">{item.patentNumber || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.inventors}</td>
            <td className="px-6 py-4 text-sm">{item.year}</td>
            <td className="px-6 py-4 text-sm">{item.status}</td>
          </>
        );
      case 'bookChapters':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.title}</td>
            <td className="px-6 py-4 text-sm">{item.bookTitle}</td>
            <td className="px-6 py-4 text-sm">{item.authors}</td>
            <td className="px-6 py-4 text-sm">{item.year}</td>
            <td className="px-6 py-4 text-sm">{item.status}</td>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <Layout title="All Publications">
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-semibold">View All Publications</h3>
          <button
            onClick={handleExport}
            className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700"
          >
            Export to Excel
          </button>
        </div>

        <div className="bg-white rounded-lg shadow">
          <div className="border-b border-gray-200">
            <nav className="flex -mb-px">
              {['journals', 'conferences', 'patents', 'bookChapters'].map((tab) => (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  className={`py-4 px-6 text-sm font-medium border-b-2 ${
                    activeTab === tab
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  {tab.charAt(0).toUpperCase() + tab.slice(1).replace(/([A-Z])/g, ' $1')}
                </button>
              ))}
            </nav>
          </div>

          <div className="p-6">
            {activeTab === 'journals' && renderTable(journals, 'journals')}
            {activeTab === 'conferences' && renderTable(conferences, 'conferences')}
            {activeTab === 'patents' && renderTable(patents, 'patents')}
            {activeTab === 'bookChapters' && renderTable(bookChapters, 'bookChapters')}
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default AdminPublications;

