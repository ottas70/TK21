import React from 'react';

import UserRegistrationForm from "../forms/UserRegistrationForm";
import Heading1 from "./components/Heading1";
import MajorMinor from "./components/MajorMinor";

export default class UserRegistrationPage extends React.Component {

    render() {
        return (
            <MajorMinor>
                <Heading1 caption="Registrace uživatele" setTitle/>
                <UserRegistrationForm/>
            </MajorMinor>
        );
    }
}