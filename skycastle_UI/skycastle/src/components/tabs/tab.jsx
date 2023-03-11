import React from "react";
import PropTypes from "prop-types";


const Tab = ({label, activeTab, onClickTab}) => {
  const onClickTabItem = () => {
    onClickTab(label);
  }

  return (
    <li
      onClick={onClickTabItem}
      className={`tab-list-item ${activeTab === label ? 'list-item-active' : ''}`}
    >
      {label}
    </li>
  );
}

Tab.propTypes = {
  label: PropTypes.string.isRequired,
  activeTab: PropTypes.string.isRequired,
  onClickTab: PropTypes.func.isRequired,
}

export default Tab;