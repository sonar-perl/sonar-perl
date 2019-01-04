package com.github.sonarperl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Token;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PerlVisitor {

    private PerlVisitorContext context;

    public Set<AstNodeType> subscribedKinds() {
        return Collections.emptySet();
    }

    // default implementation does nothing
    public void visitFile(AstNode node) {
    }

    // default implementation does nothing
    public void leaveFile(AstNode node) {
    }

    // default implementation does nothing
    public void visitNode(AstNode node) {
    }

    // default implementation does nothing
    public void visitToken(Token token) {
    }

    // default implementation does nothing
    public void leaveNode(AstNode node) {
    }

    public PerlVisitorContext getContext() {
        return context;
    }

    public void scanFile(PerlVisitorContext context) {
        this.context = context;
        AstNode tree = context.rootTree();
        if (tree != null) {
            visitFile(tree);
            scanNode(tree, subscribedKinds());
            leaveFile(tree);
        }
    }

    public void scanNode(AstNode node) {
        scanNode(node, subscribedKinds());
    }

    private void scanNode(AstNode node, Set<AstNodeType> subscribedKinds) {
        boolean isSubscribedType = subscribedKinds.contains(node.getType());

        if (isSubscribedType) {
            visitNode(node);
        }

        List<AstNode> children = node.getChildren();
        if (children.isEmpty()) {
            for (Token token : node.getTokens()) {
                visitToken(token);
            }
        } else {
            for (AstNode child : children) {
                scanNode(child, subscribedKinds);
            }
        }

        if (isSubscribedType) {
            leaveNode(node);
        }
    }


}
