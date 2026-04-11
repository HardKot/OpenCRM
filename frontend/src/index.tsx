import React from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./app/App";
import "./app/globalApp";

const container = document.getElementById("root");

if (!container) {
  console.error("React root element not found in the DOM!");
} else {
  console.log("React root found, mounting application...");
  const root = createRoot(container);

  root.render(
    <React.StrictMode>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </React.StrictMode>,
  );
}
