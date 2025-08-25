package com.kiylab.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteDTO {

  private Long num;

  private String title;

  private String content;

  private String writerEmail;  // NotRepository의 writer와 구분하려고 Email적음

  private LocalDateTime regDate, modDate;
}
