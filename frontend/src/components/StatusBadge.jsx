import React from 'react';
import { getStatusColor } from '../utils/statusColors';

const StatusBadge = ({ status }) => {
  const colors = getStatusColor(status);

  return (
    <div
      className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-sm font-semibold ${colors.bg} ${colors.text} border-2 ${colors.border}`}
    >
      <span className="text-lg">{colors.icon}</span>
      <span>{status}</span>
    </div>
  );
};

export default StatusBadge;
