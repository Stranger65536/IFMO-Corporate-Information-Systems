import React from "react";
import _ from "underscore";
import Menu from "material-ui/Menu";
import AppBar from "material-ui/AppBar";
import Drawer from "material-ui/Drawer";
import MenuItem from "material-ui/MenuItem";
import IconButton from "material-ui/IconButton";
import SocialPublic from "material-ui/svg-icons/social/public";
import ActionSettings from "material-ui/svg-icons/action/settings";
import ActionAssessment from "material-ui/svg-icons/action/assessment";
import ActionExitToApp from "material-ui/svg-icons/action/exit-to-app";
import ActionDateRange from "material-ui/svg-icons/action/date-range";
import AccountCircle from "material-ui/svg-icons/action/account-circle";
import {MuiThemeProvider} from "material-ui/styles";
import WelcomePage from "./welcome-page/WelcomePage.jsx";
import {Appointments} from "./Appointments.jsx";
import {emcMuiTheme} from "./Common.jsx";
import {Users} from "./Users.jsx";
import {Resources} from "./Resources.jsx";
import {Reports} from "./Reports.jsx";

//TODO modal warn before logout
//TODO state clear on logout
//TODO refresh button

const Page = {
    APPOINTMENTS: 'appointments',
    RESOURCES: 'resources',
    USERS: 'users',
    REPORTS: 'reports',
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
                            return <Appointments user={this.state.user}/>
                        }
                    },
                    resources: {
                        pageIndicator: Page.RESOURCES,
                        label: 'Resources',
                        viewElement: () => {
                            return <Resources user={this.state.user}/>
                        }
                    },
                    users: {
                        pageIndicator: Page.USERS,
                        label: 'Users',
                        viewElement: () => {
                            return <Users user={this.state.user}/>
                        }
                    },
                    reports: {
                        pageIndicator: Page.REPORTS,
                        label: 'Reports',
                        viewElement: () => {
                            return <Reports user={this.state.user}/>
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
            }
        };

        this.state = {
            menuOpened: false,
            loggedIn: false,
            user: {},
            pageIndicator: Page.REPORTS,
        };
    }

    onLogin = (options) => {
        this.setState({
            ...this.state,
            loggedIn: true,
            user: {
                id: options.id,
                username: options.username,
                email: options.email,
                password: options.password,
                firstName: options.firstName,
                lastName: options.lastName,
                middleName: options.middleName,
                role: options.role
            }
        });
    };

    onLogoutTouchTap = () => {
        this.setState({...this.state, loggedIn: false, user: {}});
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
                <WelcomePage onLogin={this.onLogin}/>
            </div>;
        }

        return <div>
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
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.reports.pageIndicator)}
                        primaryText={this.constants.nav.items.reports.label}
                        leftIcon={<ActionAssessment/>}
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