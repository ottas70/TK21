import React from 'react';
import Heading1 from "./components/Heading1";
import {ReactComponent as Loading} from "../files/images/loading.svg";

export default class Confirmation extends React.Component {
    render() {
        return (
            <section className="page centered">
                <section class="not-found-wrap">
                    <Heading1 caption="Ověření e-mailové adresy" setTitle />
                    <div class="confirmation-loading">
                        <Loading style={{height: "50px"}} />
                        Počkejte chvíli, ověřujeme Vaši e-mailovou adresu...
                    </div>
                </section>
            </section>
        );
    }
}