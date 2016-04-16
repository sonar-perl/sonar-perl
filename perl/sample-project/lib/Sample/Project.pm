package Sample::Project;

$VERSION = '0.1';

sub new {
    my $class = shift;
    my %args  = @_;
    my $self  = bless \%args, $class;
    return $self;
}

sub hello {
	return "hello";
}

sub goodbye {
	return "goodbye";
}

1;
