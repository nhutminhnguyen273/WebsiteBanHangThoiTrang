
package com.nhom15.fashion.config;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfig {

    @Value("${paypal.client.app}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> sdkConfig = new HashMap<>();
        sdkConfig.put("mode", mode);
        return sdkConfig;
    }

    @Bean
    public OAuthTokenCredential authTokenCredential(){
        return new OAuthTokenCredential(clientId,clientSecret,paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException{
        try {
            String accessToken = authTokenCredential().getAccessToken();
            APIContext apiContext = new APIContext(accessToken);
            apiContext.setConfigurationMap(paypalSdkConfig());
            return apiContext;
        } catch (PayPalRESTException e) {
            // Log thêm thông tin chi tiết về lỗi
            System.err.println("Error getting access token: " + e.getMessage());
            throw e;
        }
    }
}

