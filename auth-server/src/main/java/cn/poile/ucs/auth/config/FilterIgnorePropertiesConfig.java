package cn.poile.ucs.auth.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 忽略安全校验url，从配置文件中读取
 * @author: yaohw
 * @create: 2019-10-10 17:59
 **/
@Data
@Configuration
@ConditionalOnExpression("!'${ignore}'.isEmpty()")
@ConfigurationProperties(prefix = "ignore")
public class FilterIgnorePropertiesConfig {

    private List<String> urls = new ArrayList<>();
}
