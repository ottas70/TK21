import React from 'react';

import BaseForm from "./BaseForm";
import TextInput from "./fields/TextInput";
import Button from "./fields/Button";
import {isEmail} from "../validators";
import {SERVER_ADDRESS} from "../constants";
import Message from "./Message";
import {UserContext} from "../contexts";

export default class LoginForm extends BaseForm {
    static contextType = UserContext;

    constructor(props, context) {
        super(props, context);

        this.state.value = this.value();
        this.required = ["email","password"];
    }

    value() {
        return {
            email: "",
            password: ""
        };
    }

    handleSubmit = (e) => {
        e.preventDefault();

        this.setState({
            loading: true
        });

        const body = {
            username: this.state.value.email,
            password: this.state.value.password
        };

        fetch(SERVER_ADDRESS+"/authenticate", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            mode: 'cors',
            credentials: 'include',
            body: JSON.stringify(body)
        })
            .then((response) => {
                switch(response.status) {
                    case 200:
                        response.json()
                            .then((data) => {
                                this.clearForm();
                                this.message( "success", `
                                    Příhlášení proběhlo úspěšně.
                                `);
                                let login = data;
                                login.isLoggedIn = true;
                                this.context.login(login);
                             })
                            .catch((err) => this.somethingWrong(err));
                        break;

                    case 401:
                        response.json()
                            .then((data) => {
                                this.message("failed", data.verifiedError ?
                                    'Emailová adresa zatím nebyla ověřena.' : 'Nesprávné přihlašovací údaje.');
                            })
                            .catch((err) => this.somethingWrong(err));
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

    handleChangeEmail = (e) => {
        const val = e.target.value;
        this.setState({value: this.appendValues("email", val)}, () =>
            this.error("email", !isEmail(val) ? "Napište platnou emailovou adresu" : undefined)
        );
    };

    handleChangePass = (e) => {
        const val = e.target.value;
        this.setState({value: this.appendValues("password", val)}, ()=>this.isFormValid());
    };

    render() {
        return (
            <section className={this.props.className}>
                <form onSubmit={this.handleSubmit}>
                    <h3>Přihlášení</h3>
                    <TextInput caption="Email" name="email" type="email" value={this.state.value.email}
                               handleChange={this.handleChangeEmail} error={this.state.error.email} />
                    <TextInput caption="Heslo" name="password" type="password" value={this.state.value.password}
                               handleChange={this.handleChangePass} error={this.state.error.password} />
                    <div className="form-group">
                        <Button caption="Přihlásit" loading={this.state.loading} disabled={!this.state.formValid} />
                    </div>
                    <Message message={this.state.message} />
                </form>
            </section>
        );
    }
}