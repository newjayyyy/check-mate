import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function SignupPage({ onSwitchToLogin }) {
   const navigate = useNavigate();
  return (
    <div className="min-h-screen flex items-center p-3">
    <div className="rounded-lg w-full max-w-[1000px] h-[650px] md:max-w-[800px] lg:max-w-[1000px] mx-auto flex-grow-0  bg-[#faede0]">
    <div className="relative w-full max-w-[1440px] h-[60px] mt-20 md:mt-10 md:mb-10">
<h1 className="text-5xl sm:text-7xl md:text-7xl lg:text-7xl xl:text-8xl font-light flex items-center justify-center text-neutral-700">Check Mate</h1>

      </div>

      {/* 오른쪽 영역 - 회원가입 폼 */}
      <div className="flex items-center justify-center bg-[#faede0] px-4">
<div className="w-full md:w-[500px] flex flex-col justify-center px-8 mt-8">
        <h2 className="text-3xl font-bold mb-2 text-center">회원가입</h2>
        <hr className="mb-6 border-t border-gray-400" />
        <form className="flex flex-col gap-4">
          <input
            type="text"
            placeholder="이름"
            className="p-3 border bg-[#ffffff] border-gray-300 rounded-md"
          />
          <input
            type="text"
            placeholder="사원번호"
            className="p-3 border bg-[#ffffff] border-gray-300 rounded-md"
          />
          <input
            type="email"
            placeholder="이메일"
            className="p-3 border bg-[#ffffff] border-gray-300 rounded-md"
          />
          <input
            type="password"
            placeholder="비밀번호"
            className="p-3 border bg-[#ffffff] border-gray-300 rounded-md"
          />
          <button
            onClick={() => navigate('/')}
            type="submit"
            className="bg-gray-800 text-white py-3 mt-4 rounded-md"
          >
            가입하기
          </button>
        </form>

        {/* 로그인으로 전환 */}
        <button
          onClick={() => navigate('/')}
          className="mt-4 text-sm underline text-gray-700 text-center"
        >
          로그인
        </button>
      </div>
      </div>
      
    </div>
    </div>
  );
};


