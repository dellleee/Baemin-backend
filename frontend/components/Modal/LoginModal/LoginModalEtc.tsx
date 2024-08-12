import React from "react";
import { IoCloseSharp } from "react-icons/io5";
import { IoIosCall } from "react-icons/io";
import { MdArrowForwardIos } from "react-icons/md";

const LoginModalEtc = () => {
  return (
    <>
      <div className="mt-12">
        <div className="w-full px-2 rounded-[40px] h-16 flex gap-2 font-semibold text-lg justify-center items-center border-[2px] cursor-pointer">
          <IoIosCall size={20} />
          휴대폰번호로 계속하기
        </div>
        <div className="w-full px-2 rounded-[40px] h-16 flex gap-2 text-base mt-4 justify-center items-center cursor-pointer">
          이메일 또는 아이디로 계속하기
          <MdArrowForwardIos size={20} />
        </div>
      </div>
      <div className="mt-24 underline text-center cursor-pointer">
        계정이 기억나지 않아요.
      </div>
    </>
  );
};

export default LoginModalEtc;
