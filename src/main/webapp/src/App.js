import React from 'react';
import {
  BrowserRouter,
  Switch,
  Route
} from "react-router-dom";

import MainPage from "./page/MainPage";
import UserRegistrationPage from "./page/UserRegistrationPage";

import {UserContext} from "./contexts";

import './forms/Forms.css';
import {APP_ADDRESS, DEFAULT_AUTH, SERVER_ADDRESS} from "./constants";
import NotFound from "./page/NotFound";
import ClubRegistrationPage from "./page/ClubRegistrationPage";
import {ReactComponent as Loading} from "./files/images/loading.svg";
import Confirmation from "./page/Confirmation";

export default class App extends React.Component {
    constructor(props, context) {
        super(props, context);

        this.state = {
            user: DEFAULT_AUTH,
            loading: true
        }
    }

    hideLoading() {
        this.setState({
            loading: false
        });
    }

    showLoading() {
        this.setState({
            loading: true
        });
    }

    componentDidMount() {
        fetch(SERVER_ADDRESS+"/user/me", {
            credentials: 'include'
        })
            .then((response)=>{
                if (response.status===200) {
                    response.json().then((data)=>{
                        let login = data;
                        login.isLoggedIn = true;
                        this.logIn(login);
                    });
                } else this.hideLoading();
            })
            .catch(()=>this.hideLoading());
    }

    logIn(auth) {
        this.setState({
            user: auth
        }, ()=>this.hideLoading());
    }

    logOut() {
        this.showLoading();
        fetch(SERVER_ADDRESS+"/logout", {
            method: "GET",
            credentials: 'include'
        })
            .then((response) => {
                if (response.status===204) {
                    this.setState({
                        user: DEFAULT_AUTH
                    });
                } else {
                    // TODO
                }
            })
            .finally(()=>this.hideLoading());
    }

    render() {
        const login = {
            user: this.state.user,
            login: (auth) => this.logIn(auth),
            logout: () => this.logOut()
        };

        return (
            <BrowserRouter>
                <div className={`big-loading ${!this.state.loading && "hide"}`}>
                    <Loading />
                </div>
                <UserContext.Provider value={login}>
                    <Switch>
                        <Route exact path={APP_ADDRESS} component={MainPage} />
                        <Route path={`${APP_ADDRESS}registrace-uzivatele`} component={UserRegistrationPage} />
                        <Route path={`${APP_ADDRESS}overeni`} component={Confirmation} />
                        {
                            this.state.user.isLoggedIn &&
                                <Route path={`${APP_ADDRESS}registrace-klubu`} component={ClubRegistrationPage} />
                        }
                        <Route component={NotFound} />
                    </Switch>
                </UserContext.Provider>
            </BrowserRouter>
        );
    }
}
