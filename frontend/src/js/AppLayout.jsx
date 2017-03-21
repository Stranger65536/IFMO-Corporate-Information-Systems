import React from "react";
import _ from "underscore";
import Menu from "material-ui/Menu";
import AppBar from "material-ui/AppBar";
import Drawer from "material-ui/Drawer";
import MenuItem from "material-ui/MenuItem";
import IconButton from 'material-ui/IconButton';
import SocialPublic from "material-ui/svg-icons/social/public";
import ActionSettings from "material-ui/svg-icons/action/settings";
import ActionExitToApp from "material-ui/svg-icons/action/exit-to-app";
import ActionDateRange from "material-ui/svg-icons/action/date-range";
import AccountCircle from "material-ui/svg-icons/action/account-circle";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import {MuiThemeProvider} from "material-ui/styles";
import WelcomePage from "./WelcomePage.jsx";

//TODO getLoggedInViewElement render error on invalid anchor
//TODO modal warn before logout
//TODO state clear on logout
//TODO anchor clear on logout

const emcMuiTheme = getMuiTheme({
    palette: {
        primary1Color: '#2c95dd',
    },
});

export default class AppLayout extends React.Component {
    constructor(props) {
        super(props);

        this.constants = {
            pageTitle: <div className='header-logo'/>,
            nav: {
                width: 250,
                swipeWidth: 100,
                items: {
                    appointments: {
                        anchor: 'appointments',
                        label: 'Appointments',
                        viewElement: () => {
                            return <div>Appointments</div>
                        }
                    },
                    resources: {
                        anchor: 'resources',
                        label: 'Resources',
                        viewElement: () => {
                            return <div>Resources</div>
                        }
                    },
                    users: {
                        anchor: 'users',
                        label: 'Users',
                        viewElement: () => {
                            return <div>Users</div>
                        }
                    },
                    settings: {
                        anchor: 'settings',
                        label: 'Settings',
                        viewElement: () => {
                            return <div>Settings</div>
                        }
                    }
                }
            }
        };

        this.state = {
            menuOpened: false,
            loggedIn: true,
            token: null,
            user: {
                username: 'username',
                id: 'ID',
                firstName: 'Firstname',
                lastName: 'Lastname',
                middleNAme: 'Middlename',
                email: ''
            },
            navSelectedItemAnchor: this.getAnchorLink()
        };
    }

    onLoggedIn = () => {
        this.setState({...this.state, loggedIn: true});
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
            ), {anchor: instance.state.navSelectedItemAnchor}
        ).viewElement();
    };

    onDrawerRequestChange = (open) => {
        this.setState({...this.state, menuOpened: open});
    };

    //noinspection FunctionWithMultipleReturnPointsJS
    getCurrentViewElement = () => {
        if (!this.state.loggedIn) {
            return <WelcomePage onLogin={this.onLoggedIn}/>;
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
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.appointments.anchor)}
                        primaryText={this.constants.nav.items.appointments.label}
                        leftIcon={<ActionDateRange/>}
                    />
                    <MenuItem
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.resources.anchor)}
                        primaryText={this.constants.nav.items.resources.label}
                        leftIcon={<SocialPublic/>}
                    />
                    <MenuItem
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.users.anchor)}
                        primaryText={this.constants.nav.items.users.label}
                        leftIcon={<AccountCircle/>}
                    />
                    <MenuItem
                        onTouchTap={(event) => this.onMenuItemTouchTap(event, this.constants.nav.items.settings.anchor)}
                        primaryText={this.constants.nav.items.settings.label}
                        leftIcon={<ActionSettings/>}
                    />
                </Menu>
            </Drawer>
            {this.getLoggedInViewElement()}
        </div>
    };

    getAnchorLink = () => {
        const anchor = '#';
        const anchorPos = window.location.hash.indexOf(anchor);
        return anchorPos == -1
            ? this.constants.nav.items.appointments.anchor
            : window.location.hash.substring(anchorPos + anchor.length);
    };

    replaceAnchor = (value) => {
        const anchor = '#';
        const anchorPos = window.location.hash.indexOf(anchor);
        if (anchorPos == -1) {
            window.location += anchor + value;
        } else {
            window.location = window.location.hash.substring(0, anchorPos) + anchor + value;
        }
    };

    onMenuItemTouchTap = (event, anchor) => {
        this.setState({...this.state, navSelectedItemAnchor: anchor});
        this.replaceAnchor(anchor);
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