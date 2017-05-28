import React from "react";
import _ from "underscore";
import XRegExp from "xregexp";
import * as $ from "jquery.soap";
import TextField from "material-ui/TextField";
import Dialog from "material-ui/Dialog";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import CircularProgress from "material-ui/CircularProgress";

export const emailPattern = /^(([^<>()\[\]\\.,;:\s@']+(\.[^<>()\[\]\\.,;:\s@']+)*)|('.+'))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
export const usernamePattern = XRegExp('^[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{5,32}$');
export const passwordPattern = /.{5,32}/;
export const namePattern = XRegExp('^[\\p{L} .\'\-]{0,35}$');

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

export class InfoModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            opened: props.opened,
            title: props.title,
            actions: props.actions,
            content: props.content
        }
    }

    render() {
        return (
            <Dialog
                contentClassName='info-dialog'
                title={this.state.title}
                actions={this.state.actions}
                modal={true}
                open={this.state.opened}>
                {this.props.children}
            </Dialog>
        )
    }
}

export function getCookie(n) {
    let a = `; ${document.cookie}`.match(`;\\s*${n}=([^;]+)`);
    return a ? a[1] : '';
}

//noinspection OverlyComplexFunctionJS
export function sendApiRequest(method, data, login, password, beforeSend, success, error) {
    const token = getCookie('XSRF-TOKEN');
    try {
        $.default({
            url: 'https://internal.emc.com:443/reserv-io/ws/api/',
            appendMethodToURL: false,
            method: method,
            namespaceQualifier: 'api',
            namespaceURL: 'https://internal.emc.com/reserv-io/schema/api',
            data: data,
            HTTPHeaders: {
                'Authorization': 'Basic ' + btoa(login + ':' + password),
                'X-XSRF-TOKEN': token
            },
            beforeSend: beforeSend,
            success: success,
            error: error
        });
    } catch (e) {
        console.log(e);
    }
}