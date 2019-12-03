import React from 'react';
import HeaderBack from "./HeaderBack";

import "./MajorMinor.css";
import Heading1 from "./Heading1";
import ClubRegistrationForm from "../../forms/ClubRegistrationForm";
export default class MajorMinor extends React.Component {
    render() {
        return (
            <section className="page major-minor">
                {
                    !this.props.reverse &&
                        <section className="major-side right-line">
                            <HeaderBack />
                            <section className="major-minor-wrap">
                                {this.props.children}
                            </section>
                        </section>
                }
                <section className={`minor-side ${this.props.picture}`} />
                {
                    this.props.reverse &&
                        <section className="major-side left-line">
                            <HeaderBack />
                            <section className="major-minor-wrap">
                                {this.props.children}
                            </section>
                        </section>
                }
            </section>
        );
    }
}