import React, {useState} from "react";
import PropTypes from "prop-types";


// file imports
import Tab from "./tab";


const Tabs = ({ children }) => {
  
  const [activeTab, setActiveTab] = useState(children[0].props.label)

  const onClickTabItem = (tab) => {
    setActiveTab(tab)
  }

  console.log(activeTab)

  return (
    <div className="tabs">
      <ol className="tab-list">
        {children.map((child) => {
          const { label } = child.props;

          return (
            <Tab
              activeTab={activeTab}
              key={label}
              label={label}
              onClickTab={() => onClickTabItem(label)}
            />
          )
        })}
      </ol>
      <div className="tab-content">
        {children.map((child) => {
          if (child.props.label !== activeTab) return undefined;
          return child.props.children;
        })}
      </div>
    </div>
  )
}

Tabs.propTypes = {
  children: PropTypes.instanceOf(Array).isRequired,
}

export default Tabs;