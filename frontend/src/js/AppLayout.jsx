import React from "react";
import _ from "underscore";
import Menu from "material-ui/Menu";
import AppBar from "material-ui/AppBar";
import Drawer from "material-ui/Drawer";
import MenuItem from "material-ui/MenuItem";
import IconButton from "material-ui/IconButton";
import SocialPublic from "material-ui/svg-icons/social/public";
import ActionSettings from "material-ui/svg-icons/action/settings";
import ActionExitToApp from "material-ui/svg-icons/action/exit-to-app";
import ActionDateRange from "material-ui/svg-icons/action/date-range";
import AccountCircle from "material-ui/svg-icons/action/account-circle";
import {MuiThemeProvider} from "material-ui/styles";
import WelcomePage from "./welcome-page/WelcomePage.jsx";
import {Appointments} from "./Appointments.jsx";
import {emcMuiTheme, InfoModal, ProgressCircle, sendApiRequest} from "./Common.jsx";

//TODO modal warn before logout
//TODO state clear on logout
//TODO refresh button

const Page = {
    APPOINTMENTS: 'appointments',
    RESOURCES: 'resources',
    USERS: 'users',
    SETTINGS: 'settings',
};

export default class AppLayout extends React.Component {
    constructor(props) {
        super(props);
        this.constants = {
            pageTitle: <div className='header-logo'/>,
            nav: {
                width: 250,
                swipeWidth: 30,
                items: {
                    appointments: {
                        pageIndicator: Page.APPOINTMENTS,
                        label: 'Appointments',
                        viewElement: () => {
                            return <Appointments/>
                        }
                    },
                    resources: {
                        pageIndicator: Page.RESOURCES,
                        label: 'Resources',
                        viewElement: () => {
                            return <div>Will look the same as the Appointments page</div>
                        }
                    },
                    users: {
                        pageIndicator: Page.USERS,
                        label: 'Users',
                        viewElement: () => {
                            return <div>Will look the same as the Appointments page</div>
                        }
                    },
                    settings: {
                        pageIndicator: Page.SETTINGS,
                        label: 'Settings',
                        viewElement: () => {
                            return <div>Registration info change & timezone</div>
                        }
                    }
                }
            },
            infoModal: {
                login: {
                    title: 'Logging in',
                    invalidLogin: 'Invalid credentials!'
                },
                register: {
                    title: 'Registering',
                    emailTaken: 'Specified email is already registered!',
                    usernameTaken: 'Specified username is already registered!'
                },
                progress: <ProgressCircle/>,
            }
        };

        this.state = {
            menuOpened: false,
            loggedIn: false,
            user: {
                username: 'username',
                id: 'ID',
                firstName: 'Firstname',
                lastName: 'Lastname',
                middleNAme: 'Middlename',
                email: ''
            },
            pageIndicator: Page.APPOINTMENTS,
            infoModal: {
                opened: false,
                title: null,
                actions: [],
                content: () => {
                }
            }
        };
    }

    beforeLogin = () => {
        this.setState({
            ...this.state,
            infoModal: {
                ...this.state.infoModal,
                opened: true,
                title: this.constants.infoModal.login.title,
                content: () => this.constants.infoModal.progress
            }
        })
    };

    onLogin = (login, password) => {
        sendApiRequest('GetUsersRequest', {
                page: 1,
                pageSize: 1,
                searchField: 'email',
                searchType: 'equals',
                searchValue: login
            }, login, password,
            this.beforeLogin,
            (soapResponse) => {
                console.log(soapResponse);
                this.setState({...this.state, loggedIn: true});
                // do stuff with soapResponse
                // if you want to have the response as JSON use soapResponse.toJSON();
                // or soapResponse.toString() to get XML string
                // or soapResponse.toXML() to get XML DOM
            },
            (SOAPResponse) => {
                console.log(SOAPResponse);
            }
        );
    };

    onLogoutTouchTap = () => {
        this.setState({...this.state, loggedIn: false});
    };

    getLoggedInViewElement = () => {
        let instance = this;

        return _.findWhere(
            _.map(
                _.keys(this.constants.nav.items), function (property) {
                    return instance.constants.nav.items[property];
                }
            ), {pageIndicator: instance.state.pageIndicator}
        ).viewElement();
    };

    onDrawerRequestChange = (open) => {
        this.setState({...this.state, menuOpened: open});
    };

    //noinspection FunctionWithMultipleReturnPointsJS
    getCurrentViewElement = () => {
        if (!this.state.loggedIn) {
            return <div>
                <InfoModal
                    title={this.state.infoModal.title}
                    actions={this.state.infoModal.actions}
                    opened={this.state.infoModal.opened}>
                    {this.state.infoModal.content()}
                </InfoModal>
                <WelcomePage onLogin={this.onLogin}/>
            </div>;
        }

        return <div>
            <InfoModal
                title={this.state.infoModal.title}
                actions={this.state.infoModal.actions}
                opened={this.state.infoModal.opened}>
                {this.state.infoModal.content()}
            </InfoModal>
            <AppBar
                className='app-bar'
                title={this.constants.pageTitle}
                onLeftIconButtonTouchTap={this.onToggle}
                iconElementRight={
                    <IconButton tooltip="Logout" onTouchTap={this.onLogoutTouchTap}>
                        <ActionExitToApp/>
                    </IconButton>}
            />
            <Drawer
                docked={false}
                className='drawer'
                width={this.constants.nav.width}
                open={this.state.menuOpened}
                containerClassName='left-nav'
                overlayClassName='nav-overlay'
                swipeAreaWidth={this.swipeWidth}
                onRequestChange={this.onDrawerRequestChange}>
                <Menu>
                    <AccountCircle className='user-image'/>
                    <div className='username'>
                        {this.state.user.username}
                    </div>
                    <MenuItem
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.appointments.pageIndicator)}
                        primaryText={this.constants.nav.items.appointments.label}
                        leftIcon={<ActionDateRange/>}
                    />
                    <MenuItem
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.resources.pageIndicator)}
                        primaryText={this.constants.nav.items.resources.label}
                        leftIcon={<SocialPublic/>}
                    />
                    <MenuItem
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.users.pageIndicator)}
                        primaryText={this.constants.nav.items.users.label}
                        leftIcon={<AccountCircle/>}
                    />
                    <MenuItem
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.settings.pageIndicator)}
                        primaryText={this.constants.nav.items.settings.label}
                        leftIcon={<ActionSettings/>}
                    />
                </Menu>
            </Drawer>
            {this.getLoggedInViewElement()}
        </div>
    };

    onMenuItemTouchTap = (event, menuItem) => {
        this.setState({...this.state, pageIndicator: menuItem, menuOpened: false});
    };

    onToggle = () => {
        return this.setState({menuOpened: !this.state.menuOpened});
    };

    render() {
        return (
            <MuiThemeProvider muiTheme={emcMuiTheme}>
                {this.getCurrentViewElement()}
            </MuiThemeProvider>
        )
    }
}