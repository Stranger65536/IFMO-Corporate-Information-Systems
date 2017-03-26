import React from "react";
import DataTables from "material-ui-datatables";
import ContentAdd from "material-ui/svg-icons/content/add";
import Dialog from "material-ui/Dialog";
import FlatButton from "material-ui/FlatButton";
import ActionDeleteForever from "material-ui/svg-icons/action/delete-forever";
import FloatingActionButton from "material-ui/FloatingActionButton";
import {MuiThemeProvider} from "material-ui/styles";

//TODO converting WS calls to convenient state
//TODO Modals for create / delete
//TODO Modals on row click
//TODO modals for API errors
//TODO sort / pagination / filter function calls
//TODO progressbar on requests
//TODO set state to [] and [all] despite of 'all' and 'none'

const ReservationTypes = {
    REGULAR: 'Regular',
    UNAVAILABLE: 'Unavailable'
};

const ReservationStatuses = {
    APPROVED: 'Approved',
    CANCELED: 'Canceled',
    WAITING_FOR_APPROVAL: 'Waiting for approval',
    NEW_TIME_PROPOSED: 'New time proposed'
};

const UserRoles = {
    USER: 'user',
    MODERATOR: 'moderator',
    ADMIN: 'admin'
};

export class Appointments extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            searchFilter: null,
            sortField: null,
            sortOrder: null,
            selectedRows: null,
            page: 1,
            rowsPerPage: 10,
            rowsNumber: 5,
            timeZone: 'Z',
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
            },
            reservationDialog: {
                opened: false,
                reservation: null
            },
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
            ]
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
            ]
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

    onRowSelection = (selectedRows) => {
        if (selectedRows === 'all' || selectedRows === 'none') {
            this.setState({
                ...this.state,
                selectedRows: selectedRows
            });
        } else {
            this.setState({
                ...this.state,
                selectedRows: selectedRows,
                reservationDialog: {
                    ...this.state.reservationDialog,
                    opened: true
                }
            });
        }
    };

    onSortOrderChange = (key, order) => {
        console.log('Sort order changed!');
    };

    onAddButtonTouchTap = (event) => {
        console.log('Add reservation pressed!');
    };

    onRemoveButtonTouchTap = (event) => {
        console.log('Remove reservation(s) pressed!');
    };

    onModalButtonTouchTap = () => {
        this.setState({
            ...this.state, reservationDialog: {
                ...this.state.reservationDialog,
                opened: false
            }
        });
    };

    //noinspection JSMethodCanBeStatic
    render() {
        const reservationDialogActions = [
            <FlatButton
                label="Accept"
                primary={true}
                disabled={true}/>,
            <FlatButton
                label="Propose new time"
                primary={true}
                disabled={true}/>,
            <FlatButton
                label="Update"
                primary={true}
                disabled={true}/>,
            <FlatButton
                label="Cancel"
                primary={true}
                disabled={true}/>,
            <FlatButton
                label="Close"
                primary={true}
                onTouchTap={this.onModalButtonTouchTap}/>,
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
                            count={this.state.rowsNumber}
                            rowSize={this.state.rowsPerPage}
                            rowSizeList={[10, 25, 50, 100]}
                        />
                    </div>
                </div>
                <FloatingActionButton
                    className='remove-button'
                    onTouchTap={this.onRemoveButtonTouchTap}>
                    <ActionDeleteForever />
                </FloatingActionButton>
                <FloatingActionButton
                    className='add-button'
                    onTouchTap={this.onAddButtonTouchTap}>
                    <ContentAdd />
                </FloatingActionButton>
                <Dialog
                    title="Appointment information"
                    actions={reservationDialogActions}
                    modal={true}
                    open={this.state.reservationDialog.opened}>
                    There will be a list with all actions made on this reservation
                </Dialog>
            </div>
        )
    }
}