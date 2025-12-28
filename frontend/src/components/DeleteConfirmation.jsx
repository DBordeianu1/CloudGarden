import React, {useState, useEffect} from 'react';

const DeleteConfirmation = ({ isOpen, onClose, onConfirm, plantName }) => {
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setIsDeleting(false);
    }
  }, [isOpen]);
  
  if (!isOpen) return null;
  if (!isOpen) return null;
  
  const handleConfirm = async () => {
    setIsDeleting(true);
    try {
      await onConfirm();
    } catch (error) {
      console.error('Failed to delete plant:', error);
      setIsDeleting(false);
    }
  };
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-3xl shadow-2xl max-w-md w-full p-8 animate-grow">
        <div className="text-center mb-6">
          <div className="text-6xl mb-3">⚠️</div>
          <h2 className="text-3xl font-bold text-red-800 mb-2">
            Remove Plant?
          </h2>
          <p className="text-gray-700 text-lg">
            Are you sure you want to remove{' '}
            <span className="font-bold text-garden-green-800">{plantName}</span>?
          </p>
          <p className="text-gray-600 text-sm mt-2">
            This action cannot be undone!
          </p>
        </div>

        <div className="flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-3 px-6 rounded-xl transition-all duration-200 hover:shadow-lg"
          >
            Cancel
          </button>
          <button
            onClick={handleConfirm}
            disabled={isDeleting}
            className="flex-1 bg-gradient-to-r from-red-600 to-red-400 hover:from-red-700 hover:to-red-500 text-white font-semibold py-3 px-6 rounded-xl transition-all duration-200 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {isDeleting ? (
            <>
      <span>Removing...</span>
            </>
          ) : (
           'Remove'
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteConfirmation;
