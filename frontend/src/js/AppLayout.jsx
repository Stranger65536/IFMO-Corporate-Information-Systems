import React from "react";
import _ from "underscore";
import AppBar from "material-ui/AppBar";
import Drawer from "material-ui/Drawer";
import List from "material-ui/List";
import ListItem from "material-ui/List";
import getMuiTheme from "material-ui/styles/getMuiTheme";
import {MuiThemeProvider, lightBaseTheme} from "material-ui/styles";
// import makeSelectable from "material-ui/List";
import ActionDashboard from "material-ui/svg-icons/action/dashboard";
import ActionVisibility from "material-ui/svg-icons/action/visibility";
import ActionSettings from "material-ui/svg-icons/action/settings";
import PoolCreation from "./PoolCreation.jsx";
import Monitor from "./Monitor.jsx";
import Settings from "./Settings.jsx";
import WelcomePage from "./WelcomePage.jsx";

const lightMuiTheme = getMuiTheme(lightBaseTheme);

// const SelectableList = makeSelectable(List);

export default class AppLayout extends React.Component {
    constants = {
        pageTitle: 'ReservIO',
        nav: {
            width: 250,
            swipeWidth: 100,
            items: {
                poolCreation: {
                    anchor: 'pool-creation',
                    label: 'Smart Virtual Pool Creation',
                    viewElement: <div/>//<PoolCreation/>
                },
                monitor: {
                    anchor: 'monitor',
                    label: 'Smart Virtual Pools Monitoring',
                    viewElement: <div/>//<Monitor/>
                },
                settings: {
                    anchor: 'settings',
                    label: 'Settings',
                    viewElement: <div/>//<Settings/>
                }
            }
        }
    };

    constructor(props) {
        super(props);

        this.state = {
            menuOpened: false,
            loggedIn: false,
            navSelectedItemAnchor: this.getAnchorLink()
        };
    }

    getCurrentViewElement = () => {
        let instance = this;

        return _.findWhere(
            _.map(
                _.keys(this.constants.nav.items), function (property) {
                    return instance.constants.nav.items[property];
                }
            ), {anchor: instance.state.navSelectedItemAnchor}
        ).viewElement;
    };

    getAnchorLink = () => {
        const anchor = '#/';
        let anchorPos = window.location.hash.indexOf(anchor);
        return anchorPos == -1
            ? this.constants.nav.items.poolCreation.anchor
            : window.location.hash.substring(anchorPos + anchor.length);
    };

    handleUpdateSelectedItem = (e, item) => {
        this.setState({navSelectedItemAnchor: item});
        window.location = '/#/' + item;
        this.getCurrentViewElement();
    };

    handleToggle = () => this.setState({menuOpened: !this.state.menuOpened});

    render() {
        return (
            <MuiThemeProvider muiTheme={lightMuiTheme}>
                <WelcomePage/>
                {/*<div>*/}
                    {/*<AppBar*/}
                        {/*className='app-bar'*/}
                        {/*title={this.constants.pageTitle}*/}
                        {/*onLeftIconButtonTouchTap={this.handleToggle}*/}
                    {/*/>*/}
                    {/*<Drawer*/}
                        {/*docked={false}*/}
                        {/*width={this.constants.nav.width}*/}
                        {/*open={this.state.menuOpened}*/}
                        {/*containerClassName='left-nav'*/}
                        {/*overlayClassName='nav-overlay'*/}
                        {/*swipeAreaWidth={this.swipeWidth}*/}
                        {/*onRequestChange={*/}
                            {/*open => this.setState({menuOpened})*/}
                        {/*}>*/}
                        {/*<List*/}
                            {/*valueLink={{*/}
                                {/*value: this.state.navSelectedItemAnchor,*/}
                                {/*requestChange: this.handleUpdateSelectedItem*/}
                            {/*}}>*/}
                            {/*<ListItem*/}
                                {/*value={this.constants.nav.items.poolCreation.anchor}*/}
                                {/*primaryText={this.constants.nav.items.poolCreation.label}*/}
                                {/*leftIcon={<ActionDashboard className='two-line-icon'/>}*/}
                            {/*/>*/}
                            {/*<ListItem*/}
                                {/*value={this.constants.nav.items.monitor.anchor}*/}
                                {/*primaryText={this.constants.nav.items.monitor.label}*/}
                                {/*leftIcon={<ActionVisibility className='two-line-icon'/>}*/}
                            {/*/>*/}
                            {/*<ListItem*/}
                                {/*value={this.constants.nav.items.settings.anchor}*/}
                                {/*primaryText={this.constants.nav.items.settings.label}*/}
                                {/*leftIcon={<ActionSettings/>}*/}
                            {/*/>*/}
                        {/*</List>*/}
                    {/*</Drawer>*/}
                    {/*{this.getCurrentViewElement()}*/}
                {/*</div>*/}
            </MuiThemeProvider>
        )
    }
}