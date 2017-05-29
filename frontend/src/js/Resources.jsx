import React from "react";
import _ from "underscore";
import Dialog from "material-ui/Dialog";
import FlatButton from "material-ui/FlatButton";
import DataTables from "material-ui-datatables";
import {MuiThemeProvider} from "material-ui/styles";
import {
    ProgressCircle,
    resourceLocationPattern,
    resourceNamePattern,
    sendApiRequest,
    UserRoles,
    UserRolesPriorities
} from "./Common.jsx";

//TODO filtration
export class Resources extends React.Component {
    constructor(props) {
        super(props);

        this.constants = {
            title: 'Resource',
            filterHint: 'Search ("Field [equals, contains, less, greater, lessEqual, greaterEqual] value" to search over specific column)',
            columns: [
                {
                    key: 'id',
                    sortable: true,
                    label: 'Resource ID',
                    className: 'id-column'
                },
                {
                    key: 'name',
                    sortable: true,
                    label: 'Name',
                    className: 'resource-name-column'//TODO
                },
                {
                    key: 'location',
                    sortable: true,
                    label: 'Location',
                    className: 'location-name-column'//TODO
                }
            ],
            validation: {
                name: {
                    patterns: [resourceNamePattern],
                    ref: 'name',
                    hintText: 'Name',
                    floatingLabelText: 'Name',
                    errorText: 'Name must be 1-25 characters length',
                },
                location: {
                    patterns: [resourceLocationPattern],
                    ref: 'location',
                    hintText: 'Location',
                    floatingLabelText: 'Location',
                    errorText: 'Name must be up to 35 characters length'
                }
            },
            infoModal: {
                title: 'Resource info',
                infoClass: 'info-dialog',
                editClass: '',
                loadingTitle: 'Loading',
                accessDeniedTitle: 'Access denied',
                unknownError: 'Unknown error',
                pageSizes: [10, 25, 50, 100]
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
            totalPages: Infinity,
            infoModal: {
                opened: false,
                title: '',
                class: this.constants.infoModal.infoClass,
                info: {
                    id: '',
                    name: '',
                    location: '',
                },
                actions: [],
                content: <div/>
            },
            originalResources: [],
            resources: []
        };
    }

    onFilterValueChange = (value) => {
        console.log('Filter value changed!');
    };

    onNextPageClick = (event) => {
        this.state.page++;
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            searchValueLowerBound: this.state.searchValueLowerBound,
            searchValueUpperBound: this.state.searchValueUpperBound,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    };

    onPreviousPageClick = (event) => {
        this.state.page--;
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            searchValueLowerBound: this.state.searchValueLowerBound,
            searchValueUpperBound: this.state.searchValueUpperBound,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    };

    onRowSizeChange = (value) => {
        this.state.pageSize = this.constants.infoModal.pageSizes[value];
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            searchValueLowerBound: this.state.searchValueLowerBound,
            searchValueUpperBound: this.state.searchValueUpperBound,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    };

    onResourceInfoModalShow = (resource) => {
        // this.state.infoModal.title = this.constants.infoModal.title;
        // this.state.infoModal.class = this.constants.infoModal.editClass;
        // this.state.infoModal.originalRole = user.role;
        // this.state.infoModal.opened = true;
        // this.state.infoModal.info = {
        //     id: user.id,
        //     username: user.username,
        //     email: user.email,
        //     firstName: user.firstName,
        //     lastName: user.lastName,
        //     middleName: user.middleName,
        //     role: user.role,
        // };
        // this.state.infoModal.actions = this.usersDialogActions();
        // this.state.infoModal.content = this.showEditInputs();
        // this.setState(this.state);
    };

    onRowSelection = (rowNumber) => {
        const resource = this.state.resources[rowNumber];
        this.onResourceInfoModalShow(resource);
    };

    onSortOrderChange = (key, order) => {
        this.state.sortingField = key;
        this.state.sortingOrder = order;
        this.performSearch({
            page: this.state.page,
            pageSize: this.state.pageSize,
            searchField: this.state.searchField,
            searchType: this.state.searchType,
            searchValue: this.state.searchValue,
            searchValueLowerBound: this.state.searchValueLowerBound,
            searchValueUpperBound: this.state.searchValueUpperBound,
            sortingOrder: this.state.sortingOrder,
            sortingField: this.state.sortingField
        });
    };

    onModalClose = () => {
        this.setState({
            ...this.state, infoModal: {
                ...this.state.infoModal,
                opened: false
            }
        });
    };

    onSaveChanges = () => {
        console.log("save");
    };

    beforeSearch = () => {
        this.setState({
            ...this.state,
            infoModal: {
                ...this.state.infoModal,
                opened: true,
                class: this.constants.infoModal.infoClass,
                title: this.constants.infoModal.loadingTitle,
                content: <ProgressCircle/>,
                actions: []
            }
        })
    };

    resourcesDialogActions = () => {
        const currentUserRolePriority = UserRolesPriorities[this.props.user.role];
        return [
            <FlatButton
                label='Save'
                primary={true}
                disabled={!(currentUserRolePriority > UserRolesPriorities[UserRoles.USER])}
                onTouchTap={this.onSaveChanges}/>,
            <FlatButton
                label='Cancel'
                primary={true}
                disabled={false}
                onTouchTap={this.onModalClose}/>,
        ]
    };

    showEditInputs = () => {
        return <div>
            {/*<AccountCircle style={iconStyle}/>*/}
            {/*<ValidTextField*/}
            {/*//TODO className='login-input'*/}
            {/*ref={this.constants.validation.username.ref}*/}
            {/*value={this.state.infoModal.info.username}*/}
            {/*hintText={this.constants.validation.username.hintText}*/}
            {/*errorText={this.constants.validation.username.errorText}*/}
            {/*floatingLabelText={this.constants.validation.username.floatingLabelText}*/}
            {/*patterns={this.constants.validation.username.patterns}*/}
            {/*validationFunction={this.constants.validation.username.validationFunction}*/}
            {/*afterGeneralValidation={this.constants.validation.username.afterGeneralValidation}*/}
            {/*rootStateUpdater={(value) => {*/}
            {/*this.state.infoModal.info.username = value;*/}
            {/*this.setState(this.state);*/}
            {/*}}*/}
            {/*/>*/}
            {/*<Email style={iconStyle}/>*/}
            {/*<ValidTextField*/}
            {/*//TODO className='login-input'*/}
            {/*value={this.state.infoModal.info.email}*/}
            {/*ref={this.constants.validation.email.ref}*/}
            {/*hintText={this.constants.validation.email.hintText}*/}
            {/*errorText={this.constants.validation.email.errorText}*/}
            {/*floatingLabelText={this.constants.validation.email.floatingLabelText}*/}
            {/*patterns={this.constants.validation.email.patterns}*/}
            {/*rootStateUpdater={(value) => {*/}
            {/*this.state.infoModal.info.email = value;*/}
            {/*this.setState(this.state);*/}
            {/*}}*/}
            {/*/>*/}
            {/*<AccountCircle style={iconStyle}/>*/}
            {/*<ValidTextField*/}
            {/*//TODO className='login-input'*/}
            {/*value={this.state.infoModal.info.firstName}*/}
            {/*ref={this.constants.validation.firstName.ref}*/}
            {/*hintText={this.constants.validation.firstName.hintText}*/}
            {/*errorText={this.constants.validation.firstName.errorText}*/}
            {/*floatingLabelText={this.constants.validation.firstName.floatingLabelText}*/}
            {/*patterns={this.constants.validation.firstName.patterns}*/}
            {/*rootStateUpdater={(value) => {*/}
            {/*this.state.infoModal.info.firstName = value;*/}
            {/*this.setState(this.state);*/}
            {/*}}*/}
            {/*/>*/}
            {/*<AccountCircle style={iconStyle}/>*/}
            {/*<ValidTextField*/}
            {/*//TODO className='login-input'*/}
            {/*value={this.state.infoModal.info.lastName}*/}
            {/*ref={this.constants.validation.lastName.ref}*/}
            {/*hintText={this.constants.validation.lastName.hintText}*/}
            {/*errorText={this.constants.validation.lastName.errorText}*/}
            {/*floatingLabelText={this.constants.validation.lastName.floatingLabelText}*/}
            {/*patterns={this.constants.validation.lastName.patterns}*/}
            {/*rootStateUpdater={(value) => {*/}
            {/*this.state.infoModal.info.lastName = value;*/}
            {/*this.setState(this.state);*/}
            {/*}}*/}
            {/*/>*/}
            {/*<AccountCircle style={iconStyle}/>*/}
            {/*<ValidTextField*/}
            {/*//TODO className='login-input'*/}
            {/*value={this.state.infoModal.info.middleName}*/}
            {/*ref={this.constants.validation.middleName.ref}*/}
            {/*hintText={this.constants.validation.middleName.hintText}*/}
            {/*errorText={this.constants.validation.middleName.errorText}*/}
            {/*floatingLabelText={this.constants.validation.middleName.floatingLabelText}*/}
            {/*patterns={this.constants.validation.middleName.patterns}*/}
            {/*rootStateUpdater={(value) => {*/}
            {/*this.state.infoModal.info.middleName = value;*/}
            {/*this.setState(this.state);*/}
            {/*}}*/}
            {/*/>*/}
            {/*<SelectField*/}
            {/*floatingLabelText="Role"*/}
            {/*value={this.state.infoModal.info.role}*/}
            {/*onChange={(event, index, value) => {*/}
            {/*this.state.infoModal.info.role = value;*/}
            {/*this.setState(this.state);*/}
            {/*}}>*/}
            {/*<MenuItem value={UserRoles.USER} primaryText={UserRoles.USER}/>*/}
            {/*<MenuItem value={UserRoles.MODERATOR} primaryText={UserRoles.MODERATOR}/>*/}
            {/*<MenuItem value={UserRoles.ADMIN} primaryText={UserRoles.ADMIN}/>*/}
            {/*</SelectField>*/}
        </div>
    };

    showErrorModal = (title) => {
        this.setState({
            ...this.state,
            infoModal: {
                ...this.state.infoModal,
                opened: true,
                class: this.constants.infoModal.infoClass,
                title: title,
                content: <div/>,
                actions: [<FlatButton
                    label='Accept'
                    primary={true}
                    disabled={false}
                    onTouchTap={this.onModalClose}/>]
            }
        })
    };

    loadResources = () => {
        sendApiRequest({
            method: 'GetResourcesRequest',
            data: {},
            login: this.props.user.username,
            password: this.props.user.password,
            beforeSend: this.beforeSearch,
            success: (soapResponse) => {
                const resources = soapResponse.content
                    .getElementsByTagName("Body")[0]
                    .getElementsByTagName("GetResourcesResponse")[0]
                    .children;
                this.state.originalResources = _.map(resources, (resource) => {
                    return {
                        id: Number(resource.getElementsByTagName('id')[0].textContent),
                        name: resource.getElementsByTagName('name')[0].textContent,
                        location: resource.getElementsByTagName('location')[0] ? resource.getElementsByTagName('location')[0].textContent : '',
                    }
                });
                this.state.totalPages = this.state.originalResources.length;
                this.performSearch({
                    page: this.state.page,
                    pageSize: this.state.pageSize,
                    searchField: this.state.searchField,
                    searchType: this.state.searchType,
                    searchValue: this.state.searchValue,
                    searchValueLowerBound: this.state.searchValueLowerBound,
                    searchValueUpperBound: this.state.searchValueUpperBound,
                    sortingOrder: this.state.sortingOrder,
                    sortingField: this.state.sortingField
                });
                this.onModalClose();
            },
            error: (soapResponse) => {
                //TODO another errors handling
                this.showErrorModal(this.constants.infoModal.unknownError);
            }
        });
    };

    performSearch = (options) => {
        const page = options.page;
        const pageSize = options.pageSize;
        const searchField = options.searchField;
        const searchType = options.searchType;
        const searchValue = options.searchValue;
        const searchValueLowerBound = options.searchValueLowerBound;
        const searchValueUpperBound = options.searchValueUpperBound;
        const sortingOrder = options.sortingOrder;
        const sortingField = options.sortingField;

        this.state.resources = _.sortBy(this.state.originalResources, function (resource) {
            return resource[sortingField];
        });
        if (sortingOrder === 'desc') {
            this.state.resources = this.state.resources.reverse()
        }

        this.state.resources = this.state.resources.slice(pageSize * (page - 1), pageSize * page);

        this.setState(this.state);
    };

    componentDidMount() {
        this.loadResources();
    }

    //noinspection JSMethodCanBeStatic
    render() {
        return (
            <div>
                <div className='content-table-wrapper full'>
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
                            data={this.state.resources}
                            showCheckboxes={false}
                            onFilterValueChange={this.onFilterValueChange}
                            onNextPageClick={this.onNextPageClick}
                            onPreviousPageClick={this.onPreviousPageClick}
                            onRowSelection={this.onRowSelection}
                            onRowSizeChange={this.onRowSizeChange}
                            onSortOrderChange={this.onSortOrderChange}
                            count={this.state.totalPages}
                            page={this.state.page}
                            rowSize={this.state.pageSize}
                            rowSizeList={this.constants.infoModal.pageSizes}
                        />
                    </div>
                </div>
                <Dialog
                    contentClassName={this.state.infoModal.class}
                    title={this.state.infoModal.title}
                    actions={this.state.infoModal.actions}
                    modal={true}
                    open={this.state.infoModal.opened}>
                    {this.state.infoModal.content}
                </Dialog>
            </div>
        )
    }
}