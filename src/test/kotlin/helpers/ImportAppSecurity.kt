package helpers

import com.realworld.spring.webflux.security.JwtConfig
import com.realworld.spring.webflux.security.JwtSigner
import com.realworld.spring.webflux.security.SecurityConfig
import com.realworld.spring.webflux.security.TokenFormatter
import org.springframework.context.annotation.Import

@Import(SecurityConfig::class, TokenFormatter::class, JwtSigner::class, JwtConfig::class)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ImportAppSecurity
