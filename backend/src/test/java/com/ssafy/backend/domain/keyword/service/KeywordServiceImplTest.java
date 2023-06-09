package com.ssafy.backend.domain.keyword.service;

import com.ssafy.backend.domain.favorites.entity.Favorite;
import com.ssafy.backend.domain.favorites.repository.FavoriteRepository;
import com.ssafy.backend.domain.keyword.api.request.GetTopNKeywordRequest;
import com.ssafy.backend.domain.keyword.dto.KeywordDto;
import com.ssafy.backend.domain.keyword.dto.KeywordStatisticDto;
import com.ssafy.backend.domain.keyword.dto.TopKeywordDTO;
import com.ssafy.backend.domain.keyword.entity.Keyword;
import com.ssafy.backend.domain.keyword.entity.KeywordStatistic;
import com.ssafy.backend.domain.keyword.enums.StatisticType;
import com.ssafy.backend.domain.keyword.repository.KeywordRepository;
import com.ssafy.backend.domain.keyword.repository.KeywordStatisticRepository;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.enums.OauthType;
import com.ssafy.backend.domain.member.repository.MemberRepository;
import com.ssafy.backend.domain.news.entity.enums.NewsType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
class KeywordServiceImplTest {

    @Autowired
    KeywordService keywordService;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    KeywordStatisticRepository keywordStatisticRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FavoriteRepository favoriteRepository;

    @Test
    void 키워드_상세_가져오기() {
        // given
        Keyword k1 = Keyword.builder().name("test").description("testest..").build();
        Keyword kwdEntity = keywordRepository.save(k1);
        // when
        KeywordDto keywordDto = keywordService.getKeywordDetail(kwdEntity.getId());
        // then
        Assertions.assertThat(keywordDto.getId()).isEqualTo(kwdEntity.getId());
    }

    @Test
    void 키워드_빈도통계() {
        Keyword k1 = Keyword.builder().name("금리").description("금리란...").build();
        Keyword k2 = Keyword.builder().name("연준").description("연준이란...").build();
        Keyword saveK1 = keywordRepository.save(k1);
        keywordRepository.save(k2);

        KeywordStatistic ks1 = KeywordStatistic.builder()
                .keyword(k1)
                .statisticDate(LocalDate.of(2022, 10, 22))
                .count(10L)
                .category(StatisticType.FREQ).build();
        KeywordStatistic ks2 = KeywordStatistic.builder()
                .keyword(k1)
                .statisticDate(LocalDate.of(2022, 11, 15))
                .count(20L)
                .category(StatisticType.FREQ).build();
        KeywordStatistic ks3 = KeywordStatistic.builder()
                .keyword(k1)
                .statisticDate(LocalDate.of(2022, 12, 3))
                .count(50L)
                .category(StatisticType.FREQ).build();

        keywordStatisticRepository.save(ks1);
        keywordStatisticRepository.save(ks2);
        keywordStatisticRepository.save(ks3);

        List<KeywordStatisticDto> keywordFreq = keywordService.getKeywordFreq(saveK1.getId());

        Assertions.assertThat(keywordFreq.size()).isEqualTo(3);

        Assertions.assertThat(keywordFreq.get(0).getStatisticDate())
                .isEqualTo(LocalDate.of(2022, 10, 22));
        Assertions.assertThat(keywordFreq.get(0).getCount())
                .isEqualTo(10);
        Assertions.assertThat(keywordFreq.get(1).getStatisticDate())
                .isEqualTo(LocalDate.of(2022, 11, 15));
        Assertions.assertThat(keywordFreq.get(1).getCount())
                .isEqualTo(20);
        Assertions.assertThat(keywordFreq.get(2).getStatisticDate())
                .isEqualTo(LocalDate.of(2022, 12, 3));
        Assertions.assertThat(keywordFreq.get(2).getCount())
                .isEqualTo(50);
    }

    @Test
    void 관심키워드출력() {
        Member member = Member.oAuthBuilder().oAuthType(OauthType.KAKAO).oAuthId(1234L).nickname("jun").build();
        Member member2 = Member.oAuthBuilder().oAuthType(OauthType.KAKAO).oAuthId(5678L).nickname("kim").build();
        Member saveMember = memberRepository.save(member);
        Member saveMember2 = memberRepository.save(member2);

        Keyword k1 = Keyword.builder().name("금리").description("금리란...").build();
        Keyword k2 = Keyword.builder().name("연준").description("연준이란...").build();
        Keyword saveK1 = keywordRepository.save(k1);
        Keyword saveK2 = keywordRepository.save(k2);

        Favorite f1 = Favorite.keywordBuilder().member(saveMember).keyword(saveK1).build();
        Favorite f2 = Favorite.keywordBuilder().member(saveMember).keyword(saveK2).build();
        favoriteRepository.save(f1);
        favoriteRepository.save(f2);

        List<Keyword> keywords = favoriteRepository.findKeywordsByMember(saveMember);
        for (Keyword keyword : keywords) {
            System.out.println("keyword = " + keyword.getId());
            System.out.println("keyword = " + keyword.getName());
        }

        Assertions.assertThat(keywords.get(0).getName()).isEqualTo("금리");
        Assertions.assertThat(keywords.get(1).getName()).isEqualTo("연준");
        Assertions.assertThat(keywords.get(0).getId()).isEqualTo(saveK1.getId());
        Assertions.assertThat(keywords.get(1).getId()).isEqualTo(saveK2.getId());

        List<Keyword> keywords2 = favoriteRepository.findKeywordsByMember(saveMember2);
        Assertions.assertThat(keywords2.size()).isEqualTo(0);
    }

    @Test
    void 관심_키워드_등록() {
        Member member = Member.oAuthBuilder().oAuthType(OauthType.KAKAO).oAuthId(1234L).nickname("jun").build();
        Member saveMember = memberRepository.save(member);

        Keyword k1 = Keyword.builder().name("금리").description("금리란...").build();
        Keyword saveK1 = keywordRepository.save(k1);

        Favorite favorite = Favorite.keywordBuilder()
                .member(saveMember)
                .keyword(saveK1)
                .build();

        // 관심 키워드 등록
        favoriteRepository.save(favorite);
        Assertions.assertThat(favoriteRepository.existsByMemberAndKeyword(saveMember, saveK1)).isTrue();

        // 이미 등록한 키워드 등록 시도 (위 라인과 동일하지만 굳이 쓴 이유는 이 기능이 돌 때 아래 로직이 돌아가는걸 표시하려고..)
        Assertions.assertThat(favoriteRepository.existsByMemberAndKeyword(saveMember, saveK1)).isTrue();
    }

    @Test
    void 관심_키워드_여부_확인() {
        Member member = Member.oAuthBuilder().oAuthType(OauthType.KAKAO).oAuthId(1234L).nickname("jun").build();
        Member member2 = Member.oAuthBuilder().oAuthType(OauthType.KAKAO).oAuthId(5678L).nickname("kim").build();
        Member saveMember = memberRepository.save(member);
        Member saveMember2 = memberRepository.save(member2);

        Keyword k1 = Keyword.builder().name("금리").description("금리란...").build();
        Keyword saveK1 = keywordRepository.save(k1);

        Favorite f1 = Favorite.keywordBuilder().member(saveMember).keyword(saveK1).build();
        favoriteRepository.save(f1);

        Assertions.assertThat(favoriteRepository.existsByMemberAndKeyword(saveMember, saveK1)).isTrue();
        Assertions.assertThat(favoriteRepository.existsByMemberAndKeyword(saveMember2, saveK1)).isFalse();
    }

    @Test
    void topN_테스트() {
        GetTopNKeywordRequest getTopNKeywordRequest = GetTopNKeywordRequest.builder()
                .topN(6)
                .id(85L)
                .newsType(NewsType.STOCK.toString())
                .startDate(LocalDate.of(2022, 10, 15))
                .endDate(LocalDate.of(2022, 12, 15))
                .build();

        List<TopKeywordDTO> topKeywords = keywordService.getTopNKeyword(getTopNKeywordRequest);

        for (TopKeywordDTO topKeyword : topKeywords) {
            System.out.println("topKeyword.getKeywordId() = " + topKeyword.getKeywordId());
            System.out.println("topKeyword.getKeywordCount() = " + topKeyword.getKeywordCount());
//
        }
    }
}