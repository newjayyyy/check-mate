import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function MainPage() {

   const navigate = useNavigate();
   const handleLogout = async () => {
    try {
      const res = await axios.get(`https://checkmate-iry6.onrender.com/api/logout`, 
      {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
         withCredentials: true
      });
      console.log('로그아웃 성공:', res.data);
      navigate('/');
    } catch (err) {
      console.error('로그아웃 실패:', err);
    }
  }
  const [employee, setEmployee] = useState({ username: "", name: "" });
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const res = await axios.get("https://checkmate-iry6.onrender.com/api/me", {
          withCredentials: true, // 쿠키 포함
        });
        setEmployee({
          username: res.data.username,
          name: res.data.name,
        });
      } catch (err) {
        console.error("사용자 정보 불러오기 실패:", err);
      }
    };

    fetchUserInfo();
  }, []);


  return (
    <div className=" min-h-screen flex items-center p-3">
      <div className="rounded-lg w-full max-w-[1000px] h-[650px] md:max-w-[800px] lg:max-w-[1000px] mx-auto flex-grow-0  bg-[#faede0]">
        {/* 타이틀 */}
        <div className="relative rounded-t-lg w-full max-w-[1440px] h-[60px] bg-[#545454] ">
          <button
            onClick={() => navigate('/result')}
            className="absolute left-4 top-1/2 -translate-y-1/2 w-[36px] h-[36px] flex items-center justify-center rounded-md bg-[#545454] hover:bg-[#faede0] transition">
            <img
              src="/sidebar.png"
              alt="sidebar"
              className="w-[20px] h-[20px]"
            />
          </button>
        </div>
        <div className="relative w-full max-w-[1440px] h-[60px] mt-5 md:mt-5 md:mb-10">
          <h1 className="text-5xl sm:text-7xl md:text-7xl lg:text-7xl xl:text-8xl font-light flex items-center justify-center text-neutral-700">Check Mate</h1>

        </div>

        {/* 사원정보 */}
        <div className="flex items-center justify-center gap-5 mb-12 mt-5 md:mb-12 md:mt-15 text-neutral-700 text-[20px] md:text-3xl font-light ">
          <span>사원번호:{employee.username}</span>
          <span>이름:{employee.name}</span>
        </div>

        {/* 이미지 두 개 */}
        <div className="flex items-center justify-center gap-5 md:flex-raw md:gap-20 mb-5">
          <button
            onClick={() => navigate('/mask')}
            className="w-40 h-30 md:w-80 md:h-60 rounded-lg overflow-hidden border-4 border-neutral-600 hover:opacity-80 transition-opacity">

            <img
              src="/maskIcon.png"
              alt="마스크 검사"
              className="w-full h-full object-cover"
            />
          </button>

          <button
            onClick={() => navigate('/magnet')}
            className="w-40 h-30 md:w-80 md:h-60 rounded-lg overflow-hidden border-4 border-neutral-600 hover:opacity-80 transition-opacity"
          >
            <img
              src="/magnetIcon.png"
              alt="정상 부품 검사"
              className="w-full h-full object-cover"
            />
          </button>
        </div>

        {/* 이미지 하단 텍스트 */}
        <div className="flex items-center justify-center gap-15 md:gap-55 text-[16px] md:text-[25px] font-bold text-neutral-800 mb-5">
          <span>마스크 착용 검사</span>
          <span>정상 부품 검사</span>
        </div>

        <div className="flex items-center justify-center">
          <button
          type='button'
            onClick={handleLogout}
            className="text-slate-600 text-xl underline">로그아웃</button>

        </div>
        

      </div>
    </div>


  );
};


