import React from "react";
import Paper from "material-ui/Paper";
import TextField from "material-ui/TextField";
import FlatButton from 'material-ui/FlatButton';
import RaisedButton from "material-ui/RaisedButton";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import {MuiThemeProvider} from "material-ui/styles";

const emcMuiTheme = getMuiTheme({
    palette: {
        primary1Color: "#2c95dd",
    },
});

export default class Login extends React.Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <MuiThemeProvider muiTheme={emcMuiTheme}>
                <div className="outer">
                    <div className="middle">
                        <div className="inner">
                            <Paper
                                rounded={false}
                                className="login-paper"
                                zDepth={5}>
                                <Paper
                                    rounded={false}
                                    className="login-header"
                                    zDepth={0}>
                                    <div className="login-logo"/>
                                    <div className="login-title">ReservIO</div>
                                </Paper>
                                <TextField
                                    className="login-input"
                                    hintText="Username / Email"
                                    floatingLabelText="Username / Email"
                                />
                                <br/>
                                <TextField
                                    className="login-input"
                                    hintText="Password"
                                    type="password"
                                    floatingLabelText="Password"
                                />
                                <br/>
                                <RaisedButton
                                    label="Login"
                                    primary={true}
                                    className="login-input button"/>
                                <br/>
                                <FlatButton
                                    label="Forgot password"
                                    className="login-input button small left"
                                />
                                <FlatButton
                                    label="Sign up"
                                    className="login-input button small right"
                                />
                            </Paper>
                        </div>
                    </div>
                </div>
            </MuiThemeProvider>
        )
    }
}