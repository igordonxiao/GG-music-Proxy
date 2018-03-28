package io.igordonxiao.github.ggmusic

import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.lang.Nullable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): FilterRegistrationBean<*> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.setOrder(0)
        return bean
    }
}

@SpringBootApplication
class GgmusicApplication

@Controller
class GgmusicController {
    // 全民UID
    val kgUid = "639d9d87232a328835"
    var client = OkHttpClient()


    @GetMapping("/list")
    @ResponseBody
    fun songList(@RequestParam page : Int = 1, @RequestParam @Nullable size: Int = 10): String {
        val url = "http://node.kg.qq.com/cgi/fcgi-bin/kg_ugc_get_homepage?type=get_ugc&start=$page&num=$size&share_uid=$kgUid"
        val request = Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        return response.body()!!.string()
    }

    @GetMapping("/song")
    @ResponseBody
    fun songInfo(@RequestParam shareId : String): String {
        val url = "http://cgi.kg.qq.com/fcgi-bin/kg_ugc_getdetail?inCharset=GB2312&outCharset=utf-8&v=4&shareid=$shareId"
        val request = Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        return response.body()!!.string()
    }
}

fun main(args: Array<String>) {
    runApplication<GgmusicApplication>(*args)
}
