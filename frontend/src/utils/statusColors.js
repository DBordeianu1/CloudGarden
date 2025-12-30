export const getStatusColor = (status) => {
  switch (status) {
    case 'HEALTHY':
      return {
        bg: 'bg-green-100',
        text: 'text-green-800',
        border: 'border-green-500',
        gradient: 'from-green-600 to-green-400',
        icon: '🪴',
      };
    case 'WILTING':
      return {
        bg: 'bg-yellow-100',
        text: 'text-yellow-800',
        border: 'border-yellow-500',
        gradient: 'from-yellow-600 to-yellow-400',
        icon: '😓',
      };
    case 'DEAD':
      return {
        bg: 'bg-stone-100',
        text: 'text-stone-800',
        border: 'border-stone-500',
        gradient: 'from-stone-600 to-stone-400',
        icon: '☠️',
      };
    case 'ZOMBIE':
      return {
        bg: 'bg-purple-100',
        text: 'text-purple-800',
        border: 'border-purple-500',
        gradient: 'from-purple-600 to-purple-400',
        icon: '🧟‍♂️',
      };
    default:
      return {
        bg: 'bg-gray-100',
        text: 'text-gray-800',
        border: 'border-gray-500',
        gradient: 'from-gray-600 to-gray-400',
        icon: '❓',
      };
  }
};

export const getWaterLevelColor = (waterLevel) => {
  if (waterLevel > 60) return 'bg-gradient-to-r from-green-600 to-green-400';
  if (waterLevel > 20) return 'bg-gradient-to-r from-yellow-600 to-yellow-400';
  return 'bg-gradient-to-r from-red-600 to-red-400';
};
