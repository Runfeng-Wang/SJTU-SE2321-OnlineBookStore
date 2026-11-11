export const generateOrderId = () => {
    return Date.now() + Math.random();
  };
  
  export const calculateTotal = (items) => {
    return items.reduce((sum, item) => sum + (item.lineTotal ?? item.price * (item.quantity ?? 1)), 0);
  };