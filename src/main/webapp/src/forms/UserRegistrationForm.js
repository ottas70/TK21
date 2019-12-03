import React from 'react';

import Message from "./Message";
import TextInput from "./fields/TextInput";
import Button from "./fields/Button";

import {isEmail} from "../validators";
import {SERVER_ADDRESS} from "../constants";
import BaseForm from "./BaseForm";

export default class UserRegistrationForm extends BaseForm {
    constructor(props) {
        super(props);

        this.state.value = this.value();
        this.required = ["name","surname","email","password"];
    }

    value() {
        return {
            name: "",
            surname: "",
            email: "",
            password: "",
            password_check: ""
        };
    }

    handleChangeInputs = (e) => {
        const name = e.target.name;
        const val = e.target.value;
        this.setState({value: this.appendValues(name, val)}, () =>
            this.error(name, val==="" ? "Vyplňte pole" : undefined)
        );
    };

    handleChangeEmail = (e) => {
        const val = e.target.value;
        this.setState({value: this.appendValues("email", val)}, () =>
            this.error("email", !isEmail(val) ? "Napište platnou emailovou adresu" : undefined)
        );
    };

    handleChangePass = (e) => {
        const val = e.target.value;
        const name = e.target.name;
        this.setState({value:this.appendValues(name, val)}, () => {
            if (this.state.value.password!==this.state.value.password_check)
                this.error("password", "Hesla se neshodují");
            else
                this.error("password", undefined);
        });
    };

    handleSubmit = (e) => {
        e.preventDefault();

        this.setState({
            loading: true
        });

        const body = {
            name:this.state.value.name,
            surname:this.state.value.surname,
            email:this.state.value.email,
            password:this.state.value.password
        };

        fetch(SERVER_ADDRESS+"/user", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        })
            .then((response) => {
                switch(response.status) {
                    case 201:
                        this.clearForm();
                        this.message( "success", `
                        Registrace proběhla v pořádku. Odeslali jsme Vám ověřovací e-mail. Jakmile ověříte Váš e-mail,
                        můžete se přihlásit.
                            `);
                        break;

                    case 409:
                        this.message("failed", 'Účet s tímto emailem již existuje.');
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
                <div className="form-row">
                    <TextInput caption="Jméno" name="name" value={this.state.value.name} error={this.state.error.name}
                               handleChange={this.handleChangeInputs} />
                    <TextInput caption="Příjmení" name="surname" value={this.state.value.surname} error={this.state.error.surname}
                               handleChange={this.handleChangeInputs} />
                </div>

                <div className="form-row">
                    <TextInput caption="Heslo" name="password" type="password" value={this.state.value.password}
                               handleChange={this.handleChangePass} />
                    <TextInput caption="Heslo znovu" name="password_check" type="password" value={this.state.value.password_check}
                               handleChange={this.handleChangePass} error={this.state.error.password} />
                </div>

                <TextInput caption="Email" name="email" type="email" value={this.state.value.email}
                           handleChange={this.handleChangeEmail} error={this.state.error.email} />

                <div className="form-group">
                    <Button caption="Registrovat" loading={this.state.loading} disabled={!this.state.formValid} />
                </div>
                <Message message={this.state.message} />
            </form>
        );
    }
}