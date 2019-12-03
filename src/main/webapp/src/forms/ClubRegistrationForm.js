import React from 'react';
import BaseForm from "./BaseForm";
import TextInput from "./fields/TextInput";
import Button from "./fields/Button";
import Message from "./Message";
import {isZip} from "../validators";
import {APP_ADDRESS, SERVER_ADDRESS} from "../constants";

import { withRouter } from 'react-router-dom';

class ClubRegistrationForm extends BaseForm {
    constructor(props) {
        super(props);

        this.state.value = this.value();
        this.required = ["name","street","city","zip"];
    }

    value() {
        return {
            name: "",
            street: "",
            city: "",
            zip: ""
        };
    }

    handleChangeInputs = (e) => {
        const name = e.target.name;
        const val = e.target.value;
        this.setState({value: this.appendValues(name, val)}, () =>
            this.error(name, val==="" ? "Vyplňte pole" : undefined)
        );
    };

    handleChangeZip = (e) => {
        const name = e.target.name;
        const val = e.target.value;
        this.setState({value: this.appendValues(name, val)}, () =>
            this.error(name, !isZip(val) ? "Napište platné PSČ" : undefined)
        );
    };

    handleSubmit = (e) => {
        e.preventDefault();

        this.setState({
            loading: true
        });

        const body = {
            name: this.state.value.name,
            address: {
                street: this.state.value.street,
                city: this.state.value.city,
                zip: this.state.value.zip
            }
        };

        fetch(SERVER_ADDRESS+"/club", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: "include",
            body: JSON.stringify(body)
        })
            .then((response) => {
                switch(response.status) {
                    case 201:
                        // TODO přesměrování na stránku klubu
                        this.clearForm();
                        this.message( "success", `
                        Klub byl úspěšně vytvořen.
                            `);
                        break;

                    case 401:
                        // TODO hláška odhlášení
                        this.props.history.push(APP_ADDRESS);
                        break;

                    default:
                        this.somethingWrong();
                        break;
                }
            })
            .catch((err) => this.somethingWrong(err))
            .finally(() => {
                this.setState({
                    loading: false
                });
            });
    };

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <TextInput caption="Název klubu" name="name" type="name" value={this.state.value.name}
                                error={this.state.error.name} handleChange={this.handleChangeInputs} />

                <div className="form-row">
                    <TextInput caption="Obec" name="city" value={this.state.value.city} error={this.state.error.city}
                               handleChange={this.handleChangeInputs} />
                    <TextInput caption="PSČ" name="zip" value={this.state.value.zip} error={this.state.error.zip}
                                handleChange={this.handleChangeZip} />
                </div>

                <TextInput caption="Ulice a č. p." name="street" value={this.state.value.street} error={this.state.error.street}
                        handleChange={this.handleChangeInputs} />

                <div className="form-group">
                    <Button caption="Registrovat" loading={this.state.loading} disabled={!this.state.formValid} />
                </div>
                <Message message={this.state.message} />
            </form>
        );
    }
}

export default withRouter(ClubRegistrationForm);