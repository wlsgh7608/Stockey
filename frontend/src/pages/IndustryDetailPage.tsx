import Grid from "@mui/material/Grid"
import { useState } from "react"
import { useParams } from "react-router-dom"
import { useIndustryList } from "../hooks/useIndustryList"
import Spinner from "../components/common/Spinner/Spinner"
import {
  DefaultLayout,
  OnIndustrySelectorLayout,
  OnKeywordPanelLayout,
} from "../components/IndustryDetailPage/PageLayouts"

const IndustryDetailPage = () => {
  const params = useParams()
  const industryName = params?.industryName

  const industryId = industryName ? industryList.indexOf(industryName) + 1 : 0

  const { isLoading, data: industryInfo } = useIndustryList(industryId)

  const [mode, setMode] = useState<string>("def")
  const [className, setClassName] = useState<string>("")

  const changeLayout = (
    e: React.MouseEvent<HTMLElement>,
    toggleMode: string
  ) => {
    switch (toggleMode) {
      case "sel":
        switch (mode) {
          case "sel":
            setClassName("sel-to-def")
            setTimeout(() => {
              setMode("def")
            }, 600)
            break
          case "def":
            setClassName("def-to-sel")
            setTimeout(() => {
              setMode("sel")
            }, 600)
            break
          case "kwd":
            setClassName("kwd-to-sel")
            setTimeout(() => {
              setMode("sel")
            }, 600)
            break
          default:
            break
        }
        break
      case "kwd":
        switch (mode) {
          case "kwd":
            setClassName("kwd-to-def")
            setTimeout(() => {
              setMode("def")
            }, 400)
            break
          case "def":
            setClassName("def-to-kwd")
            setTimeout(() => {
              setMode("kwd")
            }, 400)
            break
          case "sel":
            setClassName("sel-to-kwd")
            setTimeout(() => {
              setMode("kwd")
            }, 600)
            break
          default:
            break
        }
        break
      default:
        break
    }
  }

  return (
    <Grid container rowSpacing={3} columnSpacing={4.5}>
      {isLoading ? (
        <Spinner />
      ) : (
        {
          def: (
            <DefaultLayout
              changeLayout={changeLayout}
              className={className}
              industryInfo={industryInfo}
            />
          ),
          kwd: (
            <OnKeywordPanelLayout
              changeLayout={changeLayout}
              className={className}
              industryInfo={industryInfo}
            />
          ),
          sel: (
            <OnIndustrySelectorLayout
              changeLayout={changeLayout}
              className={className}
              industryInfo={industryInfo}
            />
          ),
        }[mode]
      )}
    </Grid>
  )
}

export default IndustryDetailPage

const industryList = [
  "에너지",
  "소재",
  "자본재",
  "운송",
  "자동차와부품",
  "내구소비재와의류",
  "호텔,레스토랑,레저등",
  "소매(유통)",
  "식품,음료,담배",
  "제약과생물공학",
  "은행",
  "증권",
  "다각화된금융",
  "보험",
  "소프트웨어와서비스",
  "기술하드웨어와장비",
  "반도체와반도체장비",
  "전기와전기제품",
  "디스플레이",
  "전기통신서비스",
  "미디어와엔터테인먼트",
  "유틸리티",
]
