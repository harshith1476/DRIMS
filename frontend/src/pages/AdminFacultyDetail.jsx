import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import { adminService } from '../services/adminService';

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

  if (loading) {
    return (
      <Layout title="Faculty Details">
        <div className="flex items-center justify-center h-64">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
            <p className="text-gray-600">Loading faculty data...</p>
          </div>
        </div>
      </Layout>
    );
  }

  if (!data) {
    return (
      <Layout title="Faculty Details">
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
          Faculty data not found
        </div>
      </Layout>
    );
  }

  const { profile, targets, journals, conferences, patents, bookChapters } = data;

  return (
    <Layout title={`Faculty Details - ${profile.name}`}>
      {/* Back Button */}
      <button
        onClick={() => navigate('/admin/faculty')}
        className="mb-4 flex items-center gap-2 text-blue-600 hover:text-blue-800 transition-colors"
      >
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        Back to Faculty List
      </button>

      {/* Profile Header Card */}
      <div className="bg-gradient-to-r from-blue-600 to-indigo-600 rounded-lg shadow-lg p-6 mb-6 text-white">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold mb-2">{profile.name}</h1>
            <div className="space-y-1">
              <p className="text-blue-100"><span className="font-semibold">Employee ID:</span> {profile.employeeId}</p>
              <p className="text-blue-100"><span className="font-semibold">Designation:</span> {profile.designation}</p>
              <p className="text-blue-100"><span className="font-semibold">Department:</span> {profile.department}</p>
              <p className="text-blue-100"><span className="font-semibold">Email:</span> {profile.email}</p>
            </div>
          </div>
          <div className="bg-white/20 backdrop-blur-sm rounded-lg p-4">
            <div className="text-center">
              <div className="text-4xl font-bold">{journals.length + conferences.length + patents.length + bookChapters.length}</div>
              <div className="text-sm text-blue-100">Total Publications</div>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow mb-6">
        <div className="border-b border-gray-200">
          <nav className="flex -mb-px">
            {[
              { id: 'overview', label: 'Overview', icon: '📊' },
              { id: 'targets', label: 'Research Targets', icon: '🎯' },
              { id: 'journals', label: `Journals (${journals.length})`, icon: '📚' },
              { id: 'conferences', label: `Conferences (${conferences.length})`, icon: '🎤' },
              { id: 'patents', label: `Patents (${patents.length})`, icon: '🔬' },
              { id: 'bookChapters', label: `Book Chapters (${bookChapters.length})`, icon: '📖' }
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`px-6 py-3 text-sm font-medium border-b-2 transition-colors ${
                  activeTab === tab.id
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <span className="mr-2">{tab.icon}</span>
                {tab.label}
              </button>
            ))}
          </nav>
        </div>
      </div>

      {/* Tab Content */}
      <div className="bg-white rounded-lg shadow p-6">
        {activeTab === 'overview' && (
          <div className="space-y-6">
            {/* Research Areas */}
            <div>
              <h3 className="text-lg font-semibold mb-3 text-gray-800">Research Areas</h3>
              <div className="flex flex-wrap gap-2">
                {profile.researchAreas?.map((area, index) => (
                  <span key={index} className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
                    {area}
                  </span>
                ))}
              </div>
            </div>

            {/* Academic Profiles */}
            <div>
              <h3 className="text-lg font-semibold mb-3 text-gray-800">Academic Profiles</h3>
              <div className="grid md:grid-cols-3 gap-4">
                {profile.orcidId && (
                  <div className="border border-gray-200 rounded-lg p-4">
                    <p className="text-sm text-gray-500 mb-1">ORCID ID</p>
                    <p className="font-medium">{profile.orcidId}</p>
                  </div>
                )}
                {profile.scopusId && (
                  <div className="border border-gray-200 rounded-lg p-4">
                    <p className="text-sm text-gray-500 mb-1">Scopus ID</p>
                    <p className="font-medium">{profile.scopusId}</p>
                  </div>
                )}
                {profile.googleScholarLink && (
                  <div className="border border-gray-200 rounded-lg p-4">
                    <p className="text-sm text-gray-500 mb-1">Google Scholar</p>
                    <a href={profile.googleScholarLink} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">
                      View Profile
                    </a>
                  </div>
                )}
              </div>
            </div>

            {/* Statistics */}
            <div>
              <h3 className="text-lg font-semibold mb-3 text-gray-800">Publication Statistics</h3>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="bg-blue-50 rounded-lg p-4 text-center">
                  <div className="text-3xl font-bold text-blue-600">{journals.length}</div>
                  <div className="text-sm text-gray-600 mt-1">Journals</div>
                </div>
                <div className="bg-purple-50 rounded-lg p-4 text-center">
                  <div className="text-3xl font-bold text-purple-600">{conferences.length}</div>
                  <div className="text-sm text-gray-600 mt-1">Conferences</div>
                </div>
                <div className="bg-orange-50 rounded-lg p-4 text-center">
                  <div className="text-3xl font-bold text-orange-600">{patents.length}</div>
                  <div className="text-sm text-gray-600 mt-1">Patents</div>
                </div>
                <div className="bg-indigo-50 rounded-lg p-4 text-center">
                  <div className="text-3xl font-bold text-indigo-600">{bookChapters.length}</div>
                  <div className="text-sm text-gray-600 mt-1">Book Chapters</div>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'targets' && (
          <div>
            <h3 className="text-lg font-semibold mb-4 text-gray-800">Research Targets</h3>
            {targets.length === 0 ? (
              <p className="text-gray-500">No targets set</p>
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
                        <td className="px-6 py-4 whitespace-nowrap text-sm">{target.journalTarget || 0}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">{target.conferenceTarget || 0}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">{target.patentTarget || 0}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm">{target.bookChapterTarget || 0}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {activeTab === 'journals' && (
          <div>
            <h3 className="text-lg font-semibold mb-4 text-gray-800">Journal Publications</h3>
            {journals.length === 0 ? (
              <p className="text-gray-500">No journal publications</p>
            ) : (
              <div className="space-y-4">
                {journals.map((journal) => (
                  <div key={journal.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                    <h4 className="font-semibold text-gray-800 mb-2">{journal.title}</h4>
                    <div className="grid md:grid-cols-2 gap-2 text-sm text-gray-600">
                      <p><span className="font-medium">Journal:</span> {journal.journalName}</p>
                      <p><span className="font-medium">Year:</span> {journal.year}</p>
                      <p><span className="font-medium">Authors:</span> {journal.authors}</p>
                      {journal.volume && <p><span className="font-medium">Volume:</span> {journal.volume}</p>}
                      {journal.issue && <p><span className="font-medium">Issue:</span> {journal.issue}</p>}
                      {journal.doi && (
                        <p><span className="font-medium">DOI:</span> 
                          <a href={journal.doi} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline ml-1">
                            {journal.doi}
                          </a>
                        </p>
                      )}
                      {journal.impactFactor && <p><span className="font-medium">Impact Factor:</span> {journal.impactFactor}</p>}
                      <p><span className="font-medium">Status:</span> 
                        <span className={`ml-2 px-2 py-1 rounded text-xs ${
                          journal.status === 'Published' ? 'bg-green-100 text-green-800' :
                          journal.status === 'Accepted' ? 'bg-blue-100 text-blue-800' :
                          'bg-yellow-100 text-yellow-800'
                        }`}>
                          {journal.status}
                        </span>
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'conferences' && (
          <div>
            <h3 className="text-lg font-semibold mb-4 text-gray-800">Conference Publications</h3>
            {conferences.length === 0 ? (
              <p className="text-gray-500">No conference publications</p>
            ) : (
              <div className="space-y-4">
                {conferences.map((conference) => (
                  <div key={conference.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                    <h4 className="font-semibold text-gray-800 mb-2">{conference.title}</h4>
                    <div className="grid md:grid-cols-2 gap-2 text-sm text-gray-600">
                      <p><span className="font-medium">Conference:</span> {conference.conferenceName}</p>
                      <p><span className="font-medium">Year:</span> {conference.year}</p>
                      <p><span className="font-medium">Authors:</span> {conference.authors}</p>
                      {conference.date && <p><span className="font-medium">Date:</span> {conference.date}</p>}
                      {conference.location && <p><span className="font-medium">Location:</span> {conference.location}</p>}
                      <p><span className="font-medium">Status:</span> 
                        <span className={`ml-2 px-2 py-1 rounded text-xs ${
                          conference.status === 'Published' ? 'bg-green-100 text-green-800' :
                          conference.status === 'Accepted' ? 'bg-blue-100 text-blue-800' :
                          'bg-yellow-100 text-yellow-800'
                        }`}>
                          {conference.status}
                        </span>
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'patents' && (
          <div>
            <h3 className="text-lg font-semibold mb-4 text-gray-800">Patents</h3>
            {patents.length === 0 ? (
              <p className="text-gray-500">No patents</p>
            ) : (
              <div className="space-y-4">
                {patents.map((patent) => (
                  <div key={patent.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                    <h4 className="font-semibold text-gray-800 mb-2">{patent.title}</h4>
                    <div className="grid md:grid-cols-2 gap-2 text-sm text-gray-600">
                      <p><span className="font-medium">Patent Number:</span> {patent.patentNumber}</p>
                      <p><span className="font-medium">Year:</span> {patent.year}</p>
                      <p><span className="font-medium">Inventors:</span> {patent.inventors}</p>
                      <p><span className="font-medium">Country:</span> {patent.country}</p>
                      <p><span className="font-medium">Status:</span> 
                        <span className={`ml-2 px-2 py-1 rounded text-xs ${
                          patent.status === 'Granted' ? 'bg-green-100 text-green-800' :
                          patent.status === 'Published' ? 'bg-blue-100 text-blue-800' :
                          'bg-yellow-100 text-yellow-800'
                        }`}>
                          {patent.status}
                        </span>
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'bookChapters' && (
          <div>
            <h3 className="text-lg font-semibold mb-4 text-gray-800">Book Chapters</h3>
            {bookChapters.length === 0 ? (
              <p className="text-gray-500">No book chapters</p>
            ) : (
              <div className="space-y-4">
                {bookChapters.map((chapter) => (
                  <div key={chapter.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                    <h4 className="font-semibold text-gray-800 mb-2">{chapter.title}</h4>
                    <div className="grid md:grid-cols-2 gap-2 text-sm text-gray-600">
                      <p><span className="font-medium">Book Title:</span> {chapter.bookTitle}</p>
                      <p><span className="font-medium">Year:</span> {chapter.year}</p>
                      <p><span className="font-medium">Authors:</span> {chapter.authors}</p>
                      {chapter.publisher && <p><span className="font-medium">Publisher:</span> {chapter.publisher}</p>}
                      {chapter.editors && <p><span className="font-medium">Editors:</span> {chapter.editors}</p>}
                      {chapter.isbn && <p><span className="font-medium">ISBN:</span> {chapter.isbn}</p>}
                      {chapter.pages && <p><span className="font-medium">Pages:</span> {chapter.pages}</p>}
                      <p><span className="font-medium">Status:</span> 
                        <span className={`ml-2 px-2 py-1 rounded text-xs ${
                          chapter.status === 'Published' ? 'bg-green-100 text-green-800' :
                          chapter.status === 'Accepted' ? 'bg-blue-100 text-blue-800' :
                          'bg-yellow-100 text-yellow-800'
                        }`}>
                          {chapter.status}
                        </span>
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </Layout>
  );
}

export default AdminFacultyDetail;

