import React from "react";
import _ from "underscore";
import Paper from "material-ui/Paper";
import TextField from "material-ui/TextField";
import FlatButton from "material-ui/FlatButton";
import RaisedButton from "material-ui/RaisedButton";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import {MuiThemeProvider} from "material-ui/styles";

const emcMuiTheme = getMuiTheme({
    palette: {
        primary1Color: '#2c95dd',
    },
});

const Form = {
    LOGIN: 'LOGIN',
    SIGN_UP: 'SIGN_UP',
    FORGOT_PASSWORD: 'FORGOT_PASSWORD',
};

const emailPattern = /^(([^<>()\[\]\\.,;:\s@']+(\.[^<>()\[\]\\.,;:\s@']+)*)|('.+'))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
const usernamePattern = /^[\w!'#$%&'()*+,.\/:;<=>?@\[\] ^_`{|}~-]{6,}$/;
const passwordPattern = /.{6,}/;

function validatePatterns(patterns, value) {
    return _.some(patterns, function (pattern) {
        return pattern.test(value);
    })
}

class ValidTextField extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            errorText: '',
            value: props.value
        }
    }

    onChange(event) {
        this.updateStateWithValue(event.target.value);
    }

    updateStateWithValue(value) {
        if (validatePatterns(this.props.patterns, value)) {
            this.setState({...this.state, errorText: '', value: value});
        } else {
            this.setState({...this.state, errorText: this.props.errorText, value: value});
        }
        this.props.rootStateUpdater(value);
    }

    forceInvalidCheck() {
        this.updateStateWithValue(this.state.value);
    }

    render() {
        return (
            <TextField
                className={this.props.className}
                hintText={this.props.hintText}
                onChange={this.onChange.bind(this)}
                errorText={this.state.errorText}
                type={this.props.type}
                value={this.state.value}
                floatingLabelText={this.props.floatingLabelText}
            />
        )
    }
}

export class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loginUsername: props.username,
            loginPassword: props.password,
        };

        this.constants = {
            validation: {
                username: {
                    patterns: [usernamePattern, emailPattern],
                    ref: 'username',
                    stateField: 'loginUsername',
                    hintText: 'Username / Email',
                    floatingLabelText: 'Username / Email',
                    errorText: 'Username must be at least 6 characters length / Invalid email'
                },
                password: {
                    patterns: [passwordPattern],
                    ref: 'password',
                    stateField: 'loginPassword',
                    hintText: 'Password',
                    floatingLabelText: 'Password',
                    errorText: 'Password must be at least 6 characters length'
                }
            }
        }
    }

    validateForm() {
        const instance = this;

        return _.all(_.values(instance.constants.validation), function (obj) {
            return validatePatterns(obj.patterns, instance.state[obj.stateField]);
        });
    }

    onLoginTouchTap = () => {
        if (this.validateForm()) {
            alert('login');
            //TODO login
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
        this.props.rootStateUpdater('activeForm', Form.FORGOT_PASSWORD);
    };

    onSignUpTouchTap = () => {
        this.props.rootStateUpdater('activeForm', Form.SIGN_UP);
    };

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div>
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

export class SignUp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            signUpEmail: props.email,
            signUpLogin: props.login,
            signUpPassword: props.loginPassword,
            signUpFirstName: props.firstName,
            signUpLastName: props.lastName,
            signUpMiddleName: props.middleName
        };
    }

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div>
                SignUp
            </div>
        )
    }
}

export class ForgotPassword extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            forgotPasswordUsername: props.usrename
        };
    }

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div>
                ForgotPassword
            </div>
        )
    }
}

export default class WelcomePage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loginUsername: '',
            loginPassword: '',
            signUpEmail: '',
            signUpLogin: '',
            signUpPassword: '',
            signUpFirstName: '',
            signUpLastName: '',
            signUpMiddleName: '',
            forgotPasswordUsername: '',
            activeForm: Form.LOGIN
        };

        this.constants = {
            appTitle: 'ReservIO',
            formShadow: 5,
            forms: {
                login: {
                    formIndicator: Form.LOGIN,
                    viewElement: <Login
                        username={this.state.loginUsername}
                        password={this.state.loginPassword}
                        rootStateUpdater={(prop, value) => this.updateStateProperty(prop, value)}/>
                },
                signUp: {
                    formIndicator: Form.SIGN_UP,
                    viewElement: <SignUp
                        email={this.state.signUpEmail}
                        login={this.state.signUpLogin}
                        password={this.state.signUpPassword}
                        firstName={this.state.signUpFirstName}
                        lastName={this.state.signUpLastName}
                        middleName={this.state.signUpMiddleName}
                        rootStateUpdater={(prop, value) => this.updateStateProperty(prop, value)}/>
                },
                forgotPassword: {
                    formIndicator: Form.FORGOT_PASSWORD,
                    viewElement: <ForgotPassword
                        username={this.state.forgotPasswordUsername}
                        rootStateUpdater={(prop, value) => this.updateStateProperty(prop, value)}/>
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
        ).viewElement;
    };

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <MuiThemeProvider muiTheme={emcMuiTheme}>
                <div className='outer'>
                    <div className='middle'>
                        <div className='inner'>
                            <Paper
                                rounded={false}
                                className='login-paper'
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
            </MuiThemeProvider>
        )
    }
}