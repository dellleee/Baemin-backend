"use client";

import useLoginModal from "@/store/useLoginModal";
import { motion } from "framer-motion";
import LoginModalAuth from "./LoginModalAuth";
import LoginModalEtc from "./LoginModalEtc";
import LoginModalInfo from "./LoginModalInfo";

const LoginModal: React.FC = () => {
  const { isOpen, closeModal } = useLoginModal();

  const modalVariants = {
    hidden: { opacity: 0, y: "100%" },
    visible: { opacity: 1, y: "0%" },
    exit: { opacity: 0, y: "100%" },
  };

  return (
    <>
      {isOpen && (
        <div
          className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
          onClick={closeModal}
        >
          <motion.div
            className="w-[440px] h-full relative p-8 rounded bg-white shadow-lg"
            variants={modalVariants}
            initial="hidden"
            animate="visible"
            exit="exit"
            transition={{ duration: 0.5 }}
            onClick={(e) => e.stopPropagation()}
          >
            <LoginModalInfo />
            <LoginModalAuth />
            <LoginModalEtc />
          </motion.div>
        </div>
      )}
    </>
  );
};

export default LoginModal;
