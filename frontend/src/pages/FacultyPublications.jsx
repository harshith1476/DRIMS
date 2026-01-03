import { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import { facultyService } from '../services/facultyService';

function FacultyPublications() {
  const [activeTab, setActiveTab] = useState('journals');
  const [journals, setJournals] = useState([]);
  const [conferences, setConferences] = useState([]);
  const [patents, setPatents] = useState([]);
  const [bookChapters, setBookChapters] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [journalsRes, conferencesRes, patentsRes, bookChaptersRes] = await Promise.all([
        facultyService.getJournals(),
        facultyService.getConferences(),
        facultyService.getPatents(),
        facultyService.getBookChapters()
      ]);
      setJournals(journalsRes.data);
      setConferences(conferencesRes.data);
      setPatents(patentsRes.data);
      setBookChapters(bookChaptersRes.data);
    } catch (error) {
      console.error('Error loading publications:', error);
    }
  };

  const openModal = (type, item = null) => {
    setEditingItem(item);
    setActiveTab(type);
    if (item) {
      setFormData(item);
    } else {
      setFormData(getDefaultFormData(type));
    }
    setShowModal(true);
  };

  const getDefaultFormData = (type) => {
    const base = { year: new Date().getFullYear(), status: 'Published' };
    switch (type) {
      case 'journals':
        return { ...base, title: '', journalName: '', authors: '', volume: '', issue: '', pages: '', doi: '', impactFactor: '' };
      case 'conferences':
        return { ...base, title: '', conferenceName: '', authors: '', location: '', date: '' };
      case 'patents':
        return { ...base, title: '', patentNumber: '', inventors: '', country: '' };
      case 'bookChapters':
        return { ...base, title: '', bookTitle: '', authors: '', editors: '', publisher: '', pages: '', isbn: '' };
      default:
        return base;
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      if (editingItem) {
        await updatePublication();
      } else {
        await createPublication();
      }
      setShowModal(false);
      loadData();
    } catch (error) {
      console.error('Error saving publication:', error);
    } finally {
      setLoading(false);
    }
  };

  const createPublication = async () => {
    switch (activeTab) {
      case 'journals':
        await facultyService.createJournal(formData);
        break;
      case 'conferences':
        await facultyService.createConference(formData);
        break;
      case 'patents':
        await facultyService.createPatent(formData);
        break;
      case 'bookChapters':
        await facultyService.createBookChapter(formData);
        break;
    }
  };

  const updatePublication = async () => {
    switch (activeTab) {
      case 'journals':
        await facultyService.updateJournal(editingItem.id, formData);
        break;
      case 'conferences':
        await facultyService.updateConference(editingItem.id, formData);
        break;
      case 'patents':
        await facultyService.updatePatent(editingItem.id, formData);
        break;
      case 'bookChapters':
        await facultyService.updateBookChapter(editingItem.id, formData);
        break;
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this publication?')) return;

    try {
      switch (activeTab) {
        case 'journals':
          await facultyService.deleteJournal(id);
          break;
        case 'conferences':
          await facultyService.deleteConference(id);
          break;
        case 'patents':
          await facultyService.deletePatent(id);
          break;
        case 'bookChapters':
          await facultyService.deleteBookChapter(id);
          break;
      }
      loadData();
    } catch (error) {
      console.error('Error deleting publication:', error);
    }
  };

  const renderTable = (data, type) => {
    if (data.length === 0) {
      return <p className="text-gray-500">No {type} found.</p>;
    }

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
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {data.map((item) => (
              <tr key={item.id}>
                {renderRow(item, type)}
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <button
                    onClick={() => openModal(type, item)}
                    className="text-blue-600 hover:text-blue-900 mr-3"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(item.id)}
                    className="text-red-600 hover:text-red-900"
                  >
                    Delete
                  </button>
                </td>
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

  const renderForm = () => {
    const commonFields = (
      <>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Title *</label>
          <input
            type="text"
            value={formData.title || ''}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Authors *</label>
          <input
            type="text"
            value={formData.authors || ''}
            onChange={(e) => setFormData({ ...formData, authors: e.target.value })}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Year *</label>
          <input
            type="number"
            value={formData.year || ''}
            onChange={(e) => setFormData({ ...formData, year: parseInt(e.target.value) })}
            required
            min="2000"
            className="w-full px-4 py-2 border border-gray-300 rounded-lg"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Status *</label>
          <select
            value={formData.status || 'Published'}
            onChange={(e) => setFormData({ ...formData, status: e.target.value })}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg"
          >
            <option value="Published">Published</option>
            <option value="Accepted">Accepted</option>
            <option value="Submitted">Submitted</option>
            {activeTab === 'patents' && <option value="Granted">Granted</option>}
            {activeTab === 'patents' && <option value="Filed">Filed</option>}
            {activeTab === 'patents' && <option value="Pending">Pending</option>}
          </select>
        </div>
      </>
    );

    switch (activeTab) {
      case 'journals':
        return (
          <>
            {commonFields}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Journal Name *</label>
              <input
                type="text"
                value={formData.journalName || ''}
                onChange={(e) => setFormData({ ...formData, journalName: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Volume</label>
                <input
                  type="text"
                  value={formData.volume || ''}
                  onChange={(e) => setFormData({ ...formData, volume: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Issue</label>
                <input
                  type="text"
                  value={formData.issue || ''}
                  onChange={(e) => setFormData({ ...formData, issue: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Pages</label>
              <input
                type="text"
                value={formData.pages || ''}
                onChange={(e) => setFormData({ ...formData, pages: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">DOI</label>
              <input
                type="text"
                value={formData.doi || ''}
                onChange={(e) => setFormData({ ...formData, doi: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Impact Factor</label>
              <input
                type="text"
                value={formData.impactFactor || ''}
                onChange={(e) => setFormData({ ...formData, impactFactor: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
          </>
        );
      case 'conferences':
        return (
          <>
            {commonFields}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Conference Name *</label>
              <input
                type="text"
                value={formData.conferenceName || ''}
                onChange={(e) => setFormData({ ...formData, conferenceName: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Location</label>
              <input
                type="text"
                value={formData.location || ''}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Date</label>
              <input
                type="text"
                value={formData.date || ''}
                onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
          </>
        );
      case 'patents':
        return (
          <>
            {commonFields}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Patent Number</label>
              <input
                type="text"
                value={formData.patentNumber || ''}
                onChange={(e) => setFormData({ ...formData, patentNumber: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Inventors *</label>
              <input
                type="text"
                value={formData.inventors || ''}
                onChange={(e) => setFormData({ ...formData, inventors: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Country</label>
              <input
                type="text"
                value={formData.country || ''}
                onChange={(e) => setFormData({ ...formData, country: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
          </>
        );
      case 'bookChapters':
        return (
          <>
            {commonFields}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Book Title *</label>
              <input
                type="text"
                value={formData.bookTitle || ''}
                onChange={(e) => setFormData({ ...formData, bookTitle: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Editors</label>
              <input
                type="text"
                value={formData.editors || ''}
                onChange={(e) => setFormData({ ...formData, editors: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Publisher</label>
              <input
                type="text"
                value={formData.publisher || ''}
                onChange={(e) => setFormData({ ...formData, publisher: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Pages</label>
              <input
                type="text"
                value={formData.pages || ''}
                onChange={(e) => setFormData({ ...formData, pages: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">ISBN</label>
              <input
                type="text"
                value={formData.isbn || ''}
                onChange={(e) => setFormData({ ...formData, isbn: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
          </>
        );
      default:
        return null;
    }
  };

  return (
    <Layout title="My Publications">
      <div className="space-y-6">
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
            <div className="mb-4">
              <button
                onClick={() => openModal(activeTab)}
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
              >
                Add New
              </button>
            </div>

            {activeTab === 'journals' && renderTable(journals, 'journals')}
            {activeTab === 'conferences' && renderTable(conferences, 'conferences')}
            {activeTab === 'patents' && renderTable(patents, 'patents')}
            {activeTab === 'bookChapters' && renderTable(bookChapters, 'bookChapters')}
          </div>
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-semibold mb-4">
              {editingItem ? 'Edit' : 'Add New'} {activeTab.charAt(0).toUpperCase() + activeTab.slice(1).replace(/([A-Z])/g, ' $1')}
            </h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              {renderForm()}
              <div className="flex gap-4">
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 disabled:opacity-50"
                >
                  {loading ? 'Saving...' : 'Save'}
                </button>
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-400"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </Layout>
  );
}

export default FacultyPublications;

