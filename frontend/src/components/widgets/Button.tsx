import React, { useState } from "react";
import IButton from "../../interfaces/IButton";
import { PinMode } from "../../interfaces/IWidget";

const Button: React.FC<IButton> = ({
  value,
  offLabel,
  onLabel,
  onValue,
  offValue,
}) => {
  const [state, setState] = useState<string | undefined>(value || "0");

  return (
    <div
      className="bg-gray-500 w-40 h-24 flex justify-center items-center"
      onClick={() => setState(state === onValue ? offValue : onValue)}
    >
      <div className="border border-green-200 w-36 h-20 rounded-l-full rounded-r-full flex justify-center items-center select-none">
        {state === onValue ? offLabel : onLabel}
      </div>
    </div>
  );
};

Button.defaultProps = {
  type: "BUTTON",
  onValue: "1",
  offValue: "0",
  onLabel: "ON",
  offLabel: "OFF",
  pinMode: PinMode.output,
  pin: 0,
  value: "0",
};

export default Button;