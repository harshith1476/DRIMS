import api from './api';

export const adminService = {
  getAllProfiles: () => api.get('/admin/faculty-profiles'),
  getProfileById: (id) => api.get(`/admin/faculty-profiles/${id}`),
  getCompleteFacultyData: (id) => api.get(`/admin/faculty-profiles/${id}/complete`),
  
  getAllTargets: () => api.get('/admin/targets'),
  
  getAllJournals: () => api.get('/admin/journals'),
  getAllConferences: () => api.get('/admin/conferences'),
  getAllPatents: () => api.get('/admin/patents'),
  getAllBookChapters: () => api.get('/admin/book-chapters'),
  
  getAnalytics: () => api.get('/admin/analytics'),
  
  exportToExcel: (year, category) => {
    const params = new URLSearchParams();
    if (year) params.append('year', year);
    if (category) params.append('category', category);
    return api.get(`/admin/export?${params.toString()}`, {
      responseType: 'blob'
    });
  }
};

