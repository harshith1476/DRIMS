import { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import { facultyService } from '../services/facultyService';
import FileUpload from '../components/FileUpload';
import { useDeviceDetection } from '../hooks/useDeviceDetection';
import './FacultyPublications.css';

function FacultyPublications() {
  const { isMobile } = useDeviceDetection();
  const [activeTab, setActiveTab] = useState('journals');
  const [journals, setJournals] = useState([]);
  const [conferences, setConferences] = useState([]);
  const [patents, setPatents] = useState([]);
  const [bookChapters, setBookChapters] = useState([]);
  const [books, setBooks] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({});
  const [loading, setLoading] = useState(false);
  const [profile, setProfile] = useState(null);
  const [fileUploads, setFileUploads] = useState({});

  useEffect(() => {
    loadData();
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const profileRes = await facultyService.getProfile();
      setProfile(profileRes.data);
    } catch (error) {
      console.error('Error loading profile:', error);
    }
  };

  const loadData = async () => {
    try {
      const [journalsRes, conferencesRes, patentsRes, bookChaptersRes, booksRes] = await Promise.all([
        facultyService.getJournals(),
        facultyService.getConferences(),
        facultyService.getPatents(),
        facultyService.getBookChapters(),
        facultyService.getBooks()
      ]);
      setJournals(journalsRes.data);
      setConferences(conferencesRes.data);
      setPatents(patentsRes.data);
      setBookChapters(bookChaptersRes.data);
      setBooks(booksRes.data);
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
    const base = { 
      year: new Date().getFullYear(), 
      status: 'Published',
      approvalStatus: 'SUBMITTED'
    };
    switch (type) {
      case 'journals':
        return { 
          ...base, 
          title: '', 
          journalName: '', 
          authors: '', 
          author2: '', 
          author3: '', 
          author4: '', 
          author5: '', 
          author6: '',
          category: 'National',
          indexType: '',
          volume: '', 
          issue: '', 
          pages: '', 
          doi: '', 
          impactFactor: '',
          publisher: '',
          issn: '',
          openAccess: 'Open Access'
        };
      case 'conferences':
        return { 
          ...base, 
          title: '', 
          conferenceName: '', 
          organizer: '',
          authors: '', 
          category: 'National',
          location: '', 
          date: '',
          registrationAmount: '',
          paymentMode: 'Online',
          isStudentPublication: false
        };
      case 'patents':
        return { 
          ...base, 
          title: '', 
          applicationNumber: '',
          filingDate: '',
          patentNumber: '', 
          inventors: '', 
          country: '',
          category: 'National',
          status: 'Filed'
        };
      case 'bookChapters':
        return { 
          ...base, 
          title: '', 
          bookTitle: '', 
          authors: '', 
          editors: '', 
          publisher: '', 
          pages: '', 
          isbn: '',
          category: 'National'
        };
      case 'books':
        return {
          ...base,
          bookTitle: '',
          publisher: '',
          isbn: '',
          publicationYear: new Date().getFullYear(),
          role: 'Author',
          category: 'National'
        };
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
      case 'books':
        await facultyService.createBook(formData);
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
      case 'books':
        await facultyService.updateBook(editingItem.id, formData);
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
        case 'books':
          await facultyService.deleteBook(id);
          break;
      }
      loadData();
    } catch (error) {
      console.error('Error deleting publication:', error);
      alert('Cannot delete approved/locked publication');
    }
  };

  const handleFileUpload = async (file, fieldName, category, fileType) => {
    if (!profile?.id) {
      alert('Profile not loaded. Please wait and try again.');
      return;
    }

    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('userType', 'faculty');
      formData.append('userId', profile.id);
      formData.append('category', `${category}/${fileType}`);

      const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || (import.meta.env.DEV ? 'http://localhost:8080/api' : 'https://drims-rnv0.onrender.com/api');
      const response = await fetch(`${apiBaseUrl}/files/upload`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
      });

      if (!response.ok) {
        throw new Error('File upload failed');
      }

      const result = await response.json();
      setFormData(prev => ({
        ...prev,
        [fieldName]: result
      }));
    } catch (error) {
      console.error('Error uploading file:', error);
      alert('Failed to upload file. Please try again.');
    }
  };

  const renderTable = (data, type) => {
    if (data.length === 0) {
      return <p className="text-gray-500">No {type} found.</p>;
    }

    // Mobile: Render cards
    if (isMobile) {
      return (
        <div className="mobile-card-view">
          {data.map((item) => (
            <div key={item.id} className="data-card">
              <div className="data-card-header">
                <div className="data-card-title">
                  {item.title || item.bookTitle || 'Untitled'}
                </div>
                <div className="data-card-status">
                  {getStatusBadge(item.approvalStatus)}
                </div>
              </div>
              <div className="data-card-body">
                {renderCardFields(item, type)}
              </div>
              <div className="data-card-actions">
                {(item.approvalStatus !== 'APPROVED' && item.approvalStatus !== 'LOCKED') ? (
                  <>
                    <button
                      onClick={() => openModal(type, item)}
                      className="bg-blue-600 text-white"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDelete(item.id)}
                      className="bg-red-600 text-white"
                    >
                      Delete
                    </button>
                  </>
                ) : (
                  <span className="text-gray-500 text-sm">Locked - No edits allowed</span>
                )}
              </div>
              {item.remarks && (
                <div className="mt-3 p-3 bg-red-50 border border-red-200 rounded">
                  <div className="data-card-label">Admin Remarks</div>
                  <div className="text-sm text-red-600">{item.remarks}</div>
                </div>
              )}
            </div>
          ))}
        </div>
      );
    }

    // Desktop: Render table
    const headers = getHeaders(type);
    return (
      <div className="overflow-x-auto table-container">
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
                  {(item.approvalStatus !== 'APPROVED' && item.approvalStatus !== 'LOCKED') && (
                    <>
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
                    </>
                  )}
                  {(item.approvalStatus === 'APPROVED' || item.approvalStatus === 'LOCKED') && (
                    <span className="text-gray-500 text-xs">Locked - No edits allowed</span>
                  )}
                  {item.remarks && (
                    <div className="mt-2 text-xs text-red-600 whitespace-normal">
                      Remarks: {item.remarks}
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  const renderCardFields = (item, type) => {
    switch (type) {
      case 'journals':
        return (
          <>
            <div className="data-card-field">
              <div className="data-card-label">Journal</div>
              <div className="data-card-value">{item.journalName || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Category</div>
              <div className="data-card-value">{item.category || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Index Type</div>
              <div className="data-card-value">{item.indexType || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Year</div>
              <div className="data-card-value">{item.year || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Status</div>
              <div className="data-card-value">{item.status || 'N/A'}</div>
            </div>
          </>
        );
      case 'conferences':
        return (
          <>
            <div className="data-card-field">
              <div className="data-card-label">Conference</div>
              <div className="data-card-value">{item.conferenceName || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Category</div>
              <div className="data-card-value">{item.category || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Year</div>
              <div className="data-card-value">{item.year || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Status</div>
              <div className="data-card-value">{item.status || 'N/A'}</div>
            </div>
          </>
        );
      case 'patents':
        return (
          <>
            <div className="data-card-field">
              <div className="data-card-label">Application Number</div>
              <div className="data-card-value">{item.applicationNumber || item.patentNumber || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Status</div>
              <div className="data-card-value">{item.status || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Category</div>
              <div className="data-card-value">{item.category || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Year</div>
              <div className="data-card-value">{item.year || 'N/A'}</div>
            </div>
          </>
        );
      case 'bookChapters':
        return (
          <>
            <div className="data-card-field">
              <div className="data-card-label">Book</div>
              <div className="data-card-value">{item.bookTitle || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Category</div>
              <div className="data-card-value">{item.category || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Year</div>
              <div className="data-card-value">{item.year || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Status</div>
              <div className="data-card-value">{item.status || 'N/A'}</div>
            </div>
          </>
        );
      case 'books':
        return (
          <>
            <div className="data-card-field">
              <div className="data-card-label">Publisher</div>
              <div className="data-card-value">{item.publisher || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">ISBN</div>
              <div className="data-card-value">{item.isbn || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Category</div>
              <div className="data-card-value">{item.category || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Role</div>
              <div className="data-card-value">{item.role || 'Author'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Year</div>
              <div className="data-card-value">{item.publicationYear || item.year || 'N/A'}</div>
            </div>
            <div className="data-card-field">
              <div className="data-card-label">Status</div>
              <div className="data-card-value">{item.status || 'N/A'}</div>
            </div>
          </>
        );
      default:
        return null;
    }
  };

  const getHeaders = (type) => {
    switch (type) {
      case 'journals':
        return ['Title', 'Journal', 'Category', 'Index Type', 'Year', 'Approval Status', 'Status'];
      case 'conferences':
        return ['Title', 'Conference', 'Category', 'Year', 'Approval Status', 'Status'];
      case 'patents':
        return ['Title', 'Application No.', 'Status', 'Category', 'Year', 'Approval Status'];
      case 'bookChapters':
        return ['Title', 'Book', 'Category', 'Year', 'Approval Status', 'Status'];
      case 'books':
        return ['Title', 'Publisher', 'ISBN', 'Category', 'Role', 'Year', 'Approval Status', 'Status'];
      default:
        return [];
    }
  };

  const getStatusBadge = (status) => {
    const statusColors = {
      'SUBMITTED': 'bg-yellow-100 text-yellow-800',
      'APPROVED': 'bg-green-100 text-green-800',
      'REJECTED': 'bg-red-100 text-red-800',
      'SENT_BACK': 'bg-orange-100 text-orange-800',
      'LOCKED': 'bg-blue-100 text-blue-800'
    };
    return (
      <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusColors[status] || 'bg-gray-100 text-gray-800'}`}>
        {status || 'SUBMITTED'}
      </span>
    );
  };

  const renderRow = (item, type) => {
    switch (type) {
      case 'journals':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.title}</td>
            <td className="px-6 py-4 text-sm">{item.journalName}</td>
            <td className="px-6 py-4 text-sm">{item.category || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.indexType || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.year}</td>
            <td className="px-6 py-4 text-sm">{getStatusBadge(item.approvalStatus)}</td>
            <td className="px-6 py-4 text-sm">{item.status}</td>
          </>
        );
      case 'conferences':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.title}</td>
            <td className="px-6 py-4 text-sm">{item.conferenceName}</td>
            <td className="px-6 py-4 text-sm">{item.category || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.year}</td>
            <td className="px-6 py-4 text-sm">{getStatusBadge(item.approvalStatus)}</td>
            <td className="px-6 py-4 text-sm">{item.status}</td>
          </>
        );
      case 'patents':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.title}</td>
            <td className="px-6 py-4 text-sm">{item.applicationNumber || item.patentNumber || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.status}</td>
            <td className="px-6 py-4 text-sm">{item.category || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.year}</td>
            <td className="px-6 py-4 text-sm">{getStatusBadge(item.approvalStatus)}</td>
          </>
        );
      case 'bookChapters':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.title}</td>
            <td className="px-6 py-4 text-sm">{item.bookTitle}</td>
            <td className="px-6 py-4 text-sm">{item.category || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.year}</td>
            <td className="px-6 py-4 text-sm">{getStatusBadge(item.approvalStatus)}</td>
            <td className="px-6 py-4 text-sm">{item.status}</td>
          </>
        );
      case 'books':
        return (
          <>
            <td className="px-6 py-4 text-sm">{item.bookTitle}</td>
            <td className="px-6 py-4 text-sm">{item.publisher || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.isbn || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.category || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{item.role || 'Author'}</td>
            <td className="px-6 py-4 text-sm">{item.publicationYear || item.year || 'N/A'}</td>
            <td className="px-6 py-4 text-sm">{getStatusBadge(item.approvalStatus)}</td>
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
        <div className="publication-form-field">
          <label className="publication-form-label">
            Title <span className="publication-form-label-required">*</span>
          </label>
          <input
            type="text"
            value={formData.title || ''}
            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            required
            className="publication-form-input"
          />
        </div>
        <div className="publication-form-field">
          <label className="publication-form-label">
            Authors <span className="publication-form-label-required">*</span>
          </label>
          <input
            type="text"
            value={formData.authors || ''}
            onChange={(e) => setFormData({ ...formData, authors: e.target.value })}
            required
            className="publication-form-input"
          />
        </div>
        <div className="publication-form-field">
          <label className="publication-form-label">
            Year <span className="publication-form-label-required">*</span>
          </label>
          <input
            type="number"
            value={formData.year || ''}
            onChange={(e) => setFormData({ ...formData, year: parseInt(e.target.value) })}
            required
            min="2000"
            className="publication-form-input"
          />
        </div>
        <div className="publication-form-field">
          <label className="publication-form-label">
            Status <span className="publication-form-label-required">*</span>
          </label>
          <select
            value={formData.status || 'Published'}
            onChange={(e) => setFormData({ ...formData, status: e.target.value })}
            required
            className="publication-form-select"
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
            <div className="publication-form-section">
              <h3 className="publication-form-section-title">Journal Details</h3>
              <div className="publication-form-fields">
                <div className="publication-form-field">
                  <label className="publication-form-label">
                    Title <span className="publication-form-label-required">*</span>
                  </label>
                  <input
                    type="text"
                    value={formData.title || ''}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    required
                    className="publication-form-input"
                  />
                </div>
                <div className="publication-form-field">
                  <label className="publication-form-label">
                    Journal Name <span className="publication-form-label-required">*</span>
                  </label>
                  <input
                    type="text"
                    value={formData.journalName || ''}
                    onChange={(e) => setFormData({ ...formData, journalName: e.target.value })}
                    required
                    className="publication-form-input"
                  />
                </div>
                <div className="publication-form-field-grid">
                  <div className="publication-form-field">
                    <label className="publication-form-label">
                      Category <span className="publication-form-label-required">*</span>
                    </label>
                    <select
                      value={formData.category || 'National'}
                      onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                      required
                      className="publication-form-select"
                    >
                      <option value="National">National</option>
                      <option value="International">International</option>
                    </select>
                  </div>
                  <div className="publication-form-field">
                    <label className="publication-form-label">Index Type</label>
                    <select
                      value={formData.indexType || ''}
                      onChange={(e) => setFormData({ ...formData, indexType: e.target.value })}
                      className="publication-form-select"
                    >
                      <option value="">Select Index Type</option>
                      <option value="SCI">SCI</option>
                      <option value="SCIE">SCIE</option>
                      <option value="Scopus">Scopus</option>
                      <option value="ESCI">ESCI</option>
                      <option value="WoS">Web of Science (WoS)</option>
                      <option value="UGC CARE">UGC CARE</option>
                    </select>
                  </div>
                </div>
                <div className="publication-form-field-grid">
                  <div className="publication-form-field">
                    <label className="publication-form-label">
                      Publication Status <span className="publication-form-label-required">*</span>
                    </label>
                    <select
                      value={formData.status || 'Published'}
                      onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                      required
                      className="publication-form-select"
                    >
                      <option value="Published">Published</option>
                      <option value="Accepted">Accepted</option>
                      <option value="Submitted">Submitted</option>
                    </select>
                  </div>
                  <div className="publication-form-field">
                    <label className="publication-form-label">
                      Publication Year <span className="publication-form-label-required">*</span>
                    </label>
                    <input
                      type="number"
                      value={formData.year || ''}
                      onChange={(e) => setFormData({ ...formData, year: parseInt(e.target.value) })}
                      required
                      min="2000"
                      className="publication-form-input"
                    />
                  </div>
                </div>
                <div className="publication-form-field">
                  <label className="publication-form-label">DOI</label>
                  <input
                    type="text"
                    value={formData.doi || ''}
                    onChange={(e) => setFormData({ ...formData, doi: e.target.value })}
                    className="publication-form-input"
                  />
                </div>
                <div className="publication-form-field-grid">
                  <div className="publication-form-field">
                    <label className="publication-form-label">Publisher</label>
                    <input
                      type="text"
                      value={formData.publisher || ''}
                      onChange={(e) => setFormData({ ...formData, publisher: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                  <div className="publication-form-field">
                    <label className="publication-form-label">ISSN</label>
                    <input
                      type="text"
                      value={formData.issn || ''}
                      onChange={(e) => setFormData({ ...formData, issn: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                </div>
                <div className="publication-form-field-grid">
                  <div className="publication-form-field">
                    <label className="publication-form-label">Volume</label>
                    <input
                      type="text"
                      value={formData.volume || ''}
                      onChange={(e) => setFormData({ ...formData, volume: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                  <div className="publication-form-field">
                    <label className="publication-form-label">Issue</label>
                    <input
                      type="text"
                      value={formData.issue || ''}
                      onChange={(e) => setFormData({ ...formData, issue: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                </div>
                <div className="publication-form-field-grid">
                  <div className="publication-form-field">
                    <label className="publication-form-label">Pages</label>
                    <input
                      type="text"
                      value={formData.pages || ''}
                      onChange={(e) => setFormData({ ...formData, pages: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                  <div className="publication-form-field">
                    <label className="publication-form-label">Impact Factor</label>
                    <input
                      type="text"
                      value={formData.impactFactor || ''}
                      onChange={(e) => setFormData({ ...formData, impactFactor: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                </div>
                <div className="publication-form-field">
                  <label className="publication-form-label">Open Access / Subscription</label>
                  <select
                    value={formData.openAccess || 'Open Access'}
                    onChange={(e) => setFormData({ ...formData, openAccess: e.target.value })}
                    className="publication-form-select"
                  >
                    <option value="Open Access">Open Access</option>
                    <option value="Subscription">Subscription</option>
                  </select>
                </div>
              </div>
            </div>

            <div className="publication-form-section">
              <h3 className="publication-form-section-title">Author Details</h3>
              <div className="publication-form-fields">
                <div className="publication-form-field">
                  <label className="publication-form-label">
                    Author 1 (Faculty - Mandatory) <span className="publication-form-label-required">*</span>
                  </label>
                  <input
                    type="text"
                    value={formData.authors || ''}
                    onChange={(e) => setFormData({ ...formData, authors: e.target.value })}
                    required
                    className="publication-form-input"
                  />
                </div>
                <div className="publication-form-field-grid">
                  <div className="publication-form-field">
                    <label className="publication-form-label">Author 2 (Optional)</label>
                    <input
                      type="text"
                      value={formData.author2 || ''}
                      onChange={(e) => setFormData({ ...formData, author2: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                  <div className="publication-form-field">
                    <label className="publication-form-label">Author 3 (Optional)</label>
                    <input
                      type="text"
                      value={formData.author3 || ''}
                      onChange={(e) => setFormData({ ...formData, author3: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                </div>
                <div className="publication-form-field-grid">
                  <div className="publication-form-field">
                    <label className="publication-form-label">Author 4 (Optional)</label>
                    <input
                      type="text"
                      value={formData.author4 || ''}
                      onChange={(e) => setFormData({ ...formData, author4: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                  <div className="publication-form-field">
                    <label className="publication-form-label">Author 5 (Optional)</label>
                    <input
                      type="text"
                      value={formData.author5 || ''}
                      onChange={(e) => setFormData({ ...formData, author5: e.target.value })}
                      className="publication-form-input"
                    />
                  </div>
                </div>
                <div className="publication-form-field">
                  <label className="publication-form-label">Author 6 (Optional)</label>
                  <input
                    type="text"
                    value={formData.author6 || ''}
                    onChange={(e) => setFormData({ ...formData, author6: e.target.value })}
                    className="publication-form-input"
                  />
                </div>
              </div>
            </div>

            <div className="publication-form-section">
              <h3 className="publication-form-section-title">Mandatory Uploads (PDF)</h3>
              <div className="publication-form-uploads">
                <p className="publication-form-uploads-title">
                  All files must be in PDF format <span className="publication-form-label-required">*</span>
                </p>
                <div className="publication-form-uploads-list">
                  <FileUpload
                    label="Acceptance Mail PDF"
                    acceptedFile={formData.acceptanceMailPath}
                    onFileChange={(file) => {
                      if (file) {
                        handleFileUpload(file, 'acceptanceMailPath', 'journals', 'acceptance-mail');
                      }
                    }}
                    required
                    userType="faculty"
                    userId={profile?.id || ''}
                    category="journals"
                    disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
                  />
                  <FileUpload
                    label="Published Paper PDF"
                    acceptedFile={formData.publishedPaperPath}
                    onFileChange={(file) => {
                      if (file) {
                        handleFileUpload(file, 'publishedPaperPath', 'journals', 'published-paper');
                      }
                    }}
                    required
                    userType="faculty"
                    userId={profile?.id || ''}
                    category="journals"
                    disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
                  />
                  <FileUpload
                    label="Index Proof PDF"
                    acceptedFile={formData.indexProofPath}
                    onFileChange={(file) => {
                      if (file) {
                        handleFileUpload(file, 'indexProofPath', 'journals', 'index-proof');
                      }
                    }}
                    required
                    userType="faculty"
                    userId={profile?.id || ''}
                    category="journals"
                    disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
                  />
                </div>
              </div>
            </div>

            {editingItem && editingItem.remarks && (
              <div className="publication-form-remarks">
                <label className="publication-form-remarks-label">Admin Remarks</label>
                <p className="publication-form-remarks-text">{editingItem.remarks}</p>
              </div>
            )}
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
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Category *</label>
                <select
                  value={formData.category || 'National'}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                  <option value="National">National</option>
                  <option value="International">International</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Organizer</label>
                <input
                  type="text"
                  value={formData.organizer || ''}
                  onChange={(e) => setFormData({ ...formData, organizer: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Location / Venue</label>
              <input
                type="text"
                value={formData.location || ''}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Date</label>
                <input
                  type="date"
                  value={formData.date || ''}
                  onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Registration Amount</label>
                <input
                  type="text"
                  value={formData.registrationAmount || ''}
                  onChange={(e) => setFormData({ ...formData, registrationAmount: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Payment Mode</label>
              <select
                value={formData.paymentMode || 'Online'}
                onChange={(e) => setFormData({ ...formData, paymentMode: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              >
                <option value="Online">Online</option>
                <option value="Cash">Cash</option>
                <option value="Cheque">Cheque</option>
                <option value="NEFT">NEFT</option>
              </select>
            </div>
            <div className="border-t pt-4 mt-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                <input
                  type="checkbox"
                  checked={formData.isStudentPublication || false}
                  onChange={(e) => setFormData({ ...formData, isStudentPublication: e.target.checked })}
                  className="mr-2"
                />
                Student Participation (Optional)
              </label>
              {formData.isStudentPublication && (
                <div className="mt-3 space-y-3 ml-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Student Name</label>
                    <input
                      type="text"
                      value={formData.studentName || ''}
                      onChange={(e) => setFormData({ ...formData, studentName: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Register Number</label>
                    <input
                      type="text"
                      value={formData.studentRegisterNumber || ''}
                      onChange={(e) => setFormData({ ...formData, studentRegisterNumber: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">Guide (Faculty Name)</label>
                    <input
                      type="text"
                      value={formData.guideName || ''}
                      onChange={(e) => setFormData({ ...formData, guideName: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                    />
                  </div>
                </div>
              )}
            </div>
            <div className="space-y-4">
              <h3 className="text-sm font-medium text-gray-700 mb-2">Mandatory Uploads (PDF) *</h3>
              <FileUpload
                label="Registration Receipt PDF"
                acceptedFile={formData.registrationReceiptPath}
                onFileChange={(file) => {
                  if (file) {
                    handleFileUpload(file, 'registrationReceiptPath', 'conferences', 'registration-receipt');
                  }
                }}
                required
                userType="faculty"
                userId={profile?.id || ''}
                category="conferences"
                disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
              />
              <FileUpload
                label="Certificate PDF"
                acceptedFile={formData.certificatePath}
                onFileChange={(file) => {
                  if (file) {
                    handleFileUpload(file, 'certificatePath', 'conferences', 'certificate');
                  }
                }}
                required
                userType="faculty"
                userId={profile?.id || ''}
                category="conferences"
                disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
              />
            </div>
            {editingItem && editingItem.remarks && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <label className="block text-sm font-medium text-red-700 mb-1">Admin Remarks</label>
                <p className="text-sm text-red-600">{editingItem.remarks}</p>
              </div>
            )}
          </>
        );
      case 'patents':
        return (
          <>
            {commonFields}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Patent Title *</label>
              <input
                type="text"
                value={formData.title || ''}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Category *</label>
                <select
                  value={formData.category || 'National'}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                  <option value="National">National</option>
                  <option value="International">International</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Status Flow *</label>
                <select
                  value={formData.status || 'Filed'}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                  <option value="Filed">Filed</option>
                  <option value="Published">Published</option>
                  <option value="Granted">Granted</option>
                </select>
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Application Number</label>
              <input
                type="text"
                value={formData.applicationNumber || ''}
                onChange={(e) => setFormData({ ...formData, applicationNumber: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Filing Date</label>
              <input
                type="date"
                value={formData.filingDate || ''}
                onChange={(e) => setFormData({ ...formData, filingDate: e.target.value })}
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
            <div className="space-y-4">
              <h3 className="text-sm font-medium text-gray-700 mb-2">Mandatory Uploads (PDF, Conditional) *</h3>
              <FileUpload
                label="Filing Proof PDF (Required for Filed status)"
                acceptedFile={formData.filingProofPath}
                onFileChange={(file) => {
                  if (file) {
                    handleFileUpload(file, 'filingProofPath', 'patents', 'filing-proof');
                  }
                }}
                required={formData.status === 'Filed'}
                userType="faculty"
                userId={profile?.id || ''}
                category="patents"
                disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
              />
              {(formData.status === 'Published' || formData.status === 'Granted' || editingItem?.publicationCertificatePath) && (
                <FileUpload
                  label="Publication Certificate PDF"
                  acceptedFile={formData.publicationCertificatePath}
                  onFileChange={(file) => {
                    if (file) {
                      handleFileUpload(file, 'publicationCertificatePath', 'patents', 'publication-certificate');
                    }
                  }}
                  required={formData.status === 'Published' || formData.status === 'Granted'}
                  userType="faculty"
                  userId={profile?.id || ''}
                  category="patents"
                  disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
                />
              )}
              {(formData.status === 'Granted' || editingItem?.grantCertificatePath) && (
                <FileUpload
                  label="Grant Certificate PDF"
                  acceptedFile={formData.grantCertificatePath}
                  onFileChange={(file) => {
                    if (file) {
                      handleFileUpload(file, 'grantCertificatePath', 'patents', 'grant-certificate');
                    }
                  }}
                  required={formData.status === 'Granted'}
                  userType="faculty"
                  userId={profile?.id || ''}
                  category="patents"
                  disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
                />
              )}
            </div>
            {editingItem && editingItem.remarks && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <label className="block text-sm font-medium text-red-700 mb-1">Admin Remarks</label>
                <p className="text-sm text-red-600">{editingItem.remarks}</p>
              </div>
            )}
          </>
        );
      case 'bookChapters':
        return (
          <>
            {commonFields}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Chapter Title *</label>
              <input
                type="text"
                value={formData.title || ''}
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
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
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Category *</label>
                <select
                  value={formData.category || 'National'}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                  <option value="National">National</option>
                  <option value="International">International</option>
                </select>
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
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Page Numbers</label>
                <input
                  type="text"
                  value={formData.pages || ''}
                  onChange={(e) => setFormData({ ...formData, pages: e.target.value })}
                  placeholder="e.g., 45-67"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">ISBN *</label>
                <input
                  type="text"
                  value={formData.isbn || ''}
                  onChange={(e) => setFormData({ ...formData, isbn: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
            </div>
            <div className="space-y-4">
              <h3 className="text-sm font-medium text-gray-700 mb-2">Mandatory Uploads (PDF) *</h3>
              <FileUpload
                label="Chapter PDF"
                acceptedFile={formData.chapterPdfPath}
                onFileChange={(file) => {
                  if (file) {
                    handleFileUpload(file, 'chapterPdfPath', 'book-chapters', 'chapter-pdf');
                  }
                }}
                required
                userType="faculty"
                userId={profile?.id || ''}
                category="book-chapters"
                disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
              />
              <FileUpload
                label="ISBN Proof PDF"
                acceptedFile={formData.isbnProofPath}
                onFileChange={(file) => {
                  if (file) {
                    handleFileUpload(file, 'isbnProofPath', 'book-chapters', 'isbn-proof');
                  }
                }}
                required
                userType="faculty"
                userId={profile?.id || ''}
                category="book-chapters"
                disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
              />
            </div>
            {editingItem && editingItem.remarks && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <label className="block text-sm font-medium text-red-700 mb-1">Admin Remarks</label>
                <p className="text-sm text-red-600">{editingItem.remarks}</p>
              </div>
            )}
          </>
        );
      case 'books':
        return (
          <>
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
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Category *</label>
                <select
                  value={formData.category || 'National'}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                  <option value="National">National</option>
                  <option value="International">International</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Role *</label>
                <select
                  value={formData.role || 'Author'}
                  onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                  <option value="Author">Author</option>
                  <option value="Editor">Editor</option>
                </select>
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Publisher *</label>
              <input
                type="text"
                value={formData.publisher || ''}
                onChange={(e) => setFormData({ ...formData, publisher: e.target.value })}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">ISBN *</label>
                <input
                  type="text"
                  value={formData.isbn || ''}
                  onChange={(e) => setFormData({ ...formData, isbn: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Publication Year *</label>
                <input
                  type="number"
                  value={formData.publicationYear || formData.year || ''}
                  onChange={(e) => setFormData({ ...formData, publicationYear: parseInt(e.target.value), year: parseInt(e.target.value) })}
                  required
                  min="2000"
                  max={new Date().getFullYear() + 1}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                />
              </div>
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
              </select>
            </div>
            <div className="space-y-4">
              <h3 className="text-sm font-medium text-gray-700 mb-2">Mandatory Uploads (PDF) *</h3>
              <FileUpload
                label="Book Cover PDF"
                acceptedFile={formData.bookCoverPath}
                onFileChange={(file) => {
                  if (file) {
                    handleFileUpload(file, 'bookCoverPath', 'books', 'book-cover');
                  }
                }}
                required
                userType="faculty"
                userId={profile?.id || ''}
                category="books"
                disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
              />
              <FileUpload
                label="ISBN Proof PDF"
                acceptedFile={formData.isbnProofPath}
                onFileChange={(file) => {
                  if (file) {
                    handleFileUpload(file, 'isbnProofPath', 'books', 'isbn-proof');
                  }
                }}
                required
                userType="faculty"
                userId={profile?.id || ''}
                category="books"
                disabled={editingItem && (editingItem.approvalStatus === 'APPROVED' || editingItem.approvalStatus === 'LOCKED')}
              />
            </div>
            {editingItem && editingItem.remarks && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <label className="block text-sm font-medium text-red-700 mb-1">Admin Remarks</label>
                <p className="text-sm text-red-600">{editingItem.remarks}</p>
              </div>
            )}
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
            <nav className={`flex -mb-px ${isMobile ? 'overflow-x-auto' : ''}`}>
              {['journals', 'conferences', 'patents', 'bookChapters', 'books'].map((tab) => (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  className={`py-4 px-4 sm:px-6 text-xs sm:text-sm font-medium border-b-2 whitespace-nowrap ${
                    activeTab === tab
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  {tab === 'bookChapters' ? 'Book Chapters' : tab.charAt(0).toUpperCase() + tab.slice(1)}
                </button>
              ))}
            </nav>
          </div>

          <div className="p-4 sm:p-6">
            <div className="mb-4">
              <button
                onClick={() => openModal(activeTab)}
                className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 text-sm sm:text-base"
              >
                Add New
              </button>
            </div>

            {activeTab === 'journals' && renderTable(journals, 'journals')}
            {activeTab === 'conferences' && renderTable(conferences, 'conferences')}
            {activeTab === 'patents' && renderTable(patents, 'patents')}
            {activeTab === 'bookChapters' && renderTable(bookChapters, 'bookChapters')}
            {activeTab === 'books' && renderTable(books, 'books')}
          </div>
        </div>
      </div>

      {showModal && (
        <div className="publication-form-modal" onClick={(e) => e.target === e.currentTarget && setShowModal(false)}>
          <div className="publication-form-container">
            <div className="publication-form-header">
              <h3 className="publication-form-title">
                {editingItem ? 'Edit' : 'Add New'} {activeTab.charAt(0).toUpperCase() + activeTab.slice(1).replace(/([A-Z])/g, ' $1')}
              </h3>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="publication-form-body">
                {renderForm()}
              </div>
              <div className="publication-form-actions">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="publication-form-btn publication-form-btn-secondary"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="publication-form-btn publication-form-btn-primary"
                >
                  {loading ? 'Saving...' : 'Save'}
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

