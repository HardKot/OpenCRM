import { Suspense } from "react";

import { CircularProgress } from "@mui/material";
import { View } from "#shared/ui";
import { StoreProvider } from "./providers/StoreProvider";
import { Route } from "./route/Route";
import { ThemeProvider } from "./providers/ThemeProvider";

function App() {
  return (
    <StoreProvider>
      <ThemeProvider>
        <View minHeight="100vh" bgcolor="background.default">
          <Suspense
            fallback={
              <View display="flex" justifyContent="center" p={4}>
                <CircularProgress />
              </View>
            }
          >
            <Route />
          </Suspense>
        </View>
      </ThemeProvider>
    </StoreProvider>
  );
}

export default App;
