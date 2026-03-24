
import React, { Suspense } from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import { HomePage } from '#pages/home';
import { AboutPage } from '#pages/about';
import { LoginPage } from '#pages/LoginPage';
import { CircularProgress } from '@mui/material';
import { View } from '#shared/ui';



function App() {
  return (
      <Suspense fallback={<View display="flex" justifyContent="center" p={4}><CircularProgress /></View>}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/login" element={<LoginPage />} />
        </Routes>
      </Suspense>
  );
}

export default App;
