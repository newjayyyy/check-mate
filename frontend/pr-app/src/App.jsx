import MaskCheckPage from './checkmate/maskCheckPage'; 
import MagnetCheckPage from './checkmate/magnetCheckPage';
import ResultPage from './checkmate/resultPage';
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function App() {
  return (
     <Router>
      <Routes>
        <Route path="/" element={<MaskCheckPage />} />
        <Route path="/magnet" element={<MagnetCheckPage />} />
        <Route path="/result" element={<ResultPage />} />
      </Routes>
    </Router>
  )
}

export default App;