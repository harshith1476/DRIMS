import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import { adminService } from '../services/adminService';
import './AdminFacultyDetail.css';

function AdminFacultyDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('overview');

  useEffect(() => {
    loadCompleteData();
  }, [id]);

  const loadCompleteData = async () => {
    try {
      const response = await adminService.getCompleteFacultyData(id);
      setData(response.data);
    } catch (error) {
      console.error('Error loading faculty data:', error);
    } finally {
      setLoading(false);
    }
  };

  // Get initials for avatar
  const getInitials = (name) => {
    if (!name) return 'FA';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  };

  if (loading) {
    return (
      <Layout title="Faculty Details">
        <div className="faculty-loading">
          <div className="loading-spinner"></div>
          <p>Loading faculty data...</p>
        </div>
      </Layout>
    );
  }

  if (!data) {
    return (
      <Layout title="Faculty Details">
        <div className="faculty-error">
          Faculty data not found
        </div>
      </Layout>
    );
  }

  const { profile, targets, journals, conferences, patents, bookChapters } = data;
  const totalPublications = journals.length + conferences.length + patents.length + bookChapters.length;

  return (
    <Layout title={`Faculty Details - ${profile.name}`}>
      <div className="faculty-detail-page">
        {/* Back Button */}
        <button
          onClick={() => navigate('/admin/faculty')}
          className="back-button"
        >
          <svg className="back-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          Back to Faculty List
        </button>

        {/* Official Profile Header */}
        <div className="faculty-header-card">
          <div className="faculty-header-content">
            <div className="faculty-avatar-section">
              <div className="faculty-avatar">
                {getInitials(profile.name)}
              </div>
            </div>
            
            <div className="faculty-info-section">
              <h1 className="faculty-name">{profile.name}</h1>
              <p className="faculty-designation">{profile.designation}</p>
              <p className="faculty-department">{profile.department}</p>
              <div className="faculty-meta">
                <span className="faculty-meta-item">
                  <strong>Employee ID:</strong> {profile.employeeId}
                </span>
                <span className="faculty-meta-item">
                  <strong>Email:</strong> {profile.email}
                </span>
              </div>
            </div>

            <div className="faculty-metrics-section">
              <div className="metric-card">
                <div className="metric-value">{totalPublications}</div>
                <div className="metric-label">Total Publications</div>
              </div>
              <div className="metric-card">
                <div className="metric-value">{journals.length}</div>
                <div className="metric-label">Journals</div>
              </div>
              <div className="metric-card">
                <div className="metric-value">{conferences.length}</div>
                <div className="metric-label">Conferences</div>
              </div>
            </div>
          </div>
        </div>

        {/* Official Tabs */}
        <div className="faculty-tabs-container">
          <nav className="faculty-tabs">
            {[
              { id: 'overview', label: 'Overview' },
              { id: 'targets', label: 'Research Targets' },
              { id: 'journals', label: `Journals (${journals.length})` },
              { id: 'conferences', label: `Conferences (${conferences.length})` },
              { id: 'patents', label: `Patents (${patents.length})` },
              { id: 'bookChapters', label: `Book Chapters (${bookChapters.length})` }
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`faculty-tab ${activeTab === tab.id ? 'active' : ''}`}
              >
                {tab.label}
              </button>
            ))}
          </nav>
        </div>

        {/* Tab Content */}
        <div className="faculty-content">
        {activeTab === 'overview' && (
          <div className="space-y-6">
            {/* Research Areas */}
            <div className="content-section">
              <h3 className="section-title">Research Areas</h3>
              <div className="research-areas">
                {profile.researchAreas?.map((area, index) => (
                  <span key={index} className="research-pill">
                    {area}
                  </span>
                ))}
              </div>
            </div>

            {/* Academic Profiles */}
            <div className="content-section">
              <h3 className="section-title">Academic Profiles</h3>
              <div className="academic-profiles-grid">
                {profile.orcidId && (
                  <div className="academic-profile-card">
                    <p className="academic-profile-label">ORCID ID</p>
                    <p className="academic-profile-value">{profile.orcidId}</p>
                  </div>
                )}
                {profile.scopusId && (
                  <div className="academic-profile-card">
                    <p className="academic-profile-label">Scopus ID</p>
                    <p className="academic-profile-value">{profile.scopusId}</p>
                  </div>
                )}
                {profile.googleScholarLink && (
                  <div className="academic-profile-card">
                    <p className="academic-profile-label">Google Scholar</p>
                    <a href={profile.googleScholarLink} target="_blank" rel="noopener noreferrer" className="academic-profile-link">
                      View Profile →
                    </a>
                  </div>
                )}
              </div>
            </div>

            {/* Statistics */}
            <div className="content-section">
              <h3 className="section-title">Publication Statistics</h3>
              <div className="stats-grid">
                <div className="stat-box stat-box-blue">
                  <div className="stat-box-value">{journals.length}</div>
                  <div className="stat-box-label">Journals</div>
                </div>
                <div className="stat-box stat-box-slate">
                  <div className="stat-box-value">{conferences.length}</div>
                  <div className="stat-box-label">Conferences</div>
                </div>
                <div className="stat-box stat-box-slate">
                  <div className="stat-box-value">{patents.length}</div>
                  <div className="stat-box-label">Patents</div>
                </div>
                <div className="stat-box stat-box-slate">
                  <div className="stat-box-value">{bookChapters.length}</div>
                  <div className="stat-box-label">Book Chapters</div>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'targets' && (
          <div className="content-section">
            <h3 className="section-title">Research Targets</h3>
            {targets.length === 0 ? (
              <p className="empty-state">No targets set</p>
            ) : (
              <div className="table-wrapper">
                <table className="faculty-table">
                  <thead>
                    <tr>
                      <th>Year</th>
                      <th>Journals</th>
                      <th>Conferences</th>
                      <th>Patents</th>
                      <th>Book Chapters</th>
                    </tr>
                  </thead>
                  <tbody>
                    {targets.map((target) => (
                      <tr key={target.id}>
                        <td className="font-medium">{target.year}</td>
                        <td>{target.journalTarget || 0}</td>
                        <td>{target.conferenceTarget || 0}</td>
                        <td>{target.patentTarget || 0}</td>
                        <td>{target.bookChapterTarget || 0}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {activeTab === 'journals' && (
          <div className="content-section">
            <h3 className="section-title">Journal Publications</h3>
            {journals.length === 0 ? (
              <p className="empty-state">No journal publications</p>
            ) : (
              <div className="publications-list">
                {journals.map((journal) => (
                  <div key={journal.id} className="publication-card">
                    <h4 className="publication-title">{journal.title}</h4>
                    <div className="publication-details">
                      <div className="publication-detail-row">
                        <span className="detail-label">Journal:</span>
                        <span className="detail-value">{journal.journalName}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Year:</span>
                        <span className="detail-value">{journal.year}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Authors:</span>
                        <span className="detail-value">{journal.authors}</span>
                      </div>
                      {journal.volume && (
                        <div className="publication-detail-row">
                          <span className="detail-label">Volume:</span>
                          <span className="detail-value">{journal.volume}</span>
                        </div>
                      )}
                      {journal.issue && (
                        <div className="publication-detail-row">
                          <span className="detail-label">Issue:</span>
                          <span className="detail-value">{journal.issue}</span>
                        </div>
                      )}
                      {journal.doi && (
                        <div className="publication-detail-row">
                          <span className="detail-label">DOI:</span>
                          <a href={journal.doi} target="_blank" rel="noopener noreferrer" className="detail-link">
                            {journal.doi}
                          </a>
                        </div>
                      )}
                      {journal.impactFactor && (
                        <div className="publication-detail-row">
                          <span className="detail-label">Impact Factor:</span>
                          <span className="detail-value">{journal.impactFactor}</span>
                        </div>
                      )}
                      <div className="publication-detail-row">
                        <span className="detail-label">Status:</span>
                        <span className={`status-badge status-${journal.status.toLowerCase()}`}>
                          {journal.status}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'conferences' && (
          <div className="content-section">
            <h3 className="section-title">Conference Publications</h3>
            {conferences.length === 0 ? (
              <p className="empty-state">No conference publications</p>
            ) : (
              <div className="publications-list">
                {conferences.map((conference) => (
                  <div key={conference.id} className="publication-card">
                    <h4 className="publication-title">{conference.title}</h4>
                    <div className="publication-details">
                      <div className="publication-detail-row">
                        <span className="detail-label">Conference:</span>
                        <span className="detail-value">{conference.conferenceName}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Year:</span>
                        <span className="detail-value">{conference.year}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Authors:</span>
                        <span className="detail-value">{conference.authors}</span>
                      </div>
                      {conference.date && (
                        <div className="publication-detail-row">
                          <span className="detail-label">Date:</span>
                          <span className="detail-value">{conference.date}</span>
                        </div>
                      )}
                      {conference.location && (
                        <div className="publication-detail-row">
                          <span className="detail-label">Location:</span>
                          <span className="detail-value">{conference.location}</span>
                        </div>
                      )}
                      <div className="publication-detail-row">
                        <span className="detail-label">Status:</span>
                        <span className={`status-badge status-${conference.status.toLowerCase()}`}>
                          {conference.status}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'patents' && (
          <div className="content-section">
            <h3 className="section-title">Patents</h3>
            {patents.length === 0 ? (
              <p className="empty-state">No patents</p>
            ) : (
              <div className="publications-list">
                {patents.map((patent) => (
                  <div key={patent.id} className="publication-card">
                    <h4 className="publication-title">{patent.title}</h4>
                    <div className="publication-details">
                      <div className="publication-detail-row">
                        <span className="detail-label">Patent Number:</span>
                        <span className="detail-value">{patent.patentNumber}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Year:</span>
                        <span className="detail-value">{patent.year}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Inventors:</span>
                        <span className="detail-value">{patent.inventors}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Country:</span>
                        <span className="detail-value">{patent.country}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Status:</span>
                        <span className={`status-badge status-${patent.status.toLowerCase()}`}>
                          {patent.status}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'bookChapters' && (
          <div className="content-section">
            <h3 className="section-title">Book Chapters</h3>
            {bookChapters.length === 0 ? (
              <p className="empty-state">No book chapters</p>
            ) : (
              <div className="publications-list">
                {bookChapters.map((chapter) => (
                  <div key={chapter.id} className="publication-card">
                    <h4 className="publication-title">{chapter.title}</h4>
                    <div className="publication-details">
                      <div className="publication-detail-row">
                        <span className="detail-label">Book Title:</span>
                        <span className="detail-value">{chapter.bookTitle}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Year:</span>
                        <span className="detail-value">{chapter.year}</span>
                      </div>
                      <div className="publication-detail-row">
                        <span className="detail-label">Authors:</span>
                        <span className="detail-value">{chapter.authors}</span>
                      </div>
                      {chapter.publisher && (
                        <div className="publication-detail-row">
                          <span className="detail-label">Publisher:</span>
                          <span className="detail-value">{chapter.publisher}</span>
                        </div>
                      )}
                      {chapter.editors && (
                        <div className="publication-detail-row">
                          <span className="detail-label">Editors:</span>
                          <span className="detail-value">{chapter.editors}</span>
                        </div>
                      )}
                      {chapter.isbn && (
                        <div className="publication-detail-row">
                          <span className="detail-label">ISBN:</span>
                          <span className="detail-value">{chapter.isbn}</span>
                        </div>
                      )}
                      {chapter.pages && (
                        <div className="publication-detail-row">
                          <span className="detail-label">Pages:</span>
                          <span className="detail-value">{chapter.pages}</span>
                        </div>
                      )}
                      <div className="publication-detail-row">
                        <span className="detail-label">Status:</span>
                        <span className={`status-badge status-${chapter.status.toLowerCase()}`}>
                          {chapter.status}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
        </div>
      </div>
    </Layout>
  );
}

export default AdminFacultyDetail;

