import React from "react";
import { SiNaver } from "react-icons/si";
import { FaApple } from "react-icons/fa";
import { RiKakaoTalkFill } from "react-icons/ri";

const LoginModalAuth = () => {
  return (
    <div className="mt-24 flex flex-col gap-4">
      <div className="w-full px-2 rounded-[40px] h-16 flex gap-2 font-semibold text-lg justify-center items-center bg-[#FFFF00] border cursor-pointer">
        <RiKakaoTalkFill size={20} />
        카카오로 계속하기
      </div>
      <div className="w-full px-2 rounded-[40px] h-16 flex gap-2 font-semibold text-lg justify-center text-white items-center bg-green-400 border cursor-pointer">
        <SiNaver size={20} color="white" />
        네이버로 계속하기
      </div>
      <div className="w-full px-2 rounded-[40px] h-16 flex gap-2 font-semibold text-lg justify-center items-center bg-white-400 border-[2px] cursor-pointer">
        <FaApple size={20} /> Apple로 계속하기
      </div>
    </div>
  );
};

export default LoginModalAuth;
