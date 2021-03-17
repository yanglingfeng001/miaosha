package com.ylf.miaosha.config;
import com.ylf.miaosha.domain.MiaoshaUser;
import com.ylf.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz== MiaoshaUser.class;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletResponse response=webRequest.getNativeResponse(HttpServletResponse.class);
        HttpServletRequest request=webRequest.getNativeRequest(HttpServletRequest.class);
        String paramToken=request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken=getCookieValue(request,MiaoshaUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken))
        {
            return "null";//没有cookie访问该页面，直接返回登陆页面
        }
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return miaoshaUserService.getByToken(response,token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies=request.getCookies();//获取所有cookie
        for(Cookie cookie:cookies)
        {
            if(cookie.getName().equals(cookieNameToken))
            {
                return cookie.getValue();
            }
        }
        return null;
    }

}
