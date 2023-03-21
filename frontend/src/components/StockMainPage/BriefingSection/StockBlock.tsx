import { useState } from "react"
import Paper from "@mui/material/Paper"
import styled from "styled-components"

interface Props {
  companyLogo?: string
  companyName: string
  currentPrice: number
  priceChange: number
}

const StockBlock = ({
  companyLogo,
  companyName,
  currentPrice,
  priceChange,
}: Props) => {
  const [state, setState] = useState<"default" | "selected">("default")
  const clickHandler = () => {
    setState("selected")
  }
  return (
    <StyledBlock state={state} onClick={clickHandler}>
      <StyledPaper elevation={0} state={state}>
        <StyledContent state={state}>
          <StyledLogo src={`logo_images/${companyLogo}.png`} />
          <StyledInfo>
            <StyledName>{companyName}</StyledName>
            <StyledStats>
              <StyledPrice>{currentPrice.toLocaleString("ko-KR")}</StyledPrice>
              <StyledChange isIncreasing={priceChange > 0 ? true : false}>
                {`${priceChange > 0 ? "▲" : "▼"} ${priceChange}%`}
              </StyledChange>
            </StyledStats>
          </StyledInfo>
        </StyledContent>
      </StyledPaper>
    </StyledBlock>
  )
}

export default StockBlock

const StyledBlock = styled.div<{ state: "default" | "selected" }>`
  position: relative;
  width: 30%;
  ::after {
    content: "";
    padding-bottom: 100%;
    display: block;
  }
`

const StyledPaper = styled(Paper)<{ state: "default" | "selected" }>`
  && {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border-radius: 40px;
    border: ${(props) =>
      props.state === "default"
        ? "5px solid #f8f8f8"
        : "5px solid transparent"};
    background-image: ${(props) =>
      props.state === "default"
        ? "null"
        : "linear-gradient(#FAF5F7, #FAF5F7), linear-gradient(130deg, #99C2FF 0%, #FFA7D1 100%)"};
    background-origin: border-box;
    background-clip: content-box, border-box;
    object-fit: cover;
  }
`

const StyledContent = styled.div<{ state: "default" | "selected" }>`
  padding: 20%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background-color: ${(props) =>
    props.state === "default" ? "white" : "#FAF5F7"};
  border-radius: 40px;
  width: -webkit-fill-available;
  height: -webkit-fill-available;
`

const StyledLogo = styled.img`
  width: 25%;
  height: 25%;
  max-width: 100px;
  border-radius: 24px;
`

const StyledInfo = styled.div`
  display: flex;
  flex-direction: column;
`

const StyledName = styled.p`
  color: black;
  font-size: 24px;
  font-weight: Bold;
  margin-bottom: 5%;
`

const StyledStats = styled.div`
  display: flex;
  flex-direction: row;
  align-items: flex-end;
`

const StyledPrice = styled.p`
  color: #8a8a8a;
  font-size: 18px;
  margin-right: 10px;
  margin-block: 0px;
`
const StyledChange = styled.p<{ isIncreasing: boolean }>`
  font-size: 14px;
  margin-block: 0px;
  color: ${(props) => (props.isIncreasing ? "#FB6F6F" : "#72A6FA")};
`