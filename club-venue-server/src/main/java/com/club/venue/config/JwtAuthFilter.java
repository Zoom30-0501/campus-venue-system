package com.club.venue.config;

import com.club.venue.common.JwtUtil;
import com.club.venue.entity.SysUser;
import com.club.venue.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (!jwtUtil.isTokenExpired(token)) {
                    String username = jwtUtil.getUsername(token);
                    String role = jwtUtil.getRole(token);
                    Long userId = jwtUtil.getUserId(token);

                    // 校验用户状态：已删除用户不设置认证
                    if (userId != null) {
                        SysUser user = sysUserMapper.selectById(userId);
                        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
                            filterChain.doFilter(request, response);
                            return;
                        }
                    }

                    String auth = role != null ? role : "user";
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + auth.toUpperCase());
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                log.warn("JWT auth failed: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
