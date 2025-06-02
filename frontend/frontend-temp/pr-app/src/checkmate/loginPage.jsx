import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function LoginPage({ onSwitchToSignup }) {
  const [user, setUser] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async () => {
    try {
      const res = await axios.post(`${import.meta.env.CHECKMATE_SERVER_IP}/api/login`, {
        user,
        password
      });
      console.log('로그인 성공:', res.data);
       navigate('/main');
    } catch (err) {
      console.error('로그인 실패:', err);
      alert('이메일 또는 비밀번호가 올바르지 않습니다.');

    }
  };
  const navigate = useNavigate();
  return (
    <div className="min-h-screen flex items-center p-3">
      <div className="rounded-lg w-full max-w-[1000px] h-[650px] md:max-w-[800px] lg:max-w-[1000px] mx-auto flex-grow-0  bg-[#faede0]">
        <div className="relative w-full max-w-[1440px] h-[60px] mt-20 md:mt-10 md:mb-10">
          <h1 className="text-5xl sm:text-7xl md:text-7xl lg:text-7xl xl:text-8xl font-light flex items-center justify-center text-neutral-700">Check Mate</h1>
        </div>
        {/* 오른쪽 영역 - 로그인인 폼 */}
        <div className="flex items-center justify-center px-4">
          <div className="w-full md:w-[500px] flex flex-col justify-center px-8 mt-8">
            <h2 className="text-3xl font-bold mb-2 text-center">로그인</h2>
            <hr className="mb-6 border-t border-gray-400" />
            <form className="flex flex-col gap-4">
              <input
                type="user"
                placeholder="사원번호"
                value={user}
                onChange={(e) => setUser(e.target.value)}
                className="p-3 border bg-[#ffffff]  border-gray-300 text-gray-800 px-6 py-3"
              />
              <input
                type="password"
                placeholder="비밀번호"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="p-3 border bg-[#ffffff]  border-gray-300 text-gray-800 px-6 py-3"
              />

              <div className="flex items-center justify-center">
                <button
                  onClick={handleLogin}
                  className="bg-gray-700 text-white px-16 py-3 mb-6">사원인증</button>

              </div>
            </form>

            <button onClick={() => navigate('/signup')}
              className="text-sm text-gray-700 mb-5 underline">회원가입</button>
            <label className="flex items-center text-gray-700">
              <input type="checkbox" className="mr-2 accent-[#e3c9b2]" />
              자동로그인
            </label>

          </div>
        </div>
      </div>
    </div>


  );
};


