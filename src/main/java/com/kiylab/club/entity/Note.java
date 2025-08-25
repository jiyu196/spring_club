package com.kiylab.club.entity;

import jakarta.persistence.*;
import lombok.*;

// 회원이 메모장 용도로 사용
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Note extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long num;

  private String title;

  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  private ClubMember writer;

  public void changeTitle(String title) {
    this.title = title;
  }

  public void changeContent(String content) {
    this.content = content;
  }
}

