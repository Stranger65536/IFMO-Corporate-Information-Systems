import React from "react";
import _ from "underscore";
import DataTables from "material-ui-datatables";
import ContentAdd from "material-ui/svg-icons/content/add";
import Dialog from "material-ui/Dialog";
import FlatButton from "material-ui/FlatButton";
import DatePicker from "material-ui/DatePicker";
import TimePicker from "material-ui/TimePicker";
import DropDownMenu from "material-ui/DropDownMenu";
import MenuItem from "material-ui/MenuItem";
import FloatingActionButton from "material-ui/FloatingActionButton";
import {MuiThemeProvider} from "material-ui/styles";
import {ProgressCircle, ReservationTypes, sendApiRequest} from "./Common.jsx";

//TODO converting WS calls to convenient state
//TODO modals for API errors
//TODO sort / pagination / filter function calls
//TODO progressbar on requests

//TODO dynamic buttons according to status and role
//TODO propose new time form
//TODO cancel modal
//TODO update form
//TODO Modal for delete
//TODO modal for group approve
//TODO create modal validation

export class Appointments extends React.Component {
    constructor(props) {
        super(props);

        this.constants = {
            title: 'Appointments',
            filterHint: 'Search ("Field [equals, contains, less, greater, lessEqual, greaterEqual] value" to search over specific column)',
            pageSizes: [10, 25, 50, 100],
            columns: [
                {
                    key: 'id',
                    sortable: true,
                    label: 'Reservation ID',
                    className: 'id-column'
                },
                {
                    key: 'resource',
                    sortable: true,
                    label: 'Resource',
                    className: 'resource-column'
                },
                {
                    key: 'type',
                    sortable: true,
                    label: 'Type',
                    className: 'type-column'
                },
                {
                    key: 'status',
                    sortable: true,
                    label: 'Status',
                    className: 'status-column'
                },
                {
                    key: 'owner',
                    sortable: true,
                    label: 'Owner',
                    className: 'owner-column'
                },
                {
                    key: 'startsAt',
                    sortable: true,
                    label: 'Start',
                    className: 'datetime-column'
                },
                {
                    key: 'endsAt',
                    sortable: true,
                    label: 'End',
                    className: 'datetime-column'
                },
                {
                    key: 'createdAt',
                    sortable: true,
                    label: 'Created at',
                    className: 'datetime-column'
                },
                {
                    key: 'updatedAt',
                    sortable: true,
                    label: 'Updated at',
                    className: 'datetime-column'
                }
            ],
            modal: {
                types: {
                    LOADING: 'loading',
                    CREATE: 'create',
                    ACTIONS_LIST: 'actionsList',
                    MESSAGE: 'message',
                },
                loading: {
                    title: 'Loading',
                    className: 'info-dialog'
                },
                create: {
                    title: 'Create appointment',
                    className: ''
                },
                message: {
                    className: 'info-dialog',
                    accessDeniedTitle: 'Access denied',
                    unknownError: 'Unknown error',
                    invalidStateTitle: 'Reservation has been changed',
                    overlapsTitle: 'Can\'t do action',
                },
                actionsList: {
                    title: 'Actions',
                    className: '',
                    columns: [
                        {
                            key: 'id',
                            label: 'Action ID',
                            className: 'id-column'
                        },
                        {
                            key: 'resource',
                            label: 'Resource',
                            className: 'resource-column'
                        },
                        {
                            key: 'type',
                            label: 'Type',
                            className: 'type-column'
                        },
                        {
                            key: 'status',
                            label: 'Status',
                            className: 'status-column'
                        },
                        {
                            key: 'updatedBy',
                            label: 'User',
                            className: 'owner-column'
                        },
                        {
                            key: 'startsAt',
                            label: 'Start',
                            className: 'datetime-column'
                        },
                        {
                            key: 'endsAt',
                            label: 'End',
                            className: 'datetime-column'
                        },
                        {
                            key: 'time',
                            label: 'Time',
                            className: 'datetime-column'
                        }
                    ]
                },
            }
        };

        this.state = {
            searchField: '',
            searchType: '',
            searchValue: '',
            sortingField: '',
            sortingOrder: '',
            page: 1,
            pageSize: 10,

            modal: {
                opened: false,
                type: this.constants.modal.types.LOADING,
                loading: {
                    title: this.constants.modal.loading.title,
                    className: this.constants.modal.loading.className,
                },
                create: {
                    title: this.constants.modal.create.title,
                    className: this.constants.modal.create.className,
                    resource: '',
                    startsAt: '',
                    endsAt: '',
                    type: ReservationTypes.REGULAR
                },
                actionsList: {
                    title: this.constants.modal.actionsList.title,
                    className: this.constants.modal.actionsList.className,
                    actionsTableData: [],
                    actionsPage: 1,
                    actionsRowsNumber: Infinity,
                    actionsRowsPerPage: 10,
                },
                message: {
                    title: '',
                    className: this.constants.modal.message.className,
                }
            },

            users: {},
            resources: {},
            originalReservations: [],
            reservations: [],
        };
    }

    onFilterValueChange = (value) => {
        console.log('Filter value changed!');
    };

    onNextPageClick = (event) => {
        this.state.page++;
        this.setState(this.state);
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    };

    onPreviousPageClick = (event) => {
        this.state.page--;
        this.setState(this.state);
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    };

    onRowSizeChange = (value) => {
        this.state.pageSize = this.constants.infoModal.pageSizes[value];
        this.setState(this.state);
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    };

    onModalNextPageClick = (event) => {
        console.log('Modal next page clicked!');
    };

    onModalPreviousPageClick = (event) => {
        console.log('Modal previous page clicked!');
    };

    onModalRowSizeChange = (value) => {
        console.log('Modal row size changed!');
    };

    onRowSelection = (rowNumber) => {
        console.log('Row selected!');
    };

    onSortOrderChange = (key, order) => {
        this.state.sortingField = key;
        this.state.sortingOrder = order;
        this.setState(this.state);
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    };

    datetimePickerInputStyle = {
        width: '100%'
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

    onAddButtonTouchTap = () => {
        this.setState({
            ...this.state, modal: {
                ...this.state.modal,
                opened: true,
                type: this.constants.modal.types.CREATE
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

    performSearch = (options) => {
        sendApiRequest({
            method: 'GetReservationsRequest',
            data: {
                page: options.page,
                pageSize: options.pageSize,
                searchField: options.searchField,
                searchType: options.searchType,
                searchValue: options.searchValue,
                searchValueLowerBound: options.searchValueLowerBound,
                searchValueUpperBound: options.searchValueUpperBound,
                sortingOrder: options.sortingOrder,
                sortingField: options.sortingField
            },
            login: this.props.user.username,
            password: this.props.user.password,
            beforeSend: this.showLoadingModal,
            success: (soapResponse) => {
                const users = soapResponse.content
                    .getElementsByTagName("Body")[0]
                    .getElementsByTagName("GetReservationsResponse")[0]
                    .children;
                const originalReservations = _.map(users, (reservation) => {
                    return {
                        id: Number(reservation.getElementsByTagName('id')[0].textContent),
                        ownerId: Number(reservation.getElementsByTagName('ownerId')[0].textContent),
                        lastActionUserId: Number(reservation.getElementsByTagName('lastActionUserId')[0].textContent),
                        resourceId: Number(reservation.getElementsByTagName('resourceId')[0].textContent),
                        type: reservation.getElementsByTagName('type')[0].textContent,
                        status: reservation.getElementsByTagName('status')[0].textContent,
                        startsAt: new Date(reservation.getElementsByTagName('startsAt')[0].textContent),
                        endsAt: new Date(reservation.getElementsByTagName('endsAt')[0].textContent),
                        createdAt: new Date(reservation.getElementsByTagName('createdAt')[0].textContent),
                        updatedAt: new Date(reservation.getElementsByTagName('updatedAt')[0].textContent),
                    }
                });
                const reservations = _.map(originalReservations, (reserv) => {
                    const resourceId = reserv.resourceId;
                    const ownerId = reserv.ownerId;
                    if (!(ownerId in this.state.users)) {
                        sendApiRequest({
                            method: 'GetUserRequest',
                            data: {
                                id: ownerId,
                            },
                            async: false,
                            login: this.props.user.username,
                            password: this.props.user.password,
                            success: (soapResponse) => {
                                const user = soapResponse.content
                                    .getElementsByTagName("Body")[0]
                                    .getElementsByTagName("GetUserResponse")[0]
                                    .children[0];
                                this.state.users[ownerId] = {
                                    id: Number(user.getElementsByTagName('id')[0].textContent),
                                    username: user.getElementsByTagName('username')[0].textContent,
                                    email: user.getElementsByTagName('email')[0].textContent,
                                    firstName: user.getElementsByTagName('firstName')[0] ? user.getElementsByTagName('firstName')[0].textContent : '',
                                    lastName: user.getElementsByTagName('lastName')[0] ? user.getElementsByTagName('lastName')[0].textContent : '',
                                    middleName: user.getElementsByTagName('middleName')[0] ? user.getElementsByTagName('middleName')[0].textContent : '',
                                    role: user.getElementsByTagName('role')[0] ? user.getElementsByTagName('role')[0].textContent : '',
                                };
                            },
                            error: (soapResponse) => {
                                //TODO another errors handling
                                this.showErrorModal(this.constants.modal.unknownError);
                            }
                        });
                    }
                    if (!(resourceId in this.state.resources)) {
                        sendApiRequest({
                            method: 'GetResourceRequest',
                            data: {
                                id: resourceId,
                            },
                            async: false,
                            login: this.props.user.username,
                            password: this.props.user.password,
                            success: (soapResponse) => {
                                const resource = soapResponse.content
                                    .getElementsByTagName("Body")[0]
                                    .getElementsByTagName("GetResourceResponse")[0]
                                    .children[0];
                                this.state.resources[resourceId] = {
                                    id: Number(resource.getElementsByTagName('id')[0].textContent),
                                    name: resource.getElementsByTagName('name')[0].textContent,
                                    location: resource.getElementsByTagName('location')[0] ? resource.getElementsByTagName('location')[0].textContent : '',
                                };
                            },
                            error: (soapResponse) => {
                                //TODO another errors handling
                                this.showErrorModal(this.constants.modal.unknownError);
                            }
                        });
                    }
                    return {
                        id: reserv.id,
                        resource: this.state.resources[reserv.resourceId].name + ' / ' + this.state.resources[reserv.resourceId].location,
                        type: reserv.type,
                        status: reserv.status,
                        owner: this.state.users[reserv.ownerId].username,
                        startsAt: reserv.startsAt.toISOString(),
                        endsAt: reserv.endsAt.toISOString(),
                        createdAt: reserv.createdAt.toISOString(),
                        updatedAt: reserv.updatedAt.toISOString()
                    }
                });
                this.setState({
                    ...this.state,
                    originalReservations: originalReservations,
                    reservations: reservations,
                    users: this.state.users,
                    resources: this.state.resources
                });
                this.onModalClose();
            },
            error: (soapResponse) => {
                //TODO another errors handling
                this.showErrorModal(this.constants.modal.unknownError);
            }
        });
    };

    componentDidMount() {
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            searchValueLowerBound: undefined,
            searchValueUpperBound: undefined,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    }

    //noinspection JSMethodCanBeStatic
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
                case this.constants.modal.types.ACTIONS_LIST:
                    title = this.state.modal[type].title;
                    actions = [
                        <FlatButton
                            label='Accept'
                            primary={true}
                            disabled={true}/>,
                        <FlatButton
                            label='Propose new time'
                            primary={true}
                            disabled={true}/>,
                        <FlatButton
                            label='Cancel'
                            primary={true}
                            disabled={true}/>,
                        <FlatButton
                            label='Update'
                            primary={true}
                            disabled={true}/>,
                        <FlatButton
                            label='Close'
                            primary={true}
                            onTouchTap={this.onModalClose}/>,
                    ];
                    content = <DataTables
                        height={'auto'}
                        showRowHover={true}
                        columns={this.constants.modal.columns}
                        data={this.state.modal.actionsList.actionsTableData}
                        onNextPageClick={this.onModalNextPageClick}
                        onPreviousPageClick={this.onModalPreviousPageClick}
                        onRowSizeChange={this.onModalRowSizeChange}
                        page={this.state.modal.actionsList.actionsPage}
                        count={this.state.modal.actionsList.actionsRowsNumber}
                        rowSize={this.state.modal.actionsList.actionsRowsPerPage}
                        rowSizeList={this.constants.pageSizes}
                    />;
                    className = this.constants.modal.actionsList.className;
                    break;
                case this.constants.modal.types.CREATE:
                    title = this.state.modal[type].title;
                    actions = [
                        <FlatButton
                            label='Create'
                            primary={true}
                            disabled={true}/>,
                        <FlatButton
                            label='Close'
                            primary={true}
                            onTouchTap={this.onModalClose}/>,
                    ];
                    content = <div>
                        {/*<AutoComplete*/}
                        {/*hintText='Resource'*/}
                        {/*fullWidth={true}*/}
                        {/*filter={AutoComplete.fuzzyFilter}*/}
                        {/*dataSource={_.map(_.values(this.state.resources), (e) => {*/}
                        {/*return e.name;*/}
                        {/*})}*/}
                        {/*maxSearchResults={5}*/}
                        {/*/>*/}
                        <DatePicker
                            hintText='Start date'
                            fullWidth={true}
                            textFieldStyle={this.datetimePickerInputStyle}/>
                        <TimePicker
                            format='24hr'
                            fullWidth={true}
                            hintText='Start time'
                            textFieldStyle={this.datetimePickerInputStyle}/>
                        <DatePicker
                            hintText='End date'
                            fullWidth={true}
                            textFieldStyle={this.datetimePickerInputStyle}/>
                        <TimePicker
                            format='24hr'
                            fullWidth={true}
                            hintText='End time'
                            textFieldStyle={this.datetimePickerInputStyle}/>
                        <DropDownMenu
                            value={this.state.modal.create.type}
                            onChange={() => {
                            }}>
                            <MenuItem
                                value={ReservationTypes.REGULAR}
                                primaryText={ReservationTypes.REGULAR}/>
                            <MenuItem
                                value={ReservationTypes.UNAVAILABLE}
                                primaryText={ReservationTypes.UNAVAILABLE}/>
                        </DropDownMenu>
                    </div>;
                    className = this.constants.modal.create.className;
                    break;
                default:
                    throw new Error('ERROR');
            }
            return <Dialog
                contentClassName={className}
                title={title}
                actions={this.state.modal.actions}
                modal={true}
                open={this.state.modal.opened}>
                {this.state.modal.content}
            </Dialog>
        };

        return (
            <div>
                <div className='content-table-wrapper'>
                    <div className='content-table'>
                        <DataTables
                            height={'auto'}
                            enableSelectAll={false}
                            filterHintText={this.constants.filterHint}
                            title={this.constants.title}
                            showHeaderToolbar={true}
                            selectable={true}
                            multiSelectable={false}
                            showRowHover={true}
                            columns={this.constants.columns}
                            data={this.state.reservations}
                            showCheckboxes={false}
                            onFilterValueChange={this.onFilterValueChange}
                            onNextPageClick={this.onNextPageClick}
                            onPreviousPageClick={this.onPreviousPageClick}
                            onRowSelection={this.onRowSelection}
                            onRowSizeChange={this.onRowSizeChange}
                            onSortOrderChange={this.onSortOrderChange}
                            page={this.state.page}
                            count={Infinity}
                            rowSize={this.state.pageSize}
                            rowSizeList={this.constants.pageSizes}
                        />
                    </div>
                </div>
                <FloatingActionButton
                    className='first-button'
                    onTouchTap={this.onAddButtonTouchTap}>
                    <ContentAdd />
                </FloatingActionButton>
                {getDialog()}
            </div>
        )
    }
}