import React from "react";
import _ from "underscore";
import DataTables from "material-ui-datatables";
import ContentAdd from "material-ui/svg-icons/content/add";
import Dialog from "material-ui/Dialog";
import FlatButton from "material-ui/FlatButton";
import ActionDeleteForever from "material-ui/svg-icons/action/delete-forever";
import ActionDone from "material-ui/svg-icons/action/done";
import AutoComplete from "material-ui/AutoComplete";
import DatePicker from "material-ui/DatePicker";
import TimePicker from "material-ui/TimePicker";
import DropDownMenu from "material-ui/DropDownMenu";
import MenuItem from "material-ui/MenuItem";
import FloatingActionButton from "material-ui/FloatingActionButton";
import {MuiThemeProvider} from "material-ui/styles";
import {ReservationStatuses, ReservationTypes, UserRoles} from "./Common.jsx";

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

        this.clients = props.clients;

        this.state = {
            searchFilter: null,
            sortField: null,
            sortOrder: null,
            selectedRows: [],
            page: 1,
            rowsPerPage: 10,
            rowsNumber: 5,
            timeZone: 'Z',

            actionsModal: {
                opened: false,
                actionsTableData: null,
                actionsPage: null,
                actionsRowsNumber: null,
                actionsRowsPerPage: null
            },

            createModal: {
                opened: false,
                resource: '',
                startsAt: '',
                endsAt: '',
                type: ReservationTypes.REGULAR
            },

            users: {
                1: {
                    id: 1,
                    username: 'sergeevm',
                    firstName: 'Marina',
                    lastName: 'Sergeeva',
                    middleName: '',
                    role: UserRoles.MODERATOR
                },
                2: {
                    id: 2,
                    username: 'trofiv',
                    firstName: 'Vlad',
                    lastName: 'Trofimov',
                    middleName: '',
                    role: UserRoles.ADMIN
                },
                3: {
                    id: 3,
                    username: 'glaznevm',
                    firstName: 'Mark',
                    lastName: 'Glaznev',
                    middleName: '',
                    role: UserRoles.USER
                },
                4: {
                    id: 4,
                    username: 'vinograp',
                    firstName: 'Pavel',
                    lastName: 'Vinogradov',
                    middleName: '',
                    role: UserRoles.USER
                }
            },
            resources: {
                1: {
                    id: 1,
                    name: 'Ping-Pong table',
                    location: 'SPb Development Center LLC'
                },
                2: {
                    id: 2,
                    name: 'Kicker table',
                    location: 'SPb Development Center LLC'
                }
            }
        };
        this.state = {
            ...this.state,
            data: {
                1: {
                    actions: [
                        {
                            id: 1,
                            startsAt: '2017-02-22 17:00',
                            endsAt: '2017-02-22 18:00',
                            time: '2017-02-21 15:00',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.WAITING_FOR_APPROVAL,
                            updatedBy: this.state.users[1],
                            resource: this.state.resources[1]
                        },
                        {
                            id: 2,
                            startsAt: '2017-02-22 17:00',
                            endsAt: '2017-02-22 18:00',
                            time: '2017-02-21 15:17',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.APPROVED,
                            updatedBy: this.state.users[1],
                            resource: this.state.resources[1],
                        }
                    ],
                    owner: this.state.users[1]
                },
                2: {
                    actions: [
                        {
                            id: 3,
                            startsAt: '2017-02-22 17:00',
                            endsAt: '2017-02-22 18:00',
                            time: '2017-02-22 16:00',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.WAITING_FOR_APPROVAL,
                            updatedBy: this.state.users[2],
                            resource: this.state.resources[2]
                        }
                    ],
                    owner: this.state.users[2]
                },
                3: {
                    actions: [
                        {
                            id: 4,
                            startsAt: '2017-02-22 17:00',
                            endsAt: '2017-02-22 18:00',
                            time: '2017-02-21 15:05',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.WAITING_FOR_APPROVAL,
                            updatedBy: this.state.users[3],
                            resource: this.state.resources[1],
                        },
                        {
                            id: 5,
                            startsAt: '2017-02-22 17:00',
                            endsAt: '2017-02-22 18:00',
                            time: '2017-02-21 15:20',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.CANCELED,
                            updatedBy: this.state.users[3],
                            resource: this.state.resources[1],
                        }
                    ],
                    owner: this.state.users[3]
                },
                4: {
                    actions: [
                        {
                            id: 6,
                            startsAt: '2017-02-22 17:00',
                            endsAt: '2017-02-22 18:00',
                            time: '2017-02-21 15:10',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.WAITING_FOR_APPROVAL,
                            updatedBy: this.state.users[4],
                            resource: this.state.resources[1],
                        },
                        {
                            id: 7,
                            startsAt: '2017-02-22 18:00',
                            endsAt: '2017-02-22 19:00',
                            time: '2017-02-21 15:22',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.NEW_TIME_PROPOSED,
                            updatedBy: this.state.users[1],
                            resource: this.state.resources[1],
                        }
                    ],
                    owner: this.state.users[4]
                }
            }
        };
        this.state = {
            ...this.state,
            tableData: [
                {
                    reservation: 1,
                    type: [...this.state.data[1].actions].pop().type,
                    resource: [...this.state.data[1].actions].pop().resource.name,
                    status: [...this.state.data[1].actions].pop().status,
                    owner: this.state.data[1].owner.username,
                    startsAt: [...this.state.data[1].actions].pop().startsAt,
                    endsAt: [...this.state.data[1].actions].pop().endsAt,
                    createdOn: this.state.data[1].actions[0].time,
                    updatedOn: [...this.state.data[1].actions].pop().time
                },
                {
                    reservation: 2,
                    type: [...this.state.data[2].actions].pop().type,
                    resource: [...this.state.data[2].actions].pop().resource.name,
                    status: [...this.state.data[2].actions].pop().status,
                    owner: this.state.data[2].owner.username,
                    startsAt: [...this.state.data[2].actions].pop().startsAt,
                    endsAt: [...this.state.data[2].actions].pop().endsAt,
                    createdOn: this.state.data[2].actions[0].time,
                    updatedOn: [...this.state.data[2].actions].pop().time
                },
                {
                    reservation: 3,
                    type: [...this.state.data[3].actions].pop().type,
                    resource: [...this.state.data[3].actions].pop().resource.name,
                    status: [...this.state.data[3].actions].pop().status,
                    owner: this.state.data[3].owner.username,
                    startsAt: [...this.state.data[3].actions].pop().startsAt,
                    endsAt: [...this.state.data[3].actions].pop().endsAt,
                    createdOn: this.state.data[3].actions[0].time,
                    updatedOn: [...this.state.data[3].actions].pop().time
                },
                {
                    reservation: 4,
                    type: [...this.state.data[4].actions].pop().type,
                    resource: [...this.state.data[4].actions].pop().resource.name,
                    status: [...this.state.data[4].actions].pop().status,
                    owner: this.state.data[4].owner.username,
                    startsAt: [...this.state.data[4].actions].pop().startsAt,
                    endsAt: [...this.state.data[4].actions].pop().endsAt,
                    createdOn: this.state.data[4].actions[0].time,
                    updatedOn: [...this.state.data[4].actions].pop().time
                }
            ],
        };

        this.constants = {
            title: 'Appointments',
            filterHint: 'Search ("Field: value" to search over specific column)',
            columns: [
                {
                    key: 'reservation',
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
                    key: 'createdOn',
                    sortable: true,
                    label: 'Created on',
                    className: 'datetime-column'
                },
                {
                    key: 'updatedOn',
                    sortable: true,
                    label: 'Updated on',
                    className: 'datetime-column'
                }
            ],
            actionsModal: {
                title: 'Actions',
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
            createModal: {
                title: 'Create an appointment'
            }
        };
    }

    onFilterValueChange = (value) => {
        console.log('Filter value changed!');
    };

    onNextPageClick = (event) => {
        console.log('Next page clicked!');
    };

    onPreviousPageClick = (event) => {
        console.log('Previous page clicked!');
    };

    onRowSizeChange = (value) => {
        console.log('Row size changed!');
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

    onRowSelection = (selectedRows) => {
        if (selectedRows === 'all') {
            this.setState({
                ...this.state,
                selectedRows: _.range(this.state.tableData.length)
            });
        } else if (selectedRows === 'none') {
            this.setState({
                ...this.state,
                selectedRows: []
            });
        } else {
            const diff = [...new Set(selectedRows)].filter(x => !new Set(this.state.selectedRows).has(x))[0];

            if (diff === undefined) {

            } else {
                const id = this.state.tableData[diff].reservation;
                const actions = _.map(this.state.data[id].actions, (e) => {
                    return {
                        ...e,
                        updatedBy: e.updatedBy.username,
                        resource: e.resource.name
                    }
                });

                this.setState({
                    ...this.state,
                    selectedRows: selectedRows,
                    actionsModal: {
                        ...this.state.actionsModal,
                        opened: true,
                        actionsTableData: actions,
                        actionsPage: 1,
                        actionsRowsNumber: this.state.data[id].actions.length,
                        actionsRowsPerPage: 5
                    }
                });
            }
        }
    };

    onSortOrderChange = (key, order) => {
        console.log('Sort order changed!');
    };

    onAddButtonTouchTap = (event) => {
        this.setState({
            ...this.state, createModal: {
                ...this.state.createModal,
                opened: true
            }
        });
    };

    onDoneButtonTouchTap = (event) => {
        console.log('Accept reservation(s) pressed!');
    };

    onRemoveButtonTouchTap = (event) => {
        console.log('Remove reservation(s) pressed!');
    };

    onActionsCloseButtonTouchTap = () => {
        this.setState({
            ...this.state, actionsModal: {
                opened: false
            }
        });
    };

    onCreateCloseButtonTouchTap = () => {
        this.setState({
            ...this.state, createModal: {
                ...this.state.createModal,
                opened: false
            }
        });
    };

    onCreateTypeChange = (e, key, value) => {
        this.setState({
            ...this.state, createModal: {
                ...this.state.createModal,
                type: value
            }
        });
    };

    datetimePickerInputStyle = {
        width: '100%'
    };

    //noinspection JSMethodCanBeStatic
    render() {
        const actionsDialogActions = [
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
                onTouchTap={this.onActionsCloseButtonTouchTap}/>,
        ];

        const createDialogActions = [
            <FlatButton
                label='Create'
                primary={true}
                disabled={true}/>,
            <FlatButton
                label='Close'
                primary={true}
                onTouchTap={this.onCreateCloseButtonTouchTap}/>,
        ];

        return (
            <div>
                <div className='content-table-wrapper'>
                    <div className='content-table'>
                        <DataTables
                            height={'auto'}
                            enableSelectAll={true}
                            filterHintText={this.constants.filterHint}
                            title={this.constants.title}
                            showHeaderToolbar={true}
                            selectable={true}
                            multiSelectable={true}
                            showRowHover={true}
                            columns={this.constants.columns}
                            selectedRows={this.state.selectedRows}
                            data={this.state.tableData}
                            showCheckboxes={true}
                            onFilterValueChange={this.onFilterValueChange}
                            onNextPageClick={this.onNextPageClick}
                            onPreviousPageClick={this.onPreviousPageClick}
                            onRowSelection={this.onRowSelection}
                            onRowSizeChange={this.onRowSizeChange}
                            onSortOrderChange={this.onSortOrderChange}
                            page={this.state.page}
                            count={this.state.totalPages}
                            rowSize={this.state.pageSize}
                            rowSizeList={[10, 25, 50, 100]}
                        />
                    </div>
                </div>
                <FloatingActionButton
                    className='third-button'
                    onTouchTap={this.onDoneButtonTouchTap}>
                    <ActionDone />
                </FloatingActionButton>
                <FloatingActionButton
                    className='second-button'
                    onTouchTap={this.onRemoveButtonTouchTap}>
                    <ActionDeleteForever />
                </FloatingActionButton>
                <FloatingActionButton
                    className='first-button'
                    onTouchTap={this.onAddButtonTouchTap}>
                    <ContentAdd />
                </FloatingActionButton>
                <Dialog
                    title={this.constants.actionsModal.loggingIn}
                    actions={actionsDialogActions}
                    modal={true}
                    open={this.state.actionsModal.opened}>
                    <DataTables
                        height={'auto'}
                        showRowHover={true}
                        columns={this.constants.actionsModal.columns}
                        data={this.state.actionsModal.actionsTableData}
                        onNextPageClick={this.onModalNextPageClick}
                        onPreviousPageClick={this.onModalPreviousPageClick}
                        onRowSizeChange={this.onModalRowSizeChange}
                        page={this.state.actionsModal.actionsPage}
                        count={this.state.actionsModal.actionsRowsNumber}
                        rowSize={this.state.actionsModal.actionsRowsPerPage}
                        rowSizeList={[5, 10]}
                    />
                </Dialog>
                <Dialog
                    contentClassName='create-dialog'
                    title={this.constants.createModal.loggingIn}
                    actions={createDialogActions}
                    modal={true}
                    open={this.state.createModal.opened}>
                    <AutoComplete
                        hintText='Resource'
                        fullWidth={true}
                        filter={AutoComplete.fuzzyFilter}
                        dataSource={_.map(_.values(this.state.resources), (e) => {
                            return e.name;
                        })}
                        maxSearchResults={5}
                    />
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
                        value={this.state.createModal.type}
                        onChange={this.onCreateTypeChange}>
                        <MenuItem
                            value={ReservationTypes.REGULAR}
                            primaryText={ReservationTypes.REGULAR}/>
                        <MenuItem
                            value={ReservationTypes.UNAVAILABLE}
                            primaryText={ReservationTypes.UNAVAILABLE}/>
                    </DropDownMenu>
                </Dialog>
            </div>
        )
    }
}