import React from "react";


const Input = ({ type, placeholder, value, onChange, styles }) => {
  return (
    <input
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      className={styles}
    />
  );
}


export default Input;