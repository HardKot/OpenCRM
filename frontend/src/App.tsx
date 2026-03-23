
import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import { HomePage } from './pages/home';
import { AboutPage } from './pages/about';

export function AppLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="App">
      <nav>
        <ul>
          <li><Link to="/">Home</Link></li>
          <li><Link to="/about">About</Link></li>
        </ul>
      </nav>
      {children}
    </div>
  );
}

function App() {
  return (
    <AppLayout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/about" element={<AboutPage />} />
      </Routes>
    </AppLayout>
  );
}

export default App;
