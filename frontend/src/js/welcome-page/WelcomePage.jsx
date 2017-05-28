import React from "react";
import _ from "underscore";
import Paper from "material-ui/Paper";
import {WelcomePageForm, wsdlUrls} from "../Common.jsx";
import {Login} from "./Login.jsx";
import {SignUp} from "./SignUp.jsx";
import {ForgotPassword} from "./ForgotPassword.jsx";

//TODO timeout for components rendering according to the transform duration
//TODO Modals with information about wrong login, etc.
//TODO Perform main operation on Enter press
//TODO Short timeout between button tap and execution to watch animation
//TODO progressbar on requests

export default class WelcomePage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loginUsername: '',
            loginPassword: '',
            signUpUsername: '',
            signUpEmail: '',
            signUpPassword: '',
            signUpPasswordAgain: '',
            signUpAgain: '',
            signUpFirstName: '',
            signUpLastName: '',
            signUpMiddleName: '',
            forgotPasswordUsername: '',
            activeForm: WelcomePageForm.LOGIN
        };

        this.constants = {
            appTitle: 'ReservIO',
            formShadow: 5,
            forms: {
                login: {
                    formIndicator: WelcomePageForm.LOGIN,
                    viewElement: () => {
                        return <Login
                            onLogin={this.props.onLogin}
                            username={this.state.loginUsername}
                            password={this.state.loginPassword}
                            rootStateUpdater={(prop, value) => this.updateStateProperty(prop, value)}/>
                    }
                },
                signUp: {
                    formIndicator: WelcomePageForm.SIGN_UP,
                    viewElement: () => {
                        return <SignUp
                            onLogin={this.props.onLogin}
                            username={this.state.signUpUsername}
                            email={this.state.signUpEmail}
                            password={this.state.signUpPassword}
                            passwordAgain={this.state.signUpPasswordAgain}
                            firstName={this.state.signUpFirstName}
                            lastName={this.state.signUpLastName}
                            middleName={this.state.signUpMiddleName}
                            rootStateUpdater={(prop, value) => this.updateStateProperty(prop, value)}/>
                    }
                },
                forgotPassword: {
                    formIndicator: WelcomePageForm.FORGOT_PASSWORD,
                    viewElement: () => {
                        return <ForgotPassword
                            username={this.state.forgotPasswordUsername}
                            rootStateUpdater={(prop, value) => this.updateStateProperty(prop, value)}/>
                    }
                }
            }
        };
    }

    updateStateProperty(property, value) {
        this.state[property] = value;
        this.setState(this.state);
    }

    getCurrentViewElement = () => {
        const instance = this;

        return _.findWhere(
            _.map(
                _.keys(this.constants.forms), function (property) {
                    return instance.constants.forms[property];
                }
            ), {formIndicator: instance.state.activeForm}
        ).viewElement();
    };

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div className='outer'>
                <div className='middle'>
                    <div className='inner'>
                        <Paper
                            rounded={false}
                            className={'login-paper ' + this.state.activeForm}
                            zDepth={this.constants.formShadow}>
                            <Paper
                                rounded={false}
                                className='login-header'
                                zDepth={0}>
                                <div className='login-logo'/>
                                <div className='login-title'>{this.constants.appTitle}</div>
                            </Paper>
                            {this.getCurrentViewElement()}
                        </Paper>
                    </div>
                </div>
            </div>
        )
    }
}