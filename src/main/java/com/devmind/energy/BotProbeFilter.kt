package com.devmind.energy

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Court-circuite silencieusement les sondes de robots/scanners (WordPress, PHP, fichiers
 * d'environnement, etc.) avant qu'elles n'atteignent le dispatcher Spring. Cela evite le bruit
 * dans les logs (NoResourceFoundException) genere par ces requetes malveillantes ou automatisees.
 */
@Component
@Order(1)
class BotProbeFilter : OncePerRequestFilter() {

    companion object {
        private val BLOCKED_PATTERN = Regex(
            "(?i).*(wp-admin|wp-login|wp-content|wp-includes|xmlrpc\\.php|\\.php$|" +
                "\\.env$|\\.git/|phpmyadmin|phpunit|\\.aws/credentials|vendor/.*\\.php|" +
                "appsettings\\.json$|config\\.json$|\\.well-known/security\\.txt).*"
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (BLOCKED_PATTERN.matches(request.requestURI)) {
            response.status = HttpServletResponse.SC_NOT_FOUND
            return
        }
        filterChain.doFilter(request, response)
    }
}
