import React from "react";
import _ from "underscore";
import Dialog from "material-ui/Dialog";
import MenuItem from "material-ui/MenuItem";
import FlatButton from "material-ui/FlatButton";
import DataTables from "material-ui-datatables";
import SelectField from "material-ui/SelectField";
import {MuiThemeProvider} from "material-ui/styles";
import AccountCircle from "material-ui/svg-icons/action/account-circle";
import Email from "material-ui/svg-icons/communication/email";
import {
    emailPattern,
    iconStyle,
    namePattern,
    ProgressCircle,
    sendApiRequest,
    usernamePattern,
    UserRoles,
    UserRolesPriorities,
    ValidTextField
} from "./Common.jsx";


export class Users extends React.Component {
    constructor(props) {
        super(props);

        this.constants = {
            title: 'Users',
            filterHint: 'Search ("Field [equals, contains, less, greater, lessEqual, greaterEqual] value" to search over specific column)',
            columns: [
                {
                    key: 'id',
                    sortable: true,
                    label: 'User ID',
                    className: 'id-column'
                },
                {
                    key: 'username',
                    sortable: true,
                    label: 'Username',
                    className: 'username-column'//TODO
                },
                {
                    key: 'email',
                    sortable: true,
                    label: 'Email',
                    className: 'email-column'//TODO
                },
                {
                    key: 'role',
                    sortable: true,
                    label: 'Role',
                    className: 'role-column'//TODO
                },
                {
                    key: 'firstName',
                    sortable: true,
                    label: 'First name',
                    className: 'name-column'//TODO
                },
                {
                    key: 'lastName',
                    sortable: true,
                    label: 'Last name',
                    className: 'name-column'//TODO
                },
                {
                    key: 'middleName',
                    sortable: true,
                    label: 'Middle name',
                    className: 'name-column'//TODO
                }
            ],
            validation: {
                username: {
                    patterns: [usernamePattern],
                    ref: 'username',
                    stateField: 'signUpUsername',
                    hintText: 'Username',
                    floatingLabelText: 'Username',
                    errorText: 'Username must be 5-32 characters length and must be not email-like',
                    validationFunction: this.signUpUsernameValidation,
                    afterGeneralValidation: true
                },
                email: {
                    patterns: [emailPattern],
                    ref: 'email',
                    stateField: 'signUpEmail',
                    hintText: 'Email',
                    floatingLabelText: 'Email',
                    errorText: 'Invalid email'
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
            },
            infoModal: {
                title: 'User info',
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
            infoModal: {
                opened: false,
                title: '',
                class: this.constants.infoModal.infoClass,
                originalRole: '',
                info: {
                    id: '',
                    username: '',
                    email: '',
                    firstName: '',
                    lastName: '',
                    middleName: '',
                    role: ''
                },
                actions: [],
                content: <div/>
            },
            users: []
        };
    }

    signUpUsernameValidation = (value) => {
        return !emailPattern.test(value);
    };

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

    onUserInfoModalShow = (user) => {
        this.state.infoModal.title = this.constants.infoModal.title;
        this.state.infoModal.class = this.constants.infoModal.editClass;
        this.state.infoModal.originalRole = user.role;
        this.state.infoModal.opened = true;
        this.state.infoModal.info = {
            id: user.id,
            username: user.username,
            email: user.email,
            firstName: user.firstName,
            lastName: user.lastName,
            middleName: user.middleName,
            role: user.role,
        };
        this.state.infoModal.actions = this.usersDialogActions();
        this.state.infoModal.content = this.showEditInputs();
        this.setState(this.state);
    };

    onRowSelection = (rowNumber) => {
        const user = this.state.users[rowNumber];
        this.onUserInfoModalShow(user);
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

    onUpdateRole = () => {
        console.log('update role');
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

    usersDialogActions = () => {
        const isSameUser = this.state.infoModal.info.id === this.props.user.id;
        const currentUserRolePriority = UserRolesPriorities[this.props.user.role];
        const targetUserRolePriority = UserRolesPriorities[this.state.infoModal.originalRole];
        const targetUserNewRolePriority = UserRolesPriorities[this.state.infoModal.info.role];
        return [
            <FlatButton
                label='Save'
                primary={true}
                disabled={!(isSameUser || currentUserRolePriority > UserRolesPriorities[UserRoles.USER])}
                onTouchTap={this.onSaveChanges}/>,
            <FlatButton
                label='Update role'
                primary={true}
                disabled={!(currentUserRolePriority < targetUserRolePriority || targetUserNewRolePriority > currentUserRolePriority)}
                onTouchTap={this.onUpdateRole}/>,
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
            <SelectField
                floatingLabelText="Role"
                value={this.state.infoModal.info.role}
                onChange={(event, index, value) => {
                    this.state.infoModal.info.role = value;
                    this.setState(this.state);
                }}>
                <MenuItem value={UserRoles.USER} primaryText={UserRoles.USER}/>
                <MenuItem value={UserRoles.MODERATOR} primaryText={UserRoles.MODERATOR}/>
                <MenuItem value={UserRoles.ADMIN} primaryText={UserRoles.ADMIN}/>
            </SelectField>
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

    performSearch = (options) => {
        sendApiRequest({
            method: 'GetUsersRequest',
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
            beforeSend: this.beforeSearch,
            success: (soapResponse) => {
                const users = soapResponse.content
                    .getElementsByTagName("Body")[0]
                    .getElementsByTagName("GetUsersResponse")[0]
                    .children;
                const tableData = _.map(users, (user) => {
                    return {
                        id: Number(user.getElementsByTagName('id')[0].textContent),
                        username: user.getElementsByTagName('username')[0].textContent,
                        email: user.getElementsByTagName('email')[0].textContent,
                        firstName: user.getElementsByTagName('firstName')[0] ? user.getElementsByTagName('firstName')[0].textContent : '',
                        lastName: user.getElementsByTagName('lastName')[0] ? user.getElementsByTagName('lastName')[0].textContent : '',
                        middleName: user.getElementsByTagName('middleName')[0] ? user.getElementsByTagName('middleName')[0].textContent : '',
                        role: user.getElementsByTagName('role')[0] ? user.getElementsByTagName('role')[0].textContent : '',
                    }
                });
                this.setState({...this.state, users: tableData});
                this.onModalClose();
            },
            error: (soapResponse) => {
                //TODO another errors handling
                this.showErrorModal(this.constants.infoModal.unknownError);
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
                            data={this.state.users}
                            showCheckboxes={false}
                            onFilterValueChange={this.onFilterValueChange}
                            onNextPageClick={this.onNextPageClick}
                            onPreviousPageClick={this.onPreviousPageClick}
                            onRowSelection={this.onRowSelection}
                            onRowSizeChange={this.onRowSizeChange}
                            onSortOrderChange={this.onSortOrderChange}
                            count={Infinity}
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