import React from 'react';
import {Link} from "react-router-dom";
import {APP_ADDRESS} from "../../constants";
import {ReactComponent as Logo} from "../../files/images/logo-without-text.svg";

import './Header.css';
export default class Header extends React.Component {
    render() {
        return (
            <section className={`header ${this.props.absolute && "absolute"}`}>
                <div className="left">
                    <Link to={APP_ADDRESS}>
                        <Logo className="logo-svg" />
                    </Link>
                </div>
                <div className="right">
                    {this.props.children}
                </div>
            </section>
        );
    }
}