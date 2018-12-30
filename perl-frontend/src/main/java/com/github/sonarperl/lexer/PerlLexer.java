package com.github.sonarperl.lexer;

import com.github.sonarperl.PerlConfiguration;
import com.github.sonarperl.PerlPunctuator;
import com.github.sonarperl.api.PerlKeyword;
import com.github.sonarperl.api.PerlTokenType;
import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;
import com.sonar.sslr.impl.channel.UnknownCharacterChannel;


import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.o2n;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.and;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;

public final class PerlLexer {
    private PerlLexer() {}

    public static Lexer create(PerlConfiguration config) {
        LexerState lexerState = new LexerState();
        return Lexer.builder()
                .withCharset(config.getCharset())
                .withFailIfNoChannelToConsumeOneCharacter(true)
                .withChannel(new NewLineChannel(lexerState))
                .withChannel(new BlackHoleChannel("\\s"))
                .withChannel(commentRegexp("#[^\\n\\r]*+"))
                .withChannel(new StringLiteralsChannel())
                .withChannel(regexp(PerlTokenType.NUMBER, "[1-9][0-9]*+"))
                .withChannel(regexp(PerlTokenType.NUMBER, "0++"))
                .withChannel(new IdentifierAndKeywordChannel(and("[$%&@]?[a-zA-Z_]", o2n("\\w")), true, PerlKeyword.values()))
                .withChannel(new PunctuatorChannel(PerlPunctuator.values()))
                .withChannel(new UnknownCharacterChannel())
                .build();
    }
}
