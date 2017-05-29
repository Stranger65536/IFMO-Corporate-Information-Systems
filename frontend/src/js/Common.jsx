import React from "react";
import _ from "underscore";
import XRegExp from "xregexp";
import * as $ from "./jsoap.js";
import TextField from "material-ui/TextField";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import CircularProgress from "material-ui/CircularProgress";

export const emailPattern = /^(([^<>()\[\]\\.,;:\s@']+(\.[^<>()\[\]\\.,;:\s@']+)*)|('.+'))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
export const usernamePattern = XRegExp('^[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{5,32}$');
export const passwordPattern = /.{5,32}/;
export const namePattern = XRegExp('^[\\p{L} .\'\-]{0,35}$');
export const resourceNamePattern = XRegExp('^[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{1,25}$');
export const resourceLocationPattern = XRegExp('^[\\p{L} .\'\-]{0,35}$');

export const apiEndpoint = 'https://internal.emc.com/reserv-io/ws/api.wsdl';
export const registerEndpoint = 'https://internal.emc.com/reserv-io/ws/register.wsdl';
export const reportEndpoint = 'https://internal.emc.com/reserv-io/ws/report.wsdl';

export const emcMuiTheme = getMuiTheme({
    palette: {
        primary1Color: '#2c95dd'
    },
    datePicker: {
        selectColor: '#2c95dd'
    },
    timePicker: {
        color: '#2c95dd',
        accentColor: '#2c95dd',
        headerColor: '#2c95dd',
    },
});

export const userByEmailRequest = (login) => {
    return {
        page: 1,
        pageSize: 1,
        searchField: 'email',
        searchType: 'equals',
        searchValue: login
    }
};

export const userByUsernameRequest = (login) => {
    return {
        page: 1,
        pageSize: 1,
        searchField: 'username',
        searchType: 'equals',
        searchValue: login
    }
};

export const iconStyle = {
    position: 'relative',
    color: '#2c95dd',
    top: '10px',
    width: '30px',
    height: '30px',
    margin: '0 0 0 20px'
};

export const WelcomePageForm = {
    LOGIN: 'login',
    SIGN_UP: 'sign-up',
    FORGOT_PASSWORD: 'forgot-password',
};

export const ReservationStatuses = {
    APPROVED: 'APPROVED',
    CANCELED: 'CANCELED',
    WAITING_FOR_APPROVAL: 'WAITING_FOR_APPROVAL',
    NEW_TIME_PROPOSED: 'NEW_TIME_PROPOSED'
};

export const ReservationTypes = {
    REGULAR: 'REGULAR',
    UNAVAILABLE: 'UNAVAILABLE'
};

export const UserRoles = {
    USER: 'USER',
    MODERATOR: 'MODERATOR',
    ADMIN: 'ADMIN'
};

export const UserRolesPriorities = {
    'USER': 0,
    'MODERATOR': 1,
    'ADMIN': 2
};

export const ProgressCircle = () => {
    return <CircularProgress/>
};

function validatePatterns(patterns, value) {
    return !(patterns && patterns.length > 0) || _.some(patterns, function (pattern) {
            return pattern.test(value);
        })
}

export function validateForm(instance) {
    return _.all(_.values(instance.constants.validation), function (obj) {
        return validateField(obj.patterns, instance.state[obj.stateField],
            obj.validationFunction, obj.afterGeneralValidation);
    });
}

//noinspection FunctionWithMultipleReturnPointsJS
function validateField(patterns, value, customValidationFun, afterGeneralValidation) {
    if (customValidationFun === undefined) {
        return validatePatterns(patterns, value);
    } else {
        return afterGeneralValidation
            ? validatePatterns(patterns, value) && customValidationFun(value)
            : customValidationFun(value);
    }
}

export class ValidTextField extends React.Component {
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
        if (validateField(this.props.patterns, value,
                this.props.validationFunction,
                this.props.afterGeneralValidation)) {
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

export function sendApiRequest(options) {
    try {
        $.default({
            url: 'https://internal.emc.com:443/reserv-io/ws/api/',
            appendMethodToURL: false,
            method: options.method,
            namespaceQualifier: 'api',
            namespaceURL: 'https://internal.emc.com/reserv-io/schema/api',
            data: options.data,
            HTTPHeaders: {
                'Authorization': 'Basic ' + btoa(options.login + ':' + options.password)
            },
            beforeSend: options.beforeSend,
            success: options.success,
            error: options.error
        });
    } catch (e) {
        console.log(e);
    }
}

export function sendRegistrationRequest(options) {
    try {
        $.default({
            url: 'https://internal.emc.com:443/reserv-io/ws/register',
            appendMethodToURL: false,
            method: options.method,
            namespaceQualifier: 'api',
            namespaceURL: 'https://internal.emc.com/reserv-io/schema/register',
            data: options.data,
            beforeSend: options.beforeSend,
            success: options.success,
            error: options.error
        });
    } catch (e) {
        console.log(e);
    }
}

export function updateStateWithUserInfo(users, password, callback) {
    callback({
        id: Number(users[0].getElementsByTagName('id')[0].textContent),
        email: users[0].getElementsByTagName('email')[0].textContent,
        username: users[0].getElementsByTagName('username')[0].textContent,
        firstname: users[0].getElementsByTagName('firstname')[0] ? users[0].getElementsByTagName('firstname')[0].textContent : undefined,
        lastName: users[0].getElementsByTagName('lastName')[0] ? users[0].getElementsByTagName('lastName')[0].textContent : undefined,
        middleName: users[0].getElementsByTagName('middleName')[0] ? users[0].getElementsByTagName('middleName')[0].textContent : undefined,
        role: users[0].getElementsByTagName('role')[0].textContent,
        password: password
    })
}