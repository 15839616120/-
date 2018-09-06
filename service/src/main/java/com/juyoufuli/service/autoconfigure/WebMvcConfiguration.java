package com.juyoufuli.service.autoconfigure;

import com.juyoufuli.service.config.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;


@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Autowired
	private AuthInterceptor                     authInterceptor;
	@Autowired
	private ByteArrayHttpMessageConverter       byteArrayHttpMessageConverter;
	@Autowired
	private StringHttpMessageConverter          stringHttpMessageConverter;
	@Autowired
	private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

	/**
	 * Add interceptors.
	 *
	 * @param registry the registry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/**");
	}
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		/* 是否通过请求Url的扩展名来决定media type */
		configurer.favorPathExtension(true).useRegisteredExtensionsOnly(false)
				/* 不检查Accept请求头 */
				.ignoreAcceptHeader(true)
				/*//是否使用url上的参数来指定数据返回类型*/
				.favorParameter(true)
				.parameterName("format")
				/* 设置默认的media type */
				.defaultContentType(MediaType.APPLICATION_JSON_UTF8)
				/* 请求以.html结尾的会被当成MediaType.TEXT_HTML*/
				.mediaType("html", MediaType.TEXT_HTML)
				/* 请求以.json结尾的会被当成MediaType.APPLICATION_JSON*/
				.mediaType("json", MediaType.APPLICATION_JSON)
				/* 请求以.json结尾的会被当成MediaType.APPLICATION_JSON*/
				.mediaType("xml", MediaType.APPLICATION_XML);
	}


	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.clear();
		converters.add(this.byteArrayHttpMessageConverter);
		converters.add(this.stringHttpMessageConverter);
		converters.add(this.mappingJackson2HttpMessageConverter);
	}


	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 解决 SWAGGER 404报错
		if (!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
			registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		}
	}
}
