import React from "react";
import TextField from "material-ui/TextField";
import RaisedButton from "material-ui/RaisedButton";
import Dialog from "material-ui/Dialog";
import FlatButton from "material-ui/FlatButton";
import {MuiThemeProvider} from "material-ui/styles";
import {ProgressCircle, sendReportRequest} from "./Common.jsx";

export class Reports extends React.Component {
    constructor(props) {
        super(props);

        this.constants = {
            userId: {
                hintText: 'User ID'
            },
            resourceId: {
                hintText: 'Resource ID'
            },
            reservations: {
                hintText: 'Reservation number'
            },
            modal: {
                types: {
                    LOADING: 'loading',
                    MESSAGE: 'message',
                },
                loading: {
                    title: 'Loading',
                    className: 'info-dialog'
                },
                message: {
                    className: 'info-dialog',
                    unknownError: 'Unknown error',
                }
            }
        };

        this.state = {
            userId: '',
            resourceId: '',
            reservations: '',
            modal: {
                opened: false,
                type: this.constants.modal.types.LOADING,
                loading: {
                    title: this.constants.modal.loading.title,
                    className: this.constants.modal.loading.className,
                },
                message: {
                    title: '',
                    className: this.constants.modal.message.className,
                }
            },
        };
    }

    errorStyle = {
        position: 'absolute',
        bottom: '-10px'
    };

    showErrorModal = (title) => {
        this.setState({
            ...this.state,
            modal: {
                ...this.state.modal,
                opened: true,
                type: this.constants.modal.types.MESSAGE,
                message: {
                    ...this.state.modal.message,
                    title: title
                }
            }
        })
    };

    showLoadingModal = (event) => {
        this.setState({
            ...this.state, modal: {
                ...this.state.modal,
                opened: true,
                type: this.constants.modal.types.LOADING
            }
        });
    };

    onModalClose = () => {
        this.setState({
            ...this.state, modal: {
                ...this.state.modal,
                opened: false
            }
        });
    };

    getReport = () => {
        sendReportRequest({
            method: 'GetReservationsReportRequest',
            data: {
                userId: this.state.userId,
                resourceId: this.state.resourceId
            },
            login: this.props.user.username,
            password: this.props.user.password,
            beforeSend: this.showLoadingModal,
            success: (soapResponse) => {
                const metric = soapResponse.content
                    .getElementsByTagName("Body")[0]
                    .getElementsByTagName("GetReservationsReportResponse")[0]
                    .children[0];
                const result = Number(metric.textContent);
                this.setState({...this.state, reservations: result});
                this.onModalClose();
            },
            error: (soapResponse) => {
                //TODO another errors handling
                const description = soapResponse.content
                    .getElementsByTagName("Body")[0]
                    .getElementsByTagName("Fault")[0]
                    .getElementsByTagName("detail")[0]
                    .getElementsByTagName("description")[0]
                    .textContent;
                this.showErrorModal(description);
            }
        });
    };

    render() {
        const getDialog = () => {
            const type = this.state.modal.type;
            let title, actions, content, className;
            switch (type) {
                case this.constants.modal.types.MESSAGE:
                    title = this.state.modal.message.title;
                    actions = [<FlatButton
                        label='Accept'
                        primary={true}
                        disabled={false}
                        onTouchTap={this.onModalClose}/>];
                    content = <div/>;
                    className = this.constants.modal.message.className;
                    break;
                case this.constants.modal.types.LOADING:
                    title = this.state.modal[type].title;
                    actions = [];
                    content = <ProgressCircle/>;
                    className = this.constants.modal.loading.className;
                    break;
                default:
                    throw new Error('ERROR');
            }
            return <Dialog
                className={className}
                title={title}
                actions={actions}
                modal={true}
                open={this.state.modal.opened}>
                {content}
            </Dialog>
        };

        return <div>
            <TextField
                style={{margin: '20px'}}
                hintText={this.constants.userId.hintText}
                onChange={(event) => {
                    this.setState({...this.state, userId: event.target.value})
                }}
                errorText={this.state.errorText}
                errorStyle={this.errorStyle}
                value={this.state.userId}
                floatingLabelText={this.constants.userId.hintText}/>
            <TextField
                style={{margin: '20px'}}
                hintText={this.constants.resourceId.hintText}
                onChange={(event) => {
                    this.setState({...this.state, resourceId: event.target.value})
                }}
                errorText={this.state.errorText}
                errorStyle={this.errorStyle}
                value={this.state.resourceId}
                floatingLabelText={this.constants.resourceId.hintText}/>
            <TextField
                style={{margin: '20px'}}
                hintText={this.constants.reservations.hintText}
                errorText={this.state.errorText}
                errorStyle={this.errorStyle}
                disabled={true}
                value={this.state.reservations}
                floatingLabelText={this.constants.reservations.hintText}/>
            <RaisedButton
                style={{margin: '20px'}}
                label='Get reservations number'
                primary={true}
                onTouchTap={this.getReport}/>
            {getDialog()}
        </div>

    }
}