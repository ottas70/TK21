package cz.cvut.fel.tk21.rest.dto;

import java.util.List;

public class ClubSearchDto {

    private PageDto page;

    private List<ClubDto> clubs;

    public ClubSearchDto(List<ClubDto> clubs, PageDto page) {
        this.clubs = clubs;
        this.page = page;
    }

    public ClubSearchDto(List<ClubDto> clubs, int currentPage, int lastPage) {
        this.clubs = clubs;
        this.page = new PageDto(currentPage, lastPage);
    }

    public PageDto getPage() {
        return page;
    }

    public void setPage(PageDto page) {
        this.page = page;
    }

    public List<ClubDto> getClubs() {
        return clubs;
    }

    public void setClubs(List<ClubDto> clubs) {
        this.clubs = clubs;
    }
}
