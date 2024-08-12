import React from "react";
import { IoCloseSharp } from "react-icons/io5";
import Image from "next/image";
import useLoginModal from "@/store/useLoginModal";

const LoginModalInfo = () => {
  const { closeModal } = useLoginModal();

  return (
    <>
      <button
        onClick={closeModal}
        className="cursor-pointer absolute top-6 left-6"
      >
        <IoCloseSharp size={32} />
      </button>
      <h1 className="text-4xl mb-4 mt-24 font-bold text-center">
        배달<span className="text-2xl">의</span>민족
      </h1>
      <Image
        src="/loginChar.png"
        alt="loginChar"
        width={50}
        height={50}
        className="absolute top-28 right-0"
      />
    </>
  );
};

export default LoginModalInfo;
