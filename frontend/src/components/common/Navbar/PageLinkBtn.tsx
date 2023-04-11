import React from "react"
import styled, { keyframes } from "styled-components"
// mui icon
import HomeRoundedIcon from "@mui/icons-material/HomeRounded"
import QueryStatsRoundedIcon from "@mui/icons-material/QueryStatsRounded"
import AppsRoundedIcon from "@mui/icons-material/AppsRounded"
import ArticleRoundedIcon from "@mui/icons-material/ArticleRounded"
import BookmarkIcon from "@mui/icons-material/Bookmark"
import { useNavigate } from "react-router-dom"

type PageLinkBtnProps = {
  name: string
  selected: boolean
  isNarrow: boolean
}

const PageLinkBtn = ({ name, selected, isNarrow }: PageLinkBtnProps) => {
  // 페이지 이동 함수 작성
  const navigate = useNavigate()
  const handleBtnClick = () => {
    navigate(
      name === "주식 종목"
        ? "/stock"
        : name === "산업 정보"
        ? "/industry"
        : name === "키워드"
        ? "/keyword"
        : "/my"
    )
  }

  return (
    <>
      <PageLinkBtnDiv
        className={selected ? "selected" : undefined}
        onClick={handleBtnClick}
      >
        {name === "주식 종목" ? (
          <QueryStatsRoundedIcon />
        ) : name === "산업 정보" ? (
          <AppsRoundedIcon />
        ) : name === "키워드" ? (
          <ArticleRoundedIcon />
        ) : (
          <BookmarkIcon />
        )}
        {isNarrow ? undefined : <PageLinkText>{name}</PageLinkText>}
      </PageLinkBtnDiv>
    </>
  )
}

export default PageLinkBtn

// 호버링 애니메이션
const BtnHoverAnime = keyframes`
  from {
    background: none;
  }
  to {
    background: linear-gradient(92.18deg, #ff996c 1.48%, #fe7598 98.93%);
    // background-color: white;
  }
`

// 버튼 styled
const PageLinkBtnDiv = styled.div`
  // 레이아웃
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 16px;
  gap: 10px;

  // 크기
  width: 100%;

  // 형태
  border-radius: 100px;

  // 글자
  font-weight: bold;
  font-size: 2rem;
  color: white;

  // 마진
  margin-top: 24px;

  // transition
  transition: all 0.25s ease;

  // 드래그 방지
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;

  // 커서
  cursor: pointer;

  &.selected {
    background: linear-gradient(92.18deg, #ff996c 1.48%, #fe7598 98.93%);
  }
  &:hover {
    animation: ${BtnHoverAnime} 0s 0s ease 1 forwards;
    transform: scale(1.1, 1.1);
  }
`

// 버튼 내 text style
const PageLinkText = styled.div`
  color: inherit;
  text-align: center;
  width: calc(100% - 40px);
`
