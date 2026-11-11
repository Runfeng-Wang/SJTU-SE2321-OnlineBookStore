import { useState } from 'react';

export const useCartSelection = () => {
  const [selectedIds, setSelectedIds] = useState([]);

  const toggleSelect = (cartId) => {
    setSelectedIds(prev => 
      prev.includes(cartId) ? prev.filter(id => id !== cartId) : [...prev, cartId]
    );
  };

  const clearSelection = () => setSelectedIds([]);

  return { selectedIds, toggleSelect, clearSelection };
};