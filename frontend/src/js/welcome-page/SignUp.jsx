import React from "react";
import _ from "underscore";
import Dialog from "material-ui/Dialog";
import FlatButton from "material-ui/FlatButton";
import RaisedButton from "material-ui/RaisedButton";
import AccountCircle from "material-ui/svg-icons/action/account-circle";
import Email from "material-ui/svg-icons/communication/email";
import Lock from "material-ui/svg-icons/action/lock";
import {
    emailPattern,
    iconStyle,
    namePattern,
    passwordPattern,
    usernamePattern,
    validateForm,
    ValidTextField,
    WelcomePageForm
} from "../Common.jsx";

export class SignUp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            signUpUsername: props.username,
            signUpEmail: props.email,
            signUpPassword: props.password,
            signUpPasswordAgain: props.passwordAgain,
            signUpFirstName: props.firstName,
            signUpLastName: props.lastName,
            signUpMiddleName: props.middleName,
            infoModal: {
                opened: false,
                title: null,
                actions: [],
                content: () => {
                }
            }
        };

        this.constants = {
            validation: {
                username: {
                    patterns: [usernamePattern],
                    ref: 'username',
                    stateField: 'signUpUsername',
                    hintText: 'Username',
                    floatingLabelText: 'Username',
                    errorText: 'Username must be 5-32 characters length and must be not email-like',
                    validationFunction: this.signUpUsernameValidation,
                    afterGeneralValidation: true
                },
                email: {
                    patterns: [emailPattern],
                    ref: 'email',
                    stateField: 'signUpEmail',
                    hintText: 'Email',
                    floatingLabelText: 'Email',
                    errorText: 'Invalid email'
                },
                password: {
                    patterns: [passwordPattern],
                    ref: 'password',
                    stateField: 'signUpPassword',
                    hintText: 'Password',
                    floatingLabelText: 'Password',
                    errorText: 'Password must be 5-32 characters length'
                },
                passwordAgain: {
                    patterns: [],
                    ref: 'passwordAgain',
                    stateField: 'signUpPasswordAgain',
                    hintText: 'Password again',
                    floatingLabelText: 'Password again',
                    errorText: 'Must be equal to the password',
                    validationFunction: this.signUpPasswordAgainValidation,
                    afterGeneralValidation: true
                },
                firstName: {
                    patterns: [namePattern],
                    ref: 'firstName',
                    stateField: 'signUpFirstName',
                    hintText: 'First name',
                    floatingLabelText: 'First name',
                    errorText: 'First name must be up to 35 characters length'
                },
                lastName: {
                    patterns: [namePattern],
                    ref: 'lastName',
                    stateField: 'signUpLastName',
                    hintText: 'Last name',
                    floatingLabelText: 'Last name',
                    errorText: 'Last name must be up to 35 characters length'
                },
                middleName: {
                    patterns: [namePattern],
                    ref: 'middleName',
                    stateField: 'signUpMiddleName',
                    hintText: 'Middle name',
                    floatingLabelText: 'Middle name',
                    errorText: 'Middle name must be up to 35 characters length'
                }
            },
            infoModal: {
                title: 'Registering',
                emailTaken: 'Specified email is already registered!',
                usernameTaken: 'Specified username is already registered!'
            }
        }
    }

    onForgotPasswordTouchTap = () => {
        this.props.rootStateUpdater('activeForm', WelcomePageForm.FORGOT_PASSWORD);
    };

    onLoginTouchTap = () => {
        this.props.rootStateUpdater('activeForm', WelcomePageForm.LOGIN);
    };

    onSignUpTouchTap = () => {
        if (validateForm(this)) {
            this.props.onSignUp();
            //TODO sign up
        } else {
            const instance = this;
            _.each(
                _.values(instance.constants.validation), function (obj) {
                    return instance.refs[obj.ref].forceInvalidCheck();
                }
            )
        }
    };

    signUpUsernameValidation = (value) => {
        return !emailPattern.test(value);
    };

    signUpPasswordAgainValidation = (value) => {
        return this.refs[this.constants.validation.password.ref].state.value === value;
    };

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div>
                <Dialog
                    contentClassName='info-dialog'
                    title={this.state.infoModal.loggingIn}
                    actions={this.state.infoModal.actions}
                    modal={true}
                    open={this.state.infoModal.opened}>
                </Dialog>
                <AccountCircle style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    ref={this.constants.validation.username.ref}
                    value={this.state.signUpUsername}
                    hintText={this.constants.validation.username.hintText}
                    errorText={this.constants.validation.username.errorText}
                    floatingLabelText={this.constants.validation.username.floatingLabelText}
                    patterns={this.constants.validation.username.patterns}
                    validationFunction={this.constants.validation.username.validationFunction}
                    afterGeneralValidation={this.constants.validation.username.afterGeneralValidation}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpUsername: value});
                        this.props.rootStateUpdater(this.constants.validation.username.stateField, value)
                    }}
                />
                <Email style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    value={this.state.signUpEmail}
                    ref={this.constants.validation.email.ref}
                    hintText={this.constants.validation.email.hintText}
                    errorText={this.constants.validation.email.errorText}
                    floatingLabelText={this.constants.validation.email.floatingLabelText}
                    patterns={this.constants.validation.email.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpEmail: value});
                        this.props.rootStateUpdater(this.constants.validation.email.stateField, value)
                    }}
                />
                <Lock style={iconStyle}/>
                <ValidTextField
                    type='password'
                    className='login-input'
                    value={this.state.signUpPassword}
                    ref={this.constants.validation.password.ref}
                    hintText={this.constants.validation.password.hintText}
                    errorText={this.constants.validation.password.errorText}
                    floatingLabelText={this.constants.validation.password.floatingLabelText}
                    patterns={this.constants.validation.password.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpPassword: value});
                        this.props.rootStateUpdater(this.constants.validation.password.stateField, value)
                    }}
                />
                <Lock style={iconStyle}/>
                <ValidTextField
                    type='password'
                    className='login-input'
                    value={this.state.signUpPasswordAgain}
                    ref={this.constants.validation.passwordAgain.ref}
                    hintText={this.constants.validation.passwordAgain.hintText}
                    errorText={this.constants.validation.passwordAgain.errorText}
                    floatingLabelText={this.constants.validation.passwordAgain.floatingLabelText}
                    patterns={this.constants.validation.passwordAgain.patterns}
                    validationFunction={this.signUpPasswordAgainValidation}
                    afterGeneralValidation={true}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpPasswordAgain: value});
                        this.props.rootStateUpdater(this.constants.validation.passwordAgain.stateField, value)
                    }}
                />
                <AccountCircle style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    value={this.state.signUpFirstName}
                    ref={this.constants.validation.firstName.ref}
                    hintText={this.constants.validation.firstName.hintText}
                    errorText={this.constants.validation.firstName.errorText}
                    floatingLabelText={this.constants.validation.firstName.floatingLabelText}
                    patterns={this.constants.validation.firstName.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpFirstName: value});
                        this.props.rootStateUpdater(this.constants.validation.firstName.stateField, value)
                    }}
                />
                <AccountCircle style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    value={this.state.signUpLastName}
                    ref={this.constants.validation.lastName.ref}
                    hintText={this.constants.validation.lastName.hintText}
                    errorText={this.constants.validation.lastName.errorText}
                    floatingLabelText={this.constants.validation.lastName.floatingLabelText}
                    patterns={this.constants.validation.lastName.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpLastName: value});
                        this.props.rootStateUpdater(this.constants.validation.lastName.stateField, value)
                    }}
                />
                <AccountCircle style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    value={this.state.signUpMiddleName}
                    ref={this.constants.validation.middleName.ref}
                    hintText={this.constants.validation.middleName.hintText}
                    errorText={this.constants.validation.middleName.errorText}
                    floatingLabelText={this.constants.validation.middleName.floatingLabelText}
                    patterns={this.constants.validation.middleName.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpMiddleName: value});
                        this.props.rootStateUpdater(this.constants.validation.middleName.stateField, value)
                    }}
                />
                <RaisedButton
                    label='Sign up'
                    primary={true}
                    onTouchTap={this.onSignUpTouchTap}
                    className='login-input button'/>
                <FlatButton
                    label='Login'
                    onTouchTap={this.onLoginTouchTap}
                    className='login-input button small left'
                />
                <FlatButton
                    label='Reset password'
                    onTouchTap={this.onForgotPasswordTouchTap}
                    className='login-input button small right'
                />
            </div>
        )
    }
}