package com.inlym.lifehelper.common.filter;

import com.inlym.lifehelper.common.auth.core.SimpleAuthentication;
import com.inlym.lifehelper.common.auth.jwt.JwtService;
import com.inlym.lifehelper.common.constant.CustomHttpHeader;
import com.inlym.lifehelper.common.constant.SpecialPath;
import com.inlym.lifehelper.common.model.CustomRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 鉴权过滤器
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @date 2022-01-22
 * @since 1.0.0
 */
@Order(100)
@WebFilter(urlPatterns = "/*")
@Slf4j
@RequiredArgsConstructor
public class JwtHandlerFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) throws ServletException, IOException {
        if (!SpecialPath.HEALTH_CHECK_PATH.equals(request.getRequestURI())) {
            CustomRequestContext context = (CustomRequestContext) request.getAttribute(CustomRequestContext.attributeName);

            // 从请求头获取鉴权凭证
            String token = request.getHeader(CustomHttpHeader.JWT_TOKEN);

            if (token != null) {
                try {
                    SimpleAuthentication authentication = jwtService.parse(token);

                    // 这一步是使用 Spring Security 框架必需的
                    SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                    context.setUserId(authentication.getUserId());
                } catch (Exception e) {
                    log.trace("[JWT 解析出错] " + e.getMessage());
                }
            }
        }

        chain.doFilter(request, response);
    }
}