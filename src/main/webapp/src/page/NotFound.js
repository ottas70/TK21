import React from 'react';
import Heading1 from "./components/Heading1";
import {Link} from "react-router-dom";
import {APP_ADDRESS} from "../constants";

import {faExclamationCircle} from '@fortawesome/free-solid-svg-icons';
import {faLink} from '@fortawesome/free-solid-svg-icons';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export default class NotFound extends React.Component {
    render() {
        return (
            <section className="page centered">
                <section class="not-found-wrap">
                    <Heading1 caption="Stránku nemůžeme zobrazit" setTitle />
                    <p><strong>
                        <FontAwesomeIcon icon={faExclamationCircle} /> Bohužel tuto stránku nemůžeme zobrazit, protože:
                    </strong></p>
                    <p>Je možné, že jste byl odhlášen.</p>
                    <p>Je možné, že stránka neexistuje.</p>
                    <p style={{marginBottom: 0}}>
                        <Link to={APP_ADDRESS}>
                            <FontAwesomeIcon icon={faLink} /> Na hlavní stranu
                        </Link>
                    </p>
                </section>
            </section>
        );
    }
}