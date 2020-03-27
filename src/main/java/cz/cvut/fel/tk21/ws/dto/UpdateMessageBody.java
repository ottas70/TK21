package cz.cvut.fel.tk21.ws.dto;

import cz.cvut.fel.tk21.ws.dto.helperDto.ClubDateDto;

public class UpdateMessageBody {

    private ClubDateDto unsubscribe;

    private ClubDateDto subscribe;

    public ClubDateDto getUnsubscribe() {
        return unsubscribe;
    }

    public void setUnsubscribe(ClubDateDto unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    public ClubDateDto getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(ClubDateDto subscribe) {
        this.subscribe = subscribe;
    }
}
