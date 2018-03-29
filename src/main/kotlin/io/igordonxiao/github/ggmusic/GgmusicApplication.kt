package io.igordonxiao.github.ggmusic

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
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
    var client = OkHttpClient()

    @GetMapping("/songAuthor")
    @ResponseBody
    fun songAuthor(@RequestParam uid: String): Map<String, String> {
        val doc = Jsoup.connect("http://node.kg.qq.com/personal?uid=$uid").get()
        val authorNameEl = doc.select("body > div.mod_wrap.j_personal_main > div.my_show > div.my_show__con > div.my_show__user > span.my_show__name")
        val authorImgEl = doc.select("body > div.mod_wrap.j_personal_main > div.my_show > div.my_show__con > div.my_show__photo > img")
        val levelEl = doc.select("body > div.mod_wrap.j_personal_main > div.my_show > div.my_show__con > div:nth-child(3)")
        val songCountEl = doc.select("#ugc")
        return mapOf(
                "name" to authorNameEl.text(),
                "img" to authorImgEl.attr("src"),
                "level" to levelEl.text(),
                "songCount" to songCountEl.text()
        )
    }

    @GetMapping("/songLyric")
    @ResponseBody
    fun songLyric(@RequestParam ksongmid: String): String {
        val url = "http://node.kg.qq.com/cgi/fcgi-bin/fcg_lyric?jsonpCallback=callback_0&outCharset=utf-8&format=json&ksongmid=$ksongmid"
        val request = Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        return response.body()!!.string()
    }

    @GetMapping("/list")
    @ResponseBody
    fun songList(@RequestParam uid: String, @RequestParam page: Int = 1, @RequestParam @Nullable size: Int = 10): String {
        val url = "http://node.kg.qq.com/cgi/fcgi-bin/kg_ugc_get_homepage?type=get_ugc&start=$page&num=$size&share_uid=$uid"
        val request = Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        return response.body()!!.string()
    }

    @GetMapping("/song")
    @ResponseBody
    fun songInfo(@RequestParam shareId: String): String {
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
