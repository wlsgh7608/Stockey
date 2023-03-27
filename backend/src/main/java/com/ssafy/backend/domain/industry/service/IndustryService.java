package com.ssafy.backend.domain.industry.service;

import com.ssafy.backend.domain.industry.api.response.IndustryCapitalDto;
import com.ssafy.backend.domain.industry.dto.IndustryDto;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.stock.dto.StockBriefDto;

import java.util.List;

public interface IndustryService {
     List<IndustryDto> getAll();
     IndustryDto getOne(Long id);
     List<IndustryCapitalDto> getAllMarketCap();
     List<StockBriefDto> getStockList();
     List<StockBriefDto> getStockList(Long id);
     List<IndustryDto> getMyIndustries(Member member);
     boolean checkFavorite(Member member,Long id);





}
