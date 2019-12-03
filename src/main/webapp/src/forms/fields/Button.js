import React from 'react';
import {ReactComponent as Loading} from "../../files/images/loading.svg";

export default class Button extends React.Component {
    render() {
        return (
            <button disabled={this.props.loading || this.props.disabled}>
                {
                    this.props.loading &&
                        <Loading className="loading" />
                }
                {this.props.caption}
            </button>
        );
    }
}