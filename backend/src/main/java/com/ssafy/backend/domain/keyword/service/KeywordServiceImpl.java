package com.ssafy.backend.domain.keyword.service;

import com.ssafy.backend.domain.favorites.entity.Favorite;
import com.ssafy.backend.domain.favorites.repository.FavoriteRepository;
import com.ssafy.backend.domain.keyword.api.request.GetKeyphraseRequest;
import com.ssafy.backend.domain.keyword.api.request.GetTopNKeywordRequest;
import com.ssafy.backend.domain.keyword.dto.GetKeyPhraseResponse;
import com.ssafy.backend.domain.keyword.dto.KeywordDto;
import com.ssafy.backend.domain.keyword.dto.KeywordStatisticDto;
import com.ssafy.backend.domain.keyword.dto.TopKeywordDTO;
import com.ssafy.backend.domain.keyword.entity.Keyword;
import com.ssafy.backend.domain.keyword.mapper.KeywordMapper;
import com.ssafy.backend.domain.keyword.repository.KeywordRepository;
import com.ssafy.backend.domain.keyword.repository.KeywordStatisticRepository;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.service.MemberService;
import com.ssafy.backend.domain.news.repository.NewsRelationRepository;
import com.ssafy.backend.global.exception.favorite.FavoriteException;
import com.ssafy.backend.global.exception.favorite.FavoriteExceptionType;
import com.ssafy.backend.global.exception.keyword.KeywordException;
import com.ssafy.backend.global.exception.keyword.KeywordExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService{
    @Value("${django.url}")
    private String djangoUrl;
    @Value("${django.port}")
    private String djangoPort;


    private final KeywordMapper keywordMapper;
    private final KeywordRepository keywordRepository;
    private final KeywordStatisticRepository keywordStatisticRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberService memberService;
    private final NewsRelationRepository newsRelationRepository;




    @Override
    public KeywordDto getKeywordDetail(Long keywordsId) {
        Keyword keyword = keywordRepository.findById(keywordsId).orElseThrow(()
                -> new KeywordException(KeywordExceptionType.KEYWORD_NOT_EXIST));
        return keywordMapper.toDto(keyword);
    }

    @Override
    public List<KeywordStatisticDto> getKeywordFreq(Long keywordsId) {
        return keywordStatisticRepository.findFreqStatisticsByKeywordId(keywordsId);
    }

    @Override
    public List<KeywordDto> getMyKeywords() {
        List<Keyword> keywords = favoriteRepository.findKeywordsByMember(memberService.getMemberEntity());
        return keywordMapper.toDto(keywords);
    }

    @Override
    public boolean checkFavorite(Long id) {
        Keyword keyword = keywordRepository.findById(id).orElseThrow(()
                -> new KeywordException(KeywordExceptionType.KEYWORD_NOT_EXIST));
        return favoriteRepository.existsByMemberAndKeyword(memberService.getMemberEntity(), keyword);
    }

    @Override
    @Transactional
    public void addFavorite(Long id) {
        Keyword keyword = keywordRepository.findById(id).orElseThrow(()
                -> new KeywordException(KeywordExceptionType.KEYWORD_NOT_EXIST));
        // 이미 관심 등록 했다면
        if (checkFavorite(keyword.getId())) {
            throw new FavoriteException(FavoriteExceptionType.ALREADY_EXIST);
        }

        Member member = memberService.getMemberEntity();
        Favorite favorite = Favorite.keywordBuilder()
                .member(member)
                .keyword(keyword)
                .build();
        favoriteRepository.save(favorite);
    }

    @Override
    public void deleteFavorite(Long id) {
        Keyword keyword = keywordRepository.findById(id).orElseThrow(()
                -> new KeywordException(KeywordExceptionType.KEYWORD_NOT_EXIST));
        boolean isFavorite = checkFavorite(id);
        // 관심 등록하지 않았다면
        if (!isFavorite) {
            throw new FavoriteException(FavoriteExceptionType.NOT_FOUND);
        }
        Member member = memberService.getMemberEntity();
        Favorite favorite = favoriteRepository.findByMemberAndKeyword(member, keyword);
        checkUser(member, favorite);
        favoriteRepository.delete(favorite);
    }

    @Override
    public Long getTargetNewsCount(GetTopNKeywordRequest getTopNKeywordRequest) {
        String newsType = getTopNKeywordRequest.getNewsType();
        Long domainId = getTopNKeywordRequest.getId();
        LocalDate startDate = getTopNKeywordRequest.getStartDate();
        LocalDate endDate = getTopNKeywordRequest.getEndDate();

        Long totalNewsCount = 0L;

        switch (newsType) {
            case "ECONOMY":
                totalNewsCount = newsRelationRepository.getTotalNewsCountForEconomy(startDate, endDate);
                break;
            case "INDUSTRY":
                totalNewsCount = newsRelationRepository.getTotalNewsCountForIndustry(startDate, endDate, domainId);
                break;
            case "STOCK":
                totalNewsCount = newsRelationRepository.getTotalNewsCountForStock(startDate, endDate, domainId);
                break;
            default:
                break;
        }
        return totalNewsCount;
    }

    @Override
    public List<TopKeywordDTO> getTopNKeyword(GetTopNKeywordRequest getTopNKeywordRequest) {
        Pageable topN = PageRequest.of(0, getTopNKeywordRequest.getTopN());
        String newsType = getTopNKeywordRequest.getNewsType();
        Long domainId = getTopNKeywordRequest.getId();
        LocalDate startDate = getTopNKeywordRequest.getStartDate();
        LocalDate endDate = getTopNKeywordRequest.getEndDate();

        List<TopKeywordDTO> topKeywords = new ArrayList<>();

        switch (newsType) {
            case "ECONOMY":
                topKeywords = newsRelationRepository.getTopNKeywordsForEconomy(startDate, endDate, topN);
                break;
            case "INDUSTRY":
                topKeywords = newsRelationRepository.getTopNKeywordsForIndustry(startDate, endDate, topN, domainId);
                break;
            case "STOCK":
                topKeywords = newsRelationRepository.getTopNKeywordsForStock(startDate, endDate, topN, domainId);
                break;
            default:
                break;
        }
        return topKeywords;
    }

    @Override
    public List<GetKeyPhraseResponse.Message> getKeyphrase(Long keywordId, GetKeyphraseRequest getKeyphraseRequest) {

        LocalDate startDate = getKeyphraseRequest.getStartDate();
        LocalDate endDate = getKeyphraseRequest.getEndDate();
        Long id =  getKeyphraseRequest.getId();
        String newsType = getKeyphraseRequest.getNewsType();
        // LocalDate => String 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd"); 
        String startDateString = startDate.format(formatter);
        String endDateString = endDate.format(formatter);

        // SpringBoot -> DjangoServer 데이터 요청
        RestTemplate restTemplate = new RestTemplate();
        String url = djangoUrl+":"+djangoPort+"/keywords/{keywordId}/keyphrase?";
        String queryUrl = UriComponentsBuilder.fromUriString(url)
                .queryParam("type", newsType)
                .queryParam("id", id)
                .queryParam("start_date", startDateString)
                .queryParam("end_date",endDateString)
                .buildAndExpand(keywordId)
                .toUriString();
        ResponseEntity<GetKeyPhraseResponse> response = restTemplate.exchange(queryUrl, HttpMethod.GET, null, new ParameterizedTypeReference<GetKeyPhraseResponse>() {});
        GetKeyPhraseResponse Returns = response.getBody();
        return Returns.getMessages();
    }

    @Override
    public List<KeywordDto> getSearchKeyword(String name) {
        List<Keyword> keywordList = keywordRepository.findTop10ByNameStartingWith(name);
        return keywordMapper.toDto(keywordList);
    }

    // 유저가 동일한지 체크
    private static void checkUser(Member member, Favorite favorite) {
        if (favorite.getMember() != member) {
            throw new FavoriteException(FavoriteExceptionType.DIFFERENT_USER);
        }
    }


}

