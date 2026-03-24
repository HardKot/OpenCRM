
import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { store } from '#app/store';
import App from './App';

const container = document.getElementById('root');

if (!container) {
  console.error('React root element not found in the DOM!');
} else {
  console.log('React root found, mounting application...');
  const root = createRoot(container);

  root.render(
    <React.StrictMode>
      <Provider store={store}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </Provider>
    </React.StrictMode>
  );
}
