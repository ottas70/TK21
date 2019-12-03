import React from 'react';

export default class BaseForm extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.state = this.initState();
    }

    initState() {
        return {
            error: {},
            formValid: false
        };
    }

    error(name, error) {
        let errors = this.state.error;
        errors[name] = error;
        this.setState(errors, () => {
            this.isFormValid();
        });
    }

    message(type, content) {
        this.setState({
            message: {
                type: type,
                content: content
            }
        });
    }

    value() {
        throw new Error("You have to implement the method value!");
    }

    clearForm() {
        let state = this.initState();
        state.value = this.value();
        this.setState(state);
    }

    testRequired(state) {
        let tmp = state ? state : this.state;
        for (let field of this.required) {
            if (tmp.value[field]==="")
                return false;
        }
        return true;
    }

    isError(state) {
        let tmp = state ? state : this.state;

        for (let value of Object.values(tmp.error))
            if (value) return true;

        return false;
    }

    isFormValid() {
        this.setState((state) => ({
            formValid: (this.testRequired(state) && !this.isError(state))
        }));
    }

    appendValues(name, val) {
        let values = this.state.value;
        values[name] = val;
        return values;
    }

    somethingWrong(err) {
        this.message("failed", 'Něco se nepovedlo. Zkuste to později.');
        if (err) console.error(err);
    }
}