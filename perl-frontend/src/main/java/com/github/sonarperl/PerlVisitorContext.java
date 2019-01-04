package com.github.sonarperl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.RecognitionException;

public class PerlVisitorContext {

        private final AstNode rootTree;
        private final PerlFile perlFile;
        private final RecognitionException parsingException;

        public PerlVisitorContext(AstNode rootTree, PerlFile perlFile) {
            this(rootTree, perlFile, null);
        }

        public PerlVisitorContext(PerlFile perlFile, RecognitionException parsingException) {
            this(null, perlFile, parsingException);
        }

        private PerlVisitorContext(AstNode rootTree, PerlFile perlFile, RecognitionException parsingException) {
            this.rootTree = rootTree;
            this.perlFile = perlFile;
            this.parsingException = parsingException;
        }

        public AstNode rootTree() {
            return rootTree;
        }

        public PerlFile perlFile() {
            return perlFile;
        }

        public RecognitionException parsingException() {
            return parsingException;
        }

    }
