import React, { useState, useEffect } from 'react';
import PlantGrid from './components/PlantGrid';
import AddPlantModal from './components/AddPlantModal';
import EditPlantModal from './components/EditPlantModal';
import DeleteConfirmation from './components/DeleteConfirmation';
import Toast from './components/Toast';
import { usePlants } from './hooks/usePlants';
import { useAutoRefresh } from './hooks/useAutoRefresh';
import './App.css';

function App() {
  const { plants, loading, fetchPlants, addPlant, waterPlant, updatePlant, deletePlant } = usePlants();
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedPlant, setSelectedPlant] = useState(null);
  const [plantToDelete, setPlantToDelete] = useState(null);
  const [toast, setToast] = useState(null);

  // Initial fetch
  useEffect(() => {
    fetchPlants();
  }, [fetchPlants]);

  // Auto-refresh every 10 seconds
  useAutoRefresh(() => {
    fetchPlants();
  }, 10000);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
  };

  const handleAddPlant = async (plantData) => {
    try {
      await addPlant(plantData);
      showToast(`${plantData.name} has been planted successfully!`, 'success');
    } catch (error) {
      showToast('Failed to plant succulent', 'error');
    }
  };

  const handleWaterPlant = async (id) => {
    try {
      const plant = plants.find(p => p.id === id);
      await waterPlant(id);
      showToast(`${plant.name} has been watered!`, 'success');
    } catch (error) {
      showToast('Failed to water plant', 'error');
    }
  };

  const handleEditPlant = (plant) => {
    setSelectedPlant(plant);
    setIsEditModalOpen(true);
  };

  const handleUpdatePlant = async (id, plantData) => {
    try {
      await updatePlant(id, plantData);
      showToast('Plant updated successfully!', 'success');
    } catch (error) {
      showToast('Failed to update plant', 'error');
    }
  };

  const handleDeleteClick = (id) => {
    const plant = plants.find(p => p.id === id);
    setPlantToDelete(plant);
    setIsDeleteModalOpen(true);
  };

  const handleConfirmDelete = async () => {
    try {
      await deletePlant(plantToDelete.id);
      showToast(`${plantToDelete.name} has been removed from your garden`, 'success');
      setIsDeleteModalOpen(false);
      setPlantToDelete(null);
    } catch (error) {
      showToast('Failed to delete plant', 'error');
    }
  };

  return (
    <div className="min-h-screen pb-8">
      {/* Header */}
      <header className="bg-gradient-to-r from-garden-green-800 via-garden-green-700 to-green-600 text-white shadow-2xl">
        <div className="container mx-auto px-6 py-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="text-6xl animate-float">☁️</div>
              <div>
                <h1 className="text-4xl font-bold mb-2">CloudGarden</h1>
                <p className="text-green-100 text-lg">Succulent-as-a-Service</p>
              </div>
            </div>
            <div className="hidden md:block text-right">
              <p className="text-green-200 text-lg font-semibold">Total Plants: {plants.length}</p>
              <p>Healthy: {plants.filter(p => p.status === 'HEALTHY').length}</p>
              <p>Wilting: {plants.filter(p => p.status === 'WILTING').length}</p>
              <p>Dead: {plants.filter(p => p.status === 'DEAD').length}</p>
              <p>Zombie: {plants.filter(p => p.status === 'ZOMBIE').length}</p>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4">
        {/* Plant Grid */}
        <PlantGrid
          plants={plants}
          loading={loading}
          onWater={handleWaterPlant}
          onEdit={handleEditPlant}
          onDelete={handleDeleteClick}
        />

        {/* Floating Add Button */}
        <button
          onClick={() => setIsAddModalOpen(true)}
          className="fixed bottom-8 right-8 bg-gradient-to-r from-green-600 to-green-400 hover:from-green-700 hover:to-green-500 text-white font-bold py-4 px-6 rounded-full shadow-2xl hover:shadow-3xl transform hover:scale-110 transition-all duration-300 flex items-center gap-3 text-lg z-40"
        >
          <span className="text-2xl">🌱</span>
          <span>Plant New Succulent</span>
        </button>
      </main>

      {/* Modals */}
      <AddPlantModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onAdd={handleAddPlant}
      />

      <EditPlantModal
        isOpen={isEditModalOpen}
        onClose={() => {
          setIsEditModalOpen(false);
          setSelectedPlant(null);
        }}
        onUpdate={handleUpdatePlant}
        plant={selectedPlant}
      />

      <DeleteConfirmation
        isOpen={isDeleteModalOpen}
        onClose={() => {
          setIsDeleteModalOpen(false);
          setPlantToDelete(null);
        }}
        onConfirm={handleConfirmDelete}
        plantName={plantToDelete?.name}
      />

      {/* Toast Notification */}
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  );
}

export default App;
