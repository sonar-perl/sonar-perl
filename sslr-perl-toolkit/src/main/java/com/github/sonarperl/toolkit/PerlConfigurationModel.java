package com.github.sonarperl.toolkit;

import com.github.sonarperl.PerlConfiguration;
import com.github.sonarperl.api.PerlKeyword;
import com.github.sonarperl.parser.PerlParser;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.Tokenizer;
import org.sonar.sslr.toolkit.AbstractConfigurationModel;
import org.sonar.sslr.toolkit.ConfigurationProperty;
import org.sonar.sslr.toolkit.Validators;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

public class PerlConfigurationModel extends AbstractConfigurationModel {

        private static final Logger LOG = LoggerFactory.getLogger(PerlConfigurationModel.class);

        private static final String CHARSET_PROPERTY_KEY = "sonar.sourceEncoding";

        // VisibleForTesting
        ConfigurationProperty charsetProperty = new ConfigurationProperty("Charset", CHARSET_PROPERTY_KEY,
                getPropertyOrDefaultValue(CHARSET_PROPERTY_KEY, "UTF-8"),
                Validators.charsetValidator());

        @Override
        public Charset getCharset() {
            return Charset.forName(charsetProperty.getValue());
        }

        @Override
        public List<ConfigurationProperty> getProperties() {
            return Collections.singletonList(charsetProperty);
        }

        @Override
        public Parser<Grammar> doGetParser() {
            return PerlParser.create(getConfiguration());
        }

        @Override
        public List<Tokenizer> doGetTokenizers() {
            return List.of(
                    new KeywordsTokenizer("<span class=\"k\">", "</span>", PerlKeyword.keywordValues()));
        }

        // VisibleForTesting
        PerlConfiguration getConfiguration() {
            return new PerlConfiguration(Charset.forName(charsetProperty.getValue()));
        }

        // VisibleForTesting
        static String getPropertyOrDefaultValue(String propertyKey, String defaultValue) {
            String propertyValue = System.getProperty(propertyKey);

            if (propertyValue == null) {
                LOG.info("Property \"{}\" is not set, using the default value \"{}\".", propertyKey, defaultValue);
                return defaultValue;
            } else {
                LOG.info("Property \"{}\" is set, using its value \"{}\".", propertyKey, propertyValue);
                return propertyValue;
            }
        }

}
