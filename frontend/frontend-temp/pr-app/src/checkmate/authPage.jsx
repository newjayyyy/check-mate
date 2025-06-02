// src/pages/AuthPage.jsx
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import LoginPage from './loginPage';
import SignupPage from './signupPage';

const AuthPage = () => {
  const [isSignup, setIsSignup] = useState(false);

  return (
    <div className="min-h-screen bg-[#f6e7d8] flex items-center justify-center">
      {isSignup ? (
        <SignupPage onSwitchToLogin={() => setIsSignup(false)} />
      ) : (
        <LoginPage onSwitchToSignup={() => setIsSignup(true)} />
      )}
    </div>
  );
};

export default AuthPage;
