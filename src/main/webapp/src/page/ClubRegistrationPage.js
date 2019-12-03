import React from 'react';
import Heading1 from "./components/Heading1";
import ClubRegistrationForm from "../forms/ClubRegistrationForm";
import MajorMinor from "./components/MajorMinor";

export default class ClubRegistrationPage extends React.Component {
    render() {
        return (
            <MajorMinor picture="court2" reverse>
                <Heading1 caption="Registrace klubu" setTitle />
                <ClubRegistrationForm />
            </MajorMinor>
        );
    }
}