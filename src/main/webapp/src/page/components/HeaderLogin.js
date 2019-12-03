import React from 'react';
import Header from "./Header";
import Login from "./Login";

export default class HeaderLogin extends React.Component {
    render() {
        return (
            <Header>
                <Login />
            </Header>
        );
    }
}