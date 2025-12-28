import React, { useEffect } from 'react';

const Toast = ({ message, type = 'success', onClose, duration = 3000 }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, duration);

    return () => clearTimeout(timer);
  }, [onClose, duration]);

  const getToastStyles = () => {
    switch (type) {
      case 'success':
        return 'bg-green-900 text-white';
      case 'error':
        return 'bg-red-600 text-white';
      case 'warning':
        return 'bg-yellow-600 text-white';
      default:
        return 'bg-garden-green-600 text-white';
    }
  };

  const getIcon = () => {
    switch (type) {
      case 'success':
        return '✅';
      case 'error':
        return '❌';
      case 'warning':
        return '⚠️';
      default:
        return '📢';
    }
  };

  return (
    <div className="fixed top-4 right-4 z-50 animate-grow">
      <div
        className={`${getToastStyles()} px-6 py-4 rounded-xl shadow-2xl flex items-center gap-3 min-w-[300px]`}
      >
        <span className="text-2xl">{getIcon()}</span>
        <p className="font-semibold">{message}</p>
      </div>
    </div>
  );
};

export default Toast;
