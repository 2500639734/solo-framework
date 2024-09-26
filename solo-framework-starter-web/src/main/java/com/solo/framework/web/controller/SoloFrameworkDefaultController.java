package com.solo.framework.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Api(tags = "框架默认内置控制器")
@RestController
@RequestMapping("/solo-framework")
public class SoloFrameworkDefaultController {

    @Setter(onMethod_ = {@Autowired})
    private LocaleResolver localeResolver;

    @ApiOperation(value = "设置语言环境", notes = "基于当前的LocaleResolver环境解析器")
    @GetMapping("/set-locale")
    public ResponseEntity<Void> setLocale(@RequestParam String lang, HttpServletRequest request, HttpServletResponse response) {
        localeResolver.setLocale(request, response, Locale.forLanguageTag(lang));
        return ResponseEntity.ok().build();
    }

}
