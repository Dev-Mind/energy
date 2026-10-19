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
                "(^|/)\\.?env(\\.[a-z0-9_-]+)?(\\.txt)?$|\\.git/|phpmyadmin|phpunit|\\.aws/|" +
                "appsettings(\\.[a-z]+)?\\.json$|(^|/)config(\\.(json|js|ya?ml|ini))?$|" +
                "(^|/)(auth|composer|package|credentials)\\.json$|aws-exports\\.js$|" +
                "(^|/)credentials(\\.[a-z]+)?$|(^|/)aws\\.ya?ml$|\\.(sql|bak|ini)$|" +
                "\\.well-known/security\\.txt).*"
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
