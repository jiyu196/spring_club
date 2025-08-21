package com.kiylab.club.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

  @MappedSuperclass
  @Getter
  @EntityListeners(AuditingEntityListener.class)
  @EqualsAndHashCode(exclude = "modDate")
  public class BaseEntity {
    @CreatedDate
    @Column(updatable = false, name = "regdate")
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "moddate")
    private LocalDateTime modDate;
  }

