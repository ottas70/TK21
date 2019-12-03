import React from 'react';
import {Link} from "react-router-dom";

import {APP_ADDRESS, TITLE} from "../constants";

import IsLogged from "../authentication/IsLogged";
import HeaderLogin from "./components/HeaderLogin";

import './MainPage.css';
export default class MainPage extends React.Component {

    componentDidMount() {
        document.title = TITLE;
    }

    render() {
        return (
            <section className="page centered main-page">
                <HeaderLogin />
                <section>
                    <div className="main-name">
                        TK21
                    </div>
                    <Link to={`${APP_ADDRESS}registrace-uzivatele`}>
                        Registrace uživatele
                    </Link>
                    <IsLogged>
                        <Link to={`${APP_ADDRESS}registrace-klubu`}>
                            Registrace klubu
                        </Link>
                    </IsLogged>
                </section>
            </section>
        );
    }
}