import React from 'react';
import {UserContext} from "../contexts";

export default class IsLogged extends React.Component {
    static contextType = UserContext;

    render() {
        if (this.props.false)
            return !this.context.user.isLoggedIn ? this.props.children : null;
        return this.context.user.isLoggedIn ? this.props.children : null;
    }
}