import React from 'react';

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserCircle, faTimesCircle, faPowerOff } from '@fortawesome/free-solid-svg-icons';

import LoginForm from "../../forms/LoginForm";
import IsLogged from "../../authentication/IsLogged";
import {UserContext} from "../../contexts";

export default class Login extends React.Component {
    static contextType = UserContext;

    constructor(props) {
        super(props);

        this.state = {
            show: false
        };
    }

    handleClick = (e) => {
        this.setState((state)=>({
            show: !state.show
        }));
    };

    handleLogout = (e) => {
        this.setState({
            show: false
        });
        this.context.logout();
    };

    render() {
        return (
            <section className="login">
                <IsLogged>
                    <FontAwesomeIcon className="corner-icon" onClick={this.handleLogout} icon={faPowerOff} />
                </IsLogged>
                <IsLogged false>
                    {
                        this.state.show ?
                            <FontAwesomeIcon className="corner-icon" onClick={this.handleClick} icon={faTimesCircle} />
                            :
                            <FontAwesomeIcon className="corner-icon" onClick={this.handleClick} icon={faUserCircle} />
                    }

                    <LoginForm className={'login-form-wrap '+ (this.state.show ? "show" : "")} />
                </IsLogged>
            </section>
        );
    }
}