
import { Suspense } from 'react';

import { CircularProgress } from '@mui/material';
import { View } from '#shared/ui';
import { StoreProvider } from './providers/StoreProvider';
import { Route } from './route/Route';
import { ThemeProvider } from './providers/ThemeProvider';

function App() {
  return (
      <View>
          <Suspense fallback={<View display="flex" justifyContent="center" p={4}><CircularProgress /></View>}>
            <StoreProvider>
              <ThemeProvider>
              <Route /></ThemeProvider>
            </StoreProvider>
          </Suspense>
      </View>
  );
}

export default App;
