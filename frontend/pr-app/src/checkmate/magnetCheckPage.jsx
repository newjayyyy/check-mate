import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function MaskCheckPage() {
    const videoRef = useRef(null);
    const canvasRef = useRef(null);
    const [cameraError, setCameraError] = useState(null);
    const [capturedImage, setCapturedImage] = useState(null);

    const navigate = useNavigate();
    useEffect(() => {
        async function enableCamera() {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({
                    video: { facingMode: { exact: "environment" } }, // 셀카 모드
                    audio: false,
                });
                if (videoRef.current) {
                    videoRef.current.srcObject = stream;
                }
            } catch (err) {
                console.error("카메라 오류:", err);
                setCameraError("카메라 권한이 없거나 지원하지 않는 기기입니다.");
            }
        }
        enableCamera();

        return () => {
            if (videoRef.current && videoRef.current.srcObject) {
                videoRef.current.srcObject.getTracks().forEach((track) => track.stop());
            }
        };
    }, []);

    // 사진 찍기 함수
    const takePhoto = () => {
        if (!videoRef.current) return;

        const video = videoRef.current;
        const canvas = canvasRef.current;
        const context = canvas.getContext("2d");

        // 캔버스 크기 = 비디오 크기
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;

        // 현재 비디오 프레임을 캔버스에 그림
        context.drawImage(video, 0, 0, canvas.width, canvas.height);
    };
    const handleInspect = async () => {
    if (!videoRef.current || !canvasRef.current) return;

    const video = videoRef.current;
    const canvas = canvasRef.current;
    const context = canvas.getContext("2d");

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    const imageDataUrl = canvas.toDataURL("image/png");

    // 1. 저장용: Blob 변환 → 파일명: timestamp.png
    const blob = await (await fetch(imageDataUrl)).blob();
    const timestamp = new Date().toISOString().replace(/[:.]/g, "-");
    const fileName = `capture-${timestamp}.png`;

    // 로컬 저장
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = fileName;
    link.click();

    // 2. 서버 업로드
    const formData = new FormData();
    formData.append("model", "part"); // 필요한 모델 이름
    formData.append("files", new File([blob], fileName, { type: "image/png" }));

    try {
        const response = await axios.post("https://checkmate-iry6.onrender.com/api/inspections", formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });

        // 결과 저장
        setInspectionResult(response.data); // 결과를 상태에 저장
    } catch (err) {
        console.error("서버 전송 실패:", err);
    }
    };
    const handleClick = () => {
    takePhoto();        // 기존 로직
    handleInspect();    // 검사 로직
};



    return (
        <div className=" min-h-screen flex items-center p-3">
            <div className="rounded-lg w-full max-w-[1000px] h-[650px] md:max-w-[800px] lg:max-w-[1000px] mx-auto flex-grow-0  bg-[#faede0]">
                <div className="relative rounded-t-lg w-full max-w-[1440px] h-[60px] bg-[#545454] ">
                    {/* 이미지 버튼 */}
                    <button
                        onClick={() => navigate('/result')}
                        className="absolute left-4 top-1/2 -translate-y-1/2 w-[36px] h-[36px] flex items-center justify-center bg-[#545454] rounded-md hover:bg-[#faede0] transition">
                        <img
                            src="/sidebar.png"
                            alt="sidebar"
                            className="w-[20px] h-[20px]"
                        />
                    </button>
                    <div className="h-full flex items-center justify-center">
                        <span className="block font-[200] text-[30px] text-center text-[#faede0] font-raleway leading-none">
                            부품 결함 검사
                        </span>
                    </div>
                    {/* 카메라 영역 */}
                    <div className="mt-10 flex justify-center">
                        {cameraError ? (
                            <div className="text-red-600 font-semibold">{cameraError}</div>
                        ) : (
                            <div className="w-[400px] h-[300px] bg-white rounded-xl overflow-hidden">
                                <video
                                    ref={videoRef}
                                    autoPlay
                                    playsInline
                                    muted
                                    className="w-full h-full object-cover"
                                />
                            </div>
                        )}
                        {/* 캔버스는 숨김 */}
                        <canvas ref={canvasRef} className="hidden" />
                    </div>
                    <div className="mx-3">
                        <span className="block mt-10 font-[200] text-[20px] md:text-[25px] text-center text-[#545454] font-raleway leading-none">
                            정확한 검사를 위해 부품이 움직이지 않도록 해주세요
                        </span>
                    </div>
                    {/* 버튼 영역 */}
                    <div className="w-full flex justify-center mt-6 mb-4">
                        <button
                        type="button"
                            onClick={handleClick}
                            className="group w-[200px] transition p-3 bg-gray-700 text-white mb-6 rounded-md"
                        >검사하기
                            {/*<img
                                src="/button.png" // 찍기 버튼 이미지 (로컬 public 폴더에 있어야 함)
                                alt="검사하기"
                                className="w-60 h-18 group-hover:hidden"
                            />
                            <img
                                src="/button_hover.png" // 찍기 버튼 이미지 (로컬 public 폴더에 있어야 함)
                                alt="검사하기 hover"
                                className="w-60 h-18 hidden group-hover:block"
                            />*/}
                        </button>
                    </div>

                    {/* 오른쪽 하단 이미지 버튼 2개 */}
                    <div className="relative mt-6">
                        <div className="absolute bottom-[-30px] right-4 flex gap-2">
                            {/* 버튼 1 */}
                            <button
                                onClick={() => navigate('/mask')}
                                className="w-15 h-15 flex items-center justify-center rounded-full bg-[#faede0] hover:bg-[#545454] transition">
                                <img
                                    src="/mask.png"
                                    alt="버튼1"
                                    className="w-11 h-11 group-hover:hidden"
                                />

                            </button>

                            {/* 버튼 2 */}
                            <button
                                onClick={() => navigate('/main')}
                                className="w-15 h-15 flex items-center justify-center rounded-full bg-[#faede0] hover:bg-[#545454] transition">
                                <img
                                    src="/back.png"
                                    alt="버튼2"
                                    className="w-13 h-13 group-hover:hidden"
                                />

                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

