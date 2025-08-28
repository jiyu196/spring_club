package com.kiylab.club.service;

import com.kiylab.club.dto.NoteDTO;
import com.kiylab.club.entity.Note;
import com.kiylab.club.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService{
  private final NoteRepository noteRepository;

  @Override
  public Long register(NoteDTO noteDTO) {
    Note note = dtoToEntity(noteDTO);

    log.info("===============================");
    log.info(note);
    noteRepository.save(note);
    return note.getNum();
  }

  @Override
  @PostAuthorize("returnObject.writerEmail == authentication.principal.email")
  public NoteDTO get(Long num) {
    Optional<Note> result = noteRepository.getWithWriter(num);

//    if(result.isPresent()){
//      return entityToDto(result.get());
//    }
//
//    return null;

    return noteRepository.getWithWriter(num)
            .map(this::entityToDto)  // (noteDTO) -> entityToDTO(noteDTO)
            .orElse(null);
  }

  @Override
  public void modify(NoteDTO noteDTO) {
    Long num = noteDTO.getNum();
    Optional<Note> result = noteRepository.findById(num);

    if(result.isPresent()){
      Note note = result.get();
      note.changeTitle(noteDTO.getTitle());
      note.changeContent(noteDTO.getContent());
      noteRepository.save(note);
    }
  }

  @Override
  public void remove(Long num) {

    noteRepository.deleteById(num);
  }

  @Override
  public List<NoteDTO> getAllWithWriter(String writerEmail) {
    List<Note> noteList = noteRepository.getList(writerEmail);

    return noteList.stream()
            .map(note -> entityToDto(note))
            .collect(Collectors.toList());
  }

  @Override
  public Note dtoToEntity(NoteDTO noteDTO) {
    return NoteService.super.dtoToEntity(noteDTO);
  }
}
