package com.datn.laptopshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "news")
public class New {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "title")
    private String title;

    @Column(name = "img")
    private String img;

    @Column(name = "shortDescription", length = 500)
    private String shortDescription;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private Date created_at;

    @Column(name = "updated_at")
    private Date update_at;

    @Override
    public String toString() {
        return "New{" +
                "id=" + id +
                ", category=" + category.getId() +
                ", title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", content='" + content + '\'' +
                ", created_at=" + created_at +
                ", update_at=" + update_at +
                '}';
    }
}
