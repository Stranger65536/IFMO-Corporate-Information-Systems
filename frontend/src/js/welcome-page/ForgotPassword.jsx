import React from "react";
import _ from "underscore";
import {emailPattern, iconStyle, usernamePattern, validateForm, ValidTextField, WelcomePageForm} from "../Common.jsx";
import FlatButton from "material-ui/FlatButton";
import RaisedButton from "material-ui/RaisedButton";
import AccountCircle from "material-ui/svg-icons/action/account-circle";

export class ForgotPassword extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            forgotPasswordUsername: props.username
        };

        this.constants = {
            validation: {
                username: {
                    patterns: [usernamePattern, emailPattern],
                    ref: 'username',
                    stateField: 'forgotPasswordUsername',
                    hintText: 'Username / Email',
                    floatingLabelText: 'Username / Email',
                    errorText: 'Username must be at least 6 characters length / Invalid email'
                }
            }
        }
    }

    onResetPasswordTouchTap = () => {
        if (validateForm(this, this.props.validationFunction, this.props.afterGeneralValidation)) {
            alert('reset');
            //TODO reset
        } else {
            const instance = this;
            _.each(
                _.values(instance.constants.validation), function (obj) {
                    return instance.refs[obj.ref].forceInvalidCheck();
                }
            )
        }
    };

    onLoginTouchTap = () => {
        this.props.rootStateUpdater('activeForm', WelcomePageForm.LOGIN);
    };

    onSignUpTouchTap = () => {
        this.props.rootStateUpdater('activeForm', WelcomePageForm.SIGN_UP);
    };

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div>
                <AccountCircle style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    ref={this.constants.validation.username.ref}
                    value={this.state.forgotPasswordUsername}
                    hintText={this.constants.validation.username.hintText}
                    errorText={this.constants.validation.username.errorText}
                    floatingLabelText={this.constants.validation.username.floatingLabelText}
                    patterns={this.constants.validation.username.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, forgotPasswordUsername: value});
                        this.props.rootStateUpdater(this.constants.validation.username.stateField, value)
                    }}
                />
                <RaisedButton
                    label='Reset password'
                    primary={true}
                    onTouchTap={this.onResetPasswordTouchTap}
                    className='login-input button'/>
                <FlatButton
                    label='Login'
                    onTouchTap={this.onLoginTouchTap}
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