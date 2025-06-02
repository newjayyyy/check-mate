import { useState } from "react";
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MaskCheckPage from './checkmate/maskCheckPage'; 
import MagnetCheckPage from './checkmate/magnetCheckPage';
import ResultPage from './checkmate/resultPage';
import MainPage from "./checkmate/mainPage";
import SignupPage from "./checkmate/signupPage";
import LoginPage from './checkmate/loginPage';


function App() {
   const [isLoggedIn, setIsLoggedIn] = useState(false);
  return (
   <Router>
      
        <Routes>
          <Route path="/main" element={<MainPage />} />
          <Route path="/mask" element={<MaskCheckPage />} />
          <Route path="/magnet" element={<MagnetCheckPage />} />
          <Route path="/result" element={<ResultPage />} />
          <Route path="/" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
    

        </Routes>
        
        {/*<AuthPage onLoginSuccess={() => setIsLoggedIn(true)} />*/}
    
    </Router>
  )
}

export default App;