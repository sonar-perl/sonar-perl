#!perl

use 5.006001;
use strict;
use warnings;

use English qw< -no_match_vars >;
use Readonly;

use Getopt::Long qw< GetOptions >;
use List::Util qw< first max >;
use Pod::Usage qw< pod2usage >;

use Perl::Critic::Exception::Parse ();
use Perl::Critic::Utils qw<
    :characters :severities policy_short_name
    $DEFAULT_VERBOSITY $DEFAULT_VERBOSITY_WITH_FILE_NAME
>;
use Perl::Critic::Utils::Constants qw< $_MODULE_VERSION_TERM_ANSICOLOR >;
use Perl::Critic::Violation qw<>;

Readonly::Scalar my $EXIT_SUCCESS           => 0;

my $output = \*STDOUT;

sub _out {
    my ($prototype) = @_;
    my $tmp = "";
    $tmp .= <<EOL;
<?xml version="1.0" encoding="UTF-8"?>
<profile>
    <name>Sonar way</name>
    <language>perl</language>
    <rules>
EOL
    Perl::Critic::Policy::set_format( _proto_format() );
    foreach my $item (q{$prototype->_get_policies()}) {
        $tmp .= join qq{\n}, map { "$_" } @{ $prototype->_get_policies() };
    }
    $tmp .=  <<EOL;
    </rules>
</profile>
EOL

#    $SEVERITY_HIGHEST = 5
#    $SEVERITY_HIGH = 4
#    $SEVERITY_MEDIUM= 3
#    $SEVERITY_LOW = 2
#    $SEVERITY_LOWEST = 1

    my @severities = qw( undef INFO MINOR MAJOR CRITICAL BLOCKER );

    $tmp =~ s|<priority>(\d)</priority>|"<priority>" . $severities[$1] . "</priority>"|eg;

    print {$output} $tmp;
}

sub _makeReadable {
	my ($str) = @_;
	$str =~ s/.*:://;
	$str =~ s/([a-z])([A-Z])/$1 $2/g;
	return $str;
}

sub _proto_format {
    my ($self) = @_;

return << "EOL";
  <rule>
      <repositoryKey>PerlCritic</repositoryKey>
      <key>%p</key>
      <priority>%s</priority>
  </rule>
EOL

}

sub _render_profile_prototype {

    require Perl::Critic::ProfilePrototype;
    require Perl::Critic;

    my %pc_params = (-profile => $EMPTY, -severity => $SEVERITY_LOWEST);
    my @policies = Perl::Critic->new( %pc_params )->policies();
    my $prototype = Perl::Critic::ProfilePrototype->new( -policies => \@policies );
    _out($prototype);

    exit $EXIT_SUCCESS;
}

_render_profile_prototype();
