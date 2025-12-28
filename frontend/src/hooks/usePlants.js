import { useState, useCallback } from 'react';
import { plantService } from '../services/api';

export const usePlants = () => {
  const [plants, setPlants] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchPlants = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await plantService.getAllPlants();
      setPlants(data);
    } catch (err) {
      setError(err.message || 'Failed to fetch plants');
      console.error('Error fetching plants:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  const addPlant = async (plantData) => {
    try {
      setError(null);
      const newPlant = await plantService.plantNew(plantData);
      setPlants((prev) => [...prev, newPlant]);
      return newPlant;
    } catch (err) {
      setError(err.message || 'Failed to add plant');
      throw err;
    }
  };

  const waterPlant = async (id) => {
    try {
      setError(null);
      const updatedPlant = await plantService.waterPlant(id);
      setPlants((prev) =>
        prev.map((plant) => (plant.id === id ? updatedPlant : plant))
      );
      return updatedPlant;
    } catch (err) {
      setError(err.message || 'Failed to water plant');
      throw err;
    }
  };

  const updatePlant = async (id, plantData) => {
    try {
      setError(null);
      const updatedPlant = await plantService.updatePlant(id, plantData);
      setPlants((prev) =>
        prev.map((plant) => (plant.id === id ? updatedPlant : plant))
      );
      return updatedPlant;
    } catch (err) {
      setError(err.message || 'Failed to update plant');
      throw err;
    }
  };

  const deletePlant = async (id) => {
    try {
      setError(null);
      await plantService.deletePlant(id);
      setPlants((prev) => prev.filter((plant) => plant.id !== id));
    } catch (err) {
      setError(err.message || 'Failed to delete plant');
      throw err;
    }
  };

  return {
    plants,
    loading,
    error,
    fetchPlants,
    addPlant,
    waterPlant,
    updatePlant,
    deletePlant,
  };
};
