import React from 'react';
import {TITLE} from "../../constants";

export default class Heading1 extends React.Component {

    componentDidMount() {
        if (this.props.setTitle)
            document.title = `${this.props.caption} | ${TITLE}`;
    }

    render() {
        return (
            <h1><span/>{this.props.caption}</h1>
        );
    }
}