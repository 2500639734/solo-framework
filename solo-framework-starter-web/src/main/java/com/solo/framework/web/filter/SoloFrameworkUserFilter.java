package com.solo.framework.web.filter;

import com.solo.framework.core.user.LoginUser;
import com.solo.framework.core.context.SoloFrameworkUserContextHolder;
import com.solo.framework.core.user.ISoloFrameworkUser;

import javax.servlet.*;
import java.io.IOException;

/**
 * 用户信息过滤器
 * 负责在请求开始时获取用户信息并放入上下文，请求结束时清除
 */
public class SoloFrameworkUserFilter implements Filter {

    private final ISoloFrameworkUser soloFrameworkUser;

    public SoloFrameworkUserFilter(ISoloFrameworkUser soloFrameworkUser) {
        this.soloFrameworkUser = soloFrameworkUser;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            LoginUser<?> user = soloFrameworkUser.getLoginUser();
            SoloFrameworkUserContextHolder.setUser(user);
            chain.doFilter(request, response);
        } finally {
            SoloFrameworkUserContextHolder.clear();
        }
    }

}
