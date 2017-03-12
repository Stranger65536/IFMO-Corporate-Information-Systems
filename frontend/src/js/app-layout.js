import React from "react";
import ReactDOM from "react-dom";
import injectTapEventPlugin from "react-tap-event-plugin";
import AppLayout from "./AppLayout.jsx";

injectTapEventPlugin();

ReactDOM.render(<AppLayout/>, document.getElementById('app-layout'));