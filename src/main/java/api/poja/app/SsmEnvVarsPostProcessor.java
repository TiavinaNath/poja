package api.poja.app;

import io.awspring.cloud.parameterstore.ParameterStorePropertySource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;

/**
 * Makes SSM-backed env vars behave exactly like real environment variables.
 *
 * <p>Spring resolves {@code my.api.key} against {@code MY_API_KEY}, and relaxes binding of {@code
 * spring.*}, only for a {@link SystemEnvironmentPropertySource} whose name ends with {@code
 * -systemEnvironment}. The parameter store source is a plain enumerable source and gets neither, so
 * it is re-wrapped here. The suffix is load-bearing: see
 * SpringConfigurationPropertySource#getPropertyMappers.
 */
@PojaGenerated
public class SsmEnvVarsPostProcessor implements EnvironmentPostProcessor, Ordered {

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication app) {
    MutablePropertySources propertySources = environment.getPropertySources();
    List<String> ssmSourceNames =
        propertySources.stream()
            .filter(ParameterStorePropertySource.class::isInstance)
            .map(PropertySource::getName)
            .toList();
    System.out.println(
        "SsmEnvVarsPostProcessor: rewrapping "
            + ssmSourceNames
            + " among "
            + propertySources.stream().map(PropertySource::getName).toList());
    ssmSourceNames.forEach(
        name -> propertySources.replace(name, asSystemEnvironment(propertySources.get(name))));
  }

  private static SystemEnvironmentPropertySource asSystemEnvironment(PropertySource<?> source) {
    ParameterStorePropertySource ssmSource = (ParameterStorePropertySource) source;
    Map<String, Object> properties = new HashMap<>();
    for (String name : ssmSource.getPropertyNames()) {
      properties.put(name, ssmSource.getProperty(name));
    }
    return new SystemEnvironmentPropertySource(source.getName() + "-systemEnvironment", properties);
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 11;
  }
}
