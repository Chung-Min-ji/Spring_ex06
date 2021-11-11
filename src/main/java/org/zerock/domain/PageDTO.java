package org.zerock.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDTO {

    private int startPage;      // 현재 화면에서 첫 페이지
    private int endPage;        // 현재 화면에서 끝 페이지
    private boolean prev, next;

    private int total;          // 전체 데이터 수
    private Criteria cri;

    public PageDTO(Criteria cri, int total){
        this.cri = cri;
        this.total = total;

        this.endPage = (int)(Math.ceil(cri.getPageNum() / 10.0)) * 10;

        this.startPage = this.endPage - 9;

        int realEnd = (int)(Math.ceil((total * 1.0) / cri.getAmount()));

        if(realEnd < this.endPage){
            this.endPage = realEnd;
        } //if : 전체 끝 페이지가, 현재 화면의 끝 페이지보다 작으면

        this.prev = this.startPage > 1;

        this.next = this.endPage < realEnd;
    } //constructor
} //end class
