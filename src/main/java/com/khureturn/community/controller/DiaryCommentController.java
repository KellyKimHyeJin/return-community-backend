package com.khureturn.community.controller;


import com.khureturn.community.domain.diary.Diary;
import com.khureturn.community.domain.diary.DiaryComment;
import com.khureturn.community.dto.DiaryCommentRequestDto;
import com.khureturn.community.dto.DiaryCommentResponseDto;
import com.khureturn.community.service.DiaryCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryCommentController {

    private final DiaryCommentService diaryCommentService;

    @PostMapping("/{postId}/comment")
    public ResponseEntity<DiaryCommentResponseDto.CreateDiaryCommentDto> createComment(@PathVariable(name="postId")Long postId, @RequestBody DiaryCommentRequestDto.CreateCommentDto request){
        DiaryComment diaryComment = diaryCommentService.create(postId, request);
        return ResponseEntity.ok(DiaryCommentResponseDto.CreateDiaryCommentDto.builder().commentId(diaryComment.getId()).build());
    }

    @PatchMapping("{postId}/comment/{commentId}")
    public ResponseEntity<DiaryCommentResponseDto.UpdateDiaryCommentDto> updateComment(@PathVariable(name="postId")Long postId, @PathVariable(name = "commentId")Long commentId, @RequestBody DiaryCommentRequestDto.UpdateCommentDto request){
        DiaryComment diaryComment = diaryCommentService.update(postId, commentId, request);
        return ResponseEntity.ok(DiaryCommentResponseDto.UpdateDiaryCommentDto.builder().commentId(diaryComment.getId()).build());
    }



}