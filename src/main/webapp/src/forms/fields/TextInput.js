import React from 'react';

export default class TextInput extends React.Component {
    render() {
        return (
            <label className="form-group">
                <span className="caption">{this.props.caption}</span>
                <input type={this.props.type ? this.props.type : "text"}
                       name={this.props.name}
                       value={this.props.value}
                       maxLength={this.props.maxLength ? this.props.maxLength : 250}
                       onChange={this.props.handleChange}
                       className={this.props.error && "is-invalid"}
                />
                <div className="invalid-feedback">
                    {this.props.error}
                </div>
            </label>
        );
    }
}