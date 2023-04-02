import axios from "axios"

// custom axios
// parameter => accessToken : accessToken 인증이 필요한 경우, argument 값으로 accessToken, setAccessToken을 넣어서 사용
// useQuery에서 사용하는 경우 useQuery의 queryKey에 accessToken을 넣어서 fetch 함수에 전달할 것

const customAxios = (
  accessToken: string | undefined = undefined,
  setAccessToken: Function | undefined = undefined
) => {
  const api = axios.create({
    baseURL: `${process.env.REACT_APP_SERVER_BASE_URL}`,
    timeout: 2000,
    withCredentials: true,
  })

  // refresh 여부 확인 코드
  let isTokenRefreshing = false
  let refreshSubscribers: ((token: string) => void)[] = []

  // refresh 필요 함수 저장
  const subscribeTokenRefresh = (cb: (token: string) => void) => {
    refreshSubscribers.push(cb)
  }

  // refreshToken 재실행 코드
  const onRefreshed = (token: string) => {
    refreshSubscribers.forEach((cb) => cb(token))
  }

  // header에 accessToken 추가
  api.interceptors.request.use((config) => {
    if (accessToken) {
      config.headers["Authorization"] = "Bearer " + accessToken
    }
    return config
  })

  // refresh token 요청 함수
  const getRefreshToken = async (): Promise<string | void> => {
    try {
      const { data: newAccessToken } = await axios.get(
        `${process.env.REACT_APP_SERVER_BASE_URL}/auth/refresh`,
        {
          timeout: 2000,
          withCredentials: true,
        }
      )

      isTokenRefreshing = false
      onRefreshed(newAccessToken)
      refreshSubscribers = []

      if (setAccessToken) {
        setAccessToken(newAccessToken?.data)
      }

      return newAccessToken?.data
    } catch (error) {
      isTokenRefreshing = false
      refreshSubscribers = []

      if (setAccessToken) {
        setAccessToken(undefined)
      }
    }
  }

  // refresh 요청 보내기
  api.interceptors.response.use(
    (response) => {
      return response
    },
    async (error) => {
      const {
        config,
        response: { status },
      } = error
      const originalRequest = config

      if (status !== 401 && status !== 400) {
        return Promise.reject(error)
      }

      if (isTokenRefreshing) {
        return new Promise((resolve) => {
          subscribeTokenRefresh((token: string) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            resolve(axios(originalRequest))
          })
        })
      }
      isTokenRefreshing = true
      const newAccessToken = await getRefreshToken()
      console.log(newAccessToken)
      console.log(typeof newAccessToken)

      if (typeof newAccessToken === "string") {
        console.log("refetch")
        config.headers.Authorization = `Bearer ${newAccessToken}`
        return axios(config)
      }

      return Promise.reject(error)
    }
  )

  return api
}

export default customAxios
