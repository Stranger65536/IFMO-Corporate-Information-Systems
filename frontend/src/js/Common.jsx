import React from "react";
import _ from "underscore";
import XRegExp from "xregexp";
import TextField from "material-ui/TextField";
import getMuiTheme from "material-ui/styles/getMuiTheme";

export const emailPattern = /^(([^<>()\[\]\\.,;:\s@']+(\.[^<>()\[\]\\.,;:\s@']+)*)|('.+'))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
export const usernamePattern = XRegExp('^[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{5,32}$');
export const passwordPattern = /.{5,32}/;
export const namePattern = XRegExp('^[\\p{L} .\'\-]{0,35}$');

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