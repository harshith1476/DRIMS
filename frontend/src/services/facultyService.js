import api from './api';

export const facultyService = {
  getProfile: () => api.get('/faculty/profile'),
  updateProfile: (data) => api.put('/faculty/profile', data),
  
  getTargets: () => api.get('/faculty/targets'),
  createOrUpdateTarget: (data) => api.post('/faculty/targets', data),
  
  getJournals: () => api.get('/faculty/journals'),
  createJournal: (data) => api.post('/faculty/journals', data),
  updateJournal: (id, data) => api.put(`/faculty/journals/${id}`, data),
  deleteJournal: (id) => api.delete(`/faculty/journals/${id}`),
  
  getConferences: () => api.get('/faculty/conferences'),
  createConference: (data) => api.post('/faculty/conferences', data),
  updateConference: (id, data) => api.put(`/faculty/conferences/${id}`, data),
  deleteConference: (id) => api.delete(`/faculty/conferences/${id}`),
  
  getPatents: () => api.get('/faculty/patents'),
  createPatent: (data) => api.post('/faculty/patents', data),
  updatePatent: (id, data) => api.put(`/faculty/patents/${id}`, data),
  deletePatent: (id) => api.delete(`/faculty/patents/${id}`),
  
  getBookChapters: () => api.get('/faculty/book-chapters'),
  createBookChapter: (data) => api.post('/faculty/book-chapters', data),
  updateBookChapter: (id, data) => api.put(`/faculty/book-chapters/${id}`, data),
  deleteBookChapter: (id) => api.delete(`/faculty/book-chapters/${id}`),
  
  uploadFile: (category, publicationId, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/faculty/upload/${category}/${publicationId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  }
};

