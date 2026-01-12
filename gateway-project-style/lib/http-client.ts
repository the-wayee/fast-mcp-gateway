import axios, { type AxiosResponse, type InternalAxiosRequestConfig } from "axios"
import { toast } from "sonner"

// 定义后端统一响应结构
export interface ActionResult<T = any> {
    code: string
    message: string
    data: T
    timestamp: number
    traceId?: string
}

// 扩展 AxiosRequestConfig 以支持自定义配置
declare module "axios" {
    export interface AxiosRequestConfig {
        /** 是否隐藏成功提示 */
        silentSuccess?: boolean
        /** 是否隐藏错误提示 */
        silentError?: boolean
    }
}

const httpClient = axios.create({
    baseURL: "/api", // Next.js 代理路径
    timeout: 15000,
    headers: {
        "Content-Type": "application/json",
    },
})

// 请求拦截器
httpClient.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 可以在这里添加 Token 等 Header
        return config
    },
    (error) => {
        return Promise.reject(error)
    },
)

// 响应拦截器
httpClient.interceptors.response.use(
    (response: AxiosResponse<ActionResult>) => {
        const res = response.data
        const config = response.config

        // 业务状态码判断 (假设 200 为成功)
        if (res.code === "200") {
            // 默认非 GET 请求显示成功提示，且未显式禁用
            const isMutation = ["post", "put", "delete", "patch"].includes(config.method?.toLowerCase() || "")
            if (isMutation && !config.silentSuccess) {
                toast.success(res.message || "Operation successful")
            }
            return response
        } else {
            // 业务错误
            if (!config.silentError) {
                toast.error(res.message || "Business Error")
            }
            return Promise.reject(new Error(res.message || "Business Error"))
        }
    },
    (error) => {
        const config = error.config
        if (!config?.silentError) {
            const msg = error.response?.data?.message || error.message || "Network Error"
            toast.error(msg)
        }
        return Promise.reject(error)
    },
)

export default httpClient
