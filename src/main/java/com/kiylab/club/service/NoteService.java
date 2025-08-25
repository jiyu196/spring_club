package com.kiylab.club.service;

import com.kiylab.club.dto.NoteDTO;
import com.kiylab.club.entity.ClubMember;
import com.kiylab.club.entity.Note;

import java.util.List;

public interface NoteService {
  Long register(NoteDTO noteDTO);

  NoteDTO get(Long num);

  void modify(NoteDTO noteDTO);

  void remove(Long num);

  List<NoteDTO> getAllWithWriter(String writerEmail);

  default Note dtoToEntity(NoteDTO noteDTO) {
    Note note = Note.builder()
            .num(noteDTO.getNum())
            .title(noteDTO.getTitle())
            .content(noteDTO.getContent())
            .writer(ClubMember.builder()
                    .email(noteDTO.getWriterEmail())
                    .build())
            .build();

    return note;
  }

  default NoteDTO entityToDto(Note note) {
    NoteDTO noteDTO = NoteDTO.builder()
            .num(note.getNum())
            .title(note.getTitle())
            .content(note.getContent())
            .writerEmail(note.getWriter().getEmail())
            .regDate(note.getRegDate())
            .modDate(note.getModDate())
            .build();

    return noteDTO;
  }
}
