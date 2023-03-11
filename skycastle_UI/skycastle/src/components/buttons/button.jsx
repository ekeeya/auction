import React from "react";


const Button = ({ type, name, value, onClick }) => {
  return (
    <button type={type} name={name} onClick={onClick}>
      {value}
    </button>
  );
}


export default Button;