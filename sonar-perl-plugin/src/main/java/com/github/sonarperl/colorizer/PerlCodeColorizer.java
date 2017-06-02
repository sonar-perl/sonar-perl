package com.github.sonarperl.colorizer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sonar.api.web.CodeColorizerFormat;
import org.sonar.colorizer.InlineDocTokenizer;
import org.sonar.colorizer.KeywordsTokenizer;
import org.sonar.colorizer.LiteralTokenizer;
import org.sonar.colorizer.RegexpTokenizer;
import org.sonar.colorizer.StringTokenizer;
import org.sonar.colorizer.Tokenizer;

import com.github.sonarperl.PerlLanguage;

@SuppressWarnings("deprecation")
public class PerlCodeColorizer extends CodeColorizerFormat { // NOSONAR

    // from https://github.com/jploski/epic-ide/blob/b70f1c5ccb3e528f3a8c727b04dc633439f1d35a/org.epic.perleditor/src/org/epic/core/parser/PerlLexerBase.java
    private static Set<String> keywords = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "BEGIN", "CHECK", "INIT", "END", "UNITCHECK",
            "bless", "caller", "continue", "dbmclose",
            "dbmopen", "default", "die", "do", "dump",
            "else", "elsif", "eval", "exit", "for", "foreach", "given",
            "goto", "if", "import", "last", "local", "my", "new", "next",
            "no", "our", "package", "redo", "ref", "require", "return", "state",
            "sub", "tie", "tied",
            "unless", "untie", "until", "use", "wantarray", "when", "while",
            "__FILE__", "__LINE__", "__PACKAGE__", "abs", "accept",
            "alarm", "atan2", "bind", "binmode", "closedir",
            "for", "lcfirst", "opendir", "printf", "readdir", "readlink",
            "seekdir", "socketpair", "substr", "telldir", "tied", "times",
            "ucfirst", "waitpid", "chdir", "chmod", "chomp", "chop",
            "chown", "chr", "chroot", "close", "connect", "cos", "crypt",
            "defined", "delete", "each",
            "endgrent", "endhostent", "endnetent", "endprotoent",
            "endpwent", "endservent", "eof", "exec", "exists", "exp",
            "fcntl", "fileno", "flock", "fork", "formline", "getc",
            "getgrent", "getgrgid", "getgrnam", "gethostbyaddr",
            "gethostbyname", "gethostent", "getlogin", "getnetbyaddr",
            "getnetbyname", "getnetent", "getpeername", "getpgrp",
            "getppid", "getpriority", "getprotobyname", "getprotobynumber",
            "getprotoent", "getpwent", "getpwnam", "getpwuid",
            "getservbyname", "getservbyport", "getservent", "getsockname",
            "getsockopt", "glob", "gmtime", "grep", "hex", "index", "int",
            "ioctl", "join", "keys", "kill", "lc", "length", "link",
            "listen", "localtime", "log", "lstat", "map", "mkdir",
            "msgctl", "msgget", "msgrcv", "msgsnd", "oct", "open",
            "ord", "pack", "pipe", "pop", "pos", "print", "push",
            "quotemeta", "rand", "read", "recv", "rename", "reset",
            "reverse", "rewinddir", "rindex", "rmdir", "say", "scalar", "seek",
            "select", "semctl", "semget", "semop", "send", "setgrent",
            "sethostent", "setnetent", "setpgrp", "setpriority",
            "setprotoent", "setpwent", "setservent", "setsockopt", "shift",
            "shmctl", "shmget", "shmread", "shmwrite", "shutdown", "sin",
            "sleep", "socket", "sort", "splice", "split", "sprintf",
            "sqrt", "srand", "stat", "study", "sub", "symlink", "syscall",
            "sysread", "sysseek", "system", "syswrite", "tell",
            "time", "truncate", "uc", "umask", "undef", "unlink",
            "unpack", "unshift", "utime", "values", "vec",
            "wait", "warn", "write")));

    private static final String CLOSING_SPAN = "</span>";

    public PerlCodeColorizer() {
        super(PerlLanguage.KEY);
    }

    @Override
    public List<Tokenizer> getTokenizers() {
        return Arrays.asList(
            new StringTokenizer("<span class=\"s\">", CLOSING_SPAN),
            new InlineDocTokenizer("#", "<span class=\"cd\">", CLOSING_SPAN) {},
            new KeywordsTokenizer("<span class=\"k\">", CLOSING_SPAN, keywords),
            new LiteralTokenizer("<span class=\"s\">", CLOSING_SPAN),
            new RegexpTokenizer("<span class=\"j\">", CLOSING_SPAN, "#[^\\n\\r]*+"),
            new RegexpTokenizer("<span class=\"c\">", CLOSING_SPAN, "[+-]?[0-9]++(\\.[0-9]*+)?"));
    }
}