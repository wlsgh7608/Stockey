import * as Highcharts from "highcharts"
import HighchartsReact from "highcharts-react-official"
import highchartsStock from "highcharts/modules/stock"
import BrandLight from "highcharts/themes/brand-light"
import styled from "styled-components"
import { sampleData } from "./dummyData"
import { useState } from "react"

highchartsStock(Highcharts)
Highcharts.setOptions({
  lang: {
    rangeSelectorZoom: "",
    shortMonths: [
      "1월",
      "2월",
      "3월",
      "4월",
      "5월",
      "6월",
      "7월",
      "8월",
      "9월",
      "10월",
      "11월",
      "12월",
    ],
    weekdays: ["일", "월", "화", "수", "목", "금", "토"],
    rangeSelectorTo: "⁓",
  },
})

BrandLight(Highcharts)

const IndustryCandleChart = () => {
  const dummyData = sampleData

  const [options, setOptions] = useState<Highcharts.Options>({
    chart: {
      borderColor: "var(--custom-background)",
      borderRadius: 20,
      borderWidth: 2,
      margin: 20,
      backgroundColor: "transparent",
    },
    credits: {
      enabled: false,
    },
    navigator: {
      enabled: true,
      handles: {
        backgroundColor: "var(--custom-purple-2)",
        borderColor: "var(--custom-black)",
        height: 20,
      },
      height: 60,
      margin: 30,
      maskFill: "rgba(212, 193, 255, 0.4)",
    },
    plotOptions: {},
    rangeSelector: {
      allButtonsEnabled: false,
      buttons: [
        {
          type: "day",
          count: 1,
          text: "1일",
        },
        {
          type: "week",
          count: 1,
          text: "1주",
        },
        {
          type: "month",
          count: 1,
          text: "1개월",
        },
        {
          type: "year",
          count: 1,
          text: "1년",
        },
        {
          type: "all",
          text: "전체",
        },
      ],
      selected: 5,
      inputDateFormat: "%Y.%m.%d",
      inputEditDateFormat: "%Y.%m.%d",
      inputBoxHeight: 20,
      inputStyle: {
        color: "var(--custom-black)",
        fontSize: "1.4rem",
        fontWeight: "bold",
      },
      buttonTheme: {
        width: 40,
        r: 8,
        style: {
          color: "var(--custom-black)",
          fontWeight: "bold",
        },
        states: {
          select: {
            fill: "#D1F7EB",
          },
        },
      },
    },
    scrollbar: {
      enabled: false,
    },
    series: [
      {
        name: "삼성전자",
        type: "line",
        data: dummyData,
        color: "var(--custom-mint)",
        compare: "percent",
      },
    ],
    title: {
      text: "",
    },
    tooltip: {
      split: false,
      valueDecimals: 2,
      valueSuffix: "원",
      dateTimeLabelFormats: {
        day: "%Y년 %m월 %d일",
      },
      pointFormat:
        '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.change}%)<br/>',
    },
    xAxis: {
      type: "datetime",
      labels: {
        step: 1,
      },
    },
    yAxis: {
      type: "linear",
    },
  })

  const changeChartType = (event: React.MouseEvent<HTMLButtonElement>) => {
    switch ((event.target as HTMLButtonElement).value) {
      case "candlestick":
        setOptions({
          series: [
            {
              type: "candlestick",
              color: undefined,
            },
          ],
        })
        break
      case "line":
        setOptions({
          series: [
            {
              type: "line",
              color: "var(--custom-mint)",
            },
          ],
        })
        break
      default:
    }
  }

  return (
    <AreaDiv>
      <TitleDiv>산업 내 시가총액 TOP 5</TitleDiv>
      <ChartWrapper>
        <button onClick={changeChartType} value="line">
          간단히
        </button>
        <button onClick={changeChartType} value="candlestick">
          자세히
        </button>
        <HighchartsReact
          highcharts={Highcharts}
          constructorType={"stockChart"}
          options={options}
        />
      </ChartWrapper>
    </AreaDiv>
  )
}

export default IndustryCandleChart

const ChartWrapper = styled.div`
  width: 100%;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  border-radius: 24px;
  background-color: var(--custom-background);
`

const AreaDiv = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
`

const TitleDiv = styled.div`
  height: 2.4rem;
  width: auto;
  padding: 0px;

  font-family: "Inter";
  font-style: normal;
  font-weight: 700;
  font-size: 2.4rem;
  line-height: 2.4rem;
  /* or 83% */

  display: flex;
  align-items: center;
  letter-spacing: 0.1px;
`
