import React from 'react';

export default class Message extends React.Component {

    render() {
        if (this.props.message)
            return (
                <div className={`message ${this.props.message.type}`}>
                    {this.props.message.content}
                </div>
            );
        else
            return null;
    }
}