
import React, { Suspense, useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '#shared/hooks/reduxHooks';
import { getUserInited, userActions } from '#entities/User';
import { RequireAuth } from '#app/providers/router/ui/RequireAuth';
import { HomePage } from '#pages/home';
import { AboutPage } from '#pages/about';
import { LoginPage } from '#pages/LoginPage';
import { CircularProgress } from '@mui/material';
import { View } from '#shared/ui';

function App() {
  const dispatch = useAppDispatch();
  const inited = useAppSelector(getUserInited);

  useEffect(() => {
    dispatch(userActions.initAuthData());
  }, [dispatch]);

  if (!inited) {
    return (
        <View display="flex" justifyContent="center" p={4}>
            <CircularProgress />
        </View>
    );
  }

  return (
      <View>
          <Suspense fallback={<View display="flex" justifyContent="center" p={4}><CircularProgress /></View>}>
            <Routes>
              <Route path="/" element={
                  <RequireAuth>
                      <HomePage />
                  </RequireAuth>
              } />
              <Route path="/about" element={
                  <RequireAuth>
                      <AboutPage />
                  </RequireAuth>
              } />
              <Route path="/login" element={<LoginPage />} />
            </Routes>
          </Suspense>
      </View>
  );
}

export default App;
