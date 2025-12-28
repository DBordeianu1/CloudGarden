import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/plants';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const plantService = {
  // GET all plants
  getAllPlants: async () => {
    const response = await api.get('');
    return response.data;
  },

  // POST new plant
  plantNew: async (plantData) => {
    const response = await api.post('', plantData);
    return response.data;
  },

  // POST water plant
  waterPlant: async (id) => {
    const response = await api.post(`/${id}/water`);
    return response.data;
  },

  // PUT update name
  updateName: async (id, nameData) => {
    const response = await api.put(`/${id}/name`, nameData);
    return response.data;
  },

  // PUT update type
  updateType: async (id, typeData) => {
    const response = await api.put(`/${id}/type`, typeData);
    return response.data;
  },

  // PUT update plant (both name and type)
  updatePlant: async (id, plantData) => {
    const response = await api.put(`/${id}`, plantData);
    return response.data;
  },

  // DELETE plant
  deletePlant: async (id) => {
    const response = await api.delete(`/${id}`);
    return response.data;
  },
};

export default api;
