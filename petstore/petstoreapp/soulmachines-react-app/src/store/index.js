import { configureStore } from '@reduxjs/toolkit';
import smReducer from './sm';

const store = configureStore({
  reducer: {
    sm: smReducer,
  },
});

export default store;
