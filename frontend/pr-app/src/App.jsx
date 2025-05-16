import MaskCheckPage from './checkmate/maskCheckPage'; // 네가 만든 컴포넌트 경로
import React from 'react';
function App() {
  return (
     <div className="w-screen h-screen flex items-center justify-center bg-gray-100">
      <div className="w-full max-w-[1440px] h-full bg-white">
        <div className="h-full flex flex-col items-center justify-center">
          <h1 className="text-2xl font-bold mb-4">마스크 검사 페이지</h1>
          <button className="bg-blue-500 text-white px-6 py-3 rounded-lg">
            검사 시작
          </button>
        </div>
      </div>
    </div>
  );
}

export default App;