package com.github.sonarperl.critic;

import com.github.sonarperl.PerlLanguage;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;


public class SonarWayProfile implements BuiltInQualityProfilesDefinition {
    private static final Logger LOGGER = Loggers.get(SonarWayProfile.class);
    private static final String PERL_PROFILE_XML = "com/github/sonarperl/sonar-way-profile.xml";
    private static final String KEY = "PerlCritic";
    private static final String NAME = "PerlCritic";

    @Override
    public void define(final Context context) {

        final NewBuiltInQualityProfile profile = context
                .createBuiltInQualityProfile(NAME, PerlLanguage.KEY).setDefault(true);
        final String repositoryKey = KEY;

        try (final InputStream rulesXml = this.getClass().getClassLoader()
                .getResourceAsStream(PERL_PROFILE_XML)) {

            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document xmlDoc = builder.parse(rulesXml);
            final NodeList nodes = xmlDoc.getElementsByTagName("key");
            for (int i = 0; i < nodes.getLength(); i++) {
                final Node node = nodes.item(i);
                final String key = node.getTextContent();
                profile.activateRule(repositoryKey, key);
            }

        } catch (Exception e) {
            LOGGER.warn("Unexpected error while registering rules", e);
        }
        profile.done();
    }
}

