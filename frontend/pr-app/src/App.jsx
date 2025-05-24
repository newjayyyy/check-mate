import MaskCheckPage from './checkmate/maskCheckPage'; // 네가 만든 컴포넌트 경로
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MagnetCheckPage from './checkmate/magnetCheckPage';
function App() {
  return (
     <Router>
      <Routes>
        <Route path="/" element={<MaskCheckPage />} />
        <Route path="/magnet" element={<MagnetCheckPage />} />
      </Routes>
    </Router>
  )
}

export default App;