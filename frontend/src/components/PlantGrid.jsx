import React from 'react';
import PlantCard from './PlantCard';

const PlantGrid = ({ plants, onWater, onEdit, onDelete, loading }) => {
  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="text-center">
          <div className="text-6xl mb-4 animate-spin">🌱</div>
          <p className="text-xl text-garden-green-700 font-semibold">Loading your garden...</p>
        </div>
      </div>
    );
  }

  if (plants.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] text-center px-4">
        <div className="text-8xl mb-6 animate-bounce">🪴</div>
        <h2 className="text-3xl font-bold text-garden-green-900 mb-2">
          Your Garden is Empty!
        </h2>
        <p className="text-lg text-garden-green-700 mb-4">
          Time to plant your first succulent and watch it grow
        </p>
        <p className="text-sm text-garden-green-600 italic">
          Click the "Plant New Succulent" button to get started
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 p-6">
      {plants.map((plant) => (
        <div key={plant.id} className="animate-grow">
          <PlantCard
            plant={plant}
            onWater={onWater}
            onEdit={onEdit}
            onDelete={onDelete}
          />
        </div>
      ))}
    </div>
  );
};

export default PlantGrid;
