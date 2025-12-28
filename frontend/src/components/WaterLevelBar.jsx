import React from 'react';
import { getWaterLevelColor } from '../utils/statusColors';

const WaterLevelBar = ({ waterLevel }) => {
  return (
    <div className="w-full">
      <div className="flex justify-between items-center mb-1">
        <span className="text-sm font-medium text-garden-green-800">Water Level</span>
        <span className="text-sm font-bold text-garden-green-700">{waterLevel}%</span>
      </div>
      <div className="w-full h-4 bg-gray-200 rounded-full overflow-hidden shadow-inner">
        <div
          className={`h-full ${getWaterLevelColor(waterLevel)} transition-all duration-700 ease-out rounded-full shadow-lg`}
          style={{ width: `${waterLevel}%` }}
        >
          <div className="h-full w-full bg-white opacity-20 animate-pulse"></div>
        </div>
      </div>
    </div>
  );
};

export default WaterLevelBar;
