import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function ResultPage() {

  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex items-center p-3">
      <div className="rounded-lg w-full max-w-[1000px] h-[650px] md:max-w-[800px] lg:max-w-[1000px] mx-auto flex-grow-0  bg-[#faede0]">
        <div className="relative rounded-t-lg w-full max-w-[1440px] h-[60px] bg-[#545454] ">
          {/* 이미지 버튼 */}
          <button
            onClick={() => navigate('/')}
            className="absolute left-4 top-1/2 -translate-y-1/2 w-[36px] h-[36px] flex items-center justify-center bg-[#545454] rounded-md hover:bg-[#faede0] transition">
            <img
              src="/back.png"
              alt="back"
              className="w-[20px] h-[20px]"
            />
          </button>
          <div className="h-full flex items-center justify-center">
            <span className="block font-[200] text-[30px] text-center text-[#faede0] font-raleway leading-none">
              검사 결과
            </span>
          </div>

          <div className="h-[600px] overflow-auto grid grid-cols-4 gap-2 p-4 overscroll-contain">
            {Array.from({ length: 12 }, (_, i) => (
              <div
                key={i}
                className="w-[200px] h-[200px] bg-white rounded-md m-[20px] flex items-center justify-center"
              >
                {String(i + 1).padStart(2, '0')}
              </div>
            ))}
          </div>


        </div>

      </div>
    </div>

  );
}
