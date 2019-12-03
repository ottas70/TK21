import React from 'react';
import {withRouter} from 'react-router-dom';
import Header from "./Header";
import {faChevronCircleLeft} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

class HeaderBack extends React.Component {

    handleBackClick = (e) => {
        this.props.history.goBack();
    };


    render() {
        return (
            <Header absolute>
                <FontAwesomeIcon className="back-icon corner-icon" onClick={this.handleBackClick} icon={faChevronCircleLeft} />
            </Header>
        );
    }
}

export default withRouter(HeaderBack);