package org.zerock.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.util.UriComponentsBuilder;


@Getter
@Setter
@ToString
public class Criteria {

    // 페이징 처리
    private int pageNum;
    private int amount;

    // 검색 처리
    private String type;
    private String keyword;

    public Criteria(){
        this(1, 10);
    } // default constructor

    public Criteria(int pageNum, int amount){
        this.pageNum = pageNum;
        this.amount = amount;
    } // constructor

    public String[] getTypeArr(){
        return type == null ? new String[]{} : type.split("");
    } //getTypeArr


    // 검색조건 유지하면서 이동
    public String getListLink(){
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("")
                // queryParam() 통해 필요한 파라미터 추가.
                .queryParam("pageNum", this.pageNum)
                .queryParam("amount", this.getAmount())
                .queryParam("type", this.getType())
                .queryParam("keyword", this.getKeyword());

        return builder.toUriString();
    } //getListLink

} //end class
