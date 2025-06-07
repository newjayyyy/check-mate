import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function PartResultPage() {

  const [results, setResults] = useState([]);

  const loadResult = async () => {
    try {
      // 두 모델 결과 모두 요청
      const response = await axios.post('https://checkmate-iry6.onrender.com/api/viewAllInspections',{
           modelName:"part"
          },
          { headers: { "Content-Type": "application/json" } }
        );

     
      const combinedResults = response.data;

      // 정렬 (날짜 최신순)
      const sorted = combinedResults.sort(
        (a, b) => new Date(b.uploadedDate) - new Date(a.uploadedDate)
      );

      setResults(sorted);
    } catch (error) {
     
      console.error("검사 결과 불러오기 실패:", error);
    }
  };

  useEffect(() => {
    loadResult();
  }, []);
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex items-center p-3">
      <div className="rounded-lg w-full max-w-[1000px] h-[650px] md:max-w-[800px] lg:max-w-[1000px] mx-auto flex-grow-0  bg-[#faede0]">
        <div className="relative rounded-t-lg w-full max-w-[1440px] h-[60px] bg-[#545454] ">
          {/* 이미지 버튼 */}
          <button
            onClick={() => navigate('/main')}
            className="absolute left-4 top-1/2 -translate-y-1/2 w-[36px] h-[36px] flex items-center justify-center bg-[#545454] rounded-md hover:bg-[#faede0] transition">
            <img
              src="/back.png"
              alt="back"
              className="w-[20px] h-[20px]"
            />
          </button>
          <div className="h-full flex items-center justify-center">
            <span className="block font-[200] text-[30px] text-center text-[#faede0] font-raleway leading-none">
              부품 검사 결과
            </span>
          </div>

          <div className="h-[600px] overflow-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-2 p-4 overscroll-contain">
  {results.length === 0 ? (
  <p className="text-center text-gray-400">검사 결과가 없습니다.</p>
) : (
  results.map((result, i) => (
          <div
            key={i}
            className="w-full h-auto bg-white rounded-md shadow p-4 space-y-2"
          >
            <p className="text-sm text-gray-500">
              업로드 날짜: {new Date(result.uploadedDate).toLocaleString()}
            </p>
            <p className="text-sm text-gray-700">검사 ID: {result.inspectId}</p>

            {result.images && result.images.length > 0 ? (
            result.images.map((img, idx) => (
              <div key={idx} className="mt-2 space-y-1">
                <img
                  src={img.imageUrl}
                  alt={img.fileName}
                  className="w-full h-32 object-cover rounded"
                />
                <p className="text-sm truncate">파일명: {img.fileName}</p>
                <p
                  className={`text-sm font-bold ${
                    img.inspectResult === "ABNORMAL"
                      ? "text-red-600"
                      : "text-green-600"
                  }`}
                >
                  검사 결과: {img.inspectResult}
                </p>
</div>
 ))
  ) : (
        <p className="text-sm text-gray-400">이미지 없음</p>
  )}
 </div>
  ))
        )}

        </div>

      </div>
    </div>
</div>
  );
}
