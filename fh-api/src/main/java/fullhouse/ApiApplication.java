package fullhouse;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.commons.util.InetUtils;

import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Arrays;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.io.IOException;

import java.io.*;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;


import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600) //An hour
@EnableConfigurationProperties
@EnableAsync
@EnableSwagger2
@ComponentScan({"fullhouse", "fullhouse.dao"})
public class ApiApplication {

	private static final Logger LOG = LoggerFactory.getLogger(ApiApplication.class);
	//@Value("${server.port}")
    private Integer service_port;

	//@Value("${bwkz.version}")
    private Double version;

	@Autowired
	private TypeResolver typeResolver;


	
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
		//new SpringApplicationBuilder().bannerMode(CONSOLE).run(args);
    }


	@Bean
    public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {
        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
        config.setNonSecurePort(4000);
        config.setPreferIpAddress(true);
        config.setIpAddress(getIpAddress());
        config.getMetadataMap().put("instanceId", "api-v");
        return config;
		}

	
	public static String getIpAddress() {
		try {
			for (Enumeration<NetworkInterface> enumNic = NetworkInterface.getNetworkInterfaces(); enumNic.hasMoreElements(); ) {
				NetworkInterface ifc = enumNic.nextElement();
				if (ifc.isUp()) {
					for (Enumeration<InetAddress> enumAddr = ifc.getInetAddresses(); enumAddr.hasMoreElements(); ) {
						InetAddress address = enumAddr.nextElement();
						if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
							return address.getHostAddress();
						}
					}
				}
			}
		} catch (IOException e) {
			// ignore
		}
		return "unknown";
	}

	@Bean
	public JedisConnectionFactory connectionFactory() {
		return new JedisConnectionFactory();
	}


	
	@Bean
	public Docket getApi() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("brandworkz")).paths(PathSelectors.any()).build().pathMapping("/").apiInfo(metadata())
			/*.directModelSubstitute(LocalDate.class, String.class)*/.genericModelSubstitutes(ResponseEntity.class)
				.alternateTypeRules(newRule(typeResolver.resolve(DeferredResult.class, typeResolver.resolve(ResponseEntity.class, WildcardType.class)), typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false)
				.globalResponseMessage(RequestMethod.GET, newArrayList(new ResponseMessageBuilder().code(500).message("500 message").responseModel(new ModelRef("Error")).build()))
				.securitySchemes(newArrayList(apiKey())).securityContexts(newArrayList(securityContext()));
	}
 
	
 
	private ApiKey apiKey() {
		return new ApiKey("mykey", "api_key", "header");
	}
 
	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/anyPath.*")).build();
	}
 
	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return newArrayList(new SecurityReference("mykey", authorizationScopes));
	}
 
	/*@Bean
	SecurityConfiguration security() {
		return new SecurityConfiguration("test-app-client-id", "test-secret", "test-app-realm", "test-app", "apiKey", "sep");
		}*/
 
	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl");
	}

	private ApiInfo metadata() {
		String s = getFileContents("swagger-info.html");
		return new ApiInfoBuilder()
			.title("Brandworkz API")
			.description(s)
			.version(this.version+"")
			.contact("mark.folkard@brandworkz.com")
			.build();
    }

	/*@Bean
	CompressResponseFilter getCompressFilter() {
		return new CompressResponseFilter();
		}*/

	private String getFileContents(String file_location){
		try {
			Resource resource = new ClassPathResource(file_location);
			BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				stringBuilder.append(line).append('\n');
			}
			br.close();
			return stringBuilder.toString();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

}
