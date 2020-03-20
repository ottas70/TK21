package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.rest.dto.PageDto;

import java.util.List;

public class ClubSearchDto {

    private PageDto page;

    private List<BasicClubInfoDto> clubs;

    public ClubSearchDto(List<BasicClubInfoDto> clubs, PageDto page) {
        this.clubs = clubs;
        this.page = page;
    }

    public ClubSearchDto(List<BasicClubInfoDto> clubs, int currentPage, int lastPage) {
        this.clubs = clubs;
        this.page = new PageDto(currentPage, lastPage);
    }

    public PageDto getPage() {
        return page;
    }

    public void setPage(PageDto page) {
        this.page = page;
    }

    public List<BasicClubInfoDto> getClubs() {
        return clubs;
    }

    public void setClubs(List<BasicClubInfoDto> clubs) {
        this.clubs = clubs;
    }
}
