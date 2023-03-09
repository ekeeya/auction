import React from "react";


const SelectInput = ({ options, value, onChange, styles }) => {
  return (
    <select
      value={value}
      onChange={onChange}
      className={styles}
    >
      {options.map((option, index) => (
        <option key={index} value={option.value}>{option.label}</option>
      ))}
    </select>
  );
}

export default SelectInput;