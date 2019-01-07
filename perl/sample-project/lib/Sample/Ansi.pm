package Sample::Ansi;

sub new {
    my $class = shift;
    my %args  = @_;
    my $self  = bless \%args, $class;
    return $self;
}

# say hello
sub hello {
    return "hellöle";
}

