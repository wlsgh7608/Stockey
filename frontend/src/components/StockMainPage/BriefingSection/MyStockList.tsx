import styled from "styled-components"
import StockBlock from "./StockBlock"
import { useRandomStock } from "../../../hooks/useRandomStock"

const MyStockList = () => {
  const { data: randomStockData } = useRandomStock(3)
  return (
    <StyledDiv>
      {randomStockData?.map((stock: any, index: number) => (
        <StockBlock
          key={`randomStock-${index}`}
          idx={index}
          stockName={stock.name}
          currentPrice={stock.currentPrice}
          priceChange={stock.changeRate}
        />
      ))}
    </StyledDiv>
  )
}

export default MyStockList

const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`
