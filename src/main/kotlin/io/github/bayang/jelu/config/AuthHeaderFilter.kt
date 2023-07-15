package io.github.bayang.jelu.config

import io.github.bayang.jelu.dao.Provider
import io.github.bayang.jelu.dto.CreateUserDto
import io.github.bayang.jelu.dto.JeluUser
import io.github.bayang.jelu.service.UserService
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val LOGGER = KotlinLogging.logger {}

@Component
@ConditionalOnProperty(name = ["jelu.auth.proxy.enabled"], havingValue = "true", matchIfMissing = false)
class AuthHeaderFilter(
    private val userService: UserService,
    private val properties: JeluProperties,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val headerName = properties.auth.proxy.header
        val headerAuth: String? = request.getHeader(headerName)
        if (!headerAuth.isNullOrBlank()) {
            LOGGER.trace("auth header $headerAuth")
            val res = userService.findByLoginAndProvider(headerAuth, Provider.PROXY)
            val user: JeluUser = if (res.isEmpty()) {
                val isAdmin = properties.auth.proxy.adminName.isNotBlank() && properties.auth.proxy.adminName == headerAuth
                val saved = userService.save(CreateUserDto(login = headerAuth, password = "proxy", isAdmin = isAdmin, Provider.PROXY))
                JeluUser(userService.findUserEntityById(saved.id!!))
            } else {
                JeluUser(userService.findUserEntityById(res.first().id!!))
            }
            val authentication = UsernamePasswordAuthenticationToken(
                user,
                null,
                user.authorities,
            )
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }
}
