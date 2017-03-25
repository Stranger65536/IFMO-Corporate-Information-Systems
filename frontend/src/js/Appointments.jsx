import React from "react";
import DataTables from "material-ui-datatables";
import ContentAdd from "material-ui/svg-icons/content/add";
import ActionDeleteForever from "material-ui/svg-icons/action/delete-forever";
import FloatingActionButton from "material-ui/FloatingActionButton";
import {MuiThemeProvider} from "material-ui/styles";

//TODO converting WS calls to convenient state
//TODO Modals for create / delete
//TODO Modals on row click
//TODO modals for API errors
//TODO sort / pagination / filter function calls
//TODO progressbar on requests

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

export class Appointments extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            searchFilter: null,
            sortField: null,
            sortOrder: null,
            selectedRows: null,
            selectedAll: false,
            page: 1,
            rowsPerPage: 10,
            rowsNumber: 5,
            timeZone: 'Z',
            data: {
                1: {
                    actions: [
                        {
                            id: 1,
                            startsAt: '2017-02-22 17:00:00Z',
                            endsAt: '2017-02-22 18:00:00Z',
                            time: '2017-02-21 15:00:00Z',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.WAITING_FOR_APPROVAL,
                            updatedBy: {
                                id: 1,
                                username: 'sergeevm',
                                firstName: 'Marina',
                                lastName: 'Sergeeva',
                                middleName: ''
                            },
                            resource: {
                                id: 1,
                                name: 'Ping-Pong table',
                                location: 'SPb Development Center LLC'
                            },
                        },
                        {
                            id: 2,
                            startsAt: '2017-02-22 17:00:00Z',
                            endsAt: '2017-02-22 18:00:00Z',
                            time: '2017-02-21 15:17:00Z',
                            type: ReservationTypes.REGULAR,
                            status: ReservationStatuses.APPROVED,
                            updatedBy: {
                                id: 1,
                                username: 'sergeevm',
                                firstName: 'Marina',
                                lastName: 'Sergeeva',
                                middleName: ''
                            },
                            resource: {
                                id: 1,
                                name: 'Ping-Pong table',
                                location: 'SPb Development Center LLC'
                            },
                        }
                    ],
                    owner: {
                        id: 1,
                        username: 'sergeevm',
                        firstName: 'Marina',
                        lastName: 'Sergeeva',
                        middleName: ''
                    }
                }
            },
            tableData: Array(5).fill({
                    reservation: 1,
                    type: ReservationTypes.REGULAR,
                    resource: 'Ping-Pong table',
                    status: ReservationStatuses.APPROVED,
                    owner: 'sergeevm',
                    startsAt: '2017-02-22 17:00',
                    endsAt: '2017-02-22 18:00',
                    createdOn: '2017-02-21 15:00',
                    updatedOn: '2017-02-21 15:17'
                })
        };

        this.constants = {
            title: 'Appointments',
            filterHint: 'Search ("Field: value" to search over specific column)',
            columns: [
                {
                    key: 'reservation',
                    sortable: true,
                    label: 'Reservation ID',
                },
                {
                    key: 'resource',
                    sortable: true,
                    label: 'Resource'
                },
                {
                    key: 'type',
                    sortable: true,
                    label: 'Type'
                },
                {
                    key: 'status',
                    sortable: true,
                    label: 'Status'
                },
                {
                    key: 'owner',
                    sortable: true,
                    label: 'Owner',
                },
                {
                    key: 'startsAt',
                    sortable: true,
                    label: 'Start',
                },
                {
                    key: 'endsAt',
                    sortable: true,
                    label: 'End',
                },
                {
                    key: 'createdOn',
                    sortable: true,
                    label: 'Created on',
                },
                {
                    key: 'updatedOn',
                    sortable: true,
                    label: 'Updated on',
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

    onRowSelection = (selectedRows) => {
        console.log('Row selected!');
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


    //noinspection JSMethodCanBeStatic
    render() {
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
                            data={this.state.tableData}
                            showCheckboxes={true}
                            onFilterValueChange={this.onFilterValueChange}
                            onNextPageClick={this.onNextPageClick}
                            onPreviousPageClick={this.onPreviousPageClick}
                            onRowSelection={this.onRowSelection}
                            onSortOrderChange={this.onSortOrderChange}
                            page={this.state.page}
                            count={this.state.rowsNumber}
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
            </div>
        )
    }
}