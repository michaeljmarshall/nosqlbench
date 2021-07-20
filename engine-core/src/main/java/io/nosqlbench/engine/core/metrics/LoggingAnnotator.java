package io.nosqlbench.engine.core.metrics;

import io.nosqlbench.nb.annotations.Service;
import io.nosqlbench.nb.api.annotations.Annotation;
import io.nosqlbench.nb.api.annotations.Annotator;
import io.nosqlbench.nb.api.config.standard.NBMapConfigurable;
import io.nosqlbench.nb.api.config.standard.ConfigModel;
import io.nosqlbench.nb.api.config.standard.NBConfigModel;
import io.nosqlbench.nb.api.config.standard.NBConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

@Service(value = Annotator.class, selector = "log")
public class LoggingAnnotator implements Annotator, NBMapConfigurable {

    private final static Logger annotatorLog = LogManager.getLogger("ANNOTATION");
    private Level level;

    private final Map<String, String> tags = new LinkedHashMap<>();

    public LoggingAnnotator() {
    }

    @Override
    public void recordAnnotation(Annotation annotation) {
        String inlineForm = annotation.asJson();
        annotatorLog.log(level, inlineForm);
    }

    @Override
    public void applyConfig(Map<String, ?> providedConfig) {
        NBConfigModel configModel = getConfigModel();
        NBConfiguration cfg = configModel.apply(providedConfig);
        String levelName = cfg.param("level", String.class);
        this.level = Level.valueOf(levelName);
    }

    @Override
    public NBConfigModel getConfigModel() {
        return ConfigModel.of(this.getClass())
                .defaults("level", "INFO",
                        "The logging level to use for this annotator")
                .asReadOnly();
    }

}
