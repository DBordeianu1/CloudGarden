import React, { useState } from 'react';

const PlantCard = ({ plant, onWater, onEdit, onDelete }) => {
  const [isWatering, setIsWatering] = useState(false);
  const [showSparkle, setShowSparkle] = useState(false);

  const handleWater = async () => {
    setIsWatering(true);
    try {
      await onWater(plant.id);
      setShowSparkle(true);
      setTimeout(() => setShowSparkle(false), 600);
    } catch (error) {
      console.error('Failed to water plant:', error);
    } finally {
      setIsWatering(false);
    }
  };

  // Get emoji based on status
  const getPlantEmoji = () => {
    switch (plant.status) {
      case 'HEALTHY':
        return '🪴';
      case 'WILTING':
        return '😓';
      case 'DEAD':
        return '💀';
      case 'ZOMBIE':
        return '🧟‍♂️';
      default:
        return '🌵';
    }
  };

  // Get progress bar color based on status
  const getProgressBarColor = () => {
    switch (plant.status) {
      case 'HEALTHY':
        return 'bg-green-500';
      case 'WILTING':
        return 'bg-yellow-500';
      case 'DEAD':
        return 'bg-red-500';
      case 'ZOMBIE':
        return 'bg-gray-500';
      default:
        return 'bg-gray-300';
    }
  };

  // Check if button should be disabled
  const isButtonDisabled = isWatering;

  return (
    <div className="relative bg-white rounded-3xl shadow-lg hover:shadow-2xl transition-all duration-300 p-6 border-4 border-green-200 hover:scale-105">
      {showSparkle && (
        <div className="absolute inset-0 flex items-center justify-center pointer-events-none z-50">
          <div className="text-6xl animate-sparkle">💧</div>
        </div>
      )}

      <div className="flex justify-between items-start mb-4">
        <div className="flex-1">
          <h3 className="text-2xl font-bold text-garden-green-900 mb-1">{plant.name}</h3>
          <p className="text-sm text-garden-green-600 italic">{plant.type}</p>
        </div>
        <div className="text-5xl ml-2">{getPlantEmoji()}</div>
      </div>

      <div className="mb-4">
        <span className="inline-block px-3 py-1 rounded-full text-sm font-semibold bg-gray-100 text-gray-700">
          {plant.status}
        </span>
      </div>

      <div className="mb-6">
        <div className="w-full">
          <div className="flex justify-between items-center mb-1">
            <span className="text-sm font-medium text-garden-green-800">Water Level</span>
            <span className="text-sm font-bold text-garden-green-700">{plant.waterLevel}%</span>
          </div>
          <div className="w-full h-4 bg-gray-200 rounded-full overflow-hidden shadow-inner">
            <div
              className={`h-full ${getProgressBarColor()} transition-all duration-700 ease-out`}
              style={{ width: `${plant.waterLevel}%` }}
            ></div>
          </div>
        </div>
      </div>

      <div className="space-y-2">
        <button
          onClick={handleWater}
          disabled={isButtonDisabled}
          className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-xl hover:shadow-lg transform hover:scale-105 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          {isWatering ? (
            <>
              <span className="animate-spin">💧</span>
              <span>Watering...</span>
            </>
          ) : (
            <>
              <span>Water</span>
            </>
          )}
        </button>

        <button
          onClick={() => onEdit(plant)}
          className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-xl hover:shadow-lg transform hover:scale-105 transition-all duration-200"
        >Edit</button>

        <button
          onClick={() => onDelete(plant.id)}
          className="w-full bg-red-500 hover:bg-red-600 text-white font-semibold py-2 px-4 rounded-xl hover:shadow-lg transform hover:scale-105 transition-all duration-200"
        >Delete</button>
      </div>

      <div className="mt-3 flex gap-2 justify-center">
        <span className="text-xs px-2 py-1 bg-gray-100 text-gray-600 rounded-full">
          Id: {plant.id}
          </span>
          <span className="text-xs px-2 py-1 bg-gray-100 text-gray-600 rounded-full">
            Response Time:{' '} 
            {plant.responseTimeMS} ms
            </span>
            </div>
    </div>
  );
};

export default PlantCard;
