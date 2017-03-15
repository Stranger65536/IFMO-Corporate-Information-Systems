import React from "react";
import _ from "underscore";
import XRegExp from "xregexp";
import Paper from "material-ui/Paper";
import TextField from "material-ui/TextField";
import FlatButton from "material-ui/FlatButton";
import RaisedButton from "material-ui/RaisedButton";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import AccountCircle from "material-ui/svg-icons/action/account-circle";
import Email from "material-ui/svg-icons/communication/email";
import Lock from "material-ui/svg-icons/action/lock";
import {MuiThemeProvider} from "material-ui/styles";

const emcMuiTheme = getMuiTheme({
    palette: {
        primary1Color: '#2c95dd',
    },
});

const Form = {
    LOGIN: 'login',
    SIGN_UP: 'sign-up',
    FORGOT_PASSWORD: 'forgot-password',
};

const emailPattern = /^(([^<>()\[\]\\.,;:\s@']+(\.[^<>()\[\]\\.,;:\s@']+)*)|('.+'))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
const usernamePattern = XRegExp('^[\\p{L}!\'#$%&()*+,.\/:;<=>?@\\[\\] ^_`{|}~-]{6,32}$');
const passwordPattern = /.{6,32}/;
const namePattern = XRegExp('^[\\p{L} .\'\-]{0,35}$');

const iconStyle = {
    position: 'relative',
    color: '#2c95dd',
    top: '10px',
    width: '30px',
    height: '30px',
    margin: '0 0 0 20px'
};

function validatePatterns(patterns, value) {
    return _.some(patterns, function (pattern) {
        return pattern.test(value);
    })
}

function validateForm(instance) {
    return _.all(_.values(instance.constants.validation), function (obj) {
        return validatePatterns(obj.patterns, instance.state[obj.stateField]);
    });
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

    errorStyle = {
        position: 'absolute',
        bottom: '-10px'
    };

    render() {
        return (
            <TextField
                className={this.props.className}
                hintText={this.props.hintText}
                onChange={this.onChange.bind(this)}
                errorText={this.state.errorText}
                errorStyle={this.errorStyle}
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
                    errorText: 'Username must be 6-32 characters length / Invalid email'
                },
                password: {
                    patterns: [passwordPattern],
                    ref: 'password',
                    stateField: 'loginPassword',
                    hintText: 'Password',
                    floatingLabelText: 'Password',
                    errorText: 'Password must be 6-32 characters length'
                }
            }
        }
    }

    onLoginTouchTap = () => {
        if (validateForm(this)) {
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
            signUpMiddleName: props.middleName
        };

        this.constants = {
            validation: {
                username: {
                    patterns: [usernamePattern],
                    ref: 'username',
                    stateField: 'signUpUsername',
                    hintText: 'Username',
                    floatingLabelText: 'Username',
                    errorText: 'Username must be 6-32 characters length'
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
                    errorText: 'Password must be 6-32 characters length'
                },
                passwordAgain: {
                    patterns: [passwordPattern],
                    ref: 'passwordAgain',
                    stateField: 'signUpPasswordAgain',
                    hintText: 'Password again',
                    floatingLabelText: 'Password again',
                    errorText: 'Password must be 6-32 characters length'
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
            }
        }
    }

    onForgotPasswordTouchTap = () => {
        this.props.rootStateUpdater('activeForm', Form.FORGOT_PASSWORD);
    };

    onLoginTouchTap = () => {
        this.props.rootStateUpdater('activeForm', Form.LOGIN);
    };

    onSignUpTouchTap = () => {
        if (validateForm(this)) {
            alert('sign up');
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

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div>
                <AccountCircle style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    ref={this.constants.validation.username.ref}
                    value={this.state.signUpUsername}
                    hintText={this.constants.validation.username.hintText}
                    errorText={this.constants.validation.username.errorText}
                    floatingLabelText={this.constants.validation.username.floatingLabelText}
                    patterns={this.constants.validation.username.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpUsername: value});
                        this.props.rootStateUpdater(this.constants.validation.username.stateField, value)
                    }}
                />
                <Email style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    ref={this.constants.validation.email.ref}
                    value={this.state.signUpEmail}
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
                    className='login-input'
                    ref={this.constants.validation.password.ref}
                    value={this.state.signUpPassword}
                    type='password'
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
                    className='login-input'
                    ref={this.constants.validation.passwordAgain.ref}
                    value={this.state.signUpPasswordAgain}
                    type='password'
                    hintText={this.constants.validation.passwordAgain.hintText}
                    errorText={this.constants.validation.passwordAgain.errorText}
                    floatingLabelText={this.constants.validation.passwordAgain.floatingLabelText}
                    patterns={this.constants.validation.passwordAgain.patterns}
                    rootStateUpdater={(value) => {
                        this.setState({...this.state, signUpPasswordAgain: value});
                        this.props.rootStateUpdater(this.constants.validation.passwordAgain.stateField, value)
                    }}
                />
                <AccountCircle style={iconStyle}/>
                <ValidTextField
                    className='login-input'
                    ref={this.constants.validation.firstName.ref}
                    value={this.state.signUpFirstName}
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
                    ref={this.constants.validation.lastName.ref}
                    value={this.state.signUpLastName}
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
                    ref={this.constants.validation.middleName.ref}
                    value={this.state.signUpMiddleName}
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
        if (validateForm(this)) {
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
        this.props.rootStateUpdater('activeForm', Form.LOGIN);
    };

    onSignUpTouchTap = () => {
        this.props.rootStateUpdater('activeForm', Form.SIGN_UP);
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
                        username={this.state.signUpUsername}
                        email={this.state.signUpEmail}
                        password={this.state.signUpPassword}
                        passwordAgain={this.state.signUpPasswordAgain}
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
            </MuiThemeProvider>
        )
    }
}