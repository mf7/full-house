package fullhouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.commons.util.InetUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.io.IOException;


@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@ComponentScan({"fullhouse"})
public class PlayerServiceApplication {
	//@Value("${server.port}")
    private Integer service_port;

	//@Value("${bwkz.version}")
    private Double version;

	
    public static void main(String[] args) {
        SpringApplication.run(PlayerServiceApplication.class, args);
    }


	@Bean
    public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils) {
        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
        config.setNonSecurePort(5000);
        config.setPreferIpAddress(true);
        config.setIpAddress(getIpAddress());
        config.getMetadataMap().put("instanceId", "PlayerService-v"+this.version);
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
}
