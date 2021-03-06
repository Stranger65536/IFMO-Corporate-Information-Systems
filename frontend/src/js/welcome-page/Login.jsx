import React from "react";
import _ from "underscore";
import Dialog from "material-ui/Dialog";
import FlatButton from "material-ui/FlatButton";
import RaisedButton from "material-ui/RaisedButton";
import AccountCircle from "material-ui/svg-icons/action/account-circle";
import Lock from "material-ui/svg-icons/action/lock";
import {
    emailPattern,
    iconStyle,
    passwordPattern,
    ProgressCircle,
    sendApiRequest,
    userByEmailRequest,
    userByUsernameRequest,
    updateStateWithUserInfo,
    usernamePattern,
    validateForm,
    ValidTextField,
    WelcomePageForm
} from "../Common.jsx";

export class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loginUsername: props.username,
            loginPassword: props.password,
            infoModal: {
                opened: false,
                title: null,
                actions: [],
                content: <div/>
            }
        };

        this.constants = {
            validation: {
                username: {
                    patterns: [usernamePattern, emailPattern],
                    ref: 'username',
                    stateField: 'loginUsername',
                    hintText: 'Username / Email',
                    floatingLabelText: 'Username / Email',
                    errorText: 'Username must be 5-32 characters length / Invalid email'
                },
                password: {
                    patterns: [passwordPattern],
                    ref: 'password',
                    stateField: 'loginPassword',
                    hintText: 'Password',
                    floatingLabelText: 'Password',
                    errorText: 'Password must be 5-32 characters length'
                }
            },
            infoModal: {
                loggingIn: 'Logging in',
                invalidLogin: 'Invalid credentials!',
                unknownError: 'Unknown error'
            }
        }
    }

    onLoginTouchTap = () => {
        if (validateForm(this, this.props.validationFunction, this.props.afterGeneralValidation)) {
            this.performLogin(this.state.loginUsername, this.state.loginPassword);
        } else {
            const instance = this;
            _.each(
                _.values(instance.constants.validation), function (obj) {
                    return instance.refs[obj.ref].forceInvalidCheck();
                }
            )
        }
    };

    onForgotPasswordTouchTap = () => {
        this.props.rootStateUpdater('activeForm', WelcomePageForm.FORGOT_PASSWORD);
    };

    onSignUpTouchTap = () => {
        this.props.rootStateUpdater('activeForm', WelcomePageForm.SIGN_UP);
    };

    beforeLogin = () => {
        this.setState({
            ...this.state,
            infoModal: {
                opened: true,
                title: this.constants.infoModal.loggingIn,
                content: <ProgressCircle/>,
                actions: []
            }
        })
    };

    closeModal = () => {
        this.setState({
            ...this.state,
            infoModal: {
                ...this.state.infoModal,
                opened: false,
            }
        })
    };

    showErrorModal = (title) => {
        this.setState({
            ...this.state,
            infoModal: {
                ...this.state.infoModal,
                opened: true,
                title: title,
                content: <div/>,
                actions: [<FlatButton
                    label='Accept'
                    primary={true}
                    disabled={false}
                    onTouchTap={this.closeModal}/>]
            }
        })
    };

    performLogin = (login, password) => {
        sendApiRequest({
            method: 'GetUsersRequest',
            data: userByEmailRequest(login),
            login: login,
            password: password,
            beforeSend: this.beforeLogin,
            success: (soapResponse) => {
                const users = soapResponse.content
                    .getElementsByTagName("Body")[0]
                    .getElementsByTagName("GetUsersResponse")[0]
                    .children;
                if (users.length === 1) {
                    updateStateWithUserInfo(users, password, this.props.onLogin);
                } else {
                    sendApiRequest({
                        method: 'GetUsersRequest',
                        data: userByUsernameRequest(login),
                        login: login,
                        password: password,
                        beforeSend: this.beforeLogin,
                        success: (soapResponse) => {
                            const users = soapResponse.content
                                .getElementsByTagName("Body")[0]
                                .getElementsByTagName("GetUsersResponse")[0]
                                .children;
                            if (users.length === 1) {
                                updateStateWithUserInfo(users, password, this.props.onLogin);
                            } else {
                                //TODO api error
                                this.showErrorModal(this.constants.infoModal.unknownError);
                                console.log('API error', soapResponse)
                            }
                        },
                        error: (soapResponse) => {
                            //TODO another errors handling
                            this.showErrorModal(this.constants.infoModal.invalidLogin);
                        }
                    });
                }
            },
            error: (soapResponse) => {
                //TODO another errors handling
                this.showErrorModal(this.constants.infoModal.invalidLogin);
            }
        });
    };

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div>
                <Dialog
                    contentClassName='info-dialog'
                    title={this.state.infoModal.title}
                    actions={this.state.infoModal.actions}
                    modal={true}
                    open={this.state.infoModal.opened}>
                    {this.state.infoModal.content}
                </Dialog>
                <AccountCircle style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    ref={this.constants.validation.username.ref}
                    value={this.state.loginUsername}
                    hintText={this.constants.validation.username.hintText}
                    errorText={this.constants.validation.username.errorText}
                    floatingLabelText={this.constants.validation.username.floatingLabelText}
                    patterns={this.constants.validation.username.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, loginUsername: value});
                        this.props.rootStateUpdater(this.constants.validation.username.stateField, value)
                    }}
                />
                <Lock style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    ref={this.constants.validation.password.ref}
                    value={this.state.loginPassword}
                    hintText={this.constants.validation.password.hintText}
                    errorText={this.constants.validation.password.errorText}
                    floatingLabelText={this.constants.validation.password.floatingLabelText}
                    type='password'
                    patterns={this.constants.validation.password.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, loginPassword: value});
                        this.props.rootStateUpdater(this.constants.validation.password.stateField, value)
                    }}
                    rootStateProperty='loginPassword'
                />
                <RaisedButton
                    label='Login'
                    primary={true}
                    onTouchTap={this.onLoginTouchTap}
                    className='login-input button'/>
                <FlatButton
                    label='Forgot password'
                    onTouchTap={this.onForgotPasswordTouchTap}
                    className='login-input button small left'
                />
                <FlatButton
                    label='Sign up'
                    onTouchTap={this.onSignUpTouchTap}
                    className='login-input button small right'
                />
            </div>
        )
    }
}