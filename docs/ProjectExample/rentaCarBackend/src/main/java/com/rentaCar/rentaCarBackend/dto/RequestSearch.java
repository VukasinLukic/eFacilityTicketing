package com.rentaCar.rentaCarBackend.dto;

/**
 *
 * @author Saki
 */
public class RequestSearch implements DomainDTO {

    private String searchType;
    private String searchField;

    public RequestSearch() {
    }

    public RequestSearch(String searchType, String searchField) {
        this.searchType = searchType;
        this.searchField = searchField;
    }

    public String getSearchType() {
        return searchType;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }
}
