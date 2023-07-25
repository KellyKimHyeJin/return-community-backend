package com.khureturn.community.service;

import com.khureturn.community.domain.Member;
import com.khureturn.community.domain.diary.Diary;
import com.khureturn.community.domain.diary.DiaryFile;
import com.khureturn.community.dto.DiaryConverter;
import com.khureturn.community.dto.DiaryRequestDto;
import com.khureturn.community.dto.DiaryResponseDto;
import com.khureturn.community.dto.MemberResponseDto;
import com.khureturn.community.exception.NotFoundException;
import com.khureturn.community.repository.DiaryFileRepository;
import com.khureturn.community.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService{

    private final DiaryRepository diaryRepository;
    private final DiaryFileRepository diaryFileRepository;

    @Transactional
    public Diary create(List<MultipartFile> mediaList, DiaryRequestDto.CreateDiaryDto request, Principal principal) throws IOException {

        Diary diary = DiaryConverter.toDiary(request, (Member) principal);
        diaryRepository.save(diary);
        DiaryFile diaryFile = DiaryConverter.toDiaryFile(request, DiaryFileService.fileUpload(mediaList), diary);
        diaryFileRepository.save(diaryFile);
        return diary;
    }

    @Transactional
    public Diary update(Long diaryId, DiaryRequestDto.UpdateDiaryDto request){
        Diary diary = diaryRepository.findById(diaryId).get();
        diary.update(request.getTitle(), request.getContent(), request.getIsAnonymous());
        return diary;
    }

    @Transactional
    public void delete(Long diaryId){
        diaryRepository.deleteById(diaryId);
    }

    public Diary findById(Long diaryId){
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException("Diary를 찾을 수 없습니다"));;
        diaryRepository.save(diary);
        diary.increaseHit();
        return diaryRepository.findById(diaryId).get();
    }


    public List<Diary> findAllByMember(Member member){
        List<Diary> diaries = diaryRepository.findAllByMember(member.getMemberId());
        return diaries;

    }

    public boolean findByMember(Long memberId){
        return diaryRepository.existsByMember(memberId);
    }

    public List<Diary> getPage(Long cursorId, int size){
        PageRequest pageRequest = PageRequest.of(0,size);
        Page<Diary> fetchPages = diaryRepository.findByIdLessThanOrderByCreatedAtDesc(cursorId, pageRequest);
        return fetchPages.getContent();
    }

    public List<Diary> getPageByLike(Long cursorId, int size, String search){
        PageRequest pageRequest = PageRequest.of(0,size);
        Page<Diary> fetchPages = diaryRepository.findByDiaryContentContainingIgnoreCaseAndIdLessThanOrderByDiaryLikeCountDesc(search, cursorId, pageRequest);
        return fetchPages.getContent();
    }

    public List<Diary> getPageByView(Long cursorId, int size, String search){
        PageRequest pageRequest = PageRequest.of(0,size);
        Page<Diary> fetchPages = diaryRepository.findByDiaryContentContainingIgnoreCaseAndIdLessThanOrderByDiaryViewCountDesc(search, cursorId,pageRequest);
        return fetchPages.getContent();
    }


}
