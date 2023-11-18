package com.datn.laptopshop.dto;

import com.datn.laptopshop.entity.New;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {
    private Long id;
    private String categoryName;
    private String title;
    private String img;
    private String shortDescription;
    private String content;

    public NewsDto toNewsDto(New n){
        return new NewsDto(n.getId(), n.getCategory().getName(), n.getTitle(), n.getImg(), n.getShortDescription(),n.getContent());
    }

    @Override
    public String toString() {
        return "NewsDto{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
